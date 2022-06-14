package com.crypteam;
import com.crypteam.rpc.RPCPublisher;
import com.crypteam.rpc.RPCSubscriber;
import com.crypteam.rpc.RPCThread;
import com.crypteam.solana.SolanaProgramProperties;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import com.crypteam.solana.program.CryptownProgram;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class PluginMain extends JavaPlugin implements Listener {

    RPCSubscriber subscriber;

    @Override
    public void onEnable() {
        Section.setMapAccess();

        try {
            SolanaProgramProperties.PROGRAM_ID = new PublicKey("BZuqbnwSbcxTM5GyDw1V1vbM7YbPqXauYRGjViBMGCor");
            SolanaProgramProperties.RPC_ENDPOINT = "https://explorer-api.devnet.solana.com/";
            SolanaProgramProperties.FRONTEND_URL = "http://localhost:3000/";
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        Section.downloadScriptData();

        JedisPool pool = new JedisPool("localhost", 6379);

        subscriber = new RPCSubscriber();
        new RPCThread(pool.getResource(), subscriber).start();
        new RPCPublisher(pool.getResource());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = BukkitAdapter.adapt(getServer().getPlayer(sender.getName()));

        switch (cmd.getName()) {
            case "getRegion" -> {
                Section sec = new Section(Integer.parseInt(args[0]));
                short[] region = sec.getRegion();
                System.out.println("Sector length: " + region.length);
            }
            case "setRegion" -> {
                Section sec = new Section(Integer.parseInt(args[0]));
                sec.setRegion(Section.testRegion);
            }
            case "initRegions" -> Section.initRegions(Integer.parseInt(args[0]));
            case "removeRegions" -> Section.removeRegions(Integer.parseInt(args[0]));
            case "setRegionAccess" -> {
                Section sec = new Section(Integer.parseInt(args[0]));
                sec.setRegionAccess(player);
            }
            case "refreshRegion" -> {
                CryptownProgram program = new CryptownProgram();
                RegionAccountInfo accountInfo;
                int areaID;

                try {
                    areaID = Section.getPlayerStandingAreaID(player);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "You need to stay in your own region to refresh it.");
                    return true;
                }


                try {
                    accountInfo = program.getRegionByID(areaID);
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_PURPLE + e);
                    return true;
                }

                ShortBuffer buf = ByteBuffer.wrap(accountInfo.getPayload()).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
                short[] region = new short[buf.limit()];
                buf.get(region);

                Section sec = new Section(areaID);
                sec.setRegion(region);

                System.out.println("Region length: " + region.length);
                System.out.println("Original region length: " + sec.getRegion().length);
                sender.sendMessage(ChatColor.GREEN + "Region successfully refreshed from solana!");
            }
            case "login" -> sender.sendMessage(ChatColor.GREEN + SolanaProgramProperties.FRONTEND_URL + "?uuid=" + player.getUniqueId() + "&nick=" + player.getName());
            case "getPlayerPosition" -> {
                try {
                    Bukkit.getLogger().info("" + Section.getPlayerStandingAreaID(player));
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Exception: " + e);
                }
            }
        }

        return true;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        subscriber.unsubscribe();
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Player player = BukkitAdapter.adapt(event.getPlayer());
        Section.removeRegionAccess(player);
        getLogger().info("Player " + event.getPlayer().getName() + " logout");
    }
}
