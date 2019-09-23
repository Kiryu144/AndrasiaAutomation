package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class StructureRegistry implements Listener {
    protected ArrayList<Structure> structures;

    public StructureRegistry(){
        structures = new ArrayList<>();
    }

    public void clear() {
        structures.clear();
    }

    public void add(Structure structure){
        structures.add(structure);
    }

    public void checkForStructure(Block block, Player placer){
        for(Structure structure : structures){
            Location location = block.getLocation().clone().subtract(structure.getCenterOffset());
            if(structure.matches(location)){
                if(placer != null){
                    placer.sendMessage("§aCreated §6" + structure.getName() + "§a.");
                }
                return;
            }
        }
    }

    @EventHandler
    protected void onBlockPlace(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
            checkForStructure(e.getBlock(), e.getPlayer());
        }
    }


}
