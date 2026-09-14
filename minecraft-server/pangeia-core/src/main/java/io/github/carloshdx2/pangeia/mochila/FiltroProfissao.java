package io.github.carloshdx2.pangeia.mochila;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public final class FiltroProfissao {

    private final Map<String, Set<String>> porProfissao = new HashMap<>();

    public void carregar(ConfigurationSection secao) {
        porProfissao.clear();
        if (secao == null) {
            return;
        }
        for (String profissao : secao.getKeys(false)) {
            List<String> entradas = secao.getStringList(profissao);
            Set<String> permitidos = new HashSet<>();
            for (String entrada : entradas) {
                permitidos.add(normalizar(entrada));
            }
            porProfissao.put(profissao.toLowerCase(Locale.ROOT), permitidos);
        }
    }

    public boolean permite(String profissao, ItemStack item) {
        Set<String> permitidos = porProfissao.get(profissao.toLowerCase(Locale.ROOT));
        // Sem lista configurada a mochila não filtra nada, em vez de rejeitar tudo:
        // uma profissão nova sem lista pronta viraria uma mochila inutilizável.
        if (permitidos == null || permitidos.isEmpty()) {
            return true;
        }
        String oraxenId = PangeiaChaves.lerOraxenId(item);
        if (oraxenId != null) {
            return permitidos.contains("oraxen:" + oraxenId.toLowerCase(Locale.ROOT));
        }
        return permitidos.contains(item.getType().name());
    }

    private String normalizar(String entrada) {
        String limpa = entrada.trim();
        if (limpa.toLowerCase(Locale.ROOT).startsWith("oraxen:")) {
            return limpa.toLowerCase(Locale.ROOT);
        }
        return limpa.toUpperCase(Locale.ROOT);
    }
}
