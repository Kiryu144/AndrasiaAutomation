package net.andrasia.kiryu144.andrasiaautomation.structure;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class Structures implements Listener {
    protected HashMap<String, Structure> structures;

    public Structures(){
        structures = new HashMap<>();
    }

    public void clear() {
        structures.clear();
    }

    public void add(Structure structure){
        structures.put(structure.getId(), structure);
    }

    public Structure checkForStructure(Block block){
        for(Structure structure : structures.values()){
            Location location = block.getLocation().clone().subtract(structure.getCenterOffset());
            if(structure.matches(location)){
                return structure;
            }
        }
        return null;
    }

    public Structure getStructure(String id){
        return structures.get(id);
    }
}
