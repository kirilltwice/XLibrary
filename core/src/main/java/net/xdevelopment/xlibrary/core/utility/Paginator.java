package net.xdevelopment.xlibrary.core.utility;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Paginator<T> {

    int pageSize;
    List<List<T>> pages;

    public Paginator(@NotNull Collection<T> objects, int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
        this.pageSize = pageSize;

        List<T> list = (objects instanceof List) ? (List<T>) objects : new ObjectArrayList<>(objects);
        this.pages = partition(list, pageSize);
    }

    @NotNull
    public List<T> getPage(int pageNumber) {
        int index = pageNumber - 1;

        if (index < 0 || index >= pages.size()) {
            return Collections.emptyList();
        }

        return pages.get(index);
    }

    public int getTotalPages() {
        return pages.size();
    }

    public int getStartIndex(int pageNumber) {
        return (pageNumber - 1) * pageSize;
    }

    @NotNull
    private static <T> List<List<T>> partition(@NotNull List<T> list, int size) {
        ObjectArrayList<List<T>> result = new ObjectArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
