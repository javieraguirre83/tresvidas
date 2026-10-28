package net.mj.thirdlife.init;

import net.mj.thirdlife.ThirdLifePlusMod;
import net.mj.thirdlife.potion.TemporaryLifeEffectMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ThirdLifePlusModMobEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ThirdLifePlusMod.MODID);

    public static final RegistryObject<MobEffect> TEMPORARY_LIFE_EFFECT =
            REGISTRY.register("temporary_life_effect", TemporaryLifeEffectMobEffect::new);

    public static Holder<MobEffect> temporaryLife() {
        return TEMPORARY_LIFE_EFFECT.getHolder().orElseThrow();
    }

    private ThirdLifePlusModMobEffects() {}
}
