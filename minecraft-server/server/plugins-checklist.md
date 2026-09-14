# Checklist de instalação de plugins

Marque conforme for instalando no servidor de teste. Baixe sempre da fonte
oficial linkada — evite mirrors/sites de "download grátis" genéricos.

## Fundação

- [ ] **Paper** 1.20.x — https://papermc.io/downloads/paper
- [ ] **LuckPerms** — https://luckperms.net/download
- [ ] **Vault** — https://www.spigotmc.org/resources/vault.34315/
- [ ] **EssentialsX** — https://essentialsx.net/downloads.html
- [ ] **Multiverse-Core** — https://www.spigotmc.org/resources/multiverse-core.390/

## Conteúdo e progressão

- [ ] **Oraxen** — https://github.com/oraxen/oraxen (itens/texturas
      customizadas: minérios, pó, fragmentos, pedras de alma, poções, chaves,
      pergaminhos)
- [ ] **ValhallaMMO** — https://www.spigotmc.org/resources/valhallammo-1-19-1-21-11.94921/
      (skills, receitas por bancada própria — base do sistema de ferreiro)
- [ ] **ValhallaRaces** (avaliar licença antes) — https://www.spigotmc.org/resources/valhallaraces-valhallammo-add-on-fantasy-roleplay-create-your-own-races-and-classes.103321/

## Dungeons e mobs

- [ ] **MythicMobs** (versão free) — https://www.spigotmc.org/resources/%E2%9A%94-mythicmobs-free-version-%E2%96%BAthe-1-custom-mob-creator%E2%97%84.5702/
- [ ] **TheDungeons** ou **DungeonInstances** (comparar as duas e escolher
      uma) — https://modrinth.com/plugin/dungeons /
      https://modrinth.com/plugin/dungeoninstances

## Regiões e monetização

- [ ] **WorldGuard** + **WorldEdit** — https://enginehub.org/worldguard
- [ ] **Tebex** (webstore, criar conta e vincular ao servidor) — https://www.tebex.io/

## Configuração pós-instalação (bloco 3)

- [ ] Rodar `config/luckperms/bootstrap-commands.txt` no console
- [ ] Rodar `config/multiverse-core/bootstrap-commands.txt` no console
- [ ] Mesclar `config/essentialsx/config-overrides.yml` no `config.yml` do
      EssentialsX
- [ ] Testar: jogador novo cai no grupo `default`, sem `/spawn`/`/home`
- [ ] Testar: `lp user <nome> parent add minerador` dá a profissão

## Configuração pós-instalação (bloco 4)

- [ ] Copiar `config/oraxen/items/*.yml` pra `plugins/Oraxen/items/` e
      gerar/aplicar o resource pack (`/oraxen pack`)
- [ ] Copiar `config/valhallammo/recipes/*.yml` pra
      `plugins/ValhallaMMO/recipes/`
- [ ] Testar: minerador funde `minerio_lixo_ferrobrenho` em
      `po_lixo_ferrobrenho` na fornalha
- [ ] Testar: sem a permissão `pangeia.profissao.ferreiro`, a receita de
      ferreiro não aparece/não funciona
- [ ] Validar/trocar os nomes propostos em
      [docs/itens-oraxen-valhalla.md](../docs/itens-oraxen-valhalla.md)
      antes de replicar pros outros tiers de minério

## Plugin próprio (`PangeiaCore`)

Ainda não existe — vai nascer no Bloco 5. Módulos planejados:

- [ ] Mochilas (cargo / profissão / místicas + maldição do ligamento)
- [ ] Níveis de profissão 0/3/5/7 (gate de receitas por tier)
- [ ] Invocações: vitalidade, evolução, skins raras/sazonais, aluguel 3 dias
- [ ] Pergaminhos de teleporte
- [ ] Trava de item de luz/trevas no mercado negro
- [ ] Reset agendado do mundo de recursos
