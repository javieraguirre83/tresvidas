package net.mj.thirdlife.item;

import net.mj.thirdlife.Lives;
import net.mj.thirdlife.client.ClientHooks;
import net.mj.thirdlife.init.ThirdLifePlusModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** "Temporary Life": 5 minutes in which dying does not cost a life. */
public class TemporaryLifeItem extends Item {
    public TemporaryLifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.literal("§8For risky situations"));
    }

    // TemporaryLifeRightClickedInAirProcedure
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        InteractionResult result = super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);

        if (Lives.temporary(player)) {
            Lives.actionBar(player, "Cannot Stack Temporary Lives!");
        } else {
            Lives.sound(level, player.blockPosition(), SoundEvents.HORSE_AMBIENT, SoundSource.PLAYERS);
            if (!level.isClientSide()) {
                player.addEffect(new MobEffectInstance(ThirdLifePlusModMobEffects.temporaryLife(), 6000, 0, false, true));
            }
            if (level.isClientSide()) ClientHooks.itemActivation(new ItemStack(this));
            if (!player.isCreative()) stack.shrink(1);
            player.swing(hand, true);
        }
        return result;
    }
}
