package net.xdevelopment.xlibrary.utility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ButtonUtility {

    @NotNull
    @NonFinal Component button;

    public ButtonUtility(@NotNull String buttonText) {
        this.button = Component.text(buttonText);
    }

    @NotNull
    public ButtonUtility setHover(@NotNull String textToShow) {
        this.button = this.button.hoverEvent(HoverEvent.showText(Component.text(textToShow)));
        return this;
    }

    @NotNull
    public ButtonUtility setClickEvent(@NotNull String commandString) {
        this.button = this.button.clickEvent(ClickEvent.runCommand(commandString));
        return this;
    }
}
