package com.tunombre.tresvidas;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Comandos:
 *   /vidas                              → ver tus vidas
 *   /vidas ver <jugador>                → ver las de otro
 *   /vidas poner   <jugadores> <0-3>    (admin)
 *   /vidas dar     <jugadores> <1-3>    (admin)
 *   /vidas quitar  <jugadores> <1-3>    (admin)
 *   /vidas doradas <jugadores> <0-3>    (admin)
 *   /vidas reiniciar <jugadores>        (admin) → vuelve a 3 vidas
 */
public final class Comandos {

    // Solo operadores (nivel 2).
    private static final Predicate<CommandSourceStack> ADMIN = Commands.hasPermission(Commands.LEVEL_GAMEMASTERS);

    public static void registrar(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("vidas")
                .executes(ctx -> {
                    mostrar(ctx.getSource(), ctx.getSource().getPlayerOrException());
                    return 1;
                })
                .then(Commands.literal("ver")
                        .then(Commands.argument("jugador", EntityArgument.player())
                                .executes(ctx -> {
                                    mostrar(ctx.getSource(), EntityArgument.getPlayer(ctx, "jugador"));
                                    return 1;
                                })))
                .then(Commands.literal("poner").requires(ADMIN)
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .then(Commands.argument("cantidad", IntegerArgumentType.integer(0, Vidas.MAX))
                                        .executes(ctx -> aplicar(ctx, (p, n) -> Vidas.setVidas(p, n))))))
                .then(Commands.literal("dar").requires(ADMIN)
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .then(Commands.argument("cantidad", IntegerArgumentType.integer(1, Vidas.MAX))
                                        .executes(ctx -> aplicar(ctx, (p, n) -> Vidas.setVidas(p, Vidas.vidas(p) + n))))))
                .then(Commands.literal("quitar").requires(ADMIN)
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .then(Commands.argument("cantidad", IntegerArgumentType.integer(1, Vidas.MAX))
                                        .executes(ctx -> aplicar(ctx, (p, n) -> Vidas.setVidas(p, Vidas.vidas(p) - n))))))
                .then(Commands.literal("doradas").requires(ADMIN)
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .then(Commands.argument("cantidad", IntegerArgumentType.integer(0, Vidas.MAX))
                                        .executes(ctx -> aplicar(ctx, (p, n) -> Vidas.setDoradas(p, n))))))
                .then(Commands.literal("reiniciar").requires(ADMIN)
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .executes(ctx -> {
                                    for (ServerPlayer p : EntityArgument.getPlayers(ctx, "jugadores")) {
                                        Vidas.setVidas(p, Vidas.MAX);
                                        Vidas.setDoradas(p, 0);
                                        Vidas.quitarTemporal(p);
                                        Vidas.sincronizarModo(p);
                                        mostrar(ctx.getSource(), p);
                                    }
                                    return 1;
                                }))));
    }

    private static int aplicar(CommandContext<CommandSourceStack> ctx, BiConsumer<ServerPlayer, Integer> accion)
            throws CommandSyntaxException {
        Collection<ServerPlayer> jugadores = EntityArgument.getPlayers(ctx, "jugadores");
        int cantidad = IntegerArgumentType.getInteger(ctx, "cantidad");
        for (ServerPlayer p : jugadores) {
            accion.accept(p, cantidad);
            Vidas.sincronizarModo(p);
            Vidas.avisar(p);
            mostrar(ctx.getSource(), p);
        }
        return jugadores.size();
    }

    private static void mostrar(CommandSourceStack fuente, ServerPlayer p) {
        String texto = "§f" + p.getScoreboardName() + ": " + Vidas.resumen(p);
        fuente.sendSuccess(() -> Component.literal(texto), false);
    }

    private Comandos() {}
}
