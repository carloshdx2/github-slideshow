package io.github.carloshdx2.pangeia.mochila;

import java.util.UUID;
import org.bukkit.inventory.Inventory;

public final class MochilaAberta {

    private final UUID id;
    private final DefinicaoMochila definicao;
    private final Inventory inventario;
    private boolean suja;

    public MochilaAberta(UUID id, DefinicaoMochila definicao, Inventory inventario) {
        this.id = id;
        this.definicao = definicao;
        this.inventario = inventario;
    }

    public UUID id() {
        return id;
    }

    public DefinicaoMochila definicao() {
        return definicao;
    }

    public Inventory inventario() {
        return inventario;
    }

    public boolean suja() {
        return suja;
    }

    public void marcarSuja() {
        this.suja = true;
    }

    public void marcarLimpa() {
        this.suja = false;
    }
}
