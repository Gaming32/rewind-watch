package io.github.gaming32.annreg;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface BlockValue<B extends Block> extends RegValue<Block, B>, ItemLike permits RegValueImpl.BlockValueImpl {
    static <B extends Block> BlockValue<B> of(Supplier<B> value) {
        return new RegValueImpl.BlockValueImpl<>(value);
    }

    static <B extends Block> BlockValue<B> of(
        Function<BlockBehaviour.Properties, B> creator,
        BlockBehaviour.Properties properties
    ) {
        RegValueImpl.validateRegisterFor(creator);
        return of(() -> creator.apply(properties));
    }

    @Override
    DeferredBlock<B> holder();

    @NotNull
    @Override
    default Item asItem() {
        return get().asItem();
    }
}
