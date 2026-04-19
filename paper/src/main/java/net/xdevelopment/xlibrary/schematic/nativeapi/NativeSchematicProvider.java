package net.xdevelopment.xlibrary.schematic.nativeapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.xdevelopment.xlibrary.schematic.SchematicProvider;
import net.xdevelopment.xlibrary.schematic.nativeapi.load.AsyncSchematicLoader;
import net.xdevelopment.xlibrary.schematic.nativeapi.load.TerritoryPasteTransaction;
import net.xdevelopment.xlibrary.schematic.nativeapi.save.TerritorySaveTransaction;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class NativeSchematicProvider implements SchematicProvider {

    Plugin plugin;
    Gson gson;
    int pasteTransactionSize;
    int saveTransactionSize;

    public NativeSchematicProvider(@NotNull Plugin plugin) {
        this(plugin, 5000, 10000);
    }

    public NativeSchematicProvider(@NotNull Plugin plugin, int pasteTransactionSize, int saveTransactionSize) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
        this.pasteTransactionSize = pasteTransactionSize;
        this.saveTransactionSize = saveTransactionSize;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    @NotNull
    public CompletableFuture<Object> paste(@NotNull File file, @NotNull Location location) {
        final CompletableFuture<Object> future = new CompletableFuture<>();
        new AsyncSchematicLoader(plugin, gson, pasteTransactionSize).loadAndPaste(file, location, future);
        return future;
    }

    @Override
    @NotNull
    public CompletableFuture<File> save(@NotNull File file, @NotNull Location corner1, @NotNull Location corner2, boolean ignoreAir) {
        final CompletableFuture<File> future = new CompletableFuture<>();

        new TerritorySaveTransaction(
                plugin, gson, file, corner1, corner2, ignoreAir, saveTransactionSize
        ).start(future);

        return future;
    }

    @Override
    @NotNull
    public CompletableFuture<Void> undo(@NotNull Object session) {
        return session instanceof TerritoryPasteTransaction transaction
                ? transaction.undo()
                : CompletableFuture.completedFuture(null);
    }
}
