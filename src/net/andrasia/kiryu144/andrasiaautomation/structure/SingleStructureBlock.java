package net.andrasia.kiryu144.andrasiaautomation.structure;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SingleStructureBlock implements StructureBlock {
    protected Material material;

    public SingleStructureBlock(Material material) {
        this.material = material;
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
