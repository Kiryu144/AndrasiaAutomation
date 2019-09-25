package net.andrasia.kiryu144.andrasiaautomation.energy;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnergyNetwork implements ConfigurationSerializable {
    public final static double LIMIT = 100000;
    protected UUID uuid;
    protected double energy;

    public EnergyNetwork(UUID uuid, double energy) {
        this.uuid = uuid;
        this.energy = energy;
    }

    public EnergyNetwork(Map<String, Object> serialization) {
        this.uuid = UUID.fromString((String) serialization.get("uuid"));
        this.energy = (double) serialization.get("energy");
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = Math.min(energy, LIMIT);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("energy", energy);
        return map;
    }
}
