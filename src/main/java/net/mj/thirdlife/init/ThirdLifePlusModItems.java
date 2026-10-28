package net.mj.thirdlife.init;

import net.mj.thirdlife.ThirdLifePlusMod;
import net.mj.thirdlife.item.GoldenHeartItem;
import net.mj.thirdlife.item.HeartItem;
import net.mj.thirdlife.item.JaggedDaggerItem;
import net.mj.thirdlife.item.TemporaryLifeItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ThirdLifePlusModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ThirdLifePlusMod.MODID);

    /** Same stats as the original custom tier: 200 uses, speed 1.5, damage bonus -3, enchantability 2, repaired with iron. */
    public static final ToolMaterial JAGGED = new ToolMaterial(
            BlockTags.INCORRECT_FOR_STONE_TOOL, 200, 1.5F, -3.0F, 2, ItemTags.IRON_TOOL_MATERIALS);

    public static final RegistryObject<Item> GOLDEN_HEART = REGISTRY.register("golden_heart",
            () -> new GoldenHeartItem(new Item.Properties().setId(REGISTRY.key("golden_heart")).stacksTo(16).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TEMPORARY_LIFE = REGISTRY.register("temporary_life",
            () -> new TemporaryLifeItem(new Item.Properties().setId(REGISTRY.key("temporary_life")).stacksTo(64).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> JAGGED_DAGGER = REGISTRY.register("jagged_dagger",
            () -> new JaggedDaggerItem(new Item.Properties().setId(REGISTRY.key("jagged_dagger")).sword(JAGGED, 3.0F, -3.0F)));
    public static final RegistryObject<Item> HEART = REGISTRY.register("heart",
            () -> new HeartItem(new Item.Properties().setId(REGISTRY.key("heart")).stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> REVIVER_0 = block("reviver_0", ThirdLifePlusModBlocks.REVIVER_0);
    public static final RegistryObject<Item> REVIVER_1 = block("reviver_1", ThirdLifePlusModBlocks.REVIVER_1);
    public static final RegistryObject<Item> REVIVER_2 = block("reviver_2", ThirdLifePlusModBlocks.REVIVER_2);
    public static final RegistryObject<Item> REVIVER_3 = block("reviver_3", ThirdLifePlusModBlocks.REVIVER_3);

    private static RegistryObject<Item> block(String name, RegistryObject<Block> block) {
        return REGISTRY.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().setId(REGISTRY.key(name)).useBlockDescriptionPrefix()));
    }

    private ThirdLifePlusModItems() {}
}
