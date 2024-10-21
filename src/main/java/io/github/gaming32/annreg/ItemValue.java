package io.github.gaming32.annreg;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface ItemValue<I extends Item> extends RegValue<Item, I>, ItemLike permits RegValueImpl.ItemValueImpl {
    static <I extends Item> ItemValue<I> of(Supplier<I> value) {
        return new RegValueImpl.ItemValueImpl<>(value);
    }

    static <I extends Item> ItemValue<I> of(Function<Item.Properties, I> creator, Item.Properties properties) {
        RegValueImpl.validateRegisterFor(creator);
        return of(() -> creator.apply(properties));
    }

    static <I extends Item> ItemValue<I> of(Function<Item.Properties, I> creator) {
        RegValueImpl.validateRegisterFor(creator);
        return of(() -> creator.apply(new Item.Properties()));
    }

    static <I extends BlockItem, B extends Block> ItemValue<I> ofBlock(
        Supplier<B> block,
        BiFunction<B, Item.Properties, I> creator,
        Item.Properties properties
    ) {
        RegValueImpl.validateRegisterFor(creator);
        return of(() -> creator.apply(block.get(), properties));
    }

    static <I extends BlockItem, B extends Block> ItemValue<I> ofBlock(
        Supplier<B> block,
        BiFunction<B, Item.Properties, I> creator
    ) {
        RegValueImpl.validateRegisterFor(creator);
        return of(() -> creator.apply(block.get(), new Item.Properties()));
    }

    @Override
    DeferredItem<I> holder();

    @NotNull
    @Override
    default Item asItem() {
        return get();
    }
}
