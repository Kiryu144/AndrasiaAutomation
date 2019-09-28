package net.andrasia.kiryu144.andrasiaautomation.structure.instance;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.energy.Energy;
import net.andrasia.kiryu144.andrasiaautomation.energy.EnergyNetwork;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StructureInstance implements ConfigurationSerializable {
    // Serialized in instance
    protected Structure structure; // The structure this instance is bound to
    protected Location location;   // The location the structure instance is placed at (PRIMARY_BLOCK)
    protected UUID player;
    protected int sleepingTicks;   // How many ticks the structure waits before ticking again


    public StructureInstance(Location location, Structure structure, UUID player) {
        this.structure = structure;
        this.location = location;
        this.player = player;
        this.sleepingTicks = 0;
    }

    public StructureInstance(Map<String, Object> savedInstanceData) {
        this.structure = AndrasiaAutomation.structures.getStructure((String) savedInstanceData.get("structure"));
        this.location = (Location) savedInstanceData.get("location");
        this.sleepingTicks = (int) savedInstanceData.getOrDefault("sleeping_ticks", 0);
        this.player = UUID.fromString((String) savedInstanceData.getOrDefault("player", UUID.randomUUID().toString()));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("structure", structure.getId());
        data.put("location", location);
        data.put("sleeping_ticks", sleepingTicks);
        data.put("player", player.toString());
        return data;
    }

    public Structure getStructure() {
        return structure;
    }

    public Location getLocation() {
        return location;
    }

    public void sleepForTicks(int ticks){
        sleepingTicks = Math.max(sleepingTicks, ticks);
    }

    public boolean isSleeping() {
        return sleepingTicks > 0;
    }

    public EnergyNetwork getEnergyNetwork() {
        return AndrasiaAutomation.energy.getNetworkFromPlayer(player);
    }

    public void onInit(Map<String, Object> structureSpecific) {
        // Called when the structure gets built / initialized on server restart
    }

    public void onDestroy() {
        // Called when the structure gets destroyed
    }

    public void tick(){
        // Called every tick (if chunk is loaded)
        if(sleepingTicks > 0){
            --sleepingTicks;
        }
    }

    public void onReload() {

    }
}
