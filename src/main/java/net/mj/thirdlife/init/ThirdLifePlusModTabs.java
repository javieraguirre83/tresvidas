package net.mj.thirdlife.init;

import net.mj.thirdlife.ThirdLifePlusMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ThirdLifePlusModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThirdLifePlusMod.MODID);

    public static final RegistryObject<CreativeModeTab> THIRD_LIFE_PLUS_CREATIVE_TAB = REGISTRY.register("third_life_plus_creative_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("item_group.third_life_plus.third_life_plus_creative_tab"))
                    .icon(() -> new ItemStack(ThirdLifePlusModItems.HEART.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ThirdLifePlusModItems.GOLDEN_HEART.get());
                        output.accept(ThirdLifePlusModItems.TEMPORARY_LIFE.get());
                        output.accept(ThirdLifePlusModItems.JAGGED_DAGGER.get());
                        output.accept(ThirdLifePlusModItems.HEART.get());
                        output.accept(ThirdLifePlusModItems.REVIVER_0.get());
                    })
                    .build());

    private ThirdLifePlusModTabs() {}
}
