package net.xdevelopment.xlibrary.command;

import net.xdevelopment.xlibrary.core.Identifiable;
import net.xdevelopment.xlibrary.command.annotation.CommandArgument;
import net.xdevelopment.xlibrary.command.annotation.CommandPermission;

import lombok.Getter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

/**
 * @author Anyachkaa
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class ArgumentCommand implements CommandExecutor, Identifiable {

    UUID uniqueId = generateUniqueId();
    String name;

    @Nullable
    String permission;

    Map<String, ArgumentCommand> argumentCommands = new HashMap<>();

    protected ArgumentCommand() {
        final var clazz = getClass();

        final var argumentAnnotation = clazz.getAnnotation(CommandArgument.class);
        if (argumentAnnotation == null) {
            throw new IllegalStateException(clazz.getSimpleName() + " must be annotated with @CommandArgument");
        }
        this.name = argumentAnnotation.value();

        final var permissionAnnotation = clazz.getAnnotation(CommandPermission.class);
        this.permission = permissionAnnotation != null ? permissionAnnotation.value() : null;
    }

    @NotNull
    public ArgumentCommand addArgument(@NotNull ArgumentCommand argumentCommand) {
        argumentCommands.put(argumentCommand.getName().toLowerCase(), argumentCommand);
        return this;
    }

    @Nullable
    public ArgumentCommand findArgumentCommand(@NotNull String name) {
        return argumentCommands.get(name.toLowerCase());
    }

    public List<String> tabComplete(@NotNull CommandContext context) {
        return List.of();
    }
}
