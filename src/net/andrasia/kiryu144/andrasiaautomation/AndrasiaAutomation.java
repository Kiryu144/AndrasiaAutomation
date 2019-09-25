package net.andrasia.kiryu144.andrasiaautomation;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import net.andrasia.kiryu144.andrasiaautomation.structure.StructureParser;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structures;
import net.andrasia.kiryu144.andrasiaautomation.structure.WorldPlacedStructuresRegistry;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.MultiStructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.SingleStructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.controller.StructureController;
import net.andrasia.kiryu144.andrasiaautomation.structure.instance.StructureInstance;
import net.andrasia.kiryu144.andrasiaautomation.util.FixedSize3DArray;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
        registerSerializedClasses();

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

    public void registerSerializedClasses() {
        ConfigurationSerialization.registerClass(MultiStructureBlock.class);
        ConfigurationSerialization.registerClass(SingleStructureBlock.class);
        ConfigurationSerialization.registerClass(StructureBlock.class);
        ConfigurationSerialization.registerClass(StructureInstance.class);
        ConfigurationSerialization.registerClass(FixedSize3DArray.class);
        ConfigurationSerialization.registerClass(Structure.class);
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
                    } catch (IOException | InvalidConfigurationException e) {
                        sender.sendMessage("§cUnable to load file! See log for more infos.");
                        e.printStackTrace();
                    }
                    return true;
                }
            }

            return true;
        }
        return false;
    }
}
