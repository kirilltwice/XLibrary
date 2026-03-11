package net.xdevelopment.xlibrary.utility.gui;


import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class MenuBuilder {

    public int firstEmptySlot(Menu menu, int startSlot, int endSlot) {
        for (int i = startSlot; i <= endSlot; i++) {
            final ItemStack item = menu.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                return i;
            }
        }
        return -1;
    }

}
