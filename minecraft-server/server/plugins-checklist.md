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

- [ ] **Oraxen** (opcional/em aberto) — https://github.com/oraxen/oraxen —
      **ficou pago** (licença via SpigotMC/BuiltByBit), ao contrário do que
      a pesquisa original do bloco 2 assumiu. O pergaminho de teleporte já
      não depende mais dele (virou item nativo do `PangeiaCore`); só
      instale se decidir comprar pra fazer minérios/pó/pedras de
      alma/poções com resource pack automático. Ver
      [docs/pangeia-core.md](../docs/pangeia-core.md#detalhes-que-valem-saber).
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

## Plugin próprio (`PangeiaCore`) — bloco 5

Código em `minecraft-server/pangeia-core/`, documentação em
[docs/pangeia-core.md](../docs/pangeia-core.md). O `.jar` sai pronto no
GitHub Actions (aba **Actions** → **Build PangeiaCore** → artefato).

- [x] Mochilas (cargo / profissão / místicas)
- [x] Maldição do ligamento (item não cai ao morrer)
- [x] Pergaminhos de teleporte (com tempo de conjuração)
- [x] Reset agendado do mundo de recursos (vem desligado; ligar só depois
      de conferir o nome do mundo — a operação apaga tudo e não tem desfazer)
- [x] Testado em servidor local: mochilas, filtro de profissão, ligamento e
      persistência após restart passaram (ver
      [docs/pangeia-core.md](../docs/pangeia-core.md#checklist-de-teste-em-jogo))
- [x] Testar a trava por cargo (precisa do LuckPerms instalado)
- [x] Testar o pergaminho de teleporte — **deixou de precisar do Oraxen**:
      o Oraxen ficou pago, e o pergaminho virou item nativo do
      `PangeiaCore` (ver [docs/pangeia-core.md](../docs/pangeia-core.md)).
      O resto do catálogo de itens do bloco 4 (minérios, pó, pedras de
      alma, poções) continua dependendo dessa mesma decisão em aberto.
- [x] Invocações — **esqueleto genérico** (bloco 6): item chama/dispensa
      uma entidade vinculada ao dono (`/pangeia invocacao dar`), 100%
      nativo no `PangeiaCore` (MythicMobs free não cobre montaria sem um
      addon pago — mesmo problema do Oraxen, então não virou dependência).
      Testado em jogo: chamar e dispensar um lobo de exemplo, confirmado
      via NBT (ver [docs/pangeia-core.md](../docs/pangeia-core.md)).
- [x] Invocações — **montaria terrestre** com mecânica de verdade: atributos
      (velocidade/pulo/vida) e cor fixos na config, monta automaticamente
      já selado e domado. Testado em jogo.
- [ ] Invocações: elemental de combate, montaria voadora e submersa (sem
      abordagem técnica decidida ainda pras duas últimas), vitalidade,
      evolução, skins raras/sazonais, aluguel 3 dias
- [ ] Trava de item de luz/trevas no mercado negro
- [ ] Níveis de profissão 0/3/5/7 (gate de receitas por tier — hoje o
      ValhallaMMO já resolve via permissão, só entra aqui se precisarmos de
      regra que ele não cubra)
