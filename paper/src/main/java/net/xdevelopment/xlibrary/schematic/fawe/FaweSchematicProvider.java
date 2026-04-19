package net.xdevelopment.xlibrary.schematic.fawe;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.mask.InverseSingleBlockTypeMask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.xdevelopment.xlibrary.schematic.SchematicProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.CompletableFuture;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FaweSchematicProvider implements SchematicProvider {

    @Override
    public boolean init() {
        return Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
    }

    @Override
    @NotNull
    public CompletableFuture<Object> paste(@NotNull File file, @NotNull Location location) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!file.exists()) return null;

                ClipboardFormat format = ClipboardFormats.findByFile(file);
                if (format == null) return null;

                World world = FaweAPI.getWorld(location.getWorld().getName());
                Clipboard clipboard;
                try (FileInputStream is = new FileInputStream(file)) {
                    clipboard = format.getReader(is).read();
                }

                EditSession es = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
                ClipboardHolder holder = new ClipboardHolder(clipboard);

                Operation operation = holder.createPaste(es)
                        .maskSource(new InverseSingleBlockTypeMask(clipboard, BlockTypes.SPONGE))
                        .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .build();

                Operations.complete(operation);
                es.close();

                return es;
            } catch (Exception exception) {
                return null;
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<File> save(@NotNull File file, @NotNull Location corner1, @NotNull Location corner2, boolean ignoreAir) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                World world = FaweAPI.getWorld(corner1.getWorld().getName());

                BlockVector3 min = BlockVector3.at(corner1.getX(), corner1.getY(), corner1.getZ());
                BlockVector3 max = BlockVector3.at(corner2.getX(), corner2.getY(), corner2.getZ());
                CuboidRegion region = new CuboidRegion(world, min, max);

                BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                try (EditSession es = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
                    ForwardExtentCopy copy = new ForwardExtentCopy(es, region, clipboard, region.getMinimumPoint());
                    if (ignoreAir) {
                        copy.setSourceMask(new InverseSingleBlockTypeMask(es, BlockTypes.AIR));
                    }
                    Operations.complete(copy);
                }

                try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
                    writer.write(clipboard);
                }

                return file;
            } catch (Exception exception) {
                return null;
            }
        });
    }

    @Override
    @NotNull
    public CompletableFuture<Void> undo(@NotNull Object session) {
        if (!(session instanceof EditSession es)) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            World world = es.getWorld();
            try (EditSession undoSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
                es.undo(undoSession);
            }
        });
    }
}
