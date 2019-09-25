package net.andrasia.kiryu144.andrasiaautomation.structure.block;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class SingleStructureBlock implements StructureBlock, ConfigurationSerializable {
    protected Material material;

    public SingleStructureBlock(Material material) {
        this.material = material;
    }

    public SingleStructureBlock(Map<String, Object> serialization) {
        ArrayList<String> materialStrings = (ArrayList<String>) serialization.get("materials");
        this.material = Material.valueOf(materialStrings.get(0));
    }

    @Override
    public boolean matches(Material material) {
        return material.equals(this.material);
    }

    @Override
    public List<Material> getMaterials() {
        List<Material> materials = new ArrayList<>();
        materials.add(material);
        return materials;
    }
}
