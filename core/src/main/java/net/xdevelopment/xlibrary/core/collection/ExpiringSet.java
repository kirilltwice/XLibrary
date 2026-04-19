package net.xdevelopment.xlibrary.core.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ExpiringSet<E> extends AbstractSet<E> implements Set<E> {

    long expireMillis;
    Object2LongMap<E> map = new Object2LongOpenHashMap<>();

    public ExpiringSet(long expireMillis) {
        this.expireMillis = expireMillis;
        this.map.defaultReturnValue(-1L);
    }

    @Override
    public boolean add(@NotNull E e) {
        cleanup();
        long previous = this.map.put(e, System.currentTimeMillis());
        return previous == -1L;
    }

    @Override
    public boolean contains(Object o) {
        cleanup();
        return this.map.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        return this.map.removeLong(o) != -1L;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        cleanup();
        return this.map.keySet().iterator();
    }

    @Override
    public int size() {
        cleanup();
        return this.map.size();
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    private void cleanup() {
        long current = System.currentTimeMillis();
        this.map.object2LongEntrySet().removeIf(entry -> current - entry.getLongValue() > expireMillis);
    }
}
