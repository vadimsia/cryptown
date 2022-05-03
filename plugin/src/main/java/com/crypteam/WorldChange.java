package com.crypteam;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorldChange {
    static int[] testRegion;
    static World world = Bukkit.getWorld("World");                                            // Мир
    private static Map<String, Integer> worldScript = new HashMap<String, Integer>();               // Колекция шифрования
    private static Map<Integer, String> worldDescriptor = new HashMap<Integer, String>();           // Коллекция дешифрования
    private static int countRegionsX = 2;                                                           // Количество регионов по X
    private static int countRegionsZ = 2;                                                           // Количество регионов по Z
    private static int regionSizeX = 4;                                                             // Размер региона X
    private static int regionSizeY = 4;                                                             // Размер региона Y
    private static int regionInitY = -60;                                                           // Начальная координата Y
    private static int regionSizeZ = 4;                                                             // Размер региона Z
    private static int regionBorder = 2;                                                            // Размер дорожек вокруг региона
    static public void downloadScriptData() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("descriptor.json"));
            JSONArray jsonArray = (JSONArray) obj;
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject k  = (JSONObject) jsonArray.get(i);
                worldScript.put(k.get("name").toString(), Integer.parseInt(k.get("id").toString()));
                worldDescriptor.put(Integer.parseInt(k.get("id").toString()), k.get("name").toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }                                                   // Загрузка коллекций на сервер
    static public void uploadScriptData() {
        JSONArray bufferArray = new JSONArray();
        for(int i = 0; i < worldScript.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("name", worldDescriptor.get(i));
            obj.put("id", worldScript.get(worldDescriptor.get(i)));
            bufferArray.add(obj);
        }
        try {
            Files.write(Paths.get("descriptor.json"), bufferArray.toJSONString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }                                                     // Выгрузка коллекций в JSON
    static void setRegion(int regionId, int[] codingWorld) {
        int[] initCoordinates = getRegionInitialCoordinates(regionId);
        for (int y = 0; y < regionSizeY; y++) {
            for(int x = 0; x < regionSizeX; x++) {
                for (int z = 0; z < regionSizeZ; z++) {
                    int id = y*regionSizeZ*regionSizeX+x*regionSizeZ+z;
                    String s = worldDescriptor.get(codingWorld[id]);
                    world.setBlockData(initCoordinates[0] + x,regionInitY + y,initCoordinates[1] + z, Bukkit.createBlockData(s.substring(s.indexOf(":") + 1, s.indexOf("}"))));
                }
            }
        }
    }

    static private int[] getRegionInitialCoordinates(int regionId) {                                      // id начинаются с 0
        int[] coordinates = new int[2];                                                            // [0] - Начальный X, [1] - Начальный Z
        int regionSizeWithBorderX = regionSizeX + 2 * regionBorder;                                 // 8
        int regionSizeWithBorderZ = regionSizeZ + 2 * regionBorder;                                 // 8
        int worldRegionCoordinatesX = regionId/countRegionsZ;
        int worldRegionCoordinatesZ = regionId % countRegionsZ;
        coordinates[0] = worldRegionCoordinatesX * regionSizeWithBorderX + regionBorder;
        coordinates[1] = worldRegionCoordinatesZ * regionSizeWithBorderZ + regionBorder;
        return  coordinates;
    }
    static int[] getRegion(int regionId) {
        int[] codingWorld = new int[regionSizeX*regionSizeY*regionSizeZ];
        int[] initCoordinates = getRegionInitialCoordinates(regionId);
        for (int y = 0; y < regionSizeY; y++) {
            for(int x = 0; x < regionSizeX; x++) {
                for (int z = 0; z < regionSizeZ; z++) {
                    int id = y*regionSizeZ*regionSizeX+x*regionSizeZ+z;
                    String block = world.getBlockAt( initCoordinates[0]+x,regionInitY + y,initCoordinates[1] + z).getBlockData().toString();
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
}
