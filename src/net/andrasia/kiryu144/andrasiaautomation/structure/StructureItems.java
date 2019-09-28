package net.andrasia.kiryu144.andrasiaautomation.structure;

import net.andrasia.kiryu144.andrasiaautomation.AndrasiaAutomation;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class StructureItems {
    public final static NamespacedKey TYPE_KEY = new NamespacedKey(AndrasiaAutomation.instance, "structure_id");

    public ItemStack getItemForStructure(Structure structure){
        ItemStack itemStack = new ItemStack(AndrasiaAutomation.PRIMARY_BLOCK);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§r§6" + structure.getName());
        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, structure.getId());
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public Structure getStructureFromItem(ItemStack itemStack){
        if(itemStack != null && itemStack.getType().equals(AndrasiaAutomation.PRIMARY_BLOCK)){
            if(itemStack.hasItemMeta()){
                ItemMeta meta = itemStack.getItemMeta();
                String s = meta.getPersistentDataContainer().get(TYPE_KEY, PersistentDataType.STRING);
                if(s != null){
                    return AndrasiaAutomation.structures.getStructure(s);
                }
            }
        }
        return null;
    }
}
