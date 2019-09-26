package net.andrasia.kiryu144.andrasiaautomation.structure.instance;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.energy.EnergyNetwork;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class StructureGeneratorInstance extends StructureInstance {
    // Serialized in structure
    protected double energyPerTick;

    public StructureGeneratorInstance(Location location, Structure structure, UUID player) {
        super(location, structure, player);
    }

    public StructureGeneratorInstance(Map<String, Object> savedInstanceData) {
        super(savedInstanceData);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    @Override
    public void onInit(Map<String, Object> structureSpecific) {
        super.onInit(structureSpecific);
        energyPerTick = (double) structureSpecific.get("energy_per_tick");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void tick() {
        super.tick();
        if(!isSleeping()){
            EnergyNetwork network = AndrasiaAutomation.energy.getNetworkFromPlayer(player);
            network.addEnergy(energyPerTick);
        }
    }
}
