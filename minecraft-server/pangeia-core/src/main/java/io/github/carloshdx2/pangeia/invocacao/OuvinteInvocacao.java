package io.github.carloshdx2.pangeia.invocacao;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class OuvinteInvocacao implements Listener {

    private final ServicoInvocacao servico;

    public OuvinteInvocacao(ServicoInvocacao servico) {
        this.servico = servico;
    }

    @EventHandler
    public void aoInteragir(PlayerInteractEvent evento) {
        if (evento.getHand() != EquipmentSlot.HAND) {
            return;
        }
        Action acao = evento.getAction();
        if (acao != Action.RIGHT_CLICK_AIR && acao != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = evento.getItem();
        if (item == null || servico.itens().lerTipo(item) == null) {
            return;
        }
        if (acao == Action.RIGHT_CLICK_BLOCK && evento.getClickedBlock() != null
                && evento.getClickedBlock().getType().isInteractable()
                && !evento.getPlayer().isSneaking()) {
            return;
        }
        evento.setCancelled(true);
        servico.alternar(evento.getPlayer(), item);
    }

    @EventHandler(ignoreCancelled = true)
    public void aoEntidadeMorrer(EntityDeathEvent evento) {
        servico.aoEntidadeMorrer(evento.getEntity());
    }

    @EventHandler
    public void aoSair(PlayerQuitEvent evento) {
        Player jogador = evento.getPlayer();
        servico.dispensar(jogador.getUniqueId());
    }
}
