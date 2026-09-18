package io.github.carloshdx2.pangeia.invocacao;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ItensInvocacao {

    private final PangeiaChaves chaves;

    public ItensInvocacao(PangeiaChaves chaves) {
        this.chaves = chaves;
    }

    public ItemStack criar(DefinicaoInvocacao definicao) {
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
        pdc.set(chaves.invocacaoTipo, PersistentDataType.STRING, definicao.id());

        item.setItemMeta(meta);
        return item;
    }

    public String lerTipo(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .get(chaves.invocacaoTipo, PersistentDataType.STRING);
    }
}
