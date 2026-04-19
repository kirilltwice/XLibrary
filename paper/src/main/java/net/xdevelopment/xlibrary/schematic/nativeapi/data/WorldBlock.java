package net.xdevelopment.xlibrary.schematic.nativeapi.data;

import org.jetbrains.annotations.NotNull;

public record WorldBlock(int dx, int dy, int dz, @NotNull String blockData) {}
