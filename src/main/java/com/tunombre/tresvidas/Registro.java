package com.tunombre.tresvidas;

import com.tunombre.tresvidas.bloque.RevividorBlock;
import com.tunombre.tresvidas.item.DagaItem;
import com.tunombre.tresvidas.item.VidaItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.tunombre.tresvidas.TresVidas.MODID;

/** Todo lo que el mod agrega al juego. */
public final class Registro {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Desde 1.21.2 cada item/bloque debe conocer su propio id
    private static ResourceKey<Item> itemKey(String nombre) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, nombre));
    }

    private static ResourceKey<Block> blockKey(String nombre) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MODID, nombre));
    }

    // ---------- Bloques ----------

    public static final RegistryObject<Block> REVIVIDOR = BLOCKS.register("revividor",
            () -> new RevividorBlock(BlockBehaviour.Properties.of()
                    .setId(blockKey("revividor"))
                    .mapColor(MapColor.COLOR_BLACK)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1200.0F)
                    .lightLevel(estado -> estado.getValue(RevividorBlock.CARGA) * 5)));

    // ---------- Items ----------

    public static final RegistryObject<Item> REVIVIDOR_ITEM = ITEMS.register("revividor",
            () -> new BlockItem(REVIVIDOR.get(), new Item.Properties()
                    .setId(itemKey("revividor"))
                    .useBlockDescriptionPrefix()));

    public static final RegistryObject<Item> VIDA = ITEMS.register("vida",
            () -> new VidaItem(VidaItem.Tipo.NORMAL, new Item.Properties()
                    .setId(itemKey("vida"))
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> VIDA_DORADA = ITEMS.register("vida_dorada",
            () -> new VidaItem(VidaItem.Tipo.DORADA, new Item.Properties()
                    .setId(itemKey("vida_dorada"))
                    .stacksTo(16)
                    .rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> VIDA_TEMPORAL = ITEMS.register("vida_temporal",
            () -> new VidaItem(VidaItem.Tipo.TEMPORAL, new Item.Properties()
                    .setId(itemKey("vida_temporal"))
                    .stacksTo(16)
                    .rarity(Rarity.RARE)));

    public static final RegistryObject<Item> DAGA = ITEMS.register("daga_dentada",
            () -> new DagaItem(new Item.Properties()
                    .setId(itemKey("daga_dentada"))
                    .sword(ToolMaterial.IRON, 3.0F, -2.4F)));

    // ---------- Pestaña del modo creativo ----------

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("principal",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.tresvidas"))
                    .icon(() -> new ItemStack(VIDA.get()))
                    .displayItems((parametros, salida) -> {
                        salida.accept(VIDA.get());
                        salida.accept(VIDA_DORADA.get());
                        salida.accept(VIDA_TEMPORAL.get());
                        salida.accept(DAGA.get());
                        salida.accept(REVIVIDOR_ITEM.get());
                    })
                    .build());

    private Registro() {}
}
