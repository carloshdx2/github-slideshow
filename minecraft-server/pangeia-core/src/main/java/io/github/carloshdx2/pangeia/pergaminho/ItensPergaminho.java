package io.github.carloshdx2.pangeia.pergaminho;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ItensPergaminho {

    private final PangeiaChaves chaves;

    public ItensPergaminho(PangeiaChaves chaves) {
        this.chaves = chaves;
    }

    public ItemStack criar(DefinicaoPergaminho definicao) {
        ItemStack item = new ItemStack(definicao.material(), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', definicao.nome()));
        if (definicao.customModelData() != null) {
            meta.setCustomModelData(definicao.customModelData());
        }
        List<String> lore = definicao.lore();
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore.stream().map(linha -> ChatColor.translateAlternateColorCodes('&', linha)).toList());
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(chaves.pergaminhoTipo, PersistentDataType.STRING, definicao.id());

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Prioriza a tag própria do PangeiaCore; cai pro ID do Oraxen se existir, para o dia
     * em que um pergaminho vier de lá em vez de ser dado por /pangeia pergaminho dar.
     */
    public String lerTipo(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String proprio = item.getItemMeta().getPersistentDataContainer()
                .get(chaves.pergaminhoTipo, PersistentDataType.STRING);
        if (proprio != null) {
            return proprio;
        }
        return PangeiaChaves.lerOraxenId(item);
    }
}
