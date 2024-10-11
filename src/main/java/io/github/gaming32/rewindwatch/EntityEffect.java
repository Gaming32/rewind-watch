package io.github.gaming32.rewindwatch;

public sealed interface EntityEffect {
    enum Simple implements EntityEffect {
        NONE, GRAYSCALE
    }

    record Dissolve(long startTick, long endTick, Type type, boolean in) implements EntityEffect {
        public enum Type {
            TRANSPARENT,
            GRAYSCALE,
            TRANSPARENT_GRAYSCALE
        }
    }
}
