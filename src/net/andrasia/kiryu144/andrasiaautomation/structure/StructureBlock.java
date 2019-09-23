package net.andrasia.kiryu144.andrasiaautomation.structure;

import org.bukkit.Material;

import java.util.List;

public interface StructureBlock {
    boolean matches(Material material);
    List<Material> getMaterials();
}
