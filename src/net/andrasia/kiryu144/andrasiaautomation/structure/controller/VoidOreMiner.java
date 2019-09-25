package net.andrasia.kiryu144.andrasiaautomation.structure.controller;

import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import net.andrasia.kiryu144.andrasiaautomation.util.WeightedRandomList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class VoidOreMiner extends StructureControllerStoraged {
    protected WeightedRandomList<ItemStack> drops;
    protected int delay;
    protected int currentTick = 0;

    public VoidOreMiner(Structure structure, Location location) {
        super(structure, location);
        drops = new WeightedRandomList<>();
    }

    @Override
    public void loadData(Map<String, Object> data) {
        super.loadData(data);
        delay = (int) data.get("delay");
        for(String drop : (List<String>) data.get("drops")){
            Material material = Material.valueOf(drop.split(":")[0]);
            int chance = Integer.parseInt(drop.split(":")[1]);
            drops.add(new ItemStack(material), chance);
        }
    }

    @Override
    public void tick() {
        currentTick += 1;
        if(currentTick >= delay){
            currentTick = 0;
            addItemToInventory(drops.getRandom().clone());
        }
    }
}
