package com.crypteam;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Section {
    public static int[] testRegion;
    static World world = Bukkit.getWorld("World");                                                  // Мир
    private static Map<String, Integer> worldScript = new HashMap<String, Integer>();               // Колекция шифрования
    private static Map<Integer, String> worldDescriptor = new HashMap<Integer, String>();           // Коллекция дешифрования

    private final static int countSectionsX = 4;                                                           // Количество секций по X
    private final static int countSectionsZ = 4;                                                           // Количество секций по Z

    private final static int sectionSizeX = 200;
    private final static int sectionSizeZ = 200;
    private final static int countSectionRegions = 48;


    private final static int regionInitY = -60;                                                           // Начальная координата Y

    private final static int regionSizeY = 64;                                                             // Размер региона Y
    private int regionId;                                                                               // Номер региона
    private int regionSectionId;                                                                    // Номер региона в секции
    private void setRegionSectionId() {
        this.regionSectionId = regionId % countSectionRegions;
    }
    private int regionStartX;                                                                       // Начальная координата региона по X
    private int regionStartZ;                                                                       // Начальная координата региона по Z
    private int regionEndX;                                                                         // Конечная координата региона по X
    private void setRegionEndX() {
        this.regionEndX = Math.abs(regionStartX + regionSizeX);
    }
    private int regionEndZ;                                                                          // Конечная координата региона по Z
    private void setRegionEndZ() {
        this.regionEndZ = Math.abs(regionStartZ + regionSizeZ);
    }
    private int regionSizeZ;                                                                        // Размер региона по Z
    private int regionSizeX;                                                                        //Размер региона по X


    public Section(int regionId) {
        try {
            this.regionId = regionId;
            setRegionSectionId();
            Connection connection = null;
            Statement statmnt;
            ResultSet result;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.s3db");
            statmnt = connection.createStatement();
            result = statmnt.executeQuery("SELECT * FROM sectionRegions WHERE id="+regionId);
            this.regionStartX = result.getInt("posX");
            this.regionStartZ = result.getInt("posZ");
            this.regionSizeX = result.getInt("sizeX");
            this.regionSizeZ = result.getInt("sizeZ");
            connection.close();
            statmnt.close();
            result.close();
        } catch (ClassNotFoundException e) {

        } catch (SQLException e) {

        }
        setRegionEndX();
        setRegionEndZ();
    }
    static public void downloadScriptData() {                                                    // Загрузка коллекций на сервер
        try {
            Connection connection = null;
            Statement statmnt;
            ResultSet result;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.s3db");
            statmnt = connection.createStatement();
            result = statmnt.executeQuery("SELECT * FROM descriptor");
            while(result.next()) {
                int id = result.getInt("id");
                String blockData = result.getString("blockData");
                worldScript.put(blockData, id);
                worldDescriptor.put(id, blockData);
            }
            Bukkit.getLogger().info(worldScript.toString());
            Bukkit.getLogger().info("==========");
            Bukkit.getLogger().info(worldDescriptor.toString());
            connection.close();
            statmnt.close();
            result.close();
        } catch (ClassNotFoundException e) {

        } catch (SQLException e) {

        }
    }
    static public void uploadScriptData(){                                                          // Выгрузка коллекций в JSON
        Connection connection = null;
        Statement statmnt;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.s3db");
            statmnt = connection.createStatement();
            int start = statmnt.executeQuery("SELECT id FROM descriptor WHERE id=(SELECT MAX(id) FROM descriptor)").getInt("id")+1;
            Bukkit.getLogger().info(""+start);
            for(int i = start; i < worldScript.size(); i++) {

                Bukkit.getLogger().info("INSERT INTO 'descriptor' ('blockData', 'id') VALUES ("+worldDescriptor.get(i)+", '"+worldScript.get(worldDescriptor.get(i))+"')");
                statmnt.execute("INSERT INTO 'descriptor' ('blockData', 'id') VALUES ('"+worldDescriptor.get(i)+"', "+worldScript.get(worldDescriptor.get(i))+")");
            }
            connection.close();
            statmnt.close();
        } catch (ClassNotFoundException e) {

        } catch (SQLException e) {

        }

    }

   public void setRegion(int[] codingWorld) {
        for (int y = 0; y < regionSizeY; y++) {
            for(int x = 0; x < regionSizeX; x++) {
                for (int z = 0; z < regionSizeZ; z++) {
                    int id = y*regionSizeZ*regionSizeX+x*regionSizeZ+z;
                    String s = worldDescriptor.get(codingWorld[id]);
                    world.setBlockData(regionStartX + x,regionInitY + y,regionStartZ + z, Bukkit.createBlockData(s.substring(s.indexOf(":") + 1, s.indexOf("}"))));
                }
            }
        }
   }
   public int[] getRegion(){
        int[] codingWorld = new int[regionSizeX*regionSizeY*regionSizeZ];
        for (int y = 0; y < regionSizeY; y++) {
            for(int x = 0; x < regionSizeX; x++) {
                for (int z = 0; z < regionSizeZ; z++) {
                    int id = y*regionSizeZ*regionSizeX+x*regionSizeZ+z;
                    String block = world.getBlockAt( regionStartX+x,regionInitY + y,regionStartZ + z).getBlockData().toString();
                    if(!worldScript.containsKey(block)) {
                        worldScript.put(block, worldScript.size());
                        worldDescriptor.put(worldScript.size()-1, block);
                    }
                    codingWorld[id]= worldScript.get(block);
                }
            }
        }

        uploadScriptData();
        testRegion = codingWorld;
        return codingWorld;
   }

    public void RegionAccess() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        BlockVector3 min = BlockVector3.at(-10, 5, -4);
    }
}
