package net.xdevelopment.xlibrary.core.utility;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FutureUtility {

    @NotNull
    public <T> CompletableFuture<List<T>> asList(@NotNull Collection<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
                );
    }
}
