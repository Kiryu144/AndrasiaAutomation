package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.instance.StructureInstance;
import net.andrasia.kiryu144.andrasiaautomation.util.FixedSize3DArray;
import net.andrasia.kiryu144.andrasiaautomation.util.LocationMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;

public class Structures implements Listener {
    protected HashMap<String, Structure> structureID;
    protected LocationMap<StructureInstance> structureInstanceLocationMap;
    protected LocationMap<StructureInstance> structureInstanceBlockMap;

    public Structures(){
        structureID = new HashMap<>();
        structureInstanceLocationMap = new LocationMap<>();
        structureInstanceBlockMap = new LocationMap<>();
    }

    public void add(Structure structure){
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

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e){
        long start = System.currentTimeMillis();
        if(e.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
            Structure structure = getStructureAtBlock(e.getBlock());
            if(structure != null){
                // A structure was built
                StructureInstance instance = new StructureInstance(structure, e.getBlock().getLocation());
                structureInstanceLocationMap.put(instance.getLocation(), instance);

                for(FixedSize3DArray<StructureBlock>.Iterator it = structure.getBlocks().iterator(); it.hasNext(); ){
                    StructureBlock structureBlock = it.next();
                    Location location = e.getBlock().getLocation().clone().add(it.toVector()).subtract(structure.getCenterOffset());
                    structureInstanceBlockMap.put(location, instance);
                }

                e.getPlayer().sendMessage(String.format("§2Successfully created §6%s", structure.getName()));

                if(AndrasiaAutomation.DEBUG){
                    AndrasiaAutomation.instance.getLogger().info(String.format("It took %dms to figure out that there was a structure placed.", System.currentTimeMillis() - start));
                }
            }else if(AndrasiaAutomation.DEBUG){
                AndrasiaAutomation.instance.getLogger().info(String.format("It took %dms to figure out that there was no structure placed.", System.currentTimeMillis() - start));
            }
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e){
        long start = System.currentTimeMillis();
        StructureInstance instance = structureInstanceBlockMap.remove(e.getBlock().getLocation());
        if(instance != null){
            // Structure was broken
            Structure structure = instance.getStructure();
            for(FixedSize3DArray<StructureBlock>.Iterator it = structure.getBlocks().iterator(); it.hasNext(); ) {
                StructureBlock structureBlock = it.next();
                Location location = instance.getLocation().clone().add(it.toVector()).subtract(structure.getCenterOffset());
                structureInstanceBlockMap.remove(location); //TODO: Evaluate to set to zero?
            }
            structureInstanceLocationMap.remove(instance.getLocation());

            e.getPlayer().sendMessage(String.format("§6%s §cis now broken.", instance.getStructure().getName()));

            if(AndrasiaAutomation.DEBUG){
                AndrasiaAutomation.instance.getLogger().info(String.format("It took %dms to break structure.", System.currentTimeMillis() - start));
            }
        }
    }
}
