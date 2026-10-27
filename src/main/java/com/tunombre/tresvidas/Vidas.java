package com.tunombre.tresvidas;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

/** Guarda y aplica las vidas de cada jugador. */
public final class Vidas {

    public static final int MAX = 3;
    /** Duración de la Vida Temporal: 6000 ticks = 5 minutos. */
    public static final int DURACION_TEMPORAL = 6000;

    private static final String K_VIDAS = "tresvidas_vidas";
    private static final String K_DORADAS = "tresvidas_doradas";
    private static final String K_TEMPORAL = "tresvidas_temporal_hasta";
    private static final String[] CLAVES = {K_VIDAS, K_DORADAS, K_TEMPORAL};

    // ---------- Lectura ----------

    public static boolean tieneDatos(ServerPlayer p) {
        return p.getPersistentData().contains(K_VIDAS);
    }

    public static int vidas(ServerPlayer p) {
        return p.getPersistentData().getIntOr(K_VIDAS, MAX);
    }

    public static int doradas(ServerPlayer p) {
        return p.getPersistentData().getIntOr(K_DORADAS, 0);
    }

    public static boolean temporalActiva(ServerPlayer p) {
        return p.getPersistentData().getLongOr(K_TEMPORAL, 0L) > p.level().getGameTime();
    }

    // ---------- Escritura ----------

    public static void setVidas(ServerPlayer p, int n) {
        p.getPersistentData().putInt(K_VIDAS, Math.max(0, Math.min(MAX, n)));
        actualizarEquipo(p);
    }

    public static void setDoradas(ServerPlayer p, int n) {
        p.getPersistentData().putInt(K_DORADAS, Math.max(0, Math.min(MAX, n)));
        actualizarEquipo(p);
    }

    public static void activarTemporal(ServerPlayer p) {
        p.getPersistentData().putLong(K_TEMPORAL, p.level().getGameTime() + DURACION_TEMPORAL);
    }

    public static void quitarTemporal(ServerPlayer p) {
        p.getPersistentData().remove(K_TEMPORAL);
    }

    /** Copia los datos al "nuevo" jugador que crea Minecraft al reaparecer. */
    public static void copiar(CompoundTag de, CompoundTag a) {
        for (String clave : CLAVES) {
            Tag valor = de.get(clave);
            if (valor != null) a.put(clave, valor.copy());
        }
    }

    // ---------- Estado del jugador ----------

    /** Ajusta el modo de juego según las vidas (para comandos de admin). */
    public static void sincronizarModo(ServerPlayer p) {
        if (vidas(p) <= 0) {
            eliminar(p);
        } else if (p.isSpectator()) {
            p.setGameMode(GameType.SURVIVAL);
        }
    }

    public static void eliminar(ServerPlayer p) {
        p.setGameMode(GameType.SPECTATOR);
        p.sendSystemMessage(Component.literal(
                "§4☠ Perdiste todas tus vidas. Busca un Revividor cargado para volver."));
    }

    public static void revivir(ServerPlayer p, int vidas, BlockPos pos) {
        setVidas(p, vidas);
        p.setGameMode(GameType.SURVIVAL);
        p.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        p.level().playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        p.sendSystemMessage(Component.literal("§a✚ ¡Has sido revivido con " + vidas
                + (vidas == 1 ? " vida!" : " vidas!")));
    }

    /** Texto como "❤❤❤ ★" para mostrar al jugador. */
    public static String resumen(ServerPlayer p) {
        int v = vidas(p), d = doradas(p);
        StringBuilder sb = new StringBuilder();
        if (v <= 0) sb.append("§8☠ Eliminado");
        else sb.append(color(v)).append("❤".repeat(v)).append("§7").append("❤".repeat(MAX - v));
        if (d > 0) sb.append(" §6").append("★".repeat(d));
        if (temporalActiva(p)) sb.append(" §b⌛");
        return sb.toString();
    }

    public static void avisar(ServerPlayer p) {
        p.sendSystemMessage(Component.literal(resumen(p)), true); // barra de acción
    }

    private static String color(int vidas) {
        return switch (vidas) {
            case 3 -> "§a";
            case 2 -> "§e";
            default -> "§c";
        };
    }

    // ---------- Equipos de color (nombre en el chat, tabla y sobre la cabeza) ----------

    public static void actualizarEquipo(ServerPlayer p) {
        MinecraftServer server = p.level().getServer();
        if (server == null) return;

        int v = vidas(p);
        String nombre;
        ChatFormatting color;
        String prefijo;
        if (v <= 0)               { nombre = "tv_eliminado"; color = ChatFormatting.DARK_GRAY; prefijo = "☠ "; }
        else if (doradas(p) > 0)  { nombre = "tv_dorado";    color = ChatFormatting.GOLD;      prefijo = "★ "; }
        else if (v == 3)          { nombre = "tv_verde";     color = ChatFormatting.GREEN;     prefijo = "❤❤❤ "; }
        else if (v == 2)          { nombre = "tv_amarillo";  color = ChatFormatting.YELLOW;    prefijo = "❤❤ "; }
        else                      { nombre = "tv_rojo";      color = ChatFormatting.RED;       prefijo = "❤ "; }

        ServerScoreboard marcador = server.getScoreboard();
        PlayerTeam equipo = marcador.getPlayerTeam(nombre);
        if (equipo == null) {
            equipo = marcador.addPlayerTeam(nombre);
            equipo.setColor(color);
            equipo.setPlayerPrefix(Component.literal(prefijo).withStyle(color));
        }
        if (marcador.getPlayersTeam(p.getScoreboardName()) != equipo) {
            marcador.addPlayerToTeam(p.getScoreboardName(), equipo);
        }
    }

    private Vidas() {}
}
