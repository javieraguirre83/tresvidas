package com.tunombre.tresvidas;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/** Reacciones a lo que pasa en el juego. */
public final class Eventos {

    /** Al entrar: primera vez = 3 vidas; si ya estaba eliminado, sigue eliminado. */
    public static void alEntrar(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;

        if (!Vidas.tieneDatos(p)) Vidas.setVidas(p, Vidas.MAX);
        Vidas.actualizarEquipo(p);

        if (Vidas.vidas(p) <= 0) {
            Vidas.eliminar(p);
        } else {
            p.sendSystemMessage(Component.literal("§fTus vidas: " + Vidas.resumen(p)));
        }
    }

    /** Al morir: primero se gasta la Vida Temporal, luego las Doradas, luego las normales. */
    public static void alMorir(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        if (p.isSpectator()) return;

        // Si te mataron con la Daga Dentada, sueltas una Vida para tu asesino
        if (event.getSource().getEntity() instanceof ServerPlayer asesino
                && asesino != p
                && asesino.getMainHandItem().is(Registro.DAGA.get())) {
            p.spawnAtLocation((ServerLevel) p.level(), new ItemStack(Registro.VIDA.get()));
        }

        if (Vidas.temporalActiva(p)) {
            Vidas.quitarTemporal(p);
            p.sendSystemMessage(Component.literal("§b⌛ Tu Vida Temporal te salvó de perder una vida."));
            return;
        }
        if (Vidas.doradas(p) > 0) {
            Vidas.setDoradas(p, Vidas.doradas(p) - 1);
            p.sendSystemMessage(Component.literal("§6★ Perdiste una Vida Dorada."));
            return;
        }
        Vidas.setVidas(p, Vidas.vidas(p) - 1);
    }

    /** Minecraft crea un jugador nuevo al reaparecer: copiamos sus vidas. */
    public static void alClonar(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        Vidas.copiar(event.getOriginal().getPersistentData(), event.getEntity().getPersistentData());
    }

    /** Al reaparecer: avisamos cuántas quedan o lo eliminamos. */
    public static void alReaparecer(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;

        Vidas.actualizarEquipo(p);
        int v = Vidas.vidas(p);
        if (v <= 0) {
            Vidas.eliminar(p);
        } else {
            String aviso = v == 1 ? "§c¡Cuidado! Es tu última vida." : "§eTe quedan " + v + " vidas.";
            p.sendSystemMessage(Component.literal(aviso));
            Vidas.avisar(p);
        }
    }

    private Eventos() {}
}
