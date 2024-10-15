package io.github.gaming32.rewindwatch.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class TestMovementLockCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("testmovementlock")
            .then(argument("player", EntityArgument.players())
                .then(literal("lock")
                    .executes(ctx -> lockMovement(ctx, true))
                )
                .then(literal("unlock")
                    .executes(ctx -> lockMovement(ctx, false))
                )
            )
        );
    }

    private static int lockMovement(
        CommandContext<CommandSourceStack> context, boolean lock
    ) throws CommandSyntaxException {
        RWAttachments.lockMovement(context.getSource().getPlayerOrException(), lock);
        return Command.SINGLE_SUCCESS;
    }
}
