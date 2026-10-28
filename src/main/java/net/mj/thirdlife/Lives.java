package net.mj.thirdlife;

import net.mj.thirdlife.init.ThirdLifePlusModItems;
import net.mj.thirdlife.init.ThirdLifePlusModMobEffects;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

/** Player variables of the original mod (Lives, golden_lives, temporary_heart) and shared helpers. */
public final class Lives {
    private static final String ROOT = "third_life_plus";
    private static final String LIVES = "Lives";
    private static final String GOLDEN = "golden_lives";

    private static CompoundTag data(Player player) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag tag = persistent.getCompoundOrEmpty(ROOT);
        if (!persistent.contains(ROOT)) persistent.put(ROOT, tag);
        return tag;
    }

    public static int lives(Player player) {
        if (player.level().isClientSide()) return clientLives(player);
        return data(player).getIntOr(LIVES, 3);
    }

    public static int golden(Player player) {
        if (player.level().isClientSide()) return clientGolden(player);
        return data(player).getIntOr(GOLDEN, 0);
    }

    /** On the client the lives are read from the synced team (black/red/yellow/green...). */
    private static String clientTeam(Player player) {
        PlayerTeam team = player.getTeam();
        return team == null ? "" : team.getName();
    }

    public static int clientLives(Player player) {
        return switch (clientTeam(player)) {
            case "black" -> 0;
            case "red" -> 1;
            case "yellow" -> 2;
            default -> 3;
        };
    }

    public static int clientGolden(Player player) {
        return switch (clientTeam(player)) {
            case "dark_green" -> 1;
            case "blue" -> 2;
            case "purple" -> 3;
            default -> 0;
        };
    }

    public static void setLives(Player player, int value) {
        CompoundTag tag = data(player);
        tag.putInt(LIVES, Math.max(0, value));
        player.getPersistentData().put(ROOT, tag);
    }

    public static void setGolden(Player player, int value) {
        CompoundTag tag = data(player);
        tag.putInt(GOLDEN, Math.max(0, value));
        player.getPersistentData().put(ROOT, tag);
    }

    public static boolean temporary(Player player) {
        return player.hasEffect(ThirdLifePlusModMobEffects.temporaryLife());
    }

    public static void clearTemporary(Player player) {
        player.removeEffect(ThirdLifePlusModMobEffects.temporaryLife());
    }

    /** Keeps the lives when Minecraft creates the new player entity after death. */
    public static void copy(Player from, Player to) {
        CompoundTag tag = from.getPersistentData().getCompoundOrEmpty(ROOT);
        to.getPersistentData().put(ROOT, tag.copy());
    }

    /** The "Life" item named after the player ("Steve's Life"). */
    public static ItemStack namedHeart(Player player) {
        ItemStack stack = new ItemStack(ThirdLifePlusModItems.HEART.get());
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(player.getDisplayName().getString() + "'s Life"));
        stack.setCount(1);
        return stack;
    }

    public static void sound(Level level, BlockPos pos, SoundEvent sound, SoundSource source) {
        if (!level.isClientSide()) {
            level.playSound(null, pos, sound, source, 1.0F, 1.0F);
        }
    }

    public static void actionBar(Player player, String text) {
        if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.literal(text), true);
        }
    }

    public static boolean hasPermission4(ServerPlayer player) {
        return Commands.hasPermission(Commands.LEVEL_OWNERS).test(player.createCommandSourceStack());
    }

    // ---------- Teams (same names and colors as the original) ----------

    private static final String[][] TEAMS = {
            {"green", "green"}, {"yellow", "yellow"}, {"red", "red"}, {"black", "dark_gray"},
            {"dark_green", "dark_green"}, {"blue", "blue"}, {"purple", "dark_purple"}};

    public static String teamFor(Player player) {
        int lives = lives(player);
        int golden = golden(player);
        if (lives <= 0) return "black";
        if (lives == 1) return "red";
        if (lives == 2) return "yellow";
        if (lives == 3) {
            return switch (golden) {
                case 1 -> "dark_green";
                case 2 -> "blue";
                case 3 -> "purple";
                default -> "green";
            };
        }
        return null;
    }

    private static void command(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), command);
    }

    public static void createTeams(MinecraftServer server) {
        for (String[] team : TEAMS) {
            if (server.getScoreboard().getPlayerTeam(team[0]) == null) {
                command(server, "team add " + team[0]);
                command(server, "team modify " + team[0] + " color " + team[1]);
            }
        }
    }

    public static void updateTeam(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        String wanted = teamFor(player);
        if (wanted == null) return;
        createTeams(server);
        PlayerTeam current = server.getScoreboard().getPlayersTeam(player.getScoreboardName());
        if (current == null || !current.getName().equals(wanted)) {
            command(server, "team join " + wanted + " " + player.getScoreboardName());
        }
    }

    private Lives() {}
}
