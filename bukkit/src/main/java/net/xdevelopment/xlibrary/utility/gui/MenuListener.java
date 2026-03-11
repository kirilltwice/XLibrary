package net.xdevelopment.xlibrary.utility.gui;


import lombok.RequiredArgsConstructor;
import net.xdevelopment.xlibrary.utility.gui.executable.ExecutableClick;
import net.xdevelopment.xlibrary.utility.gui.slot.MenuSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@RequiredArgsConstructor
public class MenuListener implements Listener {
    private static final Set<UUID> throttled = ConcurrentHashMap.newKeySet();
    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final Inventory topInventory = event.getView().getTopInventory();
        if (!(topInventory.getHolder() instanceof Menu menu)) {
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
            final UUID uuid = player.getUniqueId();
            if (!throttled.add(uuid)) {
                event.setCancelled(true);
                return;
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> throttled.remove(uuid), 2L);

            final ExecutableClick executable = slot.getExecutable();
            final ClickType click = event.getClick();


            if (click == ClickType.LEFT) executable.onLeft(player);
            else if (click == ClickType.MIDDLE) executable.onMiddle(player);
            else if (click == ClickType.RIGHT) executable.onRight(player);
            else if (click == ClickType.SHIFT_LEFT) executable.onShiftLeft(player);
            else if (click == ClickType.SHIFT_RIGHT) executable.onShiftRight(player);

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu menu && menu.isInteractDisabled()) {
             final boolean topAffected = event.getRawSlots().stream().anyMatch(slot -> slot < event.getView().getTopInventory().getSize());
             if (topAffected) {
                 event.setCancelled(true);
             }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu && menu.hasExecutableClose() && event.getPlayer() instanceof Player player) {
            menu.getExecutableClose().run(player);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(InventoryInteractEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu menu && menu.isInteractDisabled()) {
            event.setCancelled(true);
        }
    }
}
