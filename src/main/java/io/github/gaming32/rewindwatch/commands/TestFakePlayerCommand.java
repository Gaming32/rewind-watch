package io.github.gaming32.rewindwatch.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import static net.minecraft.commands.Commands.literal;

public class TestFakePlayerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("testfakeplayer")
            .executes(TestFakePlayerCommand::spawnFakePlayer)
        );
    }

    private static int spawnFakePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (!(context.getSource().getEntityOrException() instanceof LivingEntity living)) {
            context.getSource().sendFailure(Component.literal("Not run from a living entity"));
            return 0;
        }
        final var level = context.getSource().getLevel();
        final var newEntity = RewindWatchEntityTypes.FAKE_PLAYER.get().create(level);
        if (newEntity == null) {
            context.getSource().sendFailure(Component.literal("Failed to create entity"));
            return 0;
        }
        newEntity.copyInformationFrom(living);
        level.addFreshEntity(newEntity);
        return Command.SINGLE_SUCCESS;
    }
}
