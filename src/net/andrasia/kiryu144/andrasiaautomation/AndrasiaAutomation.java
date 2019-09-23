package net.andrasia.kiryu144.andrasiaautomation;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import net.andrasia.kiryu144.andrasiaautomation.structure.StructureParser;
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
    public static Material PRIMARY_BLOCK = Material.BARREL;

    @Override
    public void onEnable() {
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("automation")){
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
                }else if(args[0].equalsIgnoreCase("paste")){
                    try {
                        StructureParser.Paste(StructureParser.LoadFromConfig(file), player.getLocation().subtract(0, 1, 0));
                        sender.sendMessage("§aLoaded.");
                    } catch (IOException | InvalidConfigurationException e) {
                        sender.sendMessage("§cUnable to load file!");
                    }
                }
            }

            return true;
        }
        return false;
    }
}
