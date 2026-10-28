package net.mj.thirdlife.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/** Client-only calls used from common code (only executed on the client). */
public final class ClientHooks {
    /** The big item animation (like a totem) when a life is equipped. */
    public static void itemActivation(ItemStack stack) {
        Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
    }

    private ClientHooks() {}
}
