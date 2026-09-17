package io.github.carloshdx2.pangeia.pergaminho;

import java.util.List;
import org.bukkit.Material;

public record DefinicaoPergaminho(
        String id,
        String nome,
        Material material,
        Integer customModelData,
        List<String> lore
) {
}
