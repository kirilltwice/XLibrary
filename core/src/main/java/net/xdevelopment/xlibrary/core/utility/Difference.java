package net.xdevelopment.xlibrary.core.utility;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import lombok.ToString;

@ToString
public class Difference<T> {

    private final LinkedHashSet<Change<T>> changes = new LinkedHashSet<>();

    @NotNull
    public Set<Change<T>> getChanges() {
        return this.changes;
    }

    public boolean isEmpty() {
        return this.changes.isEmpty();
    }

    @NotNull
    public Set<T> getChanges(@NotNull ChangeType type) {
        return this.changes.stream()
                .filter(change -> change.type() == type)
                .map(Change::value)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @NotNull
    public Set<T> getAdded() {
        return getChanges(ChangeType.ADD);
    }

    @NotNull
    public Set<T> getRemoved() {
        return getChanges(ChangeType.REMOVE);
    }

    public void clear() {
        this.changes.clear();
    }

    private void recordChange(@NotNull Change<T> change) {
        if (this.changes.remove(change.inverse())) {
            return;
        }
        this.changes.add(change);
    }

    public void recordChange(@NotNull ChangeType type, @NotNull T value) {
        recordChange(new Change<>(type, value));
    }

    public void recordChanges(@NotNull ChangeType type, @NotNull Iterable<T> values) {
        for (T value : values) {
            recordChange(new Change<>(type, value));
        }
    }

    @NotNull
    public Difference<T> mergeFrom(@NotNull Difference<T> other) {
        for (Change<T> change : other.changes) {
            recordChange(change);
        }
        return this;
    }

    public record Change<T>(@NotNull ChangeType type, @NotNull T value) {
        @NotNull
        public Change<T> inverse() {
            return new Change<>(this.type.inverse(), this.value);
        }

        @Override
        public String toString() {
            return "(" + this.type + ": " + this.value + ')';
        }
    }

    public enum ChangeType {
        ADD, REMOVE;

        @NotNull
        public ChangeType inverse() {
            return this == ADD ? REMOVE : ADD;
        }
    }
}
