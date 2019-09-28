package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.instance.StructureInstance;
import net.andrasia.kiryu144.andrasiaautomation.util.FixedSize3DArray;
import net.andrasia.kiryu144.andrasiaautomation.util.LocationMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    public void addStructureInstance(Location location, Structure structure, Player placer){
        // A structure was built
        StructureInstance instance = null;
        try {
            instance = structure.getStructureInstanceClass().getConstructor(Location.class, Structure.class, UUID.class).newInstance(location, structure, placer.getUniqueId());
            instance.onInit(structure.getStructureInstanceInitData());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            placer.sendMessage("§cUnable to initialize instance! Please report to an Administrator!");
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
            placer.sendMessage("§cUnable to save to config! Please report to an Administrator!");
            ex.printStackTrace();
        }
    }

    public void addStructureInstance(StructureInstance instance){
        Structure structure = instance.getStructure();
        instance.onInit(structure.getStructureInstanceInitData());
        structureInstanceLocationMap.put(instance.getLocation(), instance);

        for(FixedSize3DArray<StructureBlock>.Iterator it = structure.getBlocks().iterator(); it.hasNext(); ){
            StructureBlock structureBlock = it.next();
            Location loc = instance.getLocation().clone().add(it.toVector()).subtract(structure.getCenterOffset());
            structureInstanceBlockMap.put(loc, instance);
        }

        structureInstances.add(instance);
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

    public void loadAll(){
        getInstanceFolder().mkdirs();
        for(File file : getInstanceFolder().listFiles()){
            if(file.isFile() && file.getName().endsWith(".yml")){
                try {
                    StructureInstance instance = load(file);
                    addStructureInstance(instance);
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
            Structure structureInItem = AndrasiaAutomation.items.getStructureFromItem(e.getItemInHand());
            if(structure != null && structureInItem != null){
                if(structureInItem.getId().equalsIgnoreCase(structure.getId())){
                    addStructureInstance(e.getBlock().getLocation(), structure, e.getPlayer());
                    e.getPlayer().sendMessage(String.format("§2Successfully created §6%s", structure.getName()));
                }else{
                    e.getPlayer().sendMessage("§cInvalid machine block!");
                    e.setCancelled(true);
                }
            }else if(structureInItem != null){
                e.setCancelled(true); //< Cancel to prevent placement
                e.getPlayer().sendMessage("§cYou cannot place this block anywhere except in a structure!");
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
            instance.getLocation().getBlock().setType(Material.AIR);

            boolean success = getFileForStructureInstance(instance).delete();
            if(success){
                if(e.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
                    e.setDropItems(false);
                }

                e.getPlayer().getWorld().dropItem(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), AndrasiaAutomation.items.getItemForStructure(structure));
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
