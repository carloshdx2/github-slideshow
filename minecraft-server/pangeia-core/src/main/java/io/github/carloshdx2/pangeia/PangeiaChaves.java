package io.github.carloshdx2.pangeia;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class PangeiaChaves {

    /** Lido diretamente do PDC em vez de depender da API do Oraxen em tempo de compilação. */
    private static final NamespacedKey ORAXEN_ID = NamespacedKey.fromString("oraxen:id");

    public final NamespacedKey mochilaId;
    public final NamespacedKey mochilaTipo;
    public final NamespacedKey ligamento;
    public final NamespacedKey pergaminhoTipo;
    public final NamespacedKey invocacaoTipo;
    public final NamespacedKey invocacaoDono;

    public PangeiaChaves(Plugin plugin) {
        this.mochilaId = new NamespacedKey(plugin, "mochila_id");
        this.mochilaTipo = new NamespacedKey(plugin, "mochila_tipo");
        this.ligamento = new NamespacedKey(plugin, "ligamento");
        this.pergaminhoTipo = new NamespacedKey(plugin, "pergaminho_tipo");
        this.invocacaoTipo = new NamespacedKey(plugin, "invocacao_tipo");
        this.invocacaoDono = new NamespacedKey(plugin, "invocacao_dono");
    }

    public static String lerOraxenId(ItemStack item) {
        if (ORAXEN_ID == null || item == null || !item.hasItemMeta()) {
            return null;
        }
        return item.getItemMeta().getPersistentDataContainer().get(ORAXEN_ID, PersistentDataType.STRING);
    }
}
