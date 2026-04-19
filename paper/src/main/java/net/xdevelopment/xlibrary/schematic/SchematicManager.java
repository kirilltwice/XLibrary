package net.xdevelopment.xlibrary.schematic;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SchematicManager {

    SchematicProvider provider;

    public SchematicManager(@NotNull SchematicProvider provider) {
        if (!provider.init()) {
            throw new IllegalStateException("Failed to initialize SchematicProvider!");
        }
        this.provider = provider;
    }

    @NotNull
    public CompletableFuture<Object> paste(@NotNull File file, @NotNull Location location) {
        return provider.paste(file, location);
    }

    @NotNull
    public CompletableFuture<File> save(@NotNull File file, @NotNull Location corner1, @NotNull Location corner2, boolean ignoreAir) {
        return provider.save(file, corner1, corner2, ignoreAir);
    }

    @NotNull
    public CompletableFuture<Void> undo(@NotNull Object session) {
        return provider.undo(session);
    }
}
