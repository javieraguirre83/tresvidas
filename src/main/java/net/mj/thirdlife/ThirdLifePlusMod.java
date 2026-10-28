package net.mj.thirdlife;

import net.mj.thirdlife.client.ThirdLifeClient;
import net.mj.thirdlife.command.ThirdLifeCommands;
import net.mj.thirdlife.init.ThirdLifePlusModBlocks;
import net.mj.thirdlife.init.ThirdLifePlusModItems;
import net.mj.thirdlife.init.ThirdLifePlusModMobEffects;
import net.mj.thirdlife.init.ThirdLifePlusModSounds;
import net.mj.thirdlife.init.ThirdLifePlusModTabs;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** 3rd Life + (supermj767), port to Minecraft 26.2 / Forge 65. */
@Mod(ThirdLifePlusMod.MODID)
public final class ThirdLifePlusMod {
    public static final String MODID = "third_life_plus";

    public ThirdLifePlusMod(FMLJavaModLoadingContext context) {
        var modBus = context.getModBusGroup();

        ThirdLifePlusModSounds.REGISTRY.register(modBus);
        ThirdLifePlusModBlocks.REGISTRY.register(modBus);
        ThirdLifePlusModItems.REGISTRY.register(modBus);
        ThirdLifePlusModTabs.REGISTRY.register(modBus);
        ThirdLifePlusModMobEffects.REGISTRY.register(modBus);

        LivingDeathEvent.BUS.addListener(LivesEvents::onDeath);
        PlayerEvent.Clone.BUS.addListener(LivesEvents::onClone);
        TickEvent.PlayerTickEvent.Post.BUS.addListener(LivesEvents::onPlayerTick);
        PlayerInteractEvent.RightClickBlock.BUS.addListener(LivesEvents::onRightClickBlock);
        RegisterCommandsEvent.BUS.addListener(ThirdLifeCommands::register);

        if (FMLEnvironment.dist.isClient()) {
            ThirdLifeClient.init();
        }
    }
}
