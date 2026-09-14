package io.github.carloshdx2.pangeia.mundo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Regenera periodicamente o mundo de recursos (mineradores/lenhadores/botânicos),
 * que por design é temporário — ver docs/fundacao-permissoes.md.
 */
public final class ServicoResetMundo {

    private static final long INTERVALO_VERIFICACAO_TICKS = 20L * 60L;

    private final Plugin plugin;
    private final File arquivoDados;
    private final Set<Integer> avisosEnviados = new HashSet<>();

    private boolean ativado;
    private String nomeMundo;
    private String nomeMundoSaida;
    private long intervaloMillis;
    private List<Integer> avisosMinutos = Collections.emptyList();
    private String comandoRegenerar;
    private long proximoReset;
    private int tarefa = -1;

    public ServicoResetMundo(Plugin plugin) {
        this.plugin = plugin;
        this.arquivoDados = new File(plugin.getDataFolder(), "dados.yml");
    }

    public void carregar(ConfigurationSection secao) {
        pararTarefa();
        avisosEnviados.clear();

        ativado = secao != null && secao.getBoolean("ativado", false);
        if (!ativado) {
            return;
        }

        nomeMundo = secao.getString("mundo", "recursos_pangeia");
        nomeMundoSaida = secao.getString("mundo-de-saida", "world");
        intervaloMillis = TimeUnit.DAYS.toMillis(Math.max(1, secao.getInt("intervalo-dias", 13)));
        comandoRegenerar = secao.getString("comando-regenerar", "mv regen %mundo% -s");

        List<Integer> avisos = new ArrayList<>(secao.getIntegerList("avisos-minutos"));
        avisos.removeIf(minuto -> minuto <= 0);
        Collections.sort(avisos);
        avisosMinutos = avisos;

        // Um erro de digitação aqui apagaria o mundo principal do servidor, que é
        // irreversível. Melhor recusar a ligar do que confiar na config.
        List<World> mundos = Bukkit.getWorlds();
        String mundoPadrao = mundos.isEmpty() ? "world" : mundos.get(0).getName();
        if (nomeMundo.equalsIgnoreCase(mundoPadrao) || nomeMundo.equalsIgnoreCase(nomeMundoSaida)) {
            ativado = false;
            plugin.getLogger().severe("Reset automático DESLIGADO: '" + nomeMundo
                    + "' é o mundo principal ou o mundo de saída. Isso apagaria o mundo errado.");
            return;
        }

        proximoReset = lerProximoReset();
        agendarTarefa();
        plugin.getLogger().info("Reset do mundo '" + nomeMundo + "' agendado para daqui a "
                + minutosRestantes() + " minutos.");
    }

    public void parar() {
        pararTarefa();
    }

    public String status() {
        if (!ativado) {
            return "Reset automático do mundo de recursos: desligado.";
        }
        return "Reset de '" + nomeMundo + "' em " + minutosRestantes() + " minutos.";
    }

    private void agendarTarefa() {
        tarefa = Bukkit.getScheduler()
                .runTaskTimer(plugin, this::verificar, INTERVALO_VERIFICACAO_TICKS, INTERVALO_VERIFICACAO_TICKS)
                .getTaskId();
    }

    private void pararTarefa() {
        if (tarefa != -1) {
            Bukkit.getScheduler().cancelTask(tarefa);
            tarefa = -1;
        }
    }

    private void verificar() {
        long restanteMillis = proximoReset - System.currentTimeMillis();
        if (restanteMillis <= 0) {
            executar();
            return;
        }
        long restanteMinutos = TimeUnit.MILLISECONDS.toMinutes(restanteMillis);
        for (int marco : avisosMinutos) {
            if (restanteMinutos <= marco && avisosEnviados.add(marco)) {
                avisar(marco);
                break;
            }
        }
    }

    private void avisar(int minutos) {
        World mundo = Bukkit.getWorld(nomeMundo);
        if (mundo == null) {
            return;
        }
        String mensagem = ChatColor.YELLOW + "O mundo de recursos será renovado em "
                + ChatColor.WHITE + minutos + ChatColor.YELLOW + " minuto(s). Leve o que coletou!";
        for (Player jogador : mundo.getPlayers()) {
            jogador.sendMessage(mensagem);
        }
    }

    private void executar() {
        World mundo = Bukkit.getWorld(nomeMundo);
        if (mundo == null) {
            plugin.getLogger().warning("Mundo '" + nomeMundo
                    + "' não está carregado; reset adiado para o próximo ciclo.");
            reagendar();
            return;
        }

        World saida = Bukkit.getWorld(nomeMundoSaida);
        if (saida == null) {
            plugin.getLogger().severe("Mundo de saída '" + nomeMundoSaida
                    + "' não existe; reset cancelado para não deixar jogadores presos.");
            reagendar();
            return;
        }

        for (Player jogador : new ArrayList<>(mundo.getPlayers())) {
            jogador.teleport(saida.getSpawnLocation());
            jogador.sendMessage(ChatColor.YELLOW + "O mundo de recursos foi renovado.");
        }

        String comando = comandoRegenerar.replace("%mundo%", nomeMundo);
        plugin.getLogger().info("Regenerando o mundo de recursos: " + comando);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), comando);
        reagendar();
    }

    private void reagendar() {
        proximoReset = System.currentTimeMillis() + intervaloMillis;
        avisosEnviados.clear();
        gravarProximoReset();
    }

    private long minutosRestantes() {
        return Math.max(0, TimeUnit.MILLISECONDS.toMinutes(proximoReset - System.currentTimeMillis()));
    }

    /** Sem marca salva, conta a partir de agora: ligar a opção não pode apagar o mundo na hora. */
    private long lerProximoReset() {
        YamlConfiguration dados = YamlConfiguration.loadConfiguration(arquivoDados);
        long salvo = dados.getLong("reset-mundo.proximo", 0L);
        if (salvo > 0L) {
            return salvo;
        }
        long proximo = System.currentTimeMillis() + intervaloMillis;
        gravar(proximo);
        return proximo;
    }

    private void gravarProximoReset() {
        gravar(proximoReset);
    }

    private void gravar(long quando) {
        YamlConfiguration dados = YamlConfiguration.loadConfiguration(arquivoDados);
        dados.set("reset-mundo.proximo", quando);
        try {
            dados.save(arquivoDados);
        } catch (IOException excecao) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao gravar a data do próximo reset.", excecao);
        }
    }
}
