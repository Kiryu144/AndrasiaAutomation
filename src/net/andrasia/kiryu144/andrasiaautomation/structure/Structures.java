package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.instance.StructureInstance;
import net.andrasia.kiryu144.andrasiaautomation.util.FixedSize3DArray;
import net.andrasia.kiryu144.andrasiaautomation.util.LocationMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Structures implements Listener {
    protected HashMap<String, Structure> structureID;
    protected LocationMap<StructureInstance> structureInstanceLocationMap;
    protected LocationMap<StructureInstance> structureInstanceBlockMap;
    protected Set<StructureInstance> structureInstances;

    public Structures(){
        structureID = new HashMap<>();
        structureInstanceLocationMap = new LocationMap<>();
        structureInstanceBlockMap = new LocationMap<>();
        structureInstances = new HashSet<>();
    }

    public void addStructure(Structure structure){
        structureID.put(structure.getId(), structure);
    }

    public Structure getStructureAtBlock(Block block){
        for(Structure structure : structureID.values()){
            Location location = block.getLocation().clone().subtract(structure.getCenterOffset());
            if(structure.matches(location)){
                return structure;
            }
        }
        return null;
    }

    public Structure getStructure(String id){
        return structureID.get(id);
    }

    public void addStructureInstance(Location location, Structure structure, CommandSender sender){
        // A structure was built
        StructureInstance instance = null;
        try {
            instance = structure.getStructureInstanceClass().getConstructor(Location.class, Structure.class).newInstance(location, structure);
            instance.onInit(structure.getStructureInstanceInitData());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            sender.sendMessage("§cUnable to initialize instance! Please report to an Administrator!");
            e.printStackTrace();
            return;
        }

        structureInstanceLocationMap.put(instance.getLocation(), instance);

        for(FixedSize3DArray<StructureBlock>.Iterator it = structure.getBlocks().iterator(); it.hasNext(); ){
            StructureBlock structureBlock = it.next();
            Location loc = location.clone().add(it.toVector()).subtract(structure.getCenterOffset());
            structureInstanceBlockMap.put(loc, instance);
        }

        structureInstances.add(instance);

        try {
            save(instance);
        } catch (IOException ex) {
            sender.sendMessage("§cUnable to save to config! Please report to an Administrator!");
            ex.printStackTrace();
        }
    }

    protected File getInstanceFolder() {
        return new File(AndrasiaAutomation.instance.getDataFolder() + "/instances/");
    }

    protected File getFileForStructureInstance(StructureInstance instance){
        Location location = instance.getLocation();
        String filename = String.format("%s;%d;%d;%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return new File(AndrasiaAutomation.instance.getDataFolder() + "/instances/" + filename + ".yml");
    }

    protected void save(StructureInstance instance) throws IOException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("instance", instance);
        yamlConfiguration.save(getFileForStructureInstance(instance));
    }

    protected StructureInstance load(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);
        return (StructureInstance) yamlConfiguration.get("instance");
    }

    public void loadAllInstances(){
        getInstanceFolder().mkdirs();
        for(File file : getInstanceFolder().listFiles()){
            if(file.isFile() && file.getName().endsWith(".yml")){
                try {
                    StructureInstance instance = load(file);
                    addStructureInstance(instance.getLocation(), instance.getStructure(), Bukkit.getConsoleSender());
                } catch (IOException e) {
                    AndrasiaAutomation.instance.getLogger().severe(String.format("Unable to load '%s'", file.getName()));
                    e.printStackTrace();
                } catch (InvalidConfigurationException e) {
                    AndrasiaAutomation.instance.getLogger().severe(String.format("Unable to load '%s' due to invalid file format.", file.getName()));
                }
            }
        }
    }

    public void tickAll() {
        for(StructureInstance instance : structureInstances){
            if(instance.getLocation().getChunk().isLoaded()){
                instance.tick();
            }
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
            Structure structure = getStructureAtBlock(e.getBlock());
            if(structure != null){
                addStructureInstance(e.getBlock().getLocation(), structure, e.getPlayer());
                e.getPlayer().sendMessage(String.format("§2Successfully created §6%s", structure.getName()));
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e){
        long start = System.currentTimeMillis();
        StructureInstance instance = structureInstanceBlockMap.remove(e.getBlock().getLocation());
        if(instance != null){
            // Structure was broken
            instance.onDestroy();

            Structure structure = instance.getStructure();
            for(FixedSize3DArray<StructureBlock>.Iterator it = structure.getBlocks().iterator(); it.hasNext(); ) {
                StructureBlock structureBlock = it.next();
                Location location = instance.getLocation().clone().add(it.toVector()).subtract(structure.getCenterOffset());
                structureInstanceBlockMap.remove(location); //TODO: Evaluate to set to zero?
            }
            structureInstanceLocationMap.remove(instance.getLocation());
            structureInstances.remove(instance);


            boolean success = getFileForStructureInstance(instance).delete();
            if(success){
                e.getPlayer().sendMessage(String.format("§6%s §cis now broken.", instance.getStructure().getName()));
            }else{
                e.getPlayer().sendMessage("§cUnable to delete config! Please report to an Administrator!");
            }

            if(AndrasiaAutomation.DEBUG){
                AndrasiaAutomation.instance.getLogger().info(String.format("It took %dms to break structure.", System.currentTimeMillis() - start));
            }
        }
    }
}
