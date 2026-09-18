package io.github.carloshdx2.pangeia.invocacao;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public record DefinicaoInvocacao(
        String id,
        String nome,
        String categoria,
        EntityType entidade,
        Material material,
        Integer customModelData,
        List<String> lore,
        Double velocidade,
        Double pulo,
        Double vida,
        String corCavalo,
        String estiloCavalo
) {
}
