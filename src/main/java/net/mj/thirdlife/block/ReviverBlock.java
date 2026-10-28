package net.mj.thirdlife.block;

import net.mj.thirdlife.Lives;
import net.mj.thirdlife.init.ThirdLifePlusModBlocks;
import net.mj.thirdlife.init.ThirdLifePlusModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/** Reviver: charge 0 is the plain block; charges 1-3 look for a dead player inside it every 10 ticks. */
public class ReviverBlock extends Block {
    private final int charge;

    public ReviverBlock(int charge, Properties properties) {
        super(properties);
        this.charge = charge;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(ThirdLifePlusModItems.REVIVER_0.get());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (charge > 0) level.scheduleTick(pos, this, 10);
    }

    // ReviverTickProcedure
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (charge <= 0) return;
        Vec3 center = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(center, center).inflate(1.0), p -> true);
        players.sort(Comparator.comparingDouble(p -> p.distanceToSqr(center)));
        for (Player found : players) {
            if (found instanceof ServerPlayer player && Lives.lives(player) == 0 && player.gameMode() == GameType.SPECTATOR) {
                level.setBlock(pos, ThirdLifePlusModBlocks.REVIVER_0.get().defaultBlockState(), 3);
                // same as "setlives @p <charge>"
                Lives.clearTemporary(player);
                Lives.setLives(player, charge);
                Lives.setGolden(player, 0);
                player.setGameMode(GameType.SURVIVAL);
                player.teleportTo(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
                Lives.updateTeam(player);
                return;
            }
        }
        level.scheduleTick(pos, this, 10);
    }
}
