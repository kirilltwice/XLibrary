package net.xdevelopment.xlibrary.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Anyachkaa
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SimpleCommandManager implements CommandManager {

    JavaPlugin plugin;
    @NonFinal
    @Nullable
    Commands registrar;

    Set<Command> commands = new HashSet<>();
    Map<String, Command> name2CommandMap = new HashMap<>();

    public SimpleCommandManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            registrar = event.registrar();
            for (final Command command : commands) {
                registerPaperCommand(command);
            }
        });
    }

    @Override
    @Nullable
    public Command findCommand(@NotNull String name) {
        return name2CommandMap.get(name.toLowerCase());
    }

    @Override
    public boolean register(@NotNull Command command) {
        if (!commands.add(command)) return false;

        name2CommandMap.put(command.getName().toLowerCase(), command);
        for (final String alias : command.getAliases()) {
            name2CommandMap.put(alias.toLowerCase(), command);
        }

        if (registrar != null) {
            registerPaperCommand(command);
        }
        return true;
    }

    @Override
    public boolean unregister(@NotNull Command command) {
        if (!commands.remove(command)) return false;

        name2CommandMap.remove(command.getName().toLowerCase(), command);
        for (final String alias : command.getAliases()) {
            name2CommandMap.remove(alias.toLowerCase(), command);
        }

        return true;
    }

    @Override
    public boolean unregister(@NotNull String name) {
        final Command command = name2CommandMap.get(name.toLowerCase());
        return command != null && unregister(command);
    }

    @Override
    public @NotNull @Unmodifiable List<Command> getCommands() {
        return List.copyOf(commands);
    }

    private void registerPaperCommand(@NotNull Command command) {
        if (registrar == null) {
            return;
        }
        registrar.register(command.getName(), null, command.getAliases(), new PaperCommandAdapter(command));
    }
}
