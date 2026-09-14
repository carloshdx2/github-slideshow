package io.github.carloshdx2.pangeia.mochila;

import org.bukkit.Material;

public record DefinicaoMochila(
        String id,
        String nome,
        Material material,
        Integer customModelData,
        int linhas,
        String cargo,
        String profissao,
        boolean ligamento,
        int atrasoAberturaTicks
) {

    public int tamanho() {
        return linhas * 9;
    }

    public String permissaoCargo() {
        return cargo == null ? null : "pangeia.cargo." + cargo;
    }
}
