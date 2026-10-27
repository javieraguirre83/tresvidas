package com.tunombre.tresvidas;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Tres Vidas — mod de vidas limitadas al estilo "3rd Life" para Forge 26.2.
 *
 * - Cada jugador empieza con 3 vidas (verde → amarillo → rojo).
 * - Al perder la tercera queda eliminado (modo espectador).
 * - Vida, Vida Dorada y Vida Temporal se usan con clic derecho.
 * - La Daga Dentada te deja arrancarte una vida para dársela a otro,
 *   y si matas a alguien con ella, suelta una Vida.
 * - El Revividor, cargado con Vidas Doradas, revive a un jugador eliminado.
 */
@Mod(TresVidas.MODID)
public final class TresVidas {

    // Debe coincidir con mod_id en gradle.properties
    public static final String MODID = "tresvidas";

    public TresVidas(FMLJavaModLoadingContext context) {
        var modBus = context.getModBusGroup();

        // Registros (bloques, items, pestaña creativa)
        Registro.BLOCKS.register(modBus);
        Registro.ITEMS.register(modBus);
        Registro.TABS.register(modBus);

        // Eventos del juego (EventBus 7: cada evento tiene su BUS)
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(Eventos::alEntrar);
        LivingDeathEvent.BUS.addListener(Eventos::alMorir);
        PlayerEvent.Clone.BUS.addListener(Eventos::alClonar);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(Eventos::alReaparecer);
        RegisterCommandsEvent.BUS.addListener(Comandos::registrar);
    }
}
