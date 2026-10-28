package net.mj.thirdlife.item;

import net.mj.thirdlife.Lives;
import net.mj.thirdlife.block.ReviverBlock;
import net.mj.thirdlife.client.ClientHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** "Golden Life": gives a normal life, or a golden life (max 3) when you already have 3. Also charges Revivers. */
public class GoldenHeartItem extends Item {
    public GoldenHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.literal("§8Another chance"));
    }

    // GoldenHeartRightClickedInAirProcedure
    @Override
    public InteractionResult useOn(UseOnContext context) {
        super.useOn(context);
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        if (player == null) return InteractionResult.SUCCESS;
        if (level.getBlockState(clicked).getBlock() instanceof ReviverBlock) return InteractionResult.SUCCESS;

        if (Lives.lives(player) < 3) {
            if (!level.isClientSide()) Lives.setLives(player, Lives.lives(player) + 1);
            equip(player, level, stack);
        } else if (Lives.lives(player) == 3) {
            if (Lives.golden(player) != 3) {
                if (!level.isClientSide()) Lives.setGolden(player, Lives.golden(player) + 1);
                equip(player, level, stack);
            } else {
                Lives.actionBar(player, "Your lives are already full.");
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void equip(Player player, Level level, ItemStack stack) {
        if (!player.isCreative()) stack.shrink(1);
        if (level.isClientSide()) ClientHooks.itemActivation(new ItemStack(this));
        player.swing(InteractionHand.MAIN_HAND, true);
        Lives.sound(level, player.blockPosition(), SoundEvents.EVOKER_PREPARE_WOLOLO, SoundSource.PLAYERS);
    }
}
