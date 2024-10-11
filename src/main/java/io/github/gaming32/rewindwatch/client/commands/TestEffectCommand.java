package io.github.gaming32.rewindwatch.client.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.gaming32.rewindwatch.EntityEffect;
import io.github.gaming32.rewindwatch.RewindWatchAttachmentTypes;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.command.EnumArgument;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TestEffectCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("testeffect")
            .then(literal("none")
                .executes(ctx -> simple(ctx.getSource().getEntityOrException(), EntityEffect.Simple.NONE))
            )
            .then(literal("grayscale")
                .executes(ctx -> simple(ctx.getSource().getEntityOrException(), EntityEffect.Simple.GRAYSCALE))
            )
            .then(literal("dissolve")
                .then(argument("time", TimeArgument.time(1))
                    .then(argument("type", EnumArgument.enumArgument(EntityEffect.Dissolve.Type.class))
                        .then(literal("in")
                            .executes(ctx -> dissolve(
                                ctx.getSource().getEntityOrException(),
                                ctx.getSource().getUnsidedLevel(),
                                IntegerArgumentType.getInteger(ctx, "time"),
                                ctx.getArgument("type", EntityEffect.Dissolve.Type.class),
                                true
                            ))
                        )
                        .then(literal("out")
                            .executes(ctx -> dissolve(
                                ctx.getSource().getEntityOrException(),
                                ctx.getSource().getUnsidedLevel(),
                                IntegerArgumentType.getInteger(ctx, "time"),
                                ctx.getArgument("type", EntityEffect.Dissolve.Type.class),
                                false
                            ))
                        )
                    )
                )
            )
        );
    }

    private static int simple(Entity player, EntityEffect.Simple effect) {
        player.setData(RewindWatchAttachmentTypes.ENTITY_EFFECT, effect);
        return Command.SINGLE_SUCCESS;
    }

    private static int dissolve(Entity player, Level level, int ticks, EntityEffect.Dissolve.Type type, boolean in) {
        final var startTime = level.getGameTime();
        player.setData(RewindWatchAttachmentTypes.ENTITY_EFFECT, new EntityEffect.Dissolve(startTime, startTime + ticks, type, in));
        return Command.SINGLE_SUCCESS;
    }
}
