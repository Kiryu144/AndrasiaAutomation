package net.andrasia.kiryu144.andrasiaautomation.structure;

import com.sk89q.worldedit.regions.Region;
import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StructureParser {

    public static Structure FromRegion(Player player, Region region){
        Structure structure = new Structure(new Vector(region.getWidth(), region.getHeight(), region.getLength()), null, null);
        for(int x = region.getMinimumPoint().getX(); x <= region.getMaximumPoint().getX(); ++x){
            for(int y = region.getMinimumPoint().getY(); y <= region.getMaximumPoint().getY(); ++y){
                for(int z = region.getMinimumPoint().getZ(); z <= region.getMaximumPoint().getZ(); ++z){
                    Location location = new Location(Bukkit.getWorld(region.getWorld().getName()), x, y, z);
                    if(!(location.getBlock().getType().equals(Material.AIR) || location.getBlock().getType().equals(Material.CAVE_AIR) || location.getBlock().getType().equals(Material.VOID_AIR))){
                        Vector offset = new Vector(x - region.getMinimumPoint().getBlockX(), y - region.getMinimumPoint().getBlockY(), z - region.getMinimumPoint().getBlockZ());
                        if(location.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
                            structure.setCenterOffset(offset);
                        }
                        structure.set(offset, new SingleStructureBlock(location.getBlock().getType()));
                    }
                }
            }
        }
        return structure;
    }

    public static void Paste(Structure structure, Location location){
        for(int x = 0; x < structure.getSize().getBlockX(); ++x) {
            for (int y = 0; y < structure.getSize().getBlockY(); ++y) {
                for (int z = 0; z < structure.getSize().getBlockZ(); ++z) {
                    Location loc = location.clone().add(x, y, z).subtract(structure.getCenterOffset());
                    StructureBlock structureBlock = structure.get(new Vector(x, y, z));
                    if(structureBlock != null && structureBlock.getMaterials().size() > 0){
                        loc.getBlock().setType(structureBlock.getMaterials().get(0));
                    }
                }
            }
        }
    }

    public static void SaveToConfig(Structure structure, File file) throws IOException {
        HashMap<String, List<String>> data = new HashMap<>();
        for(int x = 0; x < structure.getSize().getBlockX(); ++x){
            for(int y = 0; y < structure.getSize().getBlockY(); ++y){
                for(int z = 0; z < structure.getSize().getBlockZ(); ++z){
                    String formattedVector = SerializeVector(new Vector(x, y, z));
                    if(structure.get(new Vector(x, y, z)) != null){
                        List<String> matList = new ArrayList<>();
                        for(Material material : structure.get(new Vector(x, y, z)).getMaterials()){
                            matList.add(material.toString());
                        }
                        data.put(formattedVector, matList);
                    }
                }
            }
        }

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("name", "unnamed");
        yamlConfiguration.set("type", "undefined");
        yamlConfiguration.set("offset", SerializeVector(structure.getCenterOffset()));
        yamlConfiguration.set("data", data);
        yamlConfiguration.save(file);
    }

    public static String SerializeVector(Vector vector){
        return String.format("%d;%d;%d", vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static Vector DeserializeVector(String line){
        String[] args = line.split(";");
        return new Vector(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
    }

    public static Structure LoadFromConfig(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);

        Vector size = new Vector();
        HashMap<Vector, StructureBlock> data = new HashMap<>();
        for(String key : yamlConfiguration.getConfigurationSection("data").getKeys(false)){
            Vector       offset             = DeserializeVector(key);
            List<String> materialStringList = yamlConfiguration.getStringList("data." + key);
            size.setX(Math.max(size.getX(), offset.getBlockX()+1));
            size.setY(Math.max(size.getY(), offset.getBlockY()+1));
            size.setZ(Math.max(size.getZ(), offset.getBlockZ()+1));
            if(materialStringList.size() == 1){
                data.put(offset, new SingleStructureBlock(Material.valueOf(materialStringList.get(0))));
            }else if(materialStringList.size() > 1){
                MultiStructureBlock structureBlock = new MultiStructureBlock();
                for(String matString : materialStringList){
                    structureBlock.addMaterial(Material.valueOf(matString));
                }
                data.put(offset, structureBlock);
            }
        }

        Structure structure = new Structure(size, DeserializeVector(yamlConfiguration.getString("offset", "0;0;0")), yamlConfiguration.getString("name"));
        for(Vector vec : data.keySet()){
            structure.set(vec, data.get(vec));
        }

        return structure;
    }


}
