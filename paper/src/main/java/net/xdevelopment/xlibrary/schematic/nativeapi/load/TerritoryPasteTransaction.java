package net.xdevelopment.xlibrary.schematic.nativeapi.load;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.xdevelopment.xlibrary.core.utility.CollectionUtility;
import net.xdevelopment.xlibrary.schematic.nativeapi.data.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TerritoryPasteTransaction {

    Plugin plugin;
    Location origin;
    List<List<WorldBlock>> batches;
    int maxOperationsPerTick;

    List<UndoEntry> undoEntries = new ArrayList<>();

    @Getter
    @NonFinal
    int batchIndex = 0;

    public TerritoryPasteTransaction(
            @NotNull Plugin plugin,
            @NotNull Location origin,
            @NotNull List<WorldBlock> blocks,
            int maxOperationsPerTick
    ) {
        this.plugin = plugin;
        this.origin = origin;
        this.maxOperationsPerTick = maxOperationsPerTick;
        this.batches = CollectionUtility.split(blocks, maxOperationsPerTick);
    }

    public void start(@NotNull CompletableFuture<Object> completion) {
        Bukkit.getScheduler().runTask(plugin, () -> process(completion));
    }

    private void process(CompletableFuture<Object> completion) {
        if (batchIndex >= batches.size()) {
            completion.complete(this);
            return;
        }

        final World world = origin.getWorld();
        final int mx = origin.getBlockX();
        final int my = origin.getBlockY();
        final int mz = origin.getBlockZ();

        for (final WorldBlock wb : batches.get(batchIndex)) {
            final Block block = world.getBlockAt(mx - wb.dx(), my - wb.dy(), mz - wb.dz());

            undoEntries.add(new UndoEntry(
                    block.getX(), block.getY(), block.getZ(),
                    block.getBlockData().getAsString()
            ));
            block.setBlockData(Bukkit.createBlockData(wb.blockData()), false);
        }

        batchIndex++;

        if (batchIndex >= batches.size()) {
            completion.complete(this);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> process(completion), 1L);
        }
    }

    public CompletableFuture<Void> undo() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        final List<List<UndoEntry>> undoBatches = CollectionUtility.split(undoEntries, maxOperationsPerTick);
        Bukkit.getScheduler().runTask(plugin, () -> undoProcess(future, undoBatches, 0));
        return future;
    }

    private void undoProcess(CompletableFuture<Void> completion, List<List<UndoEntry>> undoBatches, int index) {
        if (index >= undoBatches.size()) {
            undoEntries.clear();
            completion.complete(null);
            return;
        }

        final World world = origin.getWorld();

        for (final UndoEntry entry : undoBatches.get(index)) {
            final Block block = world.getBlockAt(entry.x, entry.y, entry.z);
            block.setBlockData(Bukkit.createBlockData(entry.blockData), false);
        }

        final int next = index + 1;
        if (next >= undoBatches.size()) {
            undoEntries.clear();
            completion.complete(null);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> undoProcess(completion, undoBatches, next), 1L);
        }
    }

    public int getBlockCount() {
        return batches.stream().mapToInt(List::size).sum();
    }

    public int getUndoBlockCount() {
        return undoEntries.size();
    }

    private record UndoEntry(int x, int y, int z, String blockData) {}
}
