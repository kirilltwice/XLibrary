package net.xdevelopment.xlibrary.core.utility;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.concurrent.ThreadLocalRandom;

import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Randomizer<T> {

    Object2DoubleMap<T> map = new Object2DoubleLinkedOpenHashMap<>();
    @NonFinal double sum = 0.0;

    public void add(T value, double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be > 0");
        }

        double previous = this.map.put(value, weight);
        this.sum -= previous;
        this.sum += weight;
    }

    public T get() {
        if (this.map.isEmpty()) {
            throw new IllegalStateException("Randomizer is empty");
        }

        double random = ThreadLocalRandom.current().nextDouble(this.sum);
        double count = 0.0;

        for (Object2DoubleMap.Entry<T> entry : this.map.object2DoubleEntrySet()) {
            count += entry.getDoubleValue();
            if (random <= count) {
                return entry.getKey();
            }
        }

        return null;
    }

    public void clear() {
        this.map.clear();
        this.sum = 0.0;
    }
}
