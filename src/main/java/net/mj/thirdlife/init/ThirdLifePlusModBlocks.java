package net.mj.thirdlife.init;

import net.mj.thirdlife.ThirdLifePlusMod;
import net.mj.thirdlife.block.ReviverBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ThirdLifePlusModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, ThirdLifePlusMod.MODID);

    public static final RegistryObject<Block> REVIVER_0 = reviver(0);
    public static final RegistryObject<Block> REVIVER_1 = reviver(1);
    public static final RegistryObject<Block> REVIVER_2 = reviver(2);
    public static final RegistryObject<Block> REVIVER_3 = reviver(3);

    private static RegistryObject<Block> reviver(int charge) {
        String name = "reviver_" + charge;
        return REGISTRY.register(name, () -> new ReviverBlock(charge, BlockBehaviour.Properties.of()
                .setId(REGISTRY.key(name))
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.STONE)
                .strength(50.0F, 1200.0F)
                .requiresCorrectToolForDrops()));
    }

    /** Reviver block for a given charge (0-3). */
    public static Block byCharge(int charge) {
        return switch (charge) {
            case 1 -> REVIVER_1.get();
            case 2 -> REVIVER_2.get();
            case 3 -> REVIVER_3.get();
            default -> REVIVER_0.get();
        };
    }

    private ThirdLifePlusModBlocks() {}
}
