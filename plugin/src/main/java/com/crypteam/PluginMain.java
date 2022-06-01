package com.crypteam;
import com.crypteam.rpc.RPCPublisher;
import com.crypteam.rpc.RPCSubscriber;
import com.crypteam.rpc.RPCThread;
import com.crypteam.solana.SolanaProgramID;
import com.crypteam.solana.SolanaRPC;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.misc.PublicKey;
import com.crypteam.solana.misc.RegionAccountInfo;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
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
            SolanaProgramID.PROGRAM_ID = new PublicKey("u35VEZ9gPkPg1VAp3YAxPejRhKKu5q8FJagEc7vUs6Y");
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        Section.downloadScriptData();
//        Section.MapAccess();
        JedisPool pool = new JedisPool("localhost", 6379);
        Jedis subInstance = pool.getResource();
        Jedis pubInstance = pool.getResource();


        subscriber = new RPCSubscriber();
        new RPCThread(subInstance, subscriber).start();
        new RPCPublisher(pubInstance);
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
            case "regionAccess":
            {
                Section sec = new Section(Integer.parseInt(args[0]));
                sec.setRegionAccess(args[1]);
                break;
            }
            case "refreshRegion":
            {
                int areaID = Integer.parseInt(args[0]);

                SolanaRPC solanaRPC = new SolanaRPC("https://explorer-api.devnet.solana.com/");
                RegionAccountInfo accountInfo;

                try {
                    accountInfo = solanaRPC.getAccountInfoByRegionID(SolanaProgramID.PROGRAM_ID, areaID);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }

                ShortBuffer buf = ByteBuffer.wrap(accountInfo.getPayload()).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
                short[] region = new short[buf.limit()];
                buf.get(region);

                Section sec = new Section(areaID);
                sec.setRegion(region);

                System.out.println("Region length: " + region.length);
                System.out.println("Original region length: " + sec.getRegion().length);
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
        getLogger().info("Player " + event.getPlayer().getName() + " is logout");
    }
}
