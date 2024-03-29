package net.andrasia.kiryu144.andrasiaautomation;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import net.andrasia.kiryu144.andrasiaautomation.energy.Energy;
import net.andrasia.kiryu144.andrasiaautomation.energy.EnergyNetwork;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import net.andrasia.kiryu144.andrasiaautomation.structure.StructureItems;
import net.andrasia.kiryu144.andrasiaautomation.structure.StructureParser;
import net.andrasia.kiryu144.andrasiaautomation.structure.Structures;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.MultiStructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.SingleStructureBlock;
import net.andrasia.kiryu144.andrasiaautomation.structure.block.StructureBlock;
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
    public static AndrasiaAutomation instance;
    public static boolean DEBUG = true;
    public static WorldEditPlugin worldEdit;
    public static Material PRIMARY_BLOCK = Material.EMERALD_BLOCK;

    public static Structures structures;
    public static StructureItems items;
    public static Energy energy;

    @Override
    public void onEnable() {
        instance = this;
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        registerSerializedClasses();

        structures = new Structures();
        items = new StructureItems();
        energy = new Energy();

        Bukkit.getPluginManager().registerEvents(structures, this);
        Bukkit.getPluginManager().registerEvents(energy, this);

        loadConfig();
        structures.loadAll();
        energy.loadAll();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            structures.tickAll();
        }, 1, 1);
    }

    @Override
    public void onDisable() {
        energy.onServerClose();
    }

    public void registerSerializedClasses() {
        ConfigurationSerialization.registerClass(MultiStructureBlock.class);
        ConfigurationSerialization.registerClass(SingleStructureBlock.class);
        ConfigurationSerialization.registerClass(StructureBlock.class);
        ConfigurationSerialization.registerClass(StructureInstance.class);
        ConfigurationSerialization.registerClass(FixedSize3DArray.class);
        ConfigurationSerialization.registerClass(Structure.class);
        ConfigurationSerialization.registerClass(EnergyNetwork.class);
    }

    public void loadConfig() {
        saveResource("config.yml", false);
        super.reloadConfig();
        structures.clearStructures();
        for(String structureName : getConfig().getStringList("structures")){
            File file = new File(getDataFolder() + "/structures/" + structureName + ".yml");
            try {
                structures.addStructure(StructureParser.LoadFromConfig(file));
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
            if(!sender.isOp()){
                return false;
            }

            if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    loadConfig();
                    structures.loadAll();
                    sender.sendMessage("Reloaded.");
                    return true;
                    //loadConfig();
                    //sender.sendMessage("§aReloaded.");
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
                }else if(args[0].equalsIgnoreCase("energy") && args[1].equalsIgnoreCase("get")){
                    sender.sendMessage(String.format("Energy level: %f", energy.getNetworkFromPlayer(player.getUniqueId()).getEnergy()));
                }else if(args[0].equalsIgnoreCase("get")){
                    player.getInventory().addItem(items.getItemForStructure(structures.getStructure(args[1])));
                }
            }

            return true;
        }
        return false;
    }
}
