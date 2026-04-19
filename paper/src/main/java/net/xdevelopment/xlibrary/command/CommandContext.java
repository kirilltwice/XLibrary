package net.xdevelopment.xlibrary.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.xdevelopment.xlibrary.utility.ColorUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author Anyachkaa
 */
public record CommandContext(
        @NotNull CommandSender sender,
        @NotNull String name,
        int argumentOffset,
        @NotNull String[] arguments,
        @Nullable CommandSourceStack sourceStack
) implements ArgumentProvider {

    public CommandContext(
            @NotNull CommandSender sender,
            @NotNull String name,
            int argumentOffset,
            @NotNull String[] arguments,
            @Nullable CommandSourceStack sourceStack
    ) {
        this.sender = sender;
        this.name = name;
        this.argumentOffset = argumentOffset;
        this.arguments = arguments;
        this.sourceStack = sourceStack;
    }

    CommandContext(
            @NotNull CommandSender sender,
            @NotNull String name,
            @NotNull String[] arguments
    ) {
        this(sender, name, 0, arguments, null);
    }

    CommandContext(
            @NotNull CommandSourceStack sourceStack,
            @NotNull String name,
            int argumentOffset,
            @NotNull String[] arguments
    ) {
        this(sourceStack.getSender(), name, argumentOffset, arguments, sourceStack);
    }

    CommandContext(
            @NotNull CommandSourceStack sourceStack,
            @NotNull String name,
            @NotNull String[] arguments
    ) {
        this(sourceStack, name, 0, arguments);
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }

    public boolean hasSourceStack() {
        return sourceStack != null;
    }

    @NotNull
    public CommandSourceStack getSourceStack() {
        if (sourceStack == null) {
            throw new IllegalStateException("CommandSourceStack is not available");
        }
        return sourceStack;
    }

    @NotNull
    public Player getPlayer() {
        if (!(sender instanceof Player player)) {
            throw new IllegalStateException("Sender is not a player");
        }
        return player;
    }

    @Override
    @NotNull
    public String getArgument(int i) {
        return arguments[i + argumentOffset];
    }

    @Override
    public int argumentCount() {
        return arguments.length - argumentOffset;
    }

    public void sendMessage(@NotNull String message) {
        sender.sendMessage(ColorUtility.colorize(message));
    }

    public void sendMessage(@NotNull String message, @NotNull Map<String, Object> replacements) {
        sender.sendMessage(ColorUtility.colorize(message, replacements));
    }
}
