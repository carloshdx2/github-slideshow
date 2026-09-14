package io.github.carloshdx2.pangeia.mochila;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/** Identifica um inventário como mochila sem depender do título da tela. */
public final class SuporteMochila implements InventoryHolder {

    private MochilaAberta mochila;
    private Inventory inventario;

    public MochilaAberta mochila() {
        return mochila;
    }

    public void definir(MochilaAberta mochila, Inventory inventario) {
        this.mochila = mochila;
        this.inventario = inventario;
    }

    @Override
    public Inventory getInventory() {
        return inventario;
    }
}
