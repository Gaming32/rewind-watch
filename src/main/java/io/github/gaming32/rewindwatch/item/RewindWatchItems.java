package io.github.gaming32.rewindwatch.item;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RewindWatchItems {
    private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(RewindWatch.MOD_ID);

    public static final DeferredItem<RewindWatchItem> REWIND_WATCH = REGISTER.registerItem(
        "rewind_watch", RewindWatchItem::new, new Item.Properties().stacksTo(1)
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
