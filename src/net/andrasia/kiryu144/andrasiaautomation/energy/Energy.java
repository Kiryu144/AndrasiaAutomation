package net.andrasia.kiryu144.andrasiaautomation.energy;


import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Energy implements Listener {
    protected HashMap<UUID, EnergyNetwork> playerNetworkAssingment;
    protected HashMap<UUID, EnergyNetwork> networks;

    public Energy() {
        playerNetworkAssingment = new HashMap<>();
        networks = new HashMap<>();
    }

    protected EnergyNetwork getNetworkForPlayer(Player player){
        return playerNetworkAssingment.get(player.getUniqueId());
    }

    protected File getPlayerFile(Player player){
        return new File(AndrasiaAutomation.instance.getDataFolder() + "/players/" + player.getUniqueId() + ".yml");
    }

    protected void savePlayerData(Player player) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("network", playerNetworkAssingment.get(player.getUniqueId()).getUniqueId());
        File file = getPlayerFile(player);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            player.sendMessage("§cUnable to save your network data. Please report to an Administrator!");
            e.printStackTrace();
        }
    }

    protected EnergyNetwork loadPlayerData(Player player) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        File file = getPlayerFile(player);
        try {
            yamlConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }
        return networks.get(UUID.fromString(yamlConfiguration.getString("network")));
    }

    protected File getNetworkFile(EnergyNetwork network){
        return getNetworkFile(network.getUniqueId());
    }

    protected File getNetworkFile(UUID network){
        return new File(AndrasiaAutomation.instance.getDataFolder() + "/networks/" + network + ".yml");
    }

    protected void saveNetwork(EnergyNetwork network){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("network", network);
        try {
            yamlConfiguration.save(getNetworkFile(network));
        } catch (IOException e) {
            AndrasiaAutomation.instance.getLogger().warning("Could not save network " + network.getUniqueId().toString());
            e.printStackTrace();
        }
    }

    protected EnergyNetwork loadNetwork(UUID uuid){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        EnergyNetwork network = null;
        try {
            yamlConfiguration.load(getNetworkFile(uuid));
            network = (EnergyNetwork) yamlConfiguration.get("network");
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }
        return null;
    }

    public void loadAllNetworks() {
        File file = getNetworkFile(UUID.randomUUID()).getParentFile();
        file.mkdirs();
        for(File networkFile : file.listFiles()){
            if(networkFile.getName().endsWith(".yml")){
                EnergyNetwork network = loadNetwork(UUID.fromString(networkFile.getName().replace(".yml", "")));
                networks.put(network.getUniqueId(), network);
            }
        }
    }

    protected EnergyNetwork createNewNetwork(Player player){
        EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), 0.0);
        networks.put(network.getUniqueId(), network);
        return network;
    }

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent e){
        EnergyNetwork network = loadPlayerData(e.getPlayer());
        if(network == null){
            network = createNewNetwork(e.getPlayer());
        }
        playerNetworkAssingment.put(e.getPlayer().getUniqueId(), network);
        savePlayerData(e.getPlayer());
    }

    @EventHandler
    protected void onPlayerQuit(PlayerQuitEvent e){
        savePlayerData(e.getPlayer());
        saveNetwork(getNetworkForPlayer(e.getPlayer()));
    }
}
