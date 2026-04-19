package net.xdevelopment.xlibrary.registry;

import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;

import org.bukkit.NamespacedKey;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author Anyachkaa
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SimpleRegistry<T> implements Registry<T> {

    Map<NamespacedKey, Holder<T>> key2value = new HashMap<>();
    Map<T, NamespacedKey> value2key = new IdentityHashMap<>();

    public void register(@NotNull NamespacedKey key, @NotNull T value) {
        Holder<T> holder = new Holder<>(value, key);
        key2value.put(key, holder);
        value2key.put(value, key);
    }

    @Override
    public @Nullable NamespacedKey getKey(T value) {
        return value2key.get(value);
    }

    @Override
    public @Nullable Holder<T> get(@NotNull NamespacedKey key) {
        return key2value.get(key);
    }

    @Override
    public @NotNull NamespacedKey getKey(@NotNull Holder<T> holder) {
        return holder.getKey();
    }

    @Override
    public int size() {
        return key2value.size();
    }

    @Override
    public @NotNull Stream<Holder<T>> stream() {
        return key2value.values().stream();
    }

    @Override
    public @NotNull Stream<NamespacedKey> keyStream() {
        return key2value.keySet().stream();
    }

    @Override
    public @NotNull Iterator<Holder<T>> iterator() {
        return key2value.values().iterator();
    }

    @Override
    public boolean hasTag(@NotNull TagKey<Holder<T>> key) {
        return false;
    }

    @Override
    public @Nullable Tag<Holder<T>> getTag(@NotNull TagKey<Holder<T>> key) {
        return null;
    }

    @Override
    public @NotNull @Unmodifiable Collection<Tag<Holder<T>>> getTags() {
        return List.of();
    }
}
