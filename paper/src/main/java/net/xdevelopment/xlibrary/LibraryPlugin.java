package net.xdevelopment.xlibrary;

import net.xdevelopment.xlibrary.command.SimpleCommandManager;
import net.xdevelopment.xlibrary.schematic.SchematicManager;
import net.xdevelopment.xlibrary.schematic.command.SchematicCommand;
import net.xdevelopment.xlibrary.schematic.nativeapi.NativeSchematicProvider;
import net.xdevelopment.xlibrary.schematic.selection.SelectionListener;
import net.xdevelopment.xlibrary.utility.gui.MenuListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class LibraryPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final File schematicsFolder = createSchematicsFolder();
        final SelectionListener selectionListener = new SelectionListener();
        final SchematicManager schematicManager = new SchematicManager(new NativeSchematicProvider(this));

        registerListeners(selectionListener);
        registerCommands(schematicManager, selectionListener, schematicsFolder);
    }

    @NotNull
    private File createSchematicsFolder() {
        final File schematicsFolder = new File(getDataFolder(), "schematics");
        schematicsFolder.mkdirs();
        return schematicsFolder;
    }

    private void registerListeners(@NotNull SelectionListener selectionListener) {
        getServer().getPluginManager().registerEvents(selectionListener, this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
    }

    private void registerCommands(
            @NotNull SchematicManager schematicManager,
            @NotNull SelectionListener selectionListener,
            @NotNull File schematicsFolder
    ) {
        final SimpleCommandManager commandManager = new SimpleCommandManager(this);
        commandManager.register(new SchematicCommand(schematicManager, selectionListener, schematicsFolder));
    }
}
