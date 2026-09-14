package io.github.carloshdx2.pangeia.mochila;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class OuvinteMochila implements Listener {

    private final ServicoMochila servico;
    private final ItensMochila itens;

    public OuvinteMochila(ServicoMochila servico, ItensMochila itens) {
        this.servico = servico;
        this.itens = itens;
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
        if (item == null || !itens.ehMochila(item)) {
            return;
        }
        // Segurando a mochila e clicando num baú/porta, quem ganha é o bloco —
        // a menos que o jogador esteja agachado, o atalho padrão para "quero o item".
        if (acao == Action.RIGHT_CLICK_BLOCK && evento.getClickedBlock() != null
                && evento.getClickedBlock().getType().isInteractable()
                && !evento.getPlayer().isSneaking()) {
            return;
        }
        evento.setCancelled(true);
        servico.abrir(evento.getPlayer(), item);
    }

    @EventHandler(ignoreCancelled = true)
    public void aoClicar(InventoryClickEvent evento) {
        Inventory topo = evento.getView().getTopInventory();
        if (!(topo.getHolder() instanceof SuporteMochila suporte)) {
            return;
        }
        MochilaAberta mochila = suporte.mochila();

        if (moveriaMochilaAberta(evento, mochila)) {
            evento.setCancelled(true);
            evento.getWhoClicked().sendMessage(ChatColor.RED + "Feche a mochila antes de movê-la.");
            return;
        }

        // Comparar pelo slot bruto em vez de comparar objetos Inventory: o slot é o
        // dado cru do protocolo e não depende de a API devolver a mesma instância.
        boolean clicouNaMochila = evento.getRawSlot() >= 0 && evento.getRawSlot() < topo.getSize();

        ItemStack entrando = itemEntrando(evento, clicouNaMochila);
        if (entrando != null && !servico.aceita(mochila.definicao(), entrando)) {
            evento.setCancelled(true);
            avisarRecusa(evento.getWhoClicked(), mochila.definicao(), entrando);
            return;
        }

        if (clicouNaMochila || evento.isShiftClick()) {
            mochila.marcarSuja();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void aoArrastar(InventoryDragEvent evento) {
        Inventory topo = evento.getView().getTopInventory();
        if (!(topo.getHolder() instanceof SuporteMochila suporte)) {
            return;
        }
        int tamanhoTopo = topo.getSize();
        boolean tocaMochila = evento.getRawSlots().stream().anyMatch(slot -> slot < tamanhoTopo);
        if (!tocaMochila) {
            return;
        }
        MochilaAberta mochila = suporte.mochila();
        ItemStack arrastado = evento.getOldCursor();
        if (!servico.aceita(mochila.definicao(), arrastado)) {
            evento.setCancelled(true);
            avisarRecusa(evento.getWhoClicked(), mochila.definicao(), arrastado);
            return;
        }
        mochila.marcarSuja();
    }

    @EventHandler
    public void aoFechar(InventoryCloseEvent evento) {
        if (evento.getInventory().getHolder() instanceof SuporteMochila suporte) {
            servico.aoFechar(suporte.mochila());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void aoSoltar(PlayerDropItemEvent evento) {
        UUID id = itens.lerId(evento.getItemDrop().getItemStack());
        if (id != null && servico.estaAberta(id)) {
            evento.setCancelled(true);
            evento.getPlayer().sendMessage(ChatColor.RED + "Feche a mochila antes de soltá-la.");
        }
    }

    @EventHandler
    public void aoSair(PlayerQuitEvent evento) {
        servico.cancelarAbertura(evento.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void aoTomarDano(EntityDamageEvent evento) {
        if (evento.getEntity() instanceof Player jogador) {
            servico.cancelarAbertura(jogador.getUniqueId());
        }
    }

    /**
     * O conteúdo mora fora do item, então mover a mochila aberta não duplica nada —
     * mas deixaria o jogador depositando numa mochila que não está mais com ele.
     */
    private boolean moveriaMochilaAberta(InventoryClickEvent evento, MochilaAberta mochila) {
        if (ehEstaMochila(evento.getCurrentItem(), mochila) || ehEstaMochila(evento.getCursor(), mochila)) {
            return true;
        }
        if (evento.getClick() == ClickType.NUMBER_KEY) {
            ItemStack atalho = evento.getWhoClicked().getInventory().getItem(evento.getHotbarButton());
            return ehEstaMochila(atalho, mochila);
        }
        if (evento.getClick() == ClickType.SWAP_OFFHAND) {
            return ehEstaMochila(evento.getWhoClicked().getInventory().getItemInOffHand(), mochila);
        }
        return false;
    }

    private boolean ehEstaMochila(ItemStack item, MochilaAberta mochila) {
        UUID id = itens.lerId(item);
        return id != null && id.equals(mochila.id());
    }

    /** Devolve o item que o clique colocaria dentro da mochila, ou null se nada entra. */
    private ItemStack itemEntrando(InventoryClickEvent evento, boolean clicouNaMochila) {
        if (evento.isShiftClick()) {
            // Shift-click a partir do inventário do jogador empurra o item para a mochila.
            return clicouNaMochila ? null : normalizar(evento.getCurrentItem());
        }
        if (!clicouNaMochila) {
            return null;
        }
        return switch (evento.getClick()) {
            case NUMBER_KEY -> normalizar(evento.getWhoClicked().getInventory().getItem(evento.getHotbarButton()));
            case SWAP_OFFHAND -> normalizar(evento.getWhoClicked().getInventory().getItemInOffHand());
            default -> normalizar(evento.getCursor());
        };
    }

    private ItemStack normalizar(ItemStack item) {
        return item == null || item.getType().isAir() ? null : item;
    }

    private void avisarRecusa(org.bukkit.entity.HumanEntity quem, DefinicaoMochila definicao, ItemStack item) {
        if (itens.ehMochila(item)) {
            quem.sendMessage(ChatColor.RED + "Não dá para guardar uma mochila dentro de outra.");
        } else {
            quem.sendMessage(ChatColor.RED + "Essa mochila só guarda itens de "
                    + ChatColor.WHITE + definicao.profissao() + ChatColor.RED + ".");
        }
    }
}
