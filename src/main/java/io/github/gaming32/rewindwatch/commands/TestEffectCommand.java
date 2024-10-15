package io.github.gaming32.rewindwatch.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.gaming32.rewindwatch.EntityEffect;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.server.command.EnumArgument;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TestEffectCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("testeffect")
            .then(literal("none")
                .executes(ctx -> simple(ctx, EntityEffect.Simple.NONE))
            )
            .then(literal("grayscale")
                .executes(ctx -> simple(ctx, EntityEffect.Simple.GRAYSCALE))
            )
            .then(literal("dissolve")
                .then(argument("time", TimeArgument.time(1))
                    .then(argument("type", EnumArgument.enumArgument(EntityEffect.Dissolve.Type.class))
                        .then(literal("in")
                            .executes(ctx -> dissolve(ctx, true))
                        )
                        .then(literal("out")
                            .executes(ctx -> dissolve(ctx, false))
                        )
                    )
                )
            )
        );
    }

    private static int simple(
        CommandContext<CommandSourceStack> context, EntityEffect.Simple effect
    ) throws CommandSyntaxException {
        return apply(context, effect);
    }

    private static int dissolve(
        CommandContext<CommandSourceStack> context, boolean in
    ) throws CommandSyntaxException {
        final var startTime = context.getSource().getLevel().getGameTime();
        return apply(context, new EntityEffect.Dissolve(
            startTime,
            startTime + IntegerArgumentType.getInteger(context, "time"),
            context.getArgument("type", EntityEffect.Dissolve.Type.class),
            in
        ));
    }

    private static int apply(
        CommandContext<CommandSourceStack> context, EntityEffect effect
    ) throws CommandSyntaxException {
        if (!(context.getSource().getEntityOrException() instanceof LivingEntity living)) {
            context.getSource().sendFailure(Component.literal("Not run from a living entity"));
            return 0;
        }
        RWAttachments.setEntityEffect(living, effect);
        return Command.SINGLE_SUCCESS;
    }
}
