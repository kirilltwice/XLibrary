package net.xdevelopment.xlibrary.core.collection;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.xdevelopment.xlibrary.core.utility.Pair;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExpiringMap<K, V> {

    @Getter
    long expireMillis;
    Long2ObjectMap<Pair<K, V>> map = new Long2ObjectOpenHashMap<>();

    @Nullable
    public V get(@Nullable Object key) {
        validate();
        for (Pair<K, V> pair : this.map.values()) {
            if (Objects.equals(pair.getKey(), key)) return pair.getValue();
        }
        return null;
    }

    @Nullable
    public V removeOrDefault(@Nullable Object key, @Nullable V defaultValue) {
        validate();
        Pair<K, V> found = null;
        for (Pair<K, V> kvPair : this.map.values()) {
            if (Objects.equals(kvPair.getKey(), key)) {
                found = kvPair;
                break;
            }
        }
        if (found != null) {
            this.map.values().remove(found);
            return found.getValue();
        }
        return defaultValue;
    }

    @Nullable
    public V getOrDefault(@Nullable Object key, @Nullable V defaultValue) {
        validate();
        V value = get(key);
        return value != null ? value : defaultValue;
    }

    @Nullable
    public V put(@Nullable K key, @Nullable V value) {
        validate();
        this.map.put(System.currentTimeMillis(), new Pair<>(key, value));
        return value;
    }

    @Nullable
    public V putIfAbsent(@Nullable K key, @Nullable V value) {
        validate();
        V existing = get(key);
        if (existing == null) {
            existing = this.put(key, value);
        }
        return existing;
    }

    public int size() {
        validate();
        return this.map.size();
    }

    public boolean containsKey(@Nullable Object key) {
        validate();
        for (Pair<K, V> pair : this.map.values()) {
            if (Objects.equals(pair.getKey(), key)) return true;
        }
        return false;
    }

    public boolean containsValue(@Nullable Object value) {
        validate();
        for (Pair<K, V> pair : this.map.values()) {
            if (Objects.equals(pair.getValue(), value)) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        validate();
        return this.map.isEmpty();
    }

    public void clear() {
        this.map.clear();
    }

    private void validate() {
        long current = System.currentTimeMillis();
        this.map.long2ObjectEntrySet().removeIf(entry -> current - entry.getLongKey() > this.expireMillis);
    }
}
