package io.github.gaming32.rewindwatch.item;

import io.github.gaming32.annreg.ItemValue;
import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = Item.class)
public class RewindWatchItems {
    public static final ItemValue<RewindWatchItem> REWIND_WATCH = ItemValue.of(
        RewindWatchItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );
}
