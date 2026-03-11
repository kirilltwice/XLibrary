package net.xdevelopment.xlibrary.utility.gui;

import dev.alexec0de.utility.ColorUtility;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@UtilityClass
public final class MenuManager {
    public Menu createGUI(String id, String playerName, String title, int rows) {
        return create(id, title, rows);
    }

    public Menu create(String id, String title, int rows) {
        return new Menu(id, ColorUtility.colorize(title), rows);
    }

    public Menu create(String id, Component title, int rows) {
        return new Menu(id, title, rows);
    }

    public Menu create(String id, Player player, Component title, int rows) {
        return new Menu(id, title, rows);
    }

    public Menu create(String id, String title, InventoryType type) {
        return new Menu(id, ColorUtility.colorize(title), type);
    }

    public Menu getMenu(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Menu menu) {
            return menu;
        }
        return null;
    }

    public void open(Player player, Menu menu) {
        player.openInventory(menu.getInventory());
    }

    public void reopen(Player player) {
        final Inventory top = player.getOpenInventory().getTopInventory();
        final InventoryHolder holder = top.getHolder();
        if (holder instanceof Menu menu) {
            player.openInventory(menu.getInventory());
        }
    }
}
