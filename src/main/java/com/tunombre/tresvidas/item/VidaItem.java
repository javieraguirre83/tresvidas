package com.tunombre.tresvidas.item;

import com.tunombre.tresvidas.Vidas;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Vida, Vida Dorada y Vida Temporal: se usan con clic derecho. */
public class VidaItem extends Item {

    public enum Tipo { NORMAL, DORADA, TEMPORAL }

    private final Tipo tipo;

    public VidaItem(Tipo tipo, Properties propiedades) {
        super(propiedades);
        this.tipo = tipo;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer p)) return InteractionResult.SUCCESS;

        ItemStack stack = player.getItemInHand(hand);
        boolean usado = switch (tipo) {
            case NORMAL -> {
                if (Vidas.vidas(p) >= Vidas.MAX) {
                    p.sendSystemMessage(Component.literal("§c¡Ya tienes tus 3 vidas!"));
                    yield false;
                }
                Vidas.setVidas(p, Vidas.vidas(p) + 1);
                sonido(p, SoundEvents.FIREWORK_ROCKET_LAUNCH);
                yield true;
            }
            case DORADA -> {
                if (Vidas.doradas(p) >= Vidas.MAX) {
                    p.sendSystemMessage(Component.literal("§cTus Vidas Doradas ya están llenas."));
                    yield false;
                }
                Vidas.setDoradas(p, Vidas.doradas(p) + 1);
                sonido(p, SoundEvents.EVOKER_PREPARE_WOLOLO);
                yield true;
            }
            case TEMPORAL -> {
                if (Vidas.temporalActiva(p)) {
                    p.sendSystemMessage(Component.literal("§cNo puedes acumular Vidas Temporales."));
                    yield false;
                }
                Vidas.activarTemporal(p);
                // Efecto visual para saber que está activa (5 minutos)
                p.addEffect(new MobEffectInstance(MobEffects.LUCK, Vidas.DURACION_TEMPORAL, 0));
                p.sendSystemMessage(Component.literal(
                        "§b⌛ Vida Temporal activa: si mueres en los próximos 5 minutos, no pierdes vida."));
                sonido(p, SoundEvents.PLAYER_LEVELUP);
                yield true;
            }
        };

        if (!usado) return InteractionResult.FAIL;
        stack.consume(1, player);
        Vidas.avisar(p);
        return InteractionResult.SUCCESS;
    }

    private static void sonido(ServerPlayer p, SoundEvent sonido) {
        p.level().playSound(null, p.blockPosition(), sonido, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
