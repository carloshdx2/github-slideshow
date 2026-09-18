package io.github.carloshdx2.pangeia.invocacao;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 * Invocações: um item chama/dispensa uma entidade vinculada ao dono. A base é comum aos 4
 * tipos do design (ver docs/invocacoes-e-locomocao.md); montaria terrestre e voadora já têm
 * mecânica própria, elemental e submersa ainda não.
 */
public final class ServicoInvocacao {

    private static final String CATEGORIA_VOADORA = "voadora";

    private final Plugin plugin;
    private final PangeiaChaves chaves;
    private final ItensInvocacao itens;
    private final Map<UUID, UUID> ativas = new HashMap<>();
    private final Map<UUID, BukkitTask> escoltas = new HashMap<>();
    private final Set<UUID> vooConcedido = new HashSet<>();
    private Map<String, DefinicaoInvocacao> definicoes = new HashMap<>();

    public ServicoInvocacao(Plugin plugin, PangeiaChaves chaves, ItensInvocacao itens) {
        this.plugin = plugin;
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
            pararVoo(jogadorId);
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
            aplicarAtributos(viva, definicao);
        }

        if (CATEGORIA_VOADORA.equalsIgnoreCase(definicao.categoria())) {
            iniciarVoo(jogador, entidade, definicao);
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

    /**
     * Sem isso a montaria nasce com atributos aleatórios do vanilla (velocidade/pulo/vida
     * variam a cada spawn) — não combina com uma invocação específica configurada. Também
     * fixa cor/estilo do cavalo, senão cada chamada spawna uma aparência diferente.
     */
    private void aplicarAtributos(LivingEntity entidade, DefinicaoInvocacao definicao) {
        aplicarAtributo(entidade, Attribute.GENERIC_MOVEMENT_SPEED, definicao.velocidade());
        aplicarAtributo(entidade, Attribute.GENERIC_MAX_HEALTH, definicao.vida());
        if (definicao.vida() != null) {
            entidade.setHealth(definicao.vida());
        }
        if (entidade instanceof AbstractHorse) {
            aplicarAtributo(entidade, Attribute.HORSE_JUMP_STRENGTH, definicao.pulo());
        }
        if (entidade instanceof Horse cavalo) {
            aplicarEnum(definicao.corCavalo(), Horse.Color.class, cavalo::setColor);
            aplicarEnum(definicao.estiloCavalo(), Horse.Style.class, cavalo::setStyle);
        }
    }

    private void aplicarAtributo(LivingEntity entidade, Attribute atributo, Double valor) {
        if (valor == null) {
            return;
        }
        AttributeInstance instancia = entidade.getAttribute(atributo);
        if (instancia != null) {
            instancia.setBaseValue(valor);
        }
    }

    private <E extends Enum<E>> void aplicarEnum(String valorConfigurado, Class<E> tipo, Consumer<E> aplicar) {
        if (valorConfigurado == null || valorConfigurado.isBlank()) {
            return;
        }
        try {
            aplicar.accept(Enum.valueOf(tipo, valorConfigurado.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ignorado) {
            // Valor inválido na config — mantém a aparência padrão da entidade em vez de falhar.
        }
    }

    /**
     * Nenhuma entidade vanilla é ao mesmo tempo "montável" e "voadora controlável pelo
     * jogador" (cavalo não voa, fantasma/morcego não têm assento). A saída real é dar voo
     * de verdade ao jogador (como criativo) e usar a entidade como escolta cosmética que
     * segue por uma tarefa própria — não como veículo.
     */
    private void iniciarVoo(Player jogador, Entity escolta, DefinicaoInvocacao definicao) {
        UUID jogadorId = jogador.getUniqueId();
        if (jogador.getGameMode() != GameMode.CREATIVE && jogador.getGameMode() != GameMode.SPECTATOR) {
            jogador.setAllowFlight(true);
            jogador.setFlying(true);
            if (definicao.velocidade() != null) {
                float velocidade = (float) Math.max(-1.0, Math.min(1.0, definicao.velocidade()));
                jogador.setFlySpeed(velocidade);
            }
            vooConcedido.add(jogadorId);
        }

        escolta.setGravity(false);
        if (escolta instanceof LivingEntity viva) {
            viva.setAI(false);
            // Sem isso um mob hostil de passagem derruba a escolta e o jogador cai do céu
            // sem aviso — a invulnerabilidade dura só enquanto o voo está ativo.
            viva.setInvulnerable(true);
        }

        UUID entidadeId = escolta.getUniqueId();
        BukkitTask tarefa = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player jogadorAtual = Bukkit.getPlayer(jogadorId);
            Entity entidadeAtual = Bukkit.getEntity(entidadeId);
            if (jogadorAtual == null || !jogadorAtual.isOnline() || entidadeAtual == null || entidadeAtual.isDead()) {
                return;
            }
            Location doJogador = jogadorAtual.getLocation();
            Vector direcao = doJogador.getDirection().setY(0);
            if (direcao.lengthSquared() > 0.0001) {
                direcao.normalize();
            }
            Location alvo = doJogador.clone().subtract(direcao.multiply(2.5)).add(0, 1.0, 0);
            alvo.setDirection(doJogador.getDirection());
            entidadeAtual.teleport(alvo);
        }, 1L, 2L);
        escoltas.put(jogadorId, tarefa);
    }

    private void pararVoo(UUID jogadorId) {
        BukkitTask tarefa = escoltas.remove(jogadorId);
        if (tarefa != null) {
            tarefa.cancel();
        }
        if (vooConcedido.remove(jogadorId)) {
            Player jogador = Bukkit.getPlayer(jogadorId);
            if (jogador != null && jogador.getGameMode() != GameMode.CREATIVE
                    && jogador.getGameMode() != GameMode.SPECTATOR) {
                jogador.setFlying(false);
                jogador.setAllowFlight(false);
            }
        }
    }

    /**
     * Sem isso, desligar o servidor com alguém em voo concedido deixaria a habilidade de
     * voar gravada no jogador (o cliente persiste isso) — ele voltaria voando sem invocação
     * nenhuma na próxima sessão.
     */
    public void encerrarTudo() {
        for (UUID jogadorId : new ArrayList<>(vooConcedido)) {
            pararVoo(jogadorId);
        }
    }

    public void dispensar(UUID jogadorId) {
        UUID ativa = ativas.remove(jogadorId);
        if (ativa != null) {
            removerEntidade(ativa);
        }
        pararVoo(jogadorId);
    }

    /** Chamado quando qualquer entidade morre; só age se for uma invocação rastreada. */
    public void aoEntidadeMorrer(Entity entidade) {
        String donoBruto = entidade.getPersistentDataContainer().get(chaves.invocacaoDono, PersistentDataType.STRING);
        if (donoBruto == null) {
            return;
        }
        try {
            UUID dono = UUID.fromString(donoBruto);
            if (ativas.remove(dono, entidade.getUniqueId())) {
                pararVoo(dono);
            }
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
