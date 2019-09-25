package net.andrasia.kiryu144.andrasiaautomation.structure.controller;

import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class StructureController implements ConfigurationSerializable {
    protected Structure structure;
    protected Location location;

    public StructureController(Structure structure, Location location) {
        this.structure = structure;
        this.location = location;
    }

    public void loadData(Map<String, Object> data){

    }

    public boolean areRequirementsMet() {
        return true;
    }

    public void tick() {

    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
