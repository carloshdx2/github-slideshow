package io.github.carloshdx2.pangeia.mochila;

import io.github.carloshdx2.pangeia.PangeiaChaves;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class ItensMochila {

    private final PangeiaChaves chaves;

    public ItensMochila(PangeiaChaves chaves) {
        this.chaves = chaves;
    }

    public ItemStack criar(DefinicaoMochila definicao) {
        ItemStack item = new ItemStack(definicao.material(), 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', definicao.nome()));
        if (definicao.customModelData() != null) {
            meta.setCustomModelData(definicao.customModelData());
        }

        List<String> lore = new ArrayList<>();
        if (definicao.cargo() != null) {
            lore.add(ChatColor.GRAY + "Só abre para quem tem o cargo " + ChatColor.WHITE + definicao.cargo() + ChatColor.GRAY + ".");
        }
        if (definicao.profissao() != null) {
            lore.add(ChatColor.GRAY + "Só guarda itens de " + ChatColor.WHITE + definicao.profissao() + ChatColor.GRAY + ".");
        }
        if (definicao.ligamento()) {
            lore.add(ChatColor.DARK_PURPLE + "Maldição do ligamento" + ChatColor.GRAY + " — não cai ao morrer.");
        }
        meta.setLore(lore);

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(chaves.mochilaId, PersistentDataType.STRING, UUID.randomUUID().toString());
        pdc.set(chaves.mochilaTipo, PersistentDataType.STRING, definicao.id());
        if (definicao.ligamento()) {
            pdc.set(chaves.ligamento, PersistentDataType.BYTE, (byte) 1);
        }

        item.setItemMeta(meta);
        return item;
    }

    public UUID lerId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String bruto = item.getItemMeta().getPersistentDataContainer()
                .get(chaves.mochilaId, PersistentDataType.STRING);
        if (bruto == null) {
            return null;
        }
        try {
            return UUID.fromString(bruto);
        } catch (IllegalArgumentException excecao) {
            return null;
        }
    }

    public String lerTipo(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer()
                .get(chaves.mochilaTipo, PersistentDataType.STRING);
    }

    public boolean ehMochila(ItemStack item) {
        return lerId(item) != null;
    }
}
