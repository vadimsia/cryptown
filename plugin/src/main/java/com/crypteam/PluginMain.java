package com.crypteam;
import com.crypteam.rcon.RConServer;
import com.crypteam.solana.SolanaProgramID;
import com.crypteam.solana.exceptions.AddressFormatException;
import com.crypteam.solana.misc.PublicKey;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        try {
            SolanaProgramID.PROGRAM_ID = new PublicKey("AoNiQdgpqwE1PYc5R5gYqxWv9nQtr3xN3gTEdGb4tFeW");
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        Section.downloadScriptData();
//        Section.MapAccess();
        new Thread(new RConServer()).start();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Section sec;
        if (cmd.getName().equals("getRegion")) {
            sec = new Section(Integer.parseInt(args[0]));
            sec.getRegion();
            return true;
        } else if (cmd.getName().equals("setRegion")) {
            sec = new Section(Integer.parseInt(args[0]));
            sec.setRegion(Section.testRegion);
            return true;
        } else if (cmd.getName().equals("initRegions")) {
            Section.initRegions(Integer.parseInt(args[0]));
            return true;
        } else if (cmd.getName().equals("regionAccess")) {
            sec = new Section(Integer.parseInt(args[0]));
            sec.setRegionAccess(args[1]);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        Player player = BukkitAdapter.adapt(event.getPlayer());
        Section.removeRegionAccess(player);
        getLogger().info("Player " + event.getPlayer().getName() + " is logout");
    }
}
