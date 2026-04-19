package net.xdevelopment.xlibrary.schematic.nativeapi.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record JsonSchematicData(
        @NotNull JsonLocation firstPoint,
        @NotNull JsonLocation secondPoint,
        @NotNull JsonLocation midPoint,
        @NotNull List<WorldBlock> blocks
) {}
