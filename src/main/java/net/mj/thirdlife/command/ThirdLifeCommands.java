package net.mj.thirdlife.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mj.thirdlife.Lives;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;

/** /addlives, /setlives, /takelives, /resetlives — same names, arguments and messages as the original. */
public final class ThirdLifeCommands {

    public static void register(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("addlives").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("add_name", EntityArgument.players())
                        .then(Commands.argument("add_amount", DoubleArgumentType.doubleArg(1.0, 2.0))
                                .executes(ThirdLifeCommands::addLives))));

        dispatcher.register(Commands.literal("setlives").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("set_name", EntityArgument.players())
                        .then(Commands.argument("set_amount", DoubleArgumentType.doubleArg(0.0, 6.0))
                                .executes(ThirdLifeCommands::setLives))));

        dispatcher.register(Commands.literal("takelives").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("take_name", EntityArgument.players())
                        .then(Commands.argument("take_amount", DoubleArgumentType.doubleArg(1.0, 2.0))
                                .executes(ThirdLifeCommands::takeLives))));

        dispatcher.register(Commands.literal("resetlives").requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
                .then(Commands.argument("reset_name", EntityArgument.players())
                        .executes(ThirdLifeCommands::resetLives)));
    }

    /** Message to whoever ran the command (action bar or chat, like the original). */
    private static void tell(CommandSourceStack source, String text, boolean actionBar) {
        Entity entity = source.getEntity();
        if (entity instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal(text), actionBar);
        } else {
            source.sendSystemMessage(Component.literal(text));
        }
    }

    private static int addLives(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int amount = (int) DoubleArgumentType.getDouble(ctx, "add_amount");
        for (ServerPlayer target : EntityArgument.getPlayers(ctx, "add_name")) {
            if (!(Lives.lives(target) >= 3)) {
                Lives.setLives(target, Lives.lives(target) + amount);
                Lives.updateTeam(target);
            } else {
                tell(ctx.getSource(), "target(s) already have full lives!", false);
            }
        }
        return 0;
    }

    private static int setLives(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        double amount = DoubleArgumentType.getDouble(ctx, "set_amount");
        for (ServerPlayer target : EntityArgument.getPlayers(ctx, "set_name")) {
            if (amount <= 3.0) {
                Lives.clearTemporary(target);
                Lives.setLives(target, (int) amount);
                Lives.setGolden(target, 0);
            } else {
                Lives.setLives(target, 3);
                Lives.setGolden(target, (int) (amount - 3.0));
            }
            Lives.updateTeam(target);
        }
        return 0;
    }

    private static int takeLives(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        int amount = (int) DoubleArgumentType.getDouble(ctx, "take_amount");
        for (ServerPlayer target : EntityArgument.getPlayers(ctx, "take_name")) {
            if (Lives.lives(target) != 0 && Lives.golden(target) == 0) {
                Lives.setLives(target, Lives.lives(target) - amount);
                Lives.updateTeam(target);
            } else if (Lives.golden(target) > 0) {
                tell(ctx.getSource(), "target(s) have golden lives, please use /setlives instead.", false);
            } else {
                tell(ctx.getSource(), "target(s) doesn't have enough lives.", false);
            }
        }
        return 0;
    }

    private static int resetLives(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        for (ServerPlayer target : EntityArgument.getPlayers(ctx, "reset_name")) {
            if (Lives.lives(target) != 3 || Lives.golden(target) != 0 || Lives.temporary(target)) {
                Lives.clearTemporary(target);
                Lives.setLives(target, 3);
                Lives.setGolden(target, 0);
                tell(ctx.getSource(), "Target reset.", true);
                if (target.gameMode() == GameType.SPECTATOR) {
                    BlockPos spawn = target.level().getRespawnData().pos();
                    target.teleportTo(spawn.getX(), spawn.getY(), spawn.getZ());
                    target.setGameMode(Lives.hasPermission4(target) ? GameType.CREATIVE : GameType.SURVIVAL);
                }
                Lives.updateTeam(target);
            } else {
                tell(ctx.getSource(), "Target is already normal!", true);
            }
        }
        return 0;
    }

    private ThirdLifeCommands() {}
}
