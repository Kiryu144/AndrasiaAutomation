package net.andrasia.kiryu144.andrasiaautomation.structure.instance;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.lib.Laser;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import net.andrasia.kiryu144.andrasiaautomation.util.WeightedRandomList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StructureVoidMinerInstance extends StructureInstance{
    // Serialized in structure
    protected int delayInTicks;
    protected WeightedRandomList<Material> drops;

    // Not serialized
    protected Laser laser;

    public StructureVoidMinerInstance(Location location, Structure structure, UUID player) {
        super(location, structure, player);
    }

    public StructureVoidMinerInstance(Map<String, Object> savedInstanceData) {
        super(savedInstanceData);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void onInit(Map<String, Object> structureSpecific) {
        super.onInit(structureSpecific);
        drops = new WeightedRandomList<>();

        delayInTicks = (int) structureSpecific.get("delay_in_ticks");
        sleepForTicks(delayInTicks);
        for(String mat : (List<String>) structureSpecific.get("drops")){
            String[] args = mat.split(":");
            drops.add(Material.valueOf(args[0]), Integer.parseInt(args[1]));
        }

        try {
            Location start = location.clone().add(0.49, 0, 0.49);
            Location end = location.clone().add(0.51, -start.getBlockY(), 0.51);
            laser = new Laser(start, end, -1, 64);
            laser.start(AndrasiaAutomation.instance);
        } catch (ReflectiveOperationException ignored) {}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(laser != null && laser.isStarted()) {
            laser.stop();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!isSleeping()){
            sleepForTicks(delayInTicks);
            ItemStack drop = new ItemStack(drops.getRandom());

            Block block = getLocation().clone().add(0, 1, 0).getBlock();
            if(block.getType().equals(Material.CHEST)){
                Chest chest = (Chest) block.getState();
                chest.getInventory().addItem(drop);
            }else{
                getLocation().getWorld().dropItem(getLocation().clone().add(0.5, 2, 0.5), drop).setVelocity(new Vector(0, 0, 0));
            }
        }
    }

}
