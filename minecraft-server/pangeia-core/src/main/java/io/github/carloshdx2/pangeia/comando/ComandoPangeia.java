package io.github.carloshdx2.pangeia.comando;

import io.github.carloshdx2.pangeia.PangeiaCore;
import io.github.carloshdx2.pangeia.mochila.DefinicaoMochila;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ComandoPangeia implements TabExecutor {

    private final PangeiaCore plugin;

    public ComandoPangeia(PangeiaCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender remetente, Command comando, String rotulo, String[] argumentos) {
        if (argumentos.length == 0) {
            enviarAjuda(remetente);
            return true;
        }

        switch (argumentos[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                plugin.recarregar();
                remetente.sendMessage(ChatColor.GREEN + "PangeiaCore recarregado.");
            }
            case "mochila" -> {
                if (argumentos.length != 4 || !argumentos[1].equalsIgnoreCase("dar")) {
                    remetente.sendMessage(ChatColor.RED + "Uso: /pangeia mochila dar <jogador> <tipo>");
                    return true;
                }
                darMochila(remetente, argumentos[2], argumentos[3]);
            }
            case "recursos" -> remetente.sendMessage(ChatColor.GRAY + plugin.servicoResetMundo().status());
            default -> enviarAjuda(remetente);
        }
        return true;
    }

    private void darMochila(CommandSender remetente, String nomeJogador, String tipo) {
        Player jogador = Bukkit.getPlayerExact(nomeJogador);
        if (jogador == null) {
            remetente.sendMessage(ChatColor.RED + "Jogador " + nomeJogador + " não está online.");
            return;
        }
        DefinicaoMochila definicao = plugin.servicoMochila().definicao(tipo);
        if (definicao == null) {
            remetente.sendMessage(ChatColor.RED + "Tipo de mochila desconhecido: " + tipo);
            return;
        }

        ItemStack mochila = plugin.servicoMochila().itens().criar(definicao);
        // Inventário cheio não pode engolir a compra: cai no chão do lado do jogador.
        if (!jogador.getInventory().addItem(mochila).isEmpty()) {
            jogador.getWorld().dropItemNaturally(jogador.getLocation(), mochila);
            jogador.sendMessage(ChatColor.YELLOW + "Sua mochila caiu no chão porque o inventário estava cheio.");
        }
        remetente.sendMessage(ChatColor.GREEN + "Mochila " + tipo + " entregue para " + jogador.getName() + ".");
    }

    private void enviarAjuda(CommandSender remetente) {
        remetente.sendMessage(ChatColor.GRAY + "/pangeia mochila dar <jogador> <tipo>");
        remetente.sendMessage(ChatColor.GRAY + "/pangeia recursos");
        remetente.sendMessage(ChatColor.GRAY + "/pangeia reload");
    }

    @Override
    public List<String> onTabComplete(CommandSender remetente, Command comando, String rotulo, String[] argumentos) {
        if (argumentos.length == 1) {
            return filtrar(List.of("mochila", "recursos", "reload"), argumentos[0]);
        }
        if (argumentos.length == 2 && argumentos[0].equalsIgnoreCase("mochila")) {
            return filtrar(List.of("dar"), argumentos[1]);
        }
        if (argumentos.length == 3 && argumentos[0].equalsIgnoreCase("mochila")) {
            List<String> nomes = new ArrayList<>();
            for (Player jogador : Bukkit.getOnlinePlayers()) {
                nomes.add(jogador.getName());
            }
            return filtrar(nomes, argumentos[2]);
        }
        if (argumentos.length == 4 && argumentos[0].equalsIgnoreCase("mochila")) {
            return filtrar(plugin.servicoMochila().tipos(), argumentos[3]);
        }
        return Collections.emptyList();
    }

    private List<String> filtrar(List<String> opcoes, String prefixo) {
        List<String> resultado = new ArrayList<>();
        for (String opcao : opcoes) {
            if (opcao.toLowerCase(Locale.ROOT).startsWith(prefixo.toLowerCase(Locale.ROOT))) {
                resultado.add(opcao);
            }
        }
        return resultado;
    }
}
