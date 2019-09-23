package net.andrasia.kiryu144.andrasiaautomation;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import net.andrasia.kiryu144.andrasiaautomation.structure.StructureParser;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structures;
import net.andrasia.kiryu144.andrasiaautomation.structure.WorldPlacedStructuresRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class AndrasiaAutomation extends JavaPlugin {
    public static WorldEditPlugin worldEdit;
    public static Material PRIMARY_BLOCK = Material.EMERALD_BLOCK;

    public static Structures structures;
    public static WorldPlacedStructuresRegistry worldPlacedStructures;

    @Override
    public void onEnable() {
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        structures = new Structures();
        worldPlacedStructures = new WorldPlacedStructuresRegistry();

        Bukkit.getPluginManager().registerEvents(structures, this);
        Bukkit.getPluginManager().registerEvents(worldPlacedStructures, this);

        loadConfig();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            worldPlacedStructures.tickAll();
        }, 1, 1);
    }

    public void loadConfig() {
        saveResource("config.yml", false);
        super.reloadConfig();
        for(String structureName : getConfig().getStringList("structures")){
            File file = new File(getDataFolder() + "/structures/" + structureName + ".yml");
            try {
                structures.add(StructureParser.LoadFromConfig(file));
                getLogger().info("Registered structure '" + structureName + "'");
            } catch (Exception e) {
                getLogger().severe("Unable to load structure '" + structureName + "'");
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("automation")){
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    structures.clear();
                    loadConfig();
                    sender.sendMessage("§aReloaded.");
                }
            }
            if(args.length == 2){
                String structureName = args[1].toLowerCase();
                File file = new File(getDataFolder() + "/structures/" + structureName + ".yml");
                Player player = (Player) sender;

                if(args[0].equalsIgnoreCase("save")){
                    World world = BukkitAdapter.adapt(player.getWorld());
                    try {
                        StructureParser.SaveToConfig(StructureParser.FromRegion(player, worldEdit.getSession(player).getSelection(world)), file);
                        sender.sendMessage("§aSaved.");
                    } catch (IOException e) {
                        sender.sendMessage("§cUnable to write file!");
                    } catch (IncompleteRegionException e) {
                        sender.sendMessage("§cCreate a selection with WorldEdit first!");
                    }
                    return true;
                }else if(args[0].equalsIgnoreCase("paste")){
                    try {
                        StructureParser.Paste(StructureParser.LoadFromConfig(file), player.getLocation().subtract(0, 1, 0));
                        sender.sendMessage("§aLoaded.");
                    } catch (IOException | InvalidConfigurationException | ClassNotFoundException e) {
                        sender.sendMessage("§cUnable to load file!");
                    }
                    return true;
                }
            }

            return true;
        }
        return false;
    }
}
