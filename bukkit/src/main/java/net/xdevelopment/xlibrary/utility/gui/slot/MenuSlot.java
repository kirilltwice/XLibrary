package net.xdevelopment.xlibrary.utility.gui.slot;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.xdevelopment.xlibrary.utility.HeadUtility;
import net.xdevelopment.xlibrary.utility.gui.executable.ExecutableClick;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Accessors(chain = true)
public final class MenuSlot {
    private ItemStack item;
    private ExecutableClick executable;
    @Getter
    private boolean interactDisabled;
    @Setter
    private int position;
    private boolean staticSlot;

    public MenuSlot(@NotNull ItemStack item) {
        this.item = item;
    }

    public MenuSlot(@NotNull Material material) {
        this.item = new ItemStack(material);
    }

    public MenuSlot(@NotNull String materialKey) {
        this.item = HeadUtility.headBuilder(materialKey);
    }

    public MenuSlot(@NotNull Material material, @NotNull String display, @NotNull List<String> lore) {
        this.item = new ItemStack(material);
        ItemMeta meta = this.item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(display));
            meta.lore(lore.stream().map(Component::text).toList());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            this.item.setItemMeta(meta);
        }
    }

    public MenuSlot setItem(@NotNull ItemStack item) {
        this.item = item;
        return this;
    }

    public MenuSlot display(@NotNull String display) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return this;
        }
        meta.displayName(Component.text(display));
        item.setItemMeta(meta);
        return this;
    }

    public MenuSlot lore(@NotNull List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return this;
        }
        meta.lore(lore.stream().map(Component::text).toList());
        item.setItemMeta(meta);
        return this;
    }

    public MenuSlot amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public MenuSlot hideAttributes(boolean hide) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (hide) {
                meta.addItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_DYE,
                        ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                        ItemFlag.HIDE_ARMOR_TRIM
                );
            } else {
                meta.removeItemFlags(
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_DYE,
                        ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_ADDITIONAL_TOOLTIP,
                        ItemFlag.HIDE_ARMOR_TRIM
                );
            }
            item.setItemMeta(meta);
        }
        return this;
    }

    public MenuSlot enchanted(boolean enchanted) {
        if (enchanted) {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        } else {
            item.removeEnchantment(Enchantment.LURE);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
        return this;
    }

    public MenuSlot disableInteract(boolean disable) {
        this.interactDisabled = disable;
        return this;
    }

    public MenuSlot headOwner(@NotNull OfflinePlayer player) {
        if (!(item.getItemMeta() instanceof SkullMeta skullMeta)) {
            return this;
        }
        skullMeta.setOwningPlayer(player);
        item.setItemMeta(skullMeta);
        return this;
    }

    public MenuSlot headTexture(@NotNull String value) {
        this.item = HeadUtility.headBuilder("PLAYER_HEAD;" + value);
        return this;
    }

    public MenuSlot staticSlot(boolean staticSlot) {
        this.staticSlot = staticSlot;
        return this;
    }

    public boolean isStatic() {
        return staticSlot;
    }

    public MenuSlot executable(ExecutableClick executable) {
        this.executable = executable;
        if (executable != null) {
            executable.attach(this);
        }
        return this;
    }

    public boolean hasExecutable() {
        return executable != null;
    }
}
