package net.andrasia.kiryu144.andrasiaautomation.structure.instance;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.external.Laser;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import net.andrasia.kiryu144.andrasiaautomation.util.WeightedRandomList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class StructureVoidMinerInstance extends StructureInstance{
    protected int delayInTicks;
    protected WeightedRandomList<Material> drops;

    protected int ticksLeft = 0;
    protected Laser laser;

    public StructureVoidMinerInstance(Location location, Structure structure) {
        super(location, structure);
    }

    public StructureVoidMinerInstance(Map<String, Object> data) {
        super(data);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void onInit(Map<String, Object> data) {
        super.onInit(data);
        drops = new WeightedRandomList<>();

        delayInTicks = (int) data.get("delay_in_ticks");
        ticksLeft = delayInTicks;
        for(String mat : (List<String>) data.get("drops")){
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
        if(--ticksLeft <= 0){
            ticksLeft = delayInTicks;

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
