package io.github.carloshdx2.pangeia;

import io.github.carloshdx2.pangeia.comando.ComandoPangeia;
import io.github.carloshdx2.pangeia.invocacao.DefinicaoInvocacao;
import io.github.carloshdx2.pangeia.invocacao.ItensInvocacao;
import io.github.carloshdx2.pangeia.invocacao.OuvinteInvocacao;
import io.github.carloshdx2.pangeia.invocacao.ServicoInvocacao;
import io.github.carloshdx2.pangeia.ligamento.OuvinteLigamento;
import io.github.carloshdx2.pangeia.ligamento.ServicoLigamento;
import io.github.carloshdx2.pangeia.mochila.ArmazenamentoMochila;
import io.github.carloshdx2.pangeia.mochila.DefinicaoMochila;
import io.github.carloshdx2.pangeia.mochila.FiltroProfissao;
import io.github.carloshdx2.pangeia.mochila.ItensMochila;
import io.github.carloshdx2.pangeia.mochila.OuvinteMochila;
import io.github.carloshdx2.pangeia.mochila.ServicoMochila;
import io.github.carloshdx2.pangeia.mundo.ServicoResetMundo;
import io.github.carloshdx2.pangeia.pergaminho.DefinicaoPergaminho;
import io.github.carloshdx2.pangeia.pergaminho.ItensPergaminho;
import io.github.carloshdx2.pangeia.pergaminho.OuvintePergaminho;
import io.github.carloshdx2.pangeia.pergaminho.ServicoPergaminho;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public final class PangeiaCore extends JavaPlugin {

    private ServicoMochila servicoMochila;
    private ServicoLigamento servicoLigamento;
    private ServicoPergaminho servicoPergaminho;
    private ServicoInvocacao servicoInvocacao;
    private ServicoResetMundo servicoResetMundo;
    private FiltroProfissao filtroProfissao;
    private int tarefaAutosave = -1;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PangeiaChaves chaves = new PangeiaChaves(this);
        ItensMochila itens = new ItensMochila(chaves);
        filtroProfissao = new FiltroProfissao();
        servicoMochila = new ServicoMochila(this, new ArmazenamentoMochila(this), itens, filtroProfissao);
        servicoLigamento = new ServicoLigamento(chaves);
        servicoPergaminho = new ServicoPergaminho(this, new ItensPergaminho(chaves));
        servicoInvocacao = new ServicoInvocacao(chaves, new ItensInvocacao(chaves));
        servicoResetMundo = new ServicoResetMundo(this);

        aplicarConfiguracao();

        getServer().getPluginManager().registerEvents(new OuvinteMochila(servicoMochila, itens), this);
        getServer().getPluginManager().registerEvents(new OuvinteLigamento(servicoLigamento), this);
        getServer().getPluginManager().registerEvents(new OuvintePergaminho(servicoPergaminho), this);
        getServer().getPluginManager().registerEvents(new OuvinteInvocacao(servicoInvocacao), this);

        PluginCommand comando = getCommand("pangeia");
        if (comando != null) {
            ComandoPangeia executor = new ComandoPangeia(this);
            comando.setExecutor(executor);
            comando.setTabCompleter(executor);
        }
    }

    @Override
    public void onDisable() {
        if (servicoMochila != null) {
            servicoMochila.fecharTudo();
        }
        if (servicoPergaminho != null) {
            servicoPergaminho.cancelarTudo();
        }
        if (servicoResetMundo != null) {
            servicoResetMundo.parar();
        }
    }

    public void recarregar() {
        // Fecha (e salva) tudo antes de trocar as definições: uma mochila aberta com a
        // definição antiga aplicaria filtros que não existem mais.
        servicoMochila.fecharTudo();
        servicoPergaminho.cancelarTudo();
        reloadConfig();
        aplicarConfiguracao();
    }

    private void aplicarConfiguracao() {
        filtroProfissao.carregar(getConfig().getConfigurationSection("itens-por-profissao"));
        servicoMochila.definirDefinicoes(carregarDefinicoes());
        servicoLigamento.carregar(getConfig().getConfigurationSection("ligamento"));
        servicoPergaminho.carregar(getConfig().getConfigurationSection("pergaminhos"));
        servicoPergaminho.definirDefinicoes(carregarDefinicoesPergaminho());
        servicoInvocacao.definirDefinicoes(carregarDefinicoesInvocacao());
        servicoResetMundo.carregar(getConfig().getConfigurationSection("reset-mundo-recursos"));
        reagendarAutosave();
    }

    private void reagendarAutosave() {
        if (tarefaAutosave != -1) {
            getServer().getScheduler().cancelTask(tarefaAutosave);
        }
        long intervalo = Math.max(30L, getConfig().getLong("mochilas.autosave-segundos", 300L)) * 20L;
        tarefaAutosave = getServer().getScheduler()
                .runTaskTimer(this, servicoMochila::salvarPendentes, intervalo, intervalo)
                .getTaskId();
    }

    private Map<String, DefinicaoMochila> carregarDefinicoes() {
        Map<String, DefinicaoMochila> definicoes = new LinkedHashMap<>();
        ConfigurationSection tipos = getConfig().getConfigurationSection("mochilas.tipos");
        if (tipos == null) {
            getLogger().warning("Nenhum tipo de mochila configurado em mochilas.tipos.");
            return definicoes;
        }

        for (String id : tipos.getKeys(false)) {
            ConfigurationSection secao = tipos.getConfigurationSection(id);
            if (secao == null) {
                continue;
            }
            String nomeMaterial = secao.getString("material", "LEATHER");
            Material material = Material.matchMaterial(nomeMaterial);
            if (material == null) {
                getLogger().warning("Mochila " + id + " ignorada: material desconhecido " + nomeMaterial + ".");
                continue;
            }
            Integer customModelData = secao.contains("custom-model-data")
                    ? secao.getInt("custom-model-data")
                    : null;
            int linhas = Math.max(1, Math.min(6, secao.getInt("linhas", 3)));

            definicoes.put(id, new DefinicaoMochila(
                    id,
                    secao.getString("nome", id),
                    material,
                    customModelData,
                    linhas,
                    secao.getString("cargo"),
                    secao.getString("profissao"),
                    secao.getBoolean("ligamento", false),
                    Math.max(0, secao.getInt("atraso-abertura-ticks", 0))));
        }
        return definicoes;
    }

    private Map<String, DefinicaoPergaminho> carregarDefinicoesPergaminho() {
        Map<String, DefinicaoPergaminho> definicoes = new LinkedHashMap<>();
        ConfigurationSection tipos = getConfig().getConfigurationSection("pergaminhos.tipos");
        if (tipos == null) {
            return definicoes;
        }

        for (String id : tipos.getKeys(false)) {
            ConfigurationSection secao = tipos.getConfigurationSection(id);
            if (secao == null) {
                continue;
            }
            String nomeMaterial = secao.getString("material", "PAPER");
            Material material = Material.matchMaterial(nomeMaterial);
            if (material == null) {
                getLogger().warning("Pergaminho " + id + " ignorado: material desconhecido " + nomeMaterial + ".");
                continue;
            }
            Integer customModelData = secao.contains("custom-model-data")
                    ? secao.getInt("custom-model-data")
                    : null;

            definicoes.put(id, new DefinicaoPergaminho(
                    id,
                    secao.getString("nome", id),
                    material,
                    customModelData,
                    secao.getStringList("lore")));
        }
        return definicoes;
    }

    private Map<String, DefinicaoInvocacao> carregarDefinicoesInvocacao() {
        Map<String, DefinicaoInvocacao> definicoes = new LinkedHashMap<>();
        ConfigurationSection tipos = getConfig().getConfigurationSection("invocacoes.tipos");
        if (tipos == null) {
            return definicoes;
        }

        for (String id : tipos.getKeys(false)) {
            ConfigurationSection secao = tipos.getConfigurationSection(id);
            if (secao == null) {
                continue;
            }
            String nomeMaterial = secao.getString("material", "PAPER");
            Material material = Material.matchMaterial(nomeMaterial);
            if (material == null) {
                getLogger().warning("Invocação " + id + " ignorada: material desconhecido " + nomeMaterial + ".");
                continue;
            }
            String nomeEntidade = secao.getString("entidade", "");
            EntityType entidade;
            try {
                entidade = EntityType.valueOf(nomeEntidade.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException excecao) {
                getLogger().warning("Invocação " + id + " ignorada: entidade desconhecida " + nomeEntidade + ".");
                continue;
            }
            Integer customModelData = secao.contains("custom-model-data")
                    ? secao.getInt("custom-model-data")
                    : null;

            definicoes.put(id, new DefinicaoInvocacao(
                    id,
                    secao.getString("nome", id),
                    secao.getString("categoria", ""),
                    entidade,
                    material,
                    customModelData,
                    secao.getStringList("lore"),
                    secao.contains("velocidade") ? secao.getDouble("velocidade") : null,
                    secao.contains("pulo") ? secao.getDouble("pulo") : null,
                    secao.contains("vida") ? secao.getDouble("vida") : null,
                    secao.getString("cor-cavalo"),
                    secao.getString("estilo-cavalo")));
        }
        return definicoes;
    }

    public ServicoMochila servicoMochila() {
        return servicoMochila;
    }

    public ServicoPergaminho servicoPergaminho() {
        return servicoPergaminho;
    }

    public ServicoInvocacao servicoInvocacao() {
        return servicoInvocacao;
    }

    public ServicoResetMundo servicoResetMundo() {
        return servicoResetMundo;
    }
}
