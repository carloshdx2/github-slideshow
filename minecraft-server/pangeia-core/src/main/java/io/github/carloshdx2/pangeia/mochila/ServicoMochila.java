package io.github.carloshdx2.pangeia.mochila;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class ServicoMochila {

    private static final int TAMANHO_MAXIMO = 54;

    private final Plugin plugin;
    private final ArmazenamentoMochila armazenamento;
    private final ItensMochila itens;
    private final FiltroProfissao filtro;

    private final Map<UUID, MochilaAberta> abertas = new HashMap<>();
    private final Map<UUID, BukkitTask> aberturasPendentes = new HashMap<>();
    private Map<String, DefinicaoMochila> definicoes = new HashMap<>();
    private boolean desligando;

    public ServicoMochila(Plugin plugin, ArmazenamentoMochila armazenamento, ItensMochila itens, FiltroProfissao filtro) {
        this.plugin = plugin;
        this.armazenamento = armazenamento;
        this.itens = itens;
        this.filtro = filtro;
    }

    public void definirDefinicoes(Map<String, DefinicaoMochila> definicoes) {
        this.definicoes = definicoes;
    }

    public DefinicaoMochila definicao(String tipo) {
        return tipo == null ? null : definicoes.get(tipo);
    }

    public Map<String, DefinicaoMochila> definicoes() {
        return definicoes;
    }

    public ItensMochila itens() {
        return itens;
    }

    public boolean estaAberta(UUID id) {
        return abertas.containsKey(id);
    }

    public void abrir(Player jogador, ItemStack item) {
        UUID id = itens.lerId(item);
        String tipo = itens.lerTipo(item);
        DefinicaoMochila definicao = definicao(tipo);
        if (id == null || definicao == null) {
            jogador.sendMessage(ChatColor.RED + "Essa mochila não existe mais na configuração do servidor.");
            return;
        }

        String permissao = definicao.permissaoCargo();
        if (permissao != null && !jogador.hasPermission(permissao)) {
            jogador.sendMessage(ChatColor.RED + "Só quem tem o cargo "
                    + ChatColor.WHITE + definicao.cargo() + ChatColor.RED + " consegue abrir essa mochila.");
            return;
        }

        if (definicao.atrasoAberturaTicks() <= 0) {
            abrirAgora(jogador, id, definicao);
            return;
        }

        if (aberturasPendentes.containsKey(jogador.getUniqueId())) {
            return;
        }
        jogador.sendMessage(ChatColor.GRAY + "Abrindo a mochila...");
        BukkitTask tarefa = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            aberturasPendentes.remove(jogador.getUniqueId());
            if (jogador.isOnline()) {
                abrirAgora(jogador, id, definicao);
            }
        }, definicao.atrasoAberturaTicks());
        aberturasPendentes.put(jogador.getUniqueId(), tarefa);
    }

    public void cancelarAbertura(UUID jogadorId) {
        BukkitTask tarefa = aberturasPendentes.remove(jogadorId);
        if (tarefa != null) {
            tarefa.cancel();
        }
    }

    private void abrirAgora(Player jogador, UUID id, DefinicaoMochila definicao) {
        MochilaAberta mochila = abertas.get(id);
        if (mochila == null) {
            mochila = construir(id, definicao);
            abertas.put(id, mochila);
        }
        // Quando existem duas cópias do mesmo item, os dois jogadores compartilham o
        // MESMO inventário: sem cópias paralelas, não há como duplicar conteúdo.
        jogador.openInventory(mochila.inventario());
    }

    private MochilaAberta construir(UUID id, DefinicaoMochila definicao) {
        ItemStack[] conteudo = armazenamento.carregar(id);
        int tamanho = definicao.tamanho();
        if (conteudo != null && conteudo.length > tamanho) {
            // Encolher apagaria itens do jogador sem aviso se alguém reduzir "linhas"
            // na config depois que a mochila já estava em uso.
            tamanho = Math.min(TAMANHO_MAXIMO, ((conteudo.length + 8) / 9) * 9);
            plugin.getLogger().warning("A mochila " + id + " tem mais itens do que o tipo "
                    + definicao.id() + " comporta hoje; abrindo com " + tamanho
                    + " espaços para não apagar nada.");
        }

        SuporteMochila suporte = new SuporteMochila();
        String titulo = ChatColor.translateAlternateColorCodes('&', definicao.nome());
        Inventory inventario = Bukkit.createInventory(suporte, tamanho, titulo);
        if (conteudo != null) {
            inventario.setContents(conteudo.length > tamanho
                    ? Arrays.copyOf(conteudo, tamanho)
                    : conteudo);
        }

        MochilaAberta mochila = new MochilaAberta(id, definicao, inventario);
        suporte.definir(mochila, inventario);
        return mochila;
    }

    public boolean aceita(DefinicaoMochila definicao, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return true;
        }
        // Mochila dentro de mochila vira armazenamento infinito e complica o
        // rastreio de quem é dono do quê — bloqueado sempre.
        if (itens.ehMochila(item)) {
            return false;
        }
        if (definicao.profissao() == null) {
            return true;
        }
        return filtro.permite(definicao.profissao(), item);
    }

    public void aoFechar(MochilaAberta mochila) {
        if (desligando) {
            return;
        }
        if (mochila.suja()) {
            salvar(mochila, false);
        }
        // O jogador que está fechando ainda aparece na lista de espectadores aqui.
        if (mochila.inventario().getViewers().size() <= 1) {
            abertas.remove(mochila.id());
        }
    }

    public void salvarPendentes() {
        for (MochilaAberta mochila : abertas.values()) {
            if (mochila.suja()) {
                salvar(mochila, false);
            }
        }
    }

    public void fecharTudo() {
        desligando = true;
        for (MochilaAberta mochila : new ArrayList<>(abertas.values())) {
            for (HumanEntity espectador : new ArrayList<>(mochila.inventario().getViewers())) {
                espectador.closeInventory();
            }
            salvar(mochila, true);
        }
        abertas.clear();
        for (BukkitTask tarefa : aberturasPendentes.values()) {
            tarefa.cancel();
        }
        aberturasPendentes.clear();
        desligando = false;
    }

    private void salvar(MochilaAberta mochila, boolean sincrono) {
        armazenamento.salvar(mochila.id(), mochila.inventario().getContents(), sincrono);
        mochila.marcarLimpa();
    }

    public List<String> tipos() {
        return new ArrayList<>(definicoes.keySet());
    }
}
