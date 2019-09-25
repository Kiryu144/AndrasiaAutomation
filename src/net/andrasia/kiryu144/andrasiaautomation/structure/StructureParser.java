package net.andrasia.kiryu144.andrasiaautomation.structure;

import com.sk89q.worldedit.regions.Region;
import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.SingleStructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.util.FixedSize3DArray;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;

public class StructureParser {

    public static Structure FromRegion(Player player, Region region){
        Structure structure = new Structure("undefined", new Vector(region.getWidth(), region.getHeight(), region.getLength()), null, null);
        for(int x = region.getMinimumPoint().getX(); x <= region.getMaximumPoint().getX(); ++x){
            for(int y = region.getMinimumPoint().getY(); y <= region.getMaximumPoint().getY(); ++y){
                for(int z = region.getMinimumPoint().getZ(); z <= region.getMaximumPoint().getZ(); ++z){
                    Location location = new Location(Bukkit.getWorld(region.getWorld().getName()), x, y, z);
                    if(!(location.getBlock().getType().equals(Material.AIR) || location.getBlock().getType().equals(Material.CAVE_AIR) || location.getBlock().getType().equals(Material.VOID_AIR))){
                        Vector offset = new Vector(x - region.getMinimumPoint().getBlockX(), y - region.getMinimumPoint().getBlockY(), z - region.getMinimumPoint().getBlockZ());
                        if(location.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
                            structure.setCenterOffset(offset);
                        }
                        structure.getBlocks().set(offset, new SingleStructureBlock(location.getBlock().getType()));
                    }
                }
            }
        }
        return structure;
    }

    public static void Paste(Structure structure, Location location){
        Validate.notNull(structure, "Cannot paste null");
        Validate.notNull(structure.getBlocks(), "Structure does not contain blocks");
        for(FixedSize3DArray<StructureBlock>.Iterator it = structure.blocks.iterator(); it.hasNext(); ){
            StructureBlock structureBlock = it.next();
            if(structureBlock != null) {
                Vector   localPosition = it.toVector();
                Location worldLocation = location.clone().add(localPosition).subtract(structure.getCenterOffset());
                worldLocation.getBlock().setType(structureBlock.getMaterials().get(0));
            }
        }
    }

    public static void SaveToConfig(Structure structure, File file) throws IOException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("structure", structure);
        yamlConfiguration.save(file);
    }

    public static Structure LoadFromConfig(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);
        return (Structure) yamlConfiguration.get("structure");
    }
}
