package net.mj.thirdlife.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** "Temporary Life": while active, the next death does not cost a life. */
public class TemporaryLifeEffectMobEffect extends MobEffect {
    public TemporaryLifeEffectMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -9553380);
    }
}
