package com.crypteam;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;


public class Section {
    public static short[] testRegion;
    static World world = Bukkit.getWorld("World");
    private static Map<String, Short> worldScript = new HashMap();
    private static Map<Short, String> worldDescriptor = new HashMap();
    private static RegionManager regions =  WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    private static final int regionSizeY = 64;
    private int regionId;
    private int regionStartX;
    private int regionStartZ;
    private int regionEndX;
    private int regionEndZ;
    private int regionSizeZ;
    private int regionSizeX;
    private void setRegionEndX() {
        this.regionEndX = Math.abs(this.regionStartX + this.regionSizeX) - 1;
    }
    private void setRegionEndZ() {
        this.regionEndZ = Math.abs(this.regionStartZ + this.regionSizeZ) - 1;
    }
    public Section(int regionId) {
        try {
            this.regionId = regionId;
            Connection connection = null;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.s3db");
            Statement statmnt = connection.createStatement();
            ResultSet result = statmnt.executeQuery("SELECT * FROM sectionRegions WHERE id=" + this.regionId);
            this.regionStartX = result.getInt("posX");
            this.regionStartZ = result.getInt("posZ");
            this.regionSizeX = result.getInt("sizeX");
            this.regionSizeZ = result.getInt("sizeZ");
            connection.close();
            statmnt.close();
            result.close();
        } catch (ClassNotFoundException var5) {
        } catch (SQLException var6) {
        }

        this.setRegionEndX();
        this.setRegionEndZ();
    }
    public static void downloadScriptData() {
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.s3db");
            Statement statmnt = connection.createStatement();
            ResultSet result = statmnt.executeQuery("SELECT * FROM descriptor");

            while(result.next()) {
                short id = (short) result.getInt("id");
                String blockData = result.getString("blockData");
                worldScript.put(blockData, id);
                worldDescriptor.put(id, blockData);
            }

            connection.close();
            statmnt.close();
            result.close();
        } catch (ClassNotFoundException var5) {
        } catch (SQLException var6) {
        }

    }
    public static void uploadScriptData() {
        Connection connection = null;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db.s3db");
            Statement statmnt = connection.createStatement();
            int start = statmnt.executeQuery("SELECT id FROM descriptor WHERE id=(SELECT MAX(id) FROM descriptor)").getInt("id") + 1;

            for(int i = start; i < worldScript.size(); ++i) {
                String var10001 = (String) worldDescriptor.get(i);
                statmnt.execute("INSERT INTO 'descriptor' ('blockData', 'id') VALUES ('" + var10001 + "', " + worldScript.get(worldDescriptor.get(i)) + ")");
            }

            connection.close();
            statmnt.close();
        } catch (ClassNotFoundException var4) {
        } catch (SQLException var5) {
        }

    }
    public void setRegion(short [] codingWorld) {
        if(codingWorld.length == regionSizeX*regionSizeY*regionSizeZ) {
            for (int y = 0; y < 64; ++y) {
                for (int x = 0; x < this.regionSizeX; ++x) {
                    for (int z = 0; z < this.regionSizeZ; ++z) {
                        int id = y * this.regionSizeZ * this.regionSizeX + x * this.regionSizeZ + z;
                        String s = (String) worldDescriptor.get(codingWorld[id]);
                        world.setBlockData(this.regionStartX + x, -60 + y, this.regionStartZ + z, Bukkit.createBlockData(s.substring(s.indexOf(":") + 1, s.indexOf("}"))));
                    }
                }
            }
        } else Bukkit.getLogger().info("Error. Region sizes do not match.");
    }
    public short[] getRegion() {
        short[] codingWorld = new short[this.regionSizeX * 64 * this.regionSizeZ];

        for(int y = 0; y < 64; ++y) {
            for(int x = 0; x < this.regionSizeX; ++x) {
                for(int z = 0; z < this.regionSizeZ; ++z) {
                    int id = y * this.regionSizeZ * this.regionSizeX + x * this.regionSizeZ + z;
                    String block = world.getBlockAt(this.regionStartX + x, -60 + y, this.regionStartZ + z).getBlockData().toString();
                    if (!worldScript.containsKey(block)) {
                        worldScript.put(block, (short) worldScript.size());
                        worldDescriptor.put((short) (worldScript.size() - 1), block);
                    }
                    codingWorld[id] = (short) worldScript.get(block);
                }
            }
        }

        uploadScriptData();
        testRegion = codingWorld;
        return codingWorld;
    }
    public static void initRegions(int sectionId) {
        int sectionIdX = sectionId / 4;
        int sectionIdZ = sectionId % 4;
        int sectionStartX = 200 * sectionIdX;
        int sectionStartZ = 200 * sectionIdZ;

        int i;
        int z;
        int x;
        for(i = -61; i < -57; ++i) {
            for(z = 0; z < 200; ++z) {
                for(x = 0; x < 200; ++x) {
                    world.setBlockData(z + sectionStartX, i, x + sectionStartZ, Bukkit.createBlockData("stone"));
                }
            }
        }

        for(i = 0; i < 200; ++i) {
            for(z = 0; z < 200; ++z) {
                world.setBlockData(i + sectionStartX, -57, z + sectionStartZ, Bukkit.createBlockData("dirt"));
            }
        }

        for(i = 0; i < 200; ++i) {
            for(z = 0; z < 200; ++z) {
                world.setBlockData(i + sectionStartX, -56, z + sectionStartZ, Bukkit.createBlockData("stone_bricks"));
            }
        }

        for(i=0; i <= 48; ++i) {
            Section sec = new Section(i + 49 * sectionId);
            for(x = 0; x < sec.regionSizeX; ++x) {
                for(z = 0; z < sec.regionSizeZ; ++z) {
                    world.setBlockData(sec.regionStartX + x, -56, sec.regionStartZ + z, Bukkit.createBlockData("grass_block[snowy=false]"));
                }
            }
        }

        Bukkit.getLogger().info("Complete");
    }
    public static void removeRegions(int sectionId) {
        int sectionIdX = sectionId / 4;
        int sectionIdZ = sectionId % 4;
        int sectionStartX = 200 * sectionIdX;
        int sectionStartZ = 200 * sectionIdZ;

        int i;
        int z;
        int x;
        for(i = -60; i < -57; i++) {
            for(z = 0; z < 200; z++) {
                for(x = 0; x < 200; x++) {
                    world.setBlockData(z + sectionStartX, i, x + sectionStartZ, Bukkit.createBlockData("air"));
                }
            }
        }

        for(i = 0; i < 200; i++) {
            for(z = 0; z < 200; z++) {
                world.setBlockData(i + sectionStartX, -57, z + sectionStartZ, Bukkit.createBlockData("air"));
            }
        }

        for(i = 0; i < 200; i++) {
            for(z = 0; z < 200; z++) {
                world.setBlockData(i + sectionStartX, -56, z + sectionStartZ, Bukkit.createBlockData("air"));
                world.setBlockData(i + sectionStartX, -55, z + sectionStartZ, Bukkit.createBlockData("air"));
            }
        }
        Bukkit.getLogger().info("Complete");
    }
    public void setRegionAccess(Player player) {
        DefaultDomain domain = new DefaultDomain();
        domain.addPlayer(player.getName());
        BlockVector3 start = BlockVector3.at(this.regionStartX, -60, this.regionStartZ);
        BlockVector3 end = BlockVector3.at(this.regionEndX, 4, this.regionEndZ);
        ProtectedRegion region = new ProtectedCuboidRegion("" + this.regionId, start, end);
        region.setOwners(domain);
        region.setPriority(2);
        regions.addRegion(region);
    }
    public static void removeRegionAccess(Player player) {
        Map<String, ProtectedRegion> regionMap =  regions.getRegions();
        for(ProtectedRegion region : regionMap.values()) {
            if (region.isOwner(player.getName())) {
                regions.removeRegion(region.getId());
            }
        }
    }
    public static void setMapAccess() {
        ProtectedRegion region = new GlobalProtectedRegion("__global__");
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);
        regions.addRegion(region);
    }
    public static int getPositionPlayer(Player player) {
        Map<String, ProtectedRegion> regionMap = regions.getRegions();
        for(ProtectedRegion region : regionMap.values()) {
            if (region.isOwner(player.getName()) && region.contains(player.getBlockIn().getBlockX(), player.getBlockIn().getBlockY(), player.getBlockIn().getBlockZ())) {
                return Integer.parseInt(region.getId());
            }
        }

        return Integer.parseInt("");
    }
    public static String login(org.bukkit.entity.Player player) {
        return player.getUniqueId().toString();
    }
}
