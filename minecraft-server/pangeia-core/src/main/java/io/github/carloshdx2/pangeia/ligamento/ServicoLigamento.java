package io.github.carloshdx2.pangeia.ligamento;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/** "Maldição do ligamento": o item não cai quando o jogador morre. */
public final class ServicoLigamento {

    private final PangeiaChaves chaves;
    private final Set<String> oraxenIds = new HashSet<>();
    private final Set<Material> materiais = new HashSet<>();

    public ServicoLigamento(PangeiaChaves chaves) {
        this.chaves = chaves;
    }

    public void carregar(ConfigurationSection secao) {
        oraxenIds.clear();
        materiais.clear();
        if (secao == null) {
            return;
        }
        for (String id : secao.getStringList("oraxen-ids")) {
            oraxenIds.add(id.trim().toLowerCase(Locale.ROOT));
        }
        for (String nome : secao.getStringList("materiais")) {
            Material material = Material.matchMaterial(nome.trim().toUpperCase(Locale.ROOT));
            if (material != null) {
                materiais.add(material);
            }
        }
    }

    public boolean temLigamento(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer()
                .has(chaves.ligamento, PersistentDataType.BYTE)) {
            return true;
        }
        String oraxenId = PangeiaChaves.lerOraxenId(item);
        if (oraxenId != null && oraxenIds.contains(oraxenId.toLowerCase(Locale.ROOT))) {
            return true;
        }
        return materiais.contains(item.getType());
    }
}
