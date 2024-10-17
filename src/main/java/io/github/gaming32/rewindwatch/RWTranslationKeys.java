package io.github.gaming32.rewindwatch;

import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.world.level.ItemLike;

public class RWTranslationKeys {
    public static final String REWIND_WATCH_USED = subKey(RewindWatchItems.REWIND_WATCH, "used");
    public static final String REWIND_WATCH_RECALLED = subKey(RewindWatchItems.REWIND_WATCH, "recalled");
    public static final String REWIND_WATCH_VISIBLE = subKey(RewindWatchItems.REWIND_WATCH, "visible");
    public static final String REWIND_WATCH_HIDDEN = subKey(RewindWatchItems.REWIND_WATCH, "hidden");

    private static String subKey(ItemLike item, String subKey) {
        return item.asItem().getDescriptionId() + '.' + subKey;
    }
}
