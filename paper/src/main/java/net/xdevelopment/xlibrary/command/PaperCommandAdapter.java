package net.xdevelopment.xlibrary.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.xdevelopment.xlibrary.command.annotation.OnlyPlayer;
import net.xdevelopment.xlibrary.core.utility.CollectionUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
final class PaperCommandAdapter implements BasicCommand {

    Command command;
    boolean onlyPlayer;

    PaperCommandAdapter(@NotNull Command command) {
        this.command = command;
        this.onlyPlayer = command.getClass().isAnnotationPresent(OnlyPlayer.class);
    }

    @Override
    public void execute(@NotNull CommandSourceStack sourceStack, @NotNull String[] args) {
        final CommandSender sender = sourceStack.getSender();

        if (args.length > 0) {
            ArgumentCommand current = command.findArgumentCommand(args[0]);
            int depth = 1;

            while (current != null && depth < args.length) {
                final ArgumentCommand nested = current.findArgumentCommand(args[depth]);
                if (nested == null) {
                    break;
                }
                current = nested;
                depth++;
            }

            if (current != null) {
                if (current.getClass().isAnnotationPresent(OnlyPlayer.class) && !(sender instanceof Player)) {
                    sender.sendMessage(Component.text("This command is only for players."));
                    return;
                }

                final String permission = current.getPermission();
                if (permission != null && !sender.hasPermission(permission)) {
                    sender.sendMessage(Component.text("You don't have permission to use this command."));
                    return;
                }

                current.execute(new CommandContext(sourceStack, args[depth - 1], depth, args));
                return;
            }
        }

        if (onlyPlayer && !(sender instanceof Player)) {
            sender.sendMessage(Component.text("This command is only for players."));
            return;
        }

        command.execute(new CommandContext(sourceStack, command.getName(), args));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack sourceStack, @NotNull String[] args) {
        if (args.length == 0) {
            return command.tabComplete(new CommandContext(sourceStack, command.getName(), args));
        }

        Map<String, ArgumentCommand> currentArgs = command.getArgumentCommands();
        ArgumentCommand resolved = null;
        int depth = 0;

        while (depth < args.length - 1) {
            final ArgumentCommand next = currentArgs.get(args[depth].toLowerCase());
            if (next == null) {
                break;
            }
            resolved = next;
            currentArgs = next.getArgumentCommands();
            depth++;
        }

        final String input = args[args.length - 1];
        final List<String> suggestions = new ArrayList<>(CollectionUtility.getSequentialMatches(
                new ArrayList<>(currentArgs.keySet()), input));

        final List<String> contextSuggestions;
        if (resolved != null) {
            contextSuggestions = resolved.tabComplete(new CommandContext(sourceStack, args[depth - 1], depth, args));
        } else {
            contextSuggestions = command.tabComplete(new CommandContext(sourceStack, command.getName(), args));
        }

        suggestions.addAll(CollectionUtility.getSequentialMatches(contextSuggestions, input));
        return suggestions;
    }

    @Override
    public boolean canUse(final @NotNull CommandSender sender) {
        if (onlyPlayer && !(sender instanceof Player)) {
            return false;
        }
        final String permission = permission();
        return permission == null || sender.hasPermission(permission);
    }

    @Override
    public @Nullable String permission() {
        return command.getPermission();
    }
}
