package net.andrasia.kiryu144.andrasiaautomation.structure.instance;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class StructureInstance implements ConfigurationSerializable {
    protected Structure structure; // The structure this instance is bound to
    protected Location location;   // The location the structure instance is placed at (PRIMARY_BLOCK)

    public StructureInstance(Structure structure, Location location) {
        this.structure = structure;
        this.location = location;
    }

    public StructureInstance(Map<String, Object> data) {
        this.structure = AndrasiaAutomation.structures.getStructure((String) data.get("structure"));
        this.location = (Location) data.get("location");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("structure", structure.getId());
        data.put("location", location);
        return data;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
