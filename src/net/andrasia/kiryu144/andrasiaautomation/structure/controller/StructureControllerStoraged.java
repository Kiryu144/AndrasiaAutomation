package net.andrasia.kiryu144.andrasiaautomation.structure.controller;

import net.andrasia.kiryu144.andrasiaautomation.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StructureControllerStoraged extends StructureController {
    protected ArrayList<ItemStack> items;

    public StructureControllerStoraged(Structure structure, Location location) {
        super(structure, location);
        items = new ArrayList<>();
    }

    @Override
    public void loadData(ConfigurationSection section) {
        int slots = section.getInt("slots", 0);
        for(int i = 0; i < slots; ++i){
            items.add(null);
        }
    }

    public Inventory createInventoryCopy() {
        Inventory inv = Bukkit.createInventory(null, 9*6);
        for(int slot = 0; slot < items.size(); ++slot){
            inv.setItem(slot, items.get(slot));
        }
        return inv;
    }

    public void clearItemsFromInventory() {
        for(int slot = 0; slot < items.size(); ++slot){
            items.set(slot, null);
        }
    }

    public ItemStack addItemToInventory(ItemStack itemStack){
        for(int slot = 0; slot < items.size() && itemStack.getAmount() > 0; ++slot) {
            if(items.get(slot) == null){
                items.set(slot, itemStack.clone());
                return null;
            }else if(items.get(slot).isSimilar(itemStack)){
                ItemStack ininv = items.get(slot);
                int inInvAmount = Math.min(ininv.getAmount() + itemStack.getAmount(), ininv.getMaxStackSize());
                int left = itemStack.getAmount() - (inInvAmount - ininv.getAmount());
                ininv.setAmount(inInvAmount);
                itemStack.setAmount(left);
            }
        }
        return (itemStack.getAmount() > 0) ? itemStack : null;
    }

    public ArrayList<ItemStack> getItemsFromInventory() {
        return items;
    }
}
