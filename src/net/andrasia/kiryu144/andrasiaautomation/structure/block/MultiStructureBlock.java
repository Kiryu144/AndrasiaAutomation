package net.andrasia.kiryu144.andrasiaautomation.structure.block;

import org.bukkit.Material;

import java.util.*;

public class MultiStructureBlock implements StructureBlock {
    protected Set<Material> materials;

    public MultiStructureBlock() {
        materials = new HashSet<>();
    }

    public MultiStructureBlock(HashMap<String, Object> serialization) {
        materials = new HashSet<>();
        for(String matString : (List<String>) serialization.get("materials")){
            materials.add(Material.valueOf(matString));
        }
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
