package net.mj.thirdlife.item;

import net.mj.thirdlife.Lives;
import net.mj.thirdlife.client.ClientHooks;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** "Life": right click on a block to get a life back (max 3). */
public class HeartItem extends Item {
    public HeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.literal("§8A life taken from a player"));
    }

    // HeartRightClickedInAirProcedure
    @Override
    public InteractionResult useOn(UseOnContext context) {
        super.useOn(context);
        Player player = context.getPlayer();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (player == null) return InteractionResult.SUCCESS;

        if (Lives.lives(player) < 3) {
            if (!level.isClientSide()) {
                Lives.setLives(player, Lives.lives(player) + 1);
            }
            if (!player.isCreative()) stack.shrink(1);
            if (level.isClientSide()) ClientHooks.itemActivation(new ItemStack(this));
            Lives.sound(level, context.getClickedPos(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS);
        } else {
            Lives.actionBar(player, "You already have three lives!");
        }
        return InteractionResult.SUCCESS;
    }
}
