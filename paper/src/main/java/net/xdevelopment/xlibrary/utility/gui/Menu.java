package net.xdevelopment.xlibrary.utility.gui;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.xdevelopment.xlibrary.utility.gui.executable.ExecutableClose;
import net.xdevelopment.xlibrary.utility.gui.slot.MenuSlot;
import net.xdevelopment.xlibrary.core.Identifiable;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Menu implements InventoryHolder, Identifiable {
    
    final String menuId;
    final UUID uniqueId = generateUniqueId();
    Component title;
    final Inventory inventory;
    final Int2ObjectMap<MenuSlot> slots = new Int2ObjectOpenHashMap<>();
    @Setter
    boolean interactDisabled = true;
    @Setter
    ExecutableClose executableClose;

    public Menu(String id, String title, int rows) {
        this(id, Component.text(title), rows);
    }

    public Menu(String id, Component title, int rows) {
        this.menuId = id;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, rows * 9, title);
    }

    public Menu(String id, Component title, InventoryType type) {
        this.menuId = id;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, type, title);
    }

    public MenuSlot getSlot(int position) {
        return slots.get(position);
    }

    public Menu setSlot(int position, MenuSlot slot) {
        slot.setPosition(position);
        slots.put(position, slot);
        inventory.setItem(position, slot.getItem());
        return this;
    }

    public Menu removeSlot(int position) {
        slots.remove(position);
        inventory.clear(position);
        return this;
    }

    public Menu refreshItems() {
        inventory.clear();
        for (Int2ObjectMap.Entry<MenuSlot> entry : slots.int2ObjectEntrySet()) {
            inventory.setItem(entry.getIntKey(), entry.getValue().getItem());
        }
        for (HumanEntity viewer : inventory.getViewers()) {
            if (viewer instanceof Player player) {
                player.updateInventory();
            }
        }
        return this;
    }

    public Menu refreshSlot(int slot) {
        MenuSlot menuSlot = slots.get(slot);
        if (menuSlot == null) {
            inventory.clear(slot);
        } else {
            inventory.setItem(slot, menuSlot.getItem());
        }
        for (HumanEntity viewer : inventory.getViewers()) {
            if (viewer instanceof Player player) {
                player.updateInventory();
            }
        }
        return this;
    }

    public int getSlotPosition(MenuSlot slot) {
        for (Int2ObjectMap.Entry<MenuSlot> entry : slots.int2ObjectEntrySet()) {
            if (entry.getValue().equals(slot)) {
                return entry.getIntKey();
            }
        }
        return -1;
    }

    public boolean hasSlot(int slot) {
        return slots.containsKey(slot);
    }

    public boolean hasExecutableClose() {
        return executableClose != null;
    }

    public void setTitle(Component title) {
        this.title = title;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public @NotNull String getName() {
        return menuId;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uniqueId;
    }
}
