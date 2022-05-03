package simpleplugin.simpleplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimplePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Kek");
//        World world = Bukkit.getWorld("World");
//        for (int a = 0; a<= 200; a++) {
//            Bukkit.getLogger().info(world.getBlockAt(0, a, 0).toString());
//        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equals("basic")){
            World world = Bukkit.getWorld("World");
            for (int a = 0; a<= 200; a++) {
                Bukkit.getLogger().info(world.getBlockAt(0, a, 0).toString());
            }
            return true;
        } else if((cmd.getName().equals("nobasic"))) {
            World world = Bukkit.getWorld("World");
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
