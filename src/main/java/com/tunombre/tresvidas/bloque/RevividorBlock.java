package com.tunombre.tresvidas.bloque;

import com.tunombre.tresvidas.Registro;
import com.tunombre.tresvidas.Vidas;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

/**
 * Revividor.
 * 1. Un jugador vivo lo carga con Vidas Doradas (clic derecho, hasta 3).
 * 2. Un jugador eliminado (espectador) vuela dentro del bloque.
 * 3. Revive con tantas vidas como cargas tenía el bloque.
 */
public class RevividorBlock extends Block {

    public static final IntegerProperty CARGA = IntegerProperty.create("carga", 0, 3);

    public RevividorBlock(Properties propiedades) {
        super(propiedades);
        registerDefaultState(stateDefinition.any().setValue(CARGA, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CARGA);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Registro.VIDA_DORADA.get())) return InteractionResult.TRY_WITH_EMPTY_HAND;

        int carga = state.getValue(CARGA);
        if (carga >= 3) {
            if (player instanceof ServerPlayer p) {
                p.sendSystemMessage(Component.literal("§eEl Revividor ya está completamente cargado."));
            }
            return InteractionResult.FAIL;
        }

        if (level instanceof ServerLevel servidor) {
            servidor.setBlock(pos, state.setValue(CARGA, carga + 1), Block.UPDATE_ALL);
            stack.consume(1, player);
            servidor.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
            servidor.scheduleTick(pos, this, 20); // empieza a buscar jugadores eliminados
        }
        return InteractionResult.SUCCESS;
    }

    /** Cada segundo, mientras tenga carga, busca a un jugador eliminado dentro del bloque. */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int carga = state.getValue(CARGA);
        if (carga <= 0) return;

        AABB zona = new AABB(pos).inflate(1.0);
        List<ServerPlayer> eliminados = level.getPlayers(
                p -> p.isSpectator() && Vidas.vidas(p) <= 0 && zona.contains(p.position()));

        if (!eliminados.isEmpty()) {
            Vidas.revivir(eliminados.get(0), carga, pos);
            level.setBlock(pos, state.setValue(CARGA, 0), Block.UPDATE_ALL);
            return;
        }
        level.scheduleTick(pos, this, 20);
    }
}
