package net.andrasia.kiryu144.andrasiaautomation.structure.controller;

import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VoidOreMiner extends StructureControllerStoraged {
    protected List<ItemStack> drops;
    protected int delay;
    protected int currentTick = 0;

    public VoidOreMiner(Structure structure, Location location) {
        super(structure, location);
        drops = new ArrayList<>();
    }

    @Override
    public void loadData(ConfigurationSection section) {
        super.loadData(section);
        delay = section.getInt("delay", 10000000);
        for(String drop : section.getStringList("drops")){
            drops.add(new ItemStack(Material.valueOf(drop)));
        }
    }

    @Override
    public void tick() {
        currentTick += 1;
        if(currentTick >= delay){
            currentTick = 0;
            addItemToInventory(drops.get((int) Math.floor((Math.random() - 0.000001) * drops.size())).clone());
        }
    }
}
