package net.xdevelopment.xlibrary.schematic.selection;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.xdevelopment.xlibrary.schematic.SchematicMessages;
import net.xdevelopment.xlibrary.utility.ColorUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SelectionListener implements Listener {

    public static final Material WAND_MATERIAL = Material.WOODEN_AXE;
    Map<UUID, PlayerSelection> selections = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType() != WAND_MATERIAL) return;
        if (!player.hasPermission("xlibrary.schematic.wand")) return;

        final Location clicked = event.getClickedBlock() != null
                ? event.getClickedBlock().getLocation()
                : null;

        if (clicked == null) return;

        final PlayerSelection selection = getOrCreateSelection(player);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            selection.setPos1(clicked);
            player.sendMessage(ColorUtility.colorize(SchematicMessages.POS1_SET, Map.of(
                    "x", clicked.getBlockX(),
                    "y", clicked.getBlockY(),
                    "z", clicked.getBlockZ()
            )));
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            selection.setPos2(clicked);
            player.sendMessage(ColorUtility.colorize(SchematicMessages.POS2_SET, Map.of(
                    "x", clicked.getBlockX(),
                    "y", clicked.getBlockY(),
                    "z", clicked.getBlockZ()
            )));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        selections.remove(event.getPlayer().getUniqueId());
    }

    @Nullable
    public PlayerSelection getSelection(@NotNull Player player) {
        return selections.get(player.getUniqueId());
    }

    @NotNull
    public PlayerSelection getOrCreateSelection(@NotNull Player player) {
        return selections.computeIfAbsent(player.getUniqueId(), k -> new PlayerSelection());
    }
}
