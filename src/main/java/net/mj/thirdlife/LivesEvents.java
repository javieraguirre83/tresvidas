package net.mj.thirdlife;

import net.mj.thirdlife.init.ThirdLifePlusModBlocks;
import net.mj.thirdlife.init.ThirdLifePlusModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.items.ItemHandlerHelper;

/** Server-side gameplay of the original mod. */
public final class LivesEvents {

    // ----- LowerLivesAfterRespawnProcedure + DropLifeWhenDefeatedWithJaggedDaggerProcedure -----
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Level level = player.level();
        BlockPos pos = player.blockPosition();

        // Killed with the Jagged Dagger: drops "<name>'s Life"
        if (!Lives.temporary(player) && Lives.golden(player) == 0
                && event.getSource().getEntity() instanceof Player killer
                && killer.getMainHandItem().getItem() == ThirdLifePlusModItems.JAGGED_DAGGER.get()
                && level instanceof ServerLevel serverLevel) {
            ItemEntity drop = new ItemEntity(serverLevel, player.getX(), player.getY(), player.getZ(), Lives.namedHeart(player));
            drop.setPickUpDelay(10);
            serverLevel.addFreshEntity(drop);
        }

        if (Lives.temporary(player)) {
            Lives.clearTemporary(player);
            Lives.sound(level, pos, SoundEvents.HORSE_DEATH, SoundSource.PLAYERS);
        } else if (Lives.golden(player) >= 1 && Lives.golden(player) <= 3) {
            Lives.setGolden(player, Lives.golden(player) - 1);
            Lives.sound(level, pos, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS);
        } else if (Lives.golden(player) == 0) {
            int lives = Lives.lives(player);
            if (lives >= 1 && lives <= 3) {
                Lives.setLives(player, lives - 1);
                Lives.sound(level, pos, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS);
                Lives.updateTeam(player);
            }
            if (Lives.lives(player) == 0) {
                Lives.actionBar(player, "You are dead...");
            }
        }
    }

    public static void onClone(PlayerEvent.Clone event) {
        Lives.copy(event.getOriginal(), event.getEntity());
    }

    // ----- ChangeTeamsOnTickUpdateProcedure + SetToSpectatorProcedure -----
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (!(event.player() instanceof ServerPlayer player)) return;

        if (player.tickCount % 5 == 0) {
            Lives.updateTeam(player);
        }

        if (Lives.lives(player) == 0 && player.gameMode() != GameType.SPECTATOR) {
            player.setGameMode(GameType.SPECTATOR);
            if (Lives.hasPermission4(player)) {
                Lives.actionBar(player, "You don't have any lives left, please type /resetlives @p to reset your lives");
            } else {
                Lives.actionBar(player, "You are dead, find a reviver to respawn.");
            }
        }
    }

    // ----- Reviver0OnBlockRightClickedProcedure -----
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;
        Level level = player.level();
        if (level.isClientSide()) return;
        BlockPos pos = event.getPos();

        // Charge with a Golden Life (main hand first, then off hand)
        for (int charge = 2; charge >= 0; charge--) {
            if (level.getBlockState(pos).getBlock() != ThirdLifePlusModBlocks.byCharge(charge)) continue;
            InteractionHand hand = null;
            if (isChargeItem(player.getMainHandItem())) hand = InteractionHand.MAIN_HAND;
            else if (isChargeItem(player.getOffhandItem())) hand = InteractionHand.OFF_HAND;
            if (hand == null) continue;
            player.swing(hand, true);
            if (!player.isCreative()) player.getItemInHand(hand).shrink(1);
            setReviver(level, pos, charge + 1);
            Lives.sound(level, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS);
        }

        // Sneak + right click: take one charge back
        for (int charge = 1; charge <= 3; charge++) {
            if (level.getBlockState(pos).getBlock() == ThirdLifePlusModBlocks.byCharge(charge) && player.isShiftKeyDown()) {
                player.swing(InteractionHand.MAIN_HAND, true);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ThirdLifePlusModItems.GOLDEN_HEART.get()));
                setReviver(level, pos, charge - 1);
            }
        }
    }

    private static boolean isChargeItem(ItemStack stack) {
        return stack.getItem() == ThirdLifePlusModItems.GOLDEN_HEART.get();
    }

    private static void setReviver(Level level, BlockPos pos, int charge) {
        Block block = ThirdLifePlusModBlocks.byCharge(charge);
        level.setBlock(pos, block.defaultBlockState(), 3);
    }

    private LivesEvents() {}
}
