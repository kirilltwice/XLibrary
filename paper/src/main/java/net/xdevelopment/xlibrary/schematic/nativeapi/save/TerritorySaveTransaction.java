package net.xdevelopment.xlibrary.schematic.nativeapi.save;

import com.google.gson.Gson;
import net.xdevelopment.xlibrary.schematic.nativeapi.data.JsonLocation;
import net.xdevelopment.xlibrary.schematic.nativeapi.data.JsonSchematicData;
import net.xdevelopment.xlibrary.schematic.nativeapi.data.WorldBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TerritorySaveTransaction {

    final Plugin plugin;
    final Gson gson;
    final File file;
    final Location corner1;
    final Location corner2;
    final boolean ignoreAir;
    final int maxOperationsPerTick;

    final List<WorldBlock> collectedBlocks = new ArrayList<>();
    @NonFinal int currentX;
    @NonFinal int currentY;
    @NonFinal int currentZ;
    final int minX, minY, minZ, maxX, maxY, maxZ;
    final int midX, midY, midZ;
    final World world;

    public TerritorySaveTransaction(
            @NotNull Plugin plugin,
            @NotNull Gson gson,
            @NotNull File file,
            @NotNull Location corner1,
            @NotNull Location corner2,
            boolean ignoreAir,
            int maxOperationsPerTick
    ) {
        this.plugin = plugin;
        this.gson = gson;
        this.file = file;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.ignoreAir = ignoreAir;
        this.maxOperationsPerTick = maxOperationsPerTick;

        this.world = corner1.getWorld();
        this.minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        this.minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        this.minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        this.maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        this.maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        this.maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        this.midX = (minX + maxX) / 2;
        this.midY = (minY + maxY) / 2;
        this.midZ = (minZ + maxZ) / 2;

        this.currentX = minX;
        this.currentY = minY;
        this.currentZ = minZ;
    }

    public void start(@NotNull CompletableFuture<File> completion) {
        Bukkit.getScheduler().runTask(plugin, () -> snapshotTick(completion));
    }

    private void snapshotTick(CompletableFuture<File> completion) {
        try {
            int operations = 0;

            while (currentX <= maxX) {
                while (currentY <= maxY) {
                    while (currentZ <= maxZ) {
                        final Block block = world.getBlockAt(currentX, currentY, currentZ);

                        if (!(ignoreAir && block.getType().isAir())) {
                            collectedBlocks.add(new WorldBlock(
                                    midX - currentX,
                                    midY - currentY,
                                    midZ - currentZ,
                                    block.getBlockData().getAsString()
                            ));
                        }

                        currentZ++;
                        operations++;

                        if (operations >= maxOperationsPerTick) {
                            Bukkit.getScheduler().runTask(plugin, () -> snapshotTick(completion));
                            return;
                        }
                    }
                    currentZ = minZ;
                    currentY++;
                }
                currentY = minY;
                currentX++;
            }

            writeAsync(completion);
        } catch (Exception exception) {
            completion.completeExceptionally(exception);
        }
    }

    private void writeAsync(CompletableFuture<File> completion) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                final JsonSchematicData dataModel = new JsonSchematicData(
                        new JsonLocation(
                                midX - corner1.getBlockX(),
                                midY - corner1.getBlockY(),
                                midZ - corner1.getBlockZ(),
                                world.getName()
                        ),
                        new JsonLocation(
                                midX - corner2.getBlockX(),
                                midY - corner2.getBlockY(),
                                midZ - corner2.getBlockZ(),
                                world.getName()
                        ),
                        new JsonLocation(midX, midY, midZ, world.getName()),
                        collectedBlocks
                );

                Files.writeString(file.toPath(), gson.toJson(dataModel));
                completion.complete(file);
            } catch (Exception exception) {
                completion.completeExceptionally(exception);
            }
        });
    }
}
