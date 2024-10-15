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
                    .executes(TestMovementLockCommand::lockMovement)
                )
                .then(literal("unlock")
                    .executes(TestMovementLockCommand::unlockMovement)
                )
            )
        );
    }

    private static int lockMovement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        RWAttachments.lockMovement(context.getSource().getPlayerOrException());
        return Command.SINGLE_SUCCESS;
    }

    private static int unlockMovement(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        RWAttachments.unlockMovement(context.getSource().getPlayerOrException());
        return Command.SINGLE_SUCCESS;
    }
}
