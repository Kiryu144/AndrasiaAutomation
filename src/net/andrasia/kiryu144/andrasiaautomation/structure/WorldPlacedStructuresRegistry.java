package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import net.andrasia.kiryu144.andrasiaautomation.structure.controller.StructureController;
import net.andrasia.kiryu144.andrasiaautomation.structure.controller.StructureControllerStoraged;
import net.andrasia.kiryu144.andrasiaautomation.util.LocationMap;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public class WorldPlacedStructuresRegistry implements Listener {
    protected LocationMap<StructureController> structures;
    protected ArrayList<StructureController> structureControllers;

    public WorldPlacedStructuresRegistry() {
        structures = new LocationMap<>();
        structureControllers = new ArrayList<>();
    }

    public void tickAll() {
        for(StructureController controller : structureControllers){
            controller.tick();
        }
    }

    @EventHandler
    protected void onBlockPlace(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)) {
            Structure structure = AndrasiaAutomation.structures.checkForStructure(e.getBlock());
            if (structure != null) {
                try {
                    StructureController controller = structure.getStructureInstanceClass().getConstructor(Structure.class, Location.class).newInstance(structure, e.getBlock().getLocation());
                    controller.loadData(structure.getStructureInstanceData());
                    structures.put(e.getBlock().getLocation(), controller);
                    structureControllers.add(controller);

                    e.getPlayer().sendMessage("§aCreated §6" + structure.getName() + "§a.");
                } catch(Exception ex){
                    e.getPlayer().sendMessage("§cError creating §6" + structure.getName() + "§c. Please report to an admin!");
                    ex.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    protected void onBlockBreak(BlockBreakEvent e){
        if(e.getBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)) {
            StructureController structure = structures.remove(e.getBlock().getLocation());
            if(structure != null){
                e.getPlayer().sendMessage("§aStructure now broken.");
            }
        }
    }

    @EventHandler
    protected void onInteract(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
            StructureController controller = structures.get(event.getClickedBlock().getLocation());
            if(controller != null){
                event.setCancelled(true);
                if(controller instanceof StructureControllerStoraged){
                    event.getPlayer().openInventory(((StructureControllerStoraged) controller).createInventoryCopy());
                }
            }else{
                structures.remove(event.getClickedBlock().getLocation());
            }
        }
    }


}
