package com.crypteam;
import com.crypteam.rcon.RConServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import static java.lang.Integer.parseInt;

public final class PluginMain extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        Section.downloadScriptData();

        new Thread(new RConServer()).start();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equals("getRegion")){
            Section sec = new Section(parseInt(args[0]));
          sec.getRegion();
            return true;
        } else if((cmd.getName().equals("setRegion"))) {
            Section sec = new Section(parseInt(args[0]));
           sec.setRegion(Section.testRegion);
            return true;
        }
        return false;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
