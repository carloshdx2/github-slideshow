package io.github.carloshdx2.pangeia.pergaminho;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class ServicoPergaminho {

    private final Plugin plugin;
    private final Map<String, Destino> destinos = new HashMap<>();
    private final Map<UUID, BukkitTask> conjuracoes = new HashMap<>();
    private int tempoConjuracaoTicks = 60;
    private boolean cancelarAoTomarDano = true;

    public ServicoPergaminho(Plugin plugin) {
        this.plugin = plugin;
    }

    public void carregar(ConfigurationSection secao) {
        destinos.clear();
        if (secao == null) {
            return;
        }
        tempoConjuracaoTicks = Math.max(0, secao.getInt("tempo-conjuracao-ticks", 60));
        cancelarAoTomarDano = secao.getBoolean("cancelar-ao-tomar-dano", true);

        ConfigurationSection lista = secao.getConfigurationSection("destinos");
        if (lista == null) {
            return;
        }
        for (String oraxenId : lista.getKeys(false)) {
            ConfigurationSection destino = lista.getConfigurationSection(oraxenId);
            if (destino == null) {
                continue;
            }
            destinos.put(oraxenId.toLowerCase(Locale.ROOT), new Destino(
                    destino.getString("mundo", "world"),
                    destino.getDouble("x"),
                    destino.getDouble("y"),
                    destino.getDouble("z"),
                    (float) destino.getDouble("yaw"),
                    (float) destino.getDouble("pitch")));
        }
    }

    public boolean cancelarAoTomarDano() {
        return cancelarAoTomarDano;
    }

    public Destino destinoDe(ItemStack item) {
        String oraxenId = PangeiaChaves.lerOraxenId(item);
        return oraxenId == null ? null : destinos.get(oraxenId.toLowerCase(Locale.ROOT));
    }

    public void usar(Player jogador, ItemStack pergaminho) {
        Destino destino = destinoDe(pergaminho);
        if (destino == null || conjuracoes.containsKey(jogador.getUniqueId())) {
            return;
        }
        String oraxenId = PangeiaChaves.lerOraxenId(pergaminho);

        if (tempoConjuracaoTicks <= 0) {
            concluir(jogador, oraxenId, destino);
            return;
        }
        jogador.sendMessage(ChatColor.GRAY + "Lendo o pergaminho... fique parado e sem levar dano.");
        BukkitTask tarefa = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            conjuracoes.remove(jogador.getUniqueId());
            if (jogador.isOnline()) {
                concluir(jogador, oraxenId, destino);
            }
        }, tempoConjuracaoTicks);
        conjuracoes.put(jogador.getUniqueId(), tarefa);
    }

    public boolean cancelar(UUID jogadorId) {
        BukkitTask tarefa = conjuracoes.remove(jogadorId);
        if (tarefa == null) {
            return false;
        }
        tarefa.cancel();
        return true;
    }

    public void cancelarTudo() {
        for (BukkitTask tarefa : conjuracoes.values()) {
            tarefa.cancel();
        }
        conjuracoes.clear();
    }

    private void concluir(Player jogador, String oraxenId, Destino destino) {
        ItemStack naMao = jogador.getInventory().getItemInMainHand();
        if (oraxenId == null || !oraxenId.equalsIgnoreCase(PangeiaChaves.lerOraxenId(naMao))) {
            jogador.sendMessage(ChatColor.RED + "Você precisa continuar segurando o pergaminho até o fim.");
            return;
        }
        World mundo = Bukkit.getWorld(destino.mundo());
        if (mundo == null) {
            jogador.sendMessage(ChatColor.RED + "O destino desse pergaminho não existe mais.");
            plugin.getLogger().warning("Pergaminho " + oraxenId + " aponta para o mundo inexistente "
                    + destino.mundo() + ".");
            return;
        }

        // Consome antes de teleportar: se o teleporte falhar depois disso o jogador perde
        // o pergaminho, mas o caminho inverso permitiria usar o mesmo pergaminho duas vezes.
        ItemStack restante = naMao.clone();
        if (restante.getAmount() <= 1) {
            jogador.getInventory().setItemInMainHand(null);
        } else {
            restante.setAmount(restante.getAmount() - 1);
            jogador.getInventory().setItemInMainHand(restante);
        }

        jogador.teleport(new Location(mundo, destino.x(), destino.y(), destino.z(),
                destino.yaw(), destino.pitch()));
    }

    public record Destino(String mundo, double x, double y, double z, float yaw, float pitch) {
    }
}
