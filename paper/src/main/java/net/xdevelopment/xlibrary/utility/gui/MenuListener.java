package net.xdevelopment.xlibrary.utility.gui;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.xdevelopment.xlibrary.core.collection.ExpiringSet;
import net.xdevelopment.xlibrary.utility.gui.executable.ExecutableClick;
import net.xdevelopment.xlibrary.utility.gui.slot.MenuSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuListener implements Listener {
    static ExpiringSet<UUID> throttled = new ExpiringSet<>(100L);

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final Inventory topInventory = event.getView().getTopInventory();
        if (!(topInventory.getHolder(false) instanceof Menu menu)) {
            return;
        }

        if (menu.isInteractDisabled()) {
            final boolean isTopClicked = event.getClickedInventory() == topInventory;
            final boolean isShiftInBottom = event.getClickedInventory() == event.getView().getBottomInventory() && event.isShiftClick();
            final boolean isCollect = event.getAction() == InventoryAction.COLLECT_TO_CURSOR;

            if (isTopClicked || isShiftInBottom || isCollect) {
                event.setCancelled(true);
            }
        }

        if (event.getAction() == InventoryAction.NOTHING || event.getClickedInventory() == null) {
            return;
        }

        if (event.getClickedInventory() != topInventory) {
            return;
        }

        final MenuSlot slot = menu.getSlot(event.getSlot());
        if (slot == null) {
            return;
        }

        if (slot.isInteractDisabled()) {
            event.setCancelled(true);
        }

        if (slot.hasExecutable()) {
            if (!throttled.add(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            final ExecutableClick executable = slot.getExecutable();
            final ClickType click = event.getClick();

            switch (click) {
                case LEFT -> executable.onLeft(player);
                case MIDDLE -> executable.onMiddle(player);
                case RIGHT -> executable.onRight(player);
                case SHIFT_LEFT -> executable.onShiftLeft(player);
                case SHIFT_RIGHT -> executable.onShiftRight(player);
                default -> {}
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof Menu menu && menu.isInteractDisabled()) {
             final boolean topAffected = event.getRawSlots().stream().anyMatch(slot -> slot < event.getView().getTopInventory().getSize());
             if (topAffected) {
                 event.setCancelled(true);
             }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder(false) instanceof Menu menu && menu.hasExecutableClose() && event.getPlayer() instanceof Player player) {
            menu.getExecutableClose().run(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(InventoryInteractEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof Menu menu && menu.isInteractDisabled()) {
            event.setCancelled(true);
        }
    }
}
