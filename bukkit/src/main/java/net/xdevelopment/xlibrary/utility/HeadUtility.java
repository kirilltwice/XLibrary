package net.xdevelopment.xlibrary.utility;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

@UtilityClass
public class HeadUtility {

    public ItemStack headBuilder(String material) {
        final String baseHead = "PLAYER_HEAD;";
        if (material.startsWith(baseHead)) {
            final ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
            final SkullMeta meta = (SkullMeta) stack.getItemMeta();
            final String texture = material.substring(baseHead.length());
            if (meta != null) {
                createRandomProfile(meta, texture);
                stack.setItemMeta(meta);
            }
            return stack;
        }
        return new ItemStack(Material.valueOf(material), 1);
    }

    private void createRandomProfile(SkullMeta skullMeta, String texture) {
        final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", texture));
        skullMeta.setPlayerProfile(profile);
    }
}
