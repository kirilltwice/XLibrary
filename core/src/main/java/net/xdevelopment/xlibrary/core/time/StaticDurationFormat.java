package net.xdevelopment.xlibrary.core.time;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.Duration;

// Adapted and improved from: https://github.com/BlackBaroness/duration-serializer-java
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class StaticDurationFormat implements DurationFormat {

    String nanosecondsFormat;
    String millisecondsFormat;
    String secondsFormat;
    String minutesFormat;
    String hoursFormat;
    String daysFormat;

    @Override
    public @NotNull String serialize(@NotNull DurationUnit unit, @Range(from = 0, to = Long.MAX_VALUE) long amount) {
        return amount + " " + getDurationFormat(unit);
    }

    @Override
    public @NotNull Duration deserialize(@NotNull String unitInput, @Range(from = 0, to = Long.MAX_VALUE) long amountInput) throws InvalidFormatException {
        return switch (getDurationUnit(unitInput)) {
            case NANOSECONDS -> Duration.ofNanos(amountInput);
            case MILLISECONDS -> Duration.ofMillis(amountInput);
            case SECONDS -> Duration.ofSeconds(amountInput);
            case MINUTES -> Duration.ofMinutes(amountInput);
            case HOURS -> Duration.ofHours(amountInput);
            case DAYS -> Duration.ofDays(amountInput);
        };
    }

    private String getDurationFormat(DurationUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> nanosecondsFormat;
            case MILLISECONDS -> millisecondsFormat;
            case SECONDS -> secondsFormat;
            case MINUTES -> minutesFormat;
            case HOURS -> hoursFormat;
            case DAYS -> daysFormat;
        };
    }

    private DurationUnit getDurationUnit(String unitInput) throws InvalidFormatException {
        if (unitInput.equalsIgnoreCase(nanosecondsFormat)) {
            return DurationUnit.NANOSECONDS;
        } else if (unitInput.equalsIgnoreCase(millisecondsFormat)) {
            return DurationUnit.MILLISECONDS;
        } else if (unitInput.equalsIgnoreCase(secondsFormat)) {
            return DurationUnit.SECONDS;
        } else if (unitInput.equalsIgnoreCase(minutesFormat)) {
            return DurationUnit.MINUTES;
        } else if (unitInput.equalsIgnoreCase(hoursFormat)) {
            return DurationUnit.HOURS;
        } else if (unitInput.equalsIgnoreCase(daysFormat)) {
            return DurationUnit.DAYS;
        } else {
            throw new InvalidFormatException("Unknown duration unit: " + unitInput);
        }
    }
}
