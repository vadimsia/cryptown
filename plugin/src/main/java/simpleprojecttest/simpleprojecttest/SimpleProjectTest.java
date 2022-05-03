package simpleprojecttest.simpleprojecttest;
import org.bukkit.Bukkit;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import static java.lang.Integer.parseInt;

public final class SimpleProjectTest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("ver1222");

        WorldChange.downloadScriptData();

    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equals("parseWorld")){
           WorldChange.getRegion(parseInt(args[0]));
            return true;
        } else if((cmd.getName().equals("setWorld"))) {
            WorldChange.setRegion(parseInt(args[0]), WorldChange.testRegion);
            return true;
        }
        return false;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
