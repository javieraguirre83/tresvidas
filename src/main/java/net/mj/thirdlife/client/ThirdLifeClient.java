package net.mj.thirdlife.client;

import net.mj.thirdlife.Lives;
import net.mj.thirdlife.ThirdLifePlusMod;
import net.mj.thirdlife.init.ThirdLifePlusModMobEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Keys (I = show/hide lives, O = switch side) and the hearts drawn on the side of the screen. */
public final class ThirdLifeClient {
    public static final KeyMapping TOGGLE_VISIBILITY_KEYBIND =
            new KeyMapping("key.third_life_plus.toggle_visibility_keybind", 73, KeyMapping.Category.MISC);
    public static final KeyMapping SET_LIVES_SIDE =
            new KeyMapping("key.third_life_plus.set_lives_side", 79, KeyMapping.Category.MISC);

    private static final Identifier HEART = tex("heart_clone");
    private static final Identifier GOLDEN = tex("golden_life");
    private static final Identifier DEAD = tex("wither");
    private static final Identifier TEMPORARY = tex("temporary_heart_hud");

    // Player choices (the original defaults: hidden, left side)
    private static boolean visible = false;
    private static String side = "left";

    private static Identifier tex(String name) {
        return Identifier.fromNamespaceAndPath(ThirdLifePlusMod.MODID, "textures/screens/" + name + ".png");
    }

    private static boolean prefsLoaded = false;

    public static void init() {
        RegisterKeyMappingsEvent.BUS.addListener(event -> {
            event.register(TOGGLE_VISIBILITY_KEYBIND);
            event.register(SET_LIVES_SIDE);
        });
        TickEvent.ClientTickEvent.Post.BUS.addListener(event -> onClientTick());
        AddGuiOverlayLayersEvent.BUS.addListener(event -> event.getLayeredDraw().add(
                Identifier.fromNamespaceAndPath(ThirdLifePlusMod.MODID, "lives"),
                (graphics, deltaTracker) -> renderLives(graphics)));
    }

    private static void onClientTick() {
        if (!prefsLoaded) {
            prefsLoaded = true;
            loadPrefs();
        }
        boolean changed = false;
        while (TOGGLE_VISIBILITY_KEYBIND.consumeClick()) {
            visible = !visible;
            changed = true;
        }
        while (SET_LIVES_SIDE.consumeClick()) {
            side = side.equals("left") ? "right" : "left";
            changed = true;
        }
        if (changed) savePrefs();
    }

    private static void renderLives(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || !visible) return;

        int w = g.guiWidth();
        int h = g.guiHeight();
        int x = side.equals("left") ? 3 : w - 19;
        int lives = Lives.clientLives(player);
        int golden = Lives.clientGolden(player);

        // Red hearts (top = 3rd life, middle = 2nd, bottom = 1st)
        if (lives == 3) draw(g, HEART, x, h / 2 - 49);
        if (lives >= 2) draw(g, HEART, x, h / 2 - 31);
        if (lives >= 1) draw(g, HEART, x, h / 2 - 13);
        // Lost lives
        if (lives < 3) draw(g, DEAD, x, h / 2 - 49);
        if (lives < 2) draw(g, DEAD, x, h / 2 - 31);
        if (lives < 1) draw(g, DEAD, x, h / 2 - 13);
        // Temporary life
        if (player.hasEffect(ThirdLifePlusModMobEffects.temporaryLife())) {
            g.blit(RenderPipelines.GUI_TEXTURED, TEMPORARY, x, h / 2 - 67, 0.0F, 0.0F, 16, 16, 16, 16);
        }
        // Golden lives (drawn on top)
        if (golden == 3) draw(g, GOLDEN, x, h / 2 - 49);
        if (golden >= 2) draw(g, GOLDEN, x, h / 2 - 31);
        if (golden >= 1) draw(g, GOLDEN, x, h / 2 - 13);
    }

    private static void draw(GuiGraphicsExtractor g, Identifier texture, int x, int y) {
        g.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F, 16, 15, 16, 15);
    }

    // ----- remember I / O choices between game sessions -----
    private static Path prefsFile() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("third_life_plus-client.properties");
    }

    private static void loadPrefs() {
        try {
            Path file = prefsFile();
            if (!Files.exists(file)) return;
            Properties p = new Properties();
            try (Reader r = Files.newBufferedReader(file)) {
                p.load(r);
            }
            visible = Boolean.parseBoolean(p.getProperty("visible", "false"));
            side = p.getProperty("side", "left").equals("right") ? "right" : "left";
        } catch (Exception ignored) {
        }
    }

    private static void savePrefs() {
        try {
            Path file = prefsFile();
            Files.createDirectories(file.getParent());
            Properties p = new Properties();
            p.setProperty("visible", Boolean.toString(visible));
            p.setProperty("side", side);
            try (Writer wr = Files.newBufferedWriter(file)) {
                p.store(wr, "3rd Life + client settings");
            }
        } catch (Exception ignored) {
        }
    }

    private ThirdLifeClient() {}
}
