package io.github.carloshdx2.pangeia.ligamento;

import java.util.Iterator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public final class OuvinteLigamento implements Listener {

    private final ServicoLigamento servico;

    public OuvinteLigamento(ServicoLigamento servico) {
        this.servico = servico;
    }

    /**
     * Prioridade alta para rodar depois dos plugins que mexem nos drops, mas ainda dando
     * chance de outro plugin reagir. O item precisa sair de getDrops() ao entrar em
     * getItemsToKeep(), senão o jogador recebe duas cópias.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void aoMorrer(PlayerDeathEvent evento) {
        if (evento.getKeepInventory()) {
            return;
        }
        Iterator<ItemStack> iterador = evento.getDrops().iterator();
        while (iterador.hasNext()) {
            ItemStack drop = iterador.next();
            if (servico.temLigamento(drop)) {
                evento.getItemsToKeep().add(drop);
                iterador.remove();
            }
        }
    }
}
