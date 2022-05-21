package com.crypteam;
import com.crypteam.rcon.RConServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import static java.lang.Integer.parseInt;

public final class PluginMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
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
        getLogger().info("Player " + event.getPlayer().getName() + " is logout");
    }
}
