package net.andrasia.kiryu144.andrasiaautomation.structure;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiStructureBlock implements StructureBlock{
    protected Set<Material> materials;

    public MultiStructureBlock() {
        materials = new HashSet<>();
    }

    public void addMaterial(Material material){
        materials.add(material);
    }

    @Override
    public boolean matches(Material material) {
        return materials.contains(material);
    }

    @Override
    public List<Material> getMaterials() {
        return new ArrayList<>(this.materials);
    }
}
