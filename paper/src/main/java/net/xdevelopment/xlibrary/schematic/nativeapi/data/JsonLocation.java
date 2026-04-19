package net.xdevelopment.xlibrary.schematic.nativeapi.data;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record JsonLocation(double x, double y, double z, @NotNull String worldName) {

    @NotNull
    public static JsonLocation fromLocation(@NotNull Location location) {
        return new JsonLocation(location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }
}
