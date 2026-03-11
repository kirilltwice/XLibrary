package net.xdevelopment.xlibrary.utility.gui.executable;


import lombok.Getter;
import net.xdevelopment.xlibrary.utility.gui.slot.MenuSlot;
import org.bukkit.entity.Player;

@Getter
public class ExecutableClick {

    private MenuSlot slot;

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
