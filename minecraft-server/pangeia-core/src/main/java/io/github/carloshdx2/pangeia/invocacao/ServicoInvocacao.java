package io.github.carloshdx2.pangeia.invocacao;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Esqueleto genérico das invocações: um item chama/dispensa uma entidade vinculada ao dono.
 * Nenhuma mecânica por categoria (elemental/terrestre/voadora/submersa) ainda — só a base
 * comum aos 4 tipos do design (ver docs/invocacoes-e-locomocao.md).
 */
public final class ServicoInvocacao {

    private final PangeiaChaves chaves;
    private final ItensInvocacao itens;
    private final Map<UUID, UUID> ativas = new HashMap<>();
    private Map<String, DefinicaoInvocacao> definicoes = new HashMap<>();

    public ServicoInvocacao(PangeiaChaves chaves, ItensInvocacao itens) {
        this.chaves = chaves;
        this.itens = itens;
    }

    public void definirDefinicoes(Map<String, DefinicaoInvocacao> definicoes) {
        this.definicoes = definicoes;
    }

    public DefinicaoInvocacao definicao(String tipo) {
        return tipo == null ? null : definicoes.get(tipo);
    }

    public List<String> tipos() {
        return new ArrayList<>(definicoes.keySet());
    }

    public ItensInvocacao itens() {
        return itens;
    }

    public void alternar(Player jogador, ItemStack item) {
        UUID jogadorId = jogador.getUniqueId();
        UUID ativa = ativas.remove(jogadorId);
        if (ativa != null) {
            removerEntidade(ativa);
            jogador.sendMessage(ChatColor.GRAY + "Invocação dispensada.");
            return;
        }

        DefinicaoInvocacao definicao = definicao(itens.lerTipo(item));
        if (definicao == null) {
            jogador.sendMessage(ChatColor.RED + "Essa invocação não existe mais na configuração do servidor.");
            return;
        }

        Entity entidade = jogador.getWorld().spawnEntity(jogador.getLocation(), definicao.entidade());
        entidade.getPersistentDataContainer().set(chaves.invocacaoDono, PersistentDataType.STRING, jogadorId.toString());
        entidade.getPersistentDataContainer().set(chaves.invocacaoTipo, PersistentDataType.STRING, definicao.id());
        entidade.setCustomName(ChatColor.translateAlternateColorCodes('&', definicao.nome()));
        entidade.setCustomNameVisible(true);
        if (entidade instanceof LivingEntity viva) {
            vincularAoDono(jogador, viva);
        }

        ativas.put(jogadorId, entidade.getUniqueId());
        jogador.sendMessage(ChatColor.GREEN + "Invocação chamada.");
    }

    /** Sem isso a entidade nasce sem dono: monta hostil (cavalo) ou solta atacável (lobo). */
    private void vincularAoDono(Player jogador, LivingEntity entidade) {
        if (entidade instanceof Tameable domavel) {
            domavel.setTamed(true);
            domavel.setOwner(jogador);
        }
        if (entidade instanceof Ageable envelhecivel) {
            envelhecivel.setAdult();
        }
        if (entidade instanceof AbstractHorse cavalo) {
            cavalo.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            cavalo.addPassenger(jogador);
        }
    }

    public void dispensar(UUID jogadorId) {
        UUID ativa = ativas.remove(jogadorId);
        if (ativa != null) {
            removerEntidade(ativa);
        }
    }

    /** Chamado quando qualquer entidade morre; só age se for uma invocação rastreada. */
    public void aoEntidadeMorrer(Entity entidade) {
        String donoBruto = entidade.getPersistentDataContainer().get(chaves.invocacaoDono, PersistentDataType.STRING);
        if (donoBruto == null) {
            return;
        }
        try {
            ativas.remove(UUID.fromString(donoBruto), entidade.getUniqueId());
        } catch (IllegalArgumentException ignorado) {
            // PDC corrompido ou de outra fonte — nada a limpar.
        }
    }

    private void removerEntidade(UUID entidadeId) {
        Entity entidade = Bukkit.getEntity(entidadeId);
        if (entidade != null) {
            entidade.remove();
        }
    }
}
