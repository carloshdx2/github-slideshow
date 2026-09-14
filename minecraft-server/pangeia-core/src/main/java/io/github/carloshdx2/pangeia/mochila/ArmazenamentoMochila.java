package io.github.carloshdx2.pangeia.mochila;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public final class ArmazenamentoMochila {

    private final Plugin plugin;
    private final File pasta;

    public ArmazenamentoMochila(Plugin plugin) {
        this.plugin = plugin;
        this.pasta = new File(plugin.getDataFolder(), "mochilas");
        if (!pasta.exists() && !pasta.mkdirs()) {
            plugin.getLogger().severe("Não consegui criar a pasta de mochilas: " + pasta.getAbsolutePath());
        }
    }

    /** Devolve null quando a mochila ainda não tem conteúdo salvo. */
    public ItemStack[] carregar(UUID id) {
        File arquivo = arquivoDe(id);
        if (!arquivo.exists()) {
            return null;
        }
        String codificado = YamlConfiguration.loadConfiguration(arquivo).getString("conteudo");
        if (codificado == null || codificado.isEmpty()) {
            return null;
        }
        try {
            return desserializar(codificado);
        } catch (IOException | ClassNotFoundException excecao) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao ler a mochila " + id
                    + " — o arquivo foi preservado para inspeção manual.", excecao);
            return null;
        }
    }

    /**
     * A serialização roda na thread principal (é rápida e precisa ler os ItemStacks com
     * segurança); só a escrita em disco vai para outra thread. No desligamento nada pode
     * ser assíncrono, porque as tasks são canceladas antes de rodar.
     */
    public void salvar(UUID id, ItemStack[] conteudo, boolean sincrono) {
        String codificado;
        try {
            codificado = serializar(conteudo);
        } catch (IOException excecao) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao serializar a mochila " + id, excecao);
            return;
        }
        if (sincrono) {
            escrever(id, codificado);
        } else {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> escrever(id, codificado));
        }
    }

    private void escrever(UUID id, String codificado) {
        File arquivo = arquivoDe(id);
        YamlConfiguration configuracao = new YamlConfiguration();
        configuracao.set("conteudo", codificado);
        try {
            configuracao.save(arquivo);
        } catch (IOException excecao) {
            plugin.getLogger().log(Level.SEVERE, "Falha ao gravar a mochila " + id, excecao);
        }
    }

    private File arquivoDe(UUID id) {
        return new File(pasta, id + ".yml");
    }

    private String serializar(ItemStack[] conteudo) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream saida = new BukkitObjectOutputStream(bytes)) {
            saida.writeInt(conteudo.length);
            for (ItemStack item : conteudo) {
                saida.writeObject(item);
            }
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

    private ItemStack[] desserializar(String codificado) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(codificado);
        try (BukkitObjectInputStream entrada = new BukkitObjectInputStream(new ByteArrayInputStream(bytes))) {
            ItemStack[] conteudo = new ItemStack[entrada.readInt()];
            for (int indice = 0; indice < conteudo.length; indice++) {
                conteudo[indice] = (ItemStack) entrada.readObject();
            }
            return conteudo;
        }
    }
}
