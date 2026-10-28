package net.mj.thirdlife.init;

import net.mj.thirdlife.ThirdLifePlusMod;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ThirdLifePlusModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ThirdLifePlusMod.MODID);

    public static final RegistryObject<SoundEvent> GOLD_LIFE = REGISTRY.register("gold_life",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(ThirdLifePlusMod.MODID, "gold_life")));
    public static final RegistryObject<SoundEvent> TEMP_LIFE = REGISTRY.register("temp_life",
            () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(ThirdLifePlusMod.MODID, "temp_life")));

    private ThirdLifePlusModSounds() {}
}
