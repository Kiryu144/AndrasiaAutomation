package net.andrasia.kiryu144.andrasiaautomation.energy;


import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Energy implements Listener {
    protected HashMap<UUID, EnergyNetwork> players;
    protected HashMap<UUID, EnergyNetwork> networks;

    public Energy() {
        players = new HashMap<>();
        networks = new HashMap<>();
    }

    public void loadAll() {
        // Load networks
        {
            File file = getNetworkFile(UUID.randomUUID()).getParentFile();
            file.mkdirs();
            for (File networkFile : file.listFiles()) {
                if (networkFile.getName().endsWith(".yml")) {
                    EnergyNetwork network = loadNetwork(UUID.fromString(networkFile.getName().replace(".yml", "")));
                    networks.put(network.getUniqueId(), network);
                }
            }
            AndrasiaAutomation.instance.getLogger().info(String.format("Loaded %d networks from config", networks.size()));
        }

        // Load players
        {
            File file = getPlayerFile(UUID.randomUUID()).getParentFile();
            file.mkdirs();
            for (File playerFile : file.listFiles()) {
                if (playerFile.getName().endsWith(".yml")) {
                    UUID player = UUID.fromString(playerFile.getName().replace(".yml", ""));
                    UUID network = loadPlayer(player);
                    players.put(player, getNetwork(network));
                }
            }
            AndrasiaAutomation.instance.getLogger().info(String.format("Loaded %d players from config", players.size()));
        }
    }

    public EnergyNetwork getNetworkFromPlayer(UUID player){
        return players.get(player);
    }

    public EnergyNetwork getNetwork(UUID network){
        return networks.get(network);
    }

    protected File getPlayerFile(UUID player){
        return new File(AndrasiaAutomation.instance.getDataFolder() + "/players/" + player.toString() + ".yml");
    }

    protected File getNetworkFile(UUID network){
        return new File(AndrasiaAutomation.instance.getDataFolder() + "/networks/" + network.toString() + ".yml");
    }

    protected void savePlayer(UUID player, UUID network) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("network", network.toString());
        File file = getPlayerFile(player);
        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            AndrasiaAutomation.instance.getLogger().warning(String.format("Unable to save network data for player %s", player));
            e.printStackTrace();
        }
    }

    protected UUID loadPlayer(UUID player) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        File file = getPlayerFile(player);
        try {
            yamlConfiguration.load(file);
            return UUID.fromString(yamlConfiguration.getString("network"));
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }
    }

    protected void saveNetwork(EnergyNetwork network){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("network", network);
        try {
            yamlConfiguration.save(getNetworkFile(network.getUniqueId()));
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
        return network;
    }

    @EventHandler
    protected void onRightClick(PlayerInteractEvent event){
        if(event.getItem().getType().equals(Material.DEBUG_STICK) && event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            event.getPlayer().sendMessage(String.format("Energy: %d/%d", Math.round(getNetworkFromPlayer(event.getPlayer().getUniqueId()).getEnergy()), Math.round(EnergyNetwork.LIMIT)));
        }
    }

    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent e){
        if(!players.containsKey(e.getPlayer().getUniqueId())){
            EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), 0.0);
            saveNetwork(network);
            savePlayer(e.getPlayer().getUniqueId(), network.getUniqueId());
            networks.put(network.getUniqueId(), network);
            players.put(e.getPlayer().getUniqueId(), network);
            AndrasiaAutomation.instance.getLogger().info(String.format("Created network '%s' for player '%s'", network.getUniqueId().toString(), e.getPlayer().getName()));
        }
    }

    @EventHandler
    protected void onPlayerQuit(PlayerQuitEvent e){
        savePlayer(e.getPlayer().getUniqueId(), getNetworkFromPlayer(e.getPlayer().getUniqueId()).getUniqueId());
        saveNetwork(getNetworkFromPlayer(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    protected void onPlayerKick(PlayerKickEvent e){
        savePlayer(e.getPlayer().getUniqueId(), getNetworkFromPlayer(e.getPlayer().getUniqueId()).getUniqueId());
        saveNetwork(getNetworkFromPlayer(e.getPlayer().getUniqueId()));
    }

    public void onServerClose() {
        for(Player player : Bukkit.getOnlinePlayers()){
            savePlayer(player.getUniqueId(), getNetworkFromPlayer(player.getUniqueId()).getUniqueId());
            saveNetwork(getNetworkFromPlayer(player.getUniqueId()));
        }
    }
}
