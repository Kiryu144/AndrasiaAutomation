package net.andrasia.kiryu144.andrasiaautomation.structure.instance;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.energy.EnergyNetwork;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class StructureGeneratorInstance extends StructureInstance {
    // Serialized in structure
    protected double energyPerTick;
    protected double efficiency;

    // Serialized in instance
    protected double buffer;

    public StructureGeneratorInstance(Location location, Structure structure, UUID player) {
        super(location, structure, player);
    }

    public StructureGeneratorInstance(Map<String, Object> savedInstanceData) {
        super(savedInstanceData);
        this.buffer = (double) savedInstanceData.getOrDefault("buffer", 0.0);
    }

    public double getEnergyFromMaterial(Material material){
        switch (material){
            case CHARCOAL:
            case COAL: return 1600;
            case COAL_BLOCK: return 16000;

        }
        return 0;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("buffer", buffer);
        return map;
    }

    @Override
    public void onInit(Map<String, Object> structureSpecific) {
        super.onInit(structureSpecific);
        energyPerTick = (double) structureSpecific.get("energy_per_tick");
        efficiency = (double) structureSpecific.get("efficiency");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void tick() {
        super.tick();
        if(!isSleeping()){
            if(buffer > 1){
                EnergyNetwork network = AndrasiaAutomation.energy.getNetworkFromPlayer(player);

                if(buffer > energyPerTick){
                    network.addEnergy(energyPerTick);
                    buffer -= energyPerTick;
                }else{
                    network.addEnergy(buffer);
                    buffer = 0;
                }
            }else{
                Block block = getLocation().clone().add(0, 1, 0).getBlock();
                if(block.getType().equals(Material.CHEST)) {
                    Chest chest = (Chest) block.getState();
                    for(ItemStack itemStack : chest.getInventory()){
                        if(itemStack != null){
                            double fuel = getEnergyFromMaterial(itemStack.getType()) * efficiency;
                            buffer += fuel;
                            itemStack.setAmount(itemStack.getAmount() - 1);
                            break;
                        }
                    }
                }

                if(buffer < 1){
                    // No item has been found
                    sleepForTicks(20);
                }
            }
        }
    }
}
