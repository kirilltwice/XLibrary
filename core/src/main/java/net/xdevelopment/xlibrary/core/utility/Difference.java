package net.xdevelopment.xlibrary.core.utility;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Difference<T> {

    @Getter
    ObjectLinkedOpenHashSet<Change<T>> changes = new ObjectLinkedOpenHashSet<>();

    public boolean isEmpty() {
        return this.changes.isEmpty();
    }

    @NotNull
    public ObjectLinkedOpenHashSet<T> getChanges(@NotNull ChangeType type) {
        ObjectLinkedOpenHashSet<T> result = new ObjectLinkedOpenHashSet<>();
        for (Change<T> change : this.changes) {
            if (change.type() == type) {
                result.add(change.value());
            }
        }
        return result;
    }

    @NotNull
    public ObjectLinkedOpenHashSet<T> getAdded() {
        return getChanges(ChangeType.ADD);
    }

    @NotNull
    public ObjectLinkedOpenHashSet<T> getRemoved() {
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
