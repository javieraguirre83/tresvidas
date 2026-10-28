package net.mj.thirdlife.item;

import net.mj.thirdlife.Lives;
import net.mj.thirdlife.ThirdLifePlusMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;

/** "Jagged Dagger": right click to tear out one of your lives as a "Life" item. */
public class JaggedDaggerItem extends Item {
    public static final ResourceKey<DamageType> JAGGED_DAGGER_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(ThirdLifePlusMod.MODID, "jagged_dagger_damage"));

    public JaggedDaggerItem(Properties properties) {
        super(properties);
    }

    // JaggedDaggerRightClickedInAirProcedure
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        InteractionResult result = super.use(level, player, hand);
        if (level.isClientSide()) return result;

        if (Lives.golden(player) != 0) {
            Lives.actionBar(player, "Your Golden Life is immune to the Jagged Dagger");
        } else if (Lives.lives(player) != 1) {
            int lives = Lives.lives(player);
            if (lives == 3 || lives == 2) {
                player.swing(InteractionHand.MAIN_HAND, true);
                Lives.setLives(player, lives - 1);
                ItemHandlerHelper.giveItemToPlayer(player, Lives.namedHeart(player));
                Lives.sound(level, player.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS);
                if (player instanceof ServerPlayer sp) Lives.updateTeam(sp);
                if (level instanceof ServerLevel serverLevel) {
                    DamageSource source = new DamageSource(serverLevel.registryAccess()
                            .lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(JAGGED_DAGGER_DAMAGE));
                    player.hurtServer(serverLevel, source, 4.0F);
                }
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, lives == 3 ? 120 : 140, 1, false, true));
            }
        } else {
            Lives.actionBar(player, "This is your last life, you do not want that gone.");
        }
        return result;
    }
}
