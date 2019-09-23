package net.andrasia.kiryu144.andrasiaautomation.structure.controller;

import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class StructureController {
    protected Structure structure;
    protected Location location;

    public StructureController(Structure structure, Location location) {
        this.structure = structure;
        this.location = location;
    }

    public void loadData(ConfigurationSection section){

    }

    public boolean areRequirementsMet() {
        return true;
    }

    public void tick() {

    }
}
