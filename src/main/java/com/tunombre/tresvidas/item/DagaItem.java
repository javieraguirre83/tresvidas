package com.tunombre.tresvidas.item;

import com.tunombre.tresvidas.Registro;
import com.tunombre.tresvidas.Vidas;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Daga Dentada.
 * - Clic derecho: te arrancas una vida normal y la obtienes como item "Vida"
 *   (para regalarla). No funciona con tu última vida ni con las Doradas.
 * - Si matas a un jugador con ella, suelta una Vida (ver Eventos.alMorir).
 */
public class DagaItem extends Item {

    public DagaItem(Properties propiedades) {
        super(propiedades);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer p)) return InteractionResult.SUCCESS;

        int vidas = Vidas.vidas(p);
        if (vidas <= 1) {
            p.sendSystemMessage(Component.literal(
                    "§cEs tu última vida, no te la puedes arrancar. (Las Vidas Doradas son inmunes a la daga)"));
            return InteractionResult.FAIL;
        }

        Vidas.setVidas(p, vidas - 1);
        ItemStack vida = new ItemStack(Registro.VIDA.get());
        if (!p.getInventory().add(vida)) p.drop(vida, false);

        ItemStack daga = player.getItemInHand(hand);
        p.getCooldowns().addCooldown(daga, 100); // 5 segundos de espera
        level.playSound(null, p.blockPosition(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 1.0F, 1.0F);
        p.sendSystemMessage(Component.literal("§4Te arrancaste una vida. Ahora puedes dársela a alguien más."));
        Vidas.avisar(p);
        return InteractionResult.SUCCESS;
    }
}
