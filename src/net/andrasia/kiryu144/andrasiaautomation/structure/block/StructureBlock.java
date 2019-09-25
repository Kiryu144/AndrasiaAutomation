package net.andrasia.kiryu144.andrasiaautomation.structure.block;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface StructureBlock extends ConfigurationSerializable {
    boolean matches(Material material);
    List<Material> getMaterials();

    default Map<String, Object> serialize() {
        ArrayList<String> materialList = new ArrayList<>();
        for(Material material : getMaterials()){
            materialList.add(material.toString());
        }
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("materials", materialList);
        return serialized;
    }
}
