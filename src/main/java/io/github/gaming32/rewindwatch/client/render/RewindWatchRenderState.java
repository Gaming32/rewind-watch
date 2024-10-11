package io.github.gaming32.rewindwatch.client.render;

public class RewindWatchRenderState {
    private static float dissolveProgress;

    public static float getDissolveOpacity() {
        return dissolveProgress;
    }

    public static void setDissolveOpacity(float progress) {
        dissolveProgress = progress;
    }
}
