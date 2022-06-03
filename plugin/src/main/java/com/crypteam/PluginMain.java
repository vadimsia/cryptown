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
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public final class PluginMain extends JavaPlugin implements Listener {

    RPCSubscriber subscriber;
    private static Server server;

    @Override
    public void onEnable() {
        Section.setMapAccess();

        try {
            SolanaProgramProperties.PROGRAM_ID = new PublicKey("u35VEZ9gPkPg1VAp3YAxPejRhKKu5q8FJagEc7vUs6Y");
            SolanaProgramProperties.RPC_ENDPOINT = "https://explorer-api.devnet.solana.com/";
            SolanaProgramProperties.FRONTEND_URL = "http://127.0.0.1:3000/";
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

        server = getServer();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName()) {
            case "getRegion":
            {
                Section sec = new Section(Integer.parseInt(args[0]));
                short[] region = sec.getRegion();
                System.out.println("Sector length: " + region.length);
                break;
            }
            case "setRegion":
            {
                Section sec = new Section(Integer.parseInt(args[0]));
                sec.setRegion(Section.testRegion);
                break;
            }
            case "initRegions":
            {
                Section.initRegions(Integer.parseInt(args[0]));
                break;
            }
            case "removeRegions":
            {
                Section.removeRegions(Integer.parseInt(args[0]));
                break;
            }
            case "setRegionAccess":
            {
                Section sec = new Section(Integer.parseInt(args[0]));
                sec.setRegionAccess(BukkitAdapter.adapt(getServer().getPlayer(sender.getName())));
                break;
            }
            case "refreshRegion":
            {
                int areaID;
                CryptownProgram program = new CryptownProgram();
                RegionAccountInfo accountInfo;

                try {
                    areaID = Section.getPositionPlayer(BukkitAdapter.adapt(getServer().getPlayer(sender.getName())));
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
                break;
            }
            case "login":
            {
                sender.sendMessage(ChatColor.GREEN + Section.login(getServer().getPlayer(sender.getName())));
            }
            case "getPlayerPosition":
            {
                int a = Section.getPositionPlayer(BukkitAdapter.adapt(getServer().getPlayer(sender.getName())));
                Bukkit.getLogger().info(""+a);
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

    @NotNull
    public static Server getBukkitServer() {
        return server;
    }
}
