package io.github.carloshdx2.pangeia.pergaminho;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class OuvintePergaminho implements Listener {

    private final ServicoPergaminho servico;

    public OuvintePergaminho(ServicoPergaminho servico) {
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
        if (item == null || servico.destinoDe(item) == null) {
            return;
        }
        if (acao == Action.RIGHT_CLICK_BLOCK && evento.getClickedBlock() != null
                && evento.getClickedBlock().getType().isInteractable()
                && !evento.getPlayer().isSneaking()) {
            return;
        }
        evento.setCancelled(true);
        servico.usar(evento.getPlayer(), item);
    }

    @EventHandler(ignoreCancelled = true)
    public void aoTomarDano(EntityDamageEvent evento) {
        if (servico.cancelarAoTomarDano() && evento.getEntity() instanceof Player jogador
                && servico.cancelar(jogador.getUniqueId())) {
            jogador.sendMessage(ChatColor.RED + "A leitura do pergaminho foi interrompida.");
        }
    }

    @EventHandler
    public void aoMorrer(PlayerDeathEvent evento) {
        servico.cancelar(evento.getEntity().getUniqueId());
    }

    @EventHandler
    public void aoSair(PlayerQuitEvent evento) {
        servico.cancelar(evento.getPlayer().getUniqueId());
    }
}
