# Deploy — Servidor Pangeia (host gerenciado)

> Antes de subir qualquer coisa no host, vale testar no seu PC:
> ver [testar-local.md](./testar-local.md).

Este projeto roda num **host gerenciado de Minecraft** (Shockbyte, Pebblehost,
Apex ou similar), então não usamos Docker — o deploy é feito subindo
arquivos pelo painel do host (ou via FTP/SFTP quando o host oferecer).

Ver [../docs/infraestrutura.md](../docs/infraestrutura.md) para as decisões
de stack e o motivo de cada escolha.

## Passo a passo de deploy

1. **No painel do host**, selecione o tipo de servidor **Paper** e a versão
   **1.20.x**.
2. **Suba os `.jar`** de cada plugin da lista em
   [plugins-checklist.md](./plugins-checklist.md) para a pasta `plugins/`
   do servidor — baixe sempre da fonte oficial de cada plugin (SpigotMC,
   Modrinth/Hangar ou GitHub Releases), nunca de sites de "plugins grátis"
   genéricos (risco de build pirata/maliciosa).
3. **Copie os arquivos de configuração** deste repositório
   (`server/config/<plugin>/`) para a pasta correspondente em
   `plugins/<Plugin>/` no servidor, sobrescrevendo os arquivos padrão gerados
   no primeiro boot.
   - **LuckPerms** e **Multiverse-Core**: não é pra copiar arquivo — rode os
     comandos de `bootstrap-commands.txt` de cada pasta, em ordem, no
     console/RCON depois do primeiro boot (ver
     [../docs/fundacao-permissoes.md](../docs/fundacao-permissoes.md)).
   - **EssentialsX**: mescle as chaves de `config-overrides.yml` no
     `config.yml` real gerado pelo plugin (não substitua o arquivo
     inteiro).
4. **Copie `server.properties`** (ajustando `server-ip`/`motd` se o host
   pedir) para a raiz do servidor.
5. Reinicie o servidor pelo painel.

## Fluxo de trabalho

- Mudou uma config de plugin no servidor de teste? Copie o `.yml` atualizado
  de volta pra `server/config/<plugin>/` neste repo e commit — é assim que
  mantemos a configuração versionada mesmo sem Docker.
- `.jar` de plugins e o mundo (`world/`, `world_nether/`, etc.) **não** vão
  pro git — são binários grandes e específicos da instância rodando. Isso já
  está coberto no `.gitignore`.
