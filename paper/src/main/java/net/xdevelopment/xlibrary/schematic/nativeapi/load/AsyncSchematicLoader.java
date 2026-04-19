package net.xdevelopment.xlibrary.schematic.nativeapi.load;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.xdevelopment.xlibrary.schematic.nativeapi.data.JsonSchematicData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class AsyncSchematicLoader {

    Plugin plugin;
    Gson gson;
    int pasteTransactionSize;

    public void loadAndPaste(@NotNull File file, @NotNull Location location, @NotNull CompletableFuture<Object> completion) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (!file.exists()) {
                    completion.complete(null);
                    return;
                }

                final String json = Files.readString(file.toPath());
                final JsonSchematicData data = gson.fromJson(json, JsonSchematicData.class);

                if (data.blocks() == null || data.blocks().isEmpty()) {
                    completion.complete(null);
                    return;
                }

                final TerritoryPasteTransaction transaction = new TerritoryPasteTransaction(
                        plugin, location, data.blocks(), pasteTransactionSize
                );

                transaction.start(completion);

            } catch (Exception exception) {
                completion.completeExceptionally(exception);
            }
        });
    }
}
