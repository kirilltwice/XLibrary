package net.xdevelopment.xlibrary.utility.gui.executable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.xdevelopment.xlibrary.utility.gui.slot.MenuSlot;
import org.bukkit.entity.Player;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExecutableClick {

    @NonFinal MenuSlot slot;

    public void onLeft(Player player) {
    }

    public void onMiddle(Player player) {
        onLeft(player);
    }

    public void onRight(Player player) {
        onLeft(player);
    }

    public void onShiftLeft(Player player) {
        onLeft(player);
    }

    public void onShiftRight(Player player) {
        onLeft(player);
    }

    public void attach(MenuSlot slot) {
        this.slot = slot;
    }
}
