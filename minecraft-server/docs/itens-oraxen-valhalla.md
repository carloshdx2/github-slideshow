# Itens (Oraxen) e Receitas (ValhallaMMO) — Bloco 4

Como modelar os itens customizados do Oraxen e as receitas do ValhallaMMO,
amarrados nas permissões de profissão/nível criadas no
[bloco 3](./fundacao-permissoes.md).

> **Decisão (bloco 5)**: o Oraxen ficou pago depois que este documento foi
> escrito, e a decisão foi seguir **sem** ele — itens customizados viram
> nativos do `PangeiaCore` (como o pergaminho de teleporte já é, ver
> [docs/pangeia-core.md](./pangeia-core.md)), não itens do Oraxen. A
> convenção de ID/`custom_model_data` abaixo continua valendo (é
> agnóstica de plugin); os arquivos `server/config/oraxen/items/*.yml`
> deste bloco viraram referência histórica — o catálogo de minérios/pó/
> pedras de alma/poções ainda não foi migrado pro formato nativo.

## Convenção de ID (Oraxen)

`<categoria>_<tier ou região>_<slug>`, tudo em minúsculas com `_`:

| Categoria | Prefixo | Exemplo |
|---|---|---|
| Minério | `minerio_` | `minerio_lixo_ferrobrenho` |
| Pó | `po_` | `po_lixo_ferrobrenho` |
| Fragmento | `fragmento_` | `fragmento_lixo_ferrobrenho` |
| Pedra de alma | `pedraalma_` | `pedraalma_marygeoise` |
| Pedra de alma purificada | `pedraalmapura_` | `pedraalmapura_marygeoise` |
| Poção | `pocao_` | `pocao_lixo` |
| Frasco | `frasco_` | `frasco_padrao` |
| Chave de masmorra | `chave_` | `chave_<masmorra>` (definir no bloco 5) |
| Pergaminho | `pergaminho_` | `pergaminho_teleporte` |

Pó e fragmento sempre herdam o slug do minério correspondente — não precisa
inventar nome novo pra cada um (ex.: minério `ferrobrenho` → `po_lixo_ferrobrenho`
+ `fragmento_lixo_ferrobrenho`).

## Faixas de `custom_model_data`

Pra nunca colidir entre categorias enquanto formos adicionando itens:

| Categoria | Faixa |
|---|---|
| Minérios | 10000–10999 |
| Pó | 11000–11999 |
| Fragmentos | 12000–12999 |
| Pedras de alma (impura + purificada) | 13000–13999 |
| Poções / frascos | 14000–14999 |
| Chaves de masmorra | 15000–15999 |
| Pergaminhos | 16000–16999 |
| Mochilas | 17000–17999 (reservado pro bloco 5) |

## Tags usadas pelas mecânicas do design

Não precisamos de NBT customizado extra pra tier — **o ID já carrega essa
informação** (`minerio_<tier>_<slug>`), então o `PangeiaCore` só faz parse
do ID (que o Oraxen já grava no item via sua própria persistent data)
pra saber o tier. Simplifica bastante:

- **Tier**: derivado do ID (`minerio_lixo_...` → tier lixo). Nada a
  configurar à parte.
- **Maldição do ligamento**: não é um campo do Oraxen — vai ser uma lista
  de IDs marcados como "com ligamento" numa config do `PangeiaCore`
  (bloco 5), checada no listener de morte. Ver
  [Mochilas](./mochilas.md#maldição-do-ligamento).
- **Trava de uso por profissão**: a receita do ValhallaMMO já impede quem
  não tem a permissão de **craftar** o item — isso cobre o caso de uso
  citado no design (mochila de profissão, cada bancada). Não precisamos de
  trava adicional no item em si por enquanto.

## Minérios, Pó e Fragmentos — proposta de nomes

> **Validado/ajustado nesta revisão** — 4 problemas corrigidos: (1) a
> tabela antiga contradizia a própria regra do design em alguns itens (ex.:
> "Estanhocinza" descrito como leve *e* pouco eficaz — a regra diz que leve
> deveria ser mais eficaz, não menos); (2) "Prafosca" era um erro de
> concatenação de "Prata" + "fosca" → virou **Pratafosca**; (3)
> "Mitrilopálido" ficava perto demais de "Mithril" (Tolkien) → virou
> **Plumalux**; (4) "Niquelrúnico" repetia a raiz "rúneo" já usada em
> "Cobrerúneo" → virou **Niquelforte**. Troquei a coluna única de
> trade-off por 3 colunas explícitas (Durabilidade/Peso/Eficácia) pra
> deixar a progressão de cada tier auditável — cada tier agora sobe de um
> item "leve e frágil" pra um "pesado e resistente" sem contradição.

Nomes fantasiosos pra Pangeia, seguindo a regra do design ("quanto mais
durável, menos eficaz e mais pesado"). **São placeholder — renomeie à
vontade**, o que importa agora é a estrutura (tier, slug, propriedade).

| Tier | Minério | Durabilidade | Peso | Eficácia |
|---|---|---|---|---|
| LIXO | Ferrobrenho | Baixa | Leve | Alta |
| LIXO | Cobrerúneo | Baixa-média | Leve | Média-alta |
| LIXO | Zincopálido | Média | Médio | Média |
| LIXO | Estanhocinza | Média-alta | Médio-alto | Média-baixa |
| LIXO | Chumbonegro | Alta | Alto | Baixa |
| COMUM | Titâniobruto | Muito baixa | Muito leve | Muito alta |
| COMUM | Bronzita | Baixa | Leve | Alta |
| COMUM | Latãovivo | Média-baixa | Médio-leve | Média-alta |
| COMUM | Pratafosca | Média | Médio | Média |
| COMUM | Niquelforte | Média-alta | Médio-alto | Média-baixa |
| COMUM | Açocinzento | Alta | Alto | Baixa |
| INCOMUM | Plumalux | Baixa | Muito leve | Muito alta |
| INCOMUM | Orictálcio | Média | Médio | Alta |
| INCOMUM | Platinasombria | Alta | Alto | Média |
| RARO | Estelarita | Baixa | Leve | Altíssima |
| RARO | Adamantinaviva | Altíssima | Alto | Alta |
| MUITO RARO | Voidferro | Altíssima | Médio | Alta |
| MUITO RARO | Draconita | Alta | Alto | Altíssima |
| MUITO RARO | Auroaço | Alta | Médio | Alta *(o mais raro/caro dos 19)* |

**Implementado neste bloco** (arquivo real, ver abaixo): os 5 minérios de
tier LIXO + seus pós e fragmentos, como prova do padrão — lore dos itens já
ajustada pra bater com a tabela corrigida acima. Os outros 14 (COMUM/
INCOMUM/RARO/MUITO RARO) seguem o mesmo molde — replico assim que você
validar os nomes ou trocar por outros.

## Pedras de Alma — proposta por região

Uma cor por vila (a região "nativa" da criatura que dropa), impura +
purificada cada:

| Região | Pedra de alma |
|---|---|
| Mary Geoise | Pedra de Alma Branca |
| Brisa Boreal | Pedra de Alma Azul-Vento |
| Condado das Colinas da Lua | Pedra de Alma Prateada |
| Governança das Planícies da Desolação | Pedra de Alma Ocre-Desolada |
| Planalto da Colina Cinzenta | Pedra de Alma Cinza-Pedra |

> **Ajustado nesta revisão**: a Planícies da Desolação era "Cinza-Desolada",
> colidindo em nome com "Cinza-Pedra" do Planalto da Colina Cinzenta (duas
> regiões diferentes, cor "cinza" repetida — fácil de confundir os drops de
> uma com a outra). Troquei pra **Ocre** (tom terroso/desértico, reforça a
> desolação sem repetir a cor de outra região).

**[em aberto]**: a grade original do Notion mostrava bem mais variações do
que 5 (provavelmente uma pedra por *criatura*, não só por região — várias
criaturas por vila). Proponho começar com 1 por região (mais simples de
balancear) e abrir mais variações conforme formos criando as criaturas no
MythicMobs (bloco 5) — cada criatura nova pode ganhar sua própria pedra
dentro da paleta de cor da região.

## Poções — proposta de tiers

Nomeação por tier, efeito específico fica pra quando desenharmos o combate/
sobrevivência com mais detalhe:

| Tier | Nome |
|---|---|
| LIXO | Poção Rudimentar |
| COMUM | Poção Comum |
| INCOMUM | Poção Refinada |
| RARO | Poção Rara |
| MUITO RARO | Poção Ancestral |
| LENDÁRIO | Poção Lendária |

Mais 2 tiers planejados pelas notas originais ("pra jogadores mais
avançados") — **[em aberto]**, entram quando tivermos o teto de nível dos
jogadores definido.

## Chaves e Pergaminhos

- **Chaves**: uma por masmorra — só dá pra nomear depois de termos a lista
  de masmorras/criaturas (bloco 5, junto com MythicMobs/TheDungeons). Por
  enquanto só reservei a faixa de `custom_model_data` e o prefixo `chave_`.
- **Pergaminho de teleporte** (`pergaminho_teleporte`): implementado neste
  bloco como item Oraxen; o efeito de teleporte em si (clique direito →
  teleporta) precisa de um listener — é um dos módulos pequenos do
  `PangeiaCore` (bloco 5), não dá pra fazer só com Oraxen/Valhalla.

## Receitas (ValhallaMMO)

Duas camadas de receita, ambas gated pelas permissões do bloco 3:

1. **Fusão minério → pó**: qualquer jogador com a profissão `minerador`
   (`pangeia.profissao.minerador`) funde minério em pó numa fornalha —
   receita "fusão" do ValhallaMMO.
2. **Crafting avançado por profissão**: cada profissão de nível 5/7 usa pó/
   fragmento pra criar itens finais, na bancada certa (bigorna pro
   ferreiro, etc — ver [Economia e Profissões](./economia-e-profissoes.md#como-se-tornar-uma-profissão--aprender-receitas)),
   exigindo a permissão `pangeia.profissao.<nome>` correspondente.

Exemplo implementado neste bloco (ver arquivos abaixo): fusão dos 5
minérios LIXO em pó, e uma receita de ferreiro usando `po_lixo_ferrobrenho`
pra criar uma ferramenta básica.

## Arquivos deste bloco

- [`server/config/oraxen/items/minerios-lixo.yml`](../server/config/oraxen/items/minerios-lixo.yml)
- [`server/config/oraxen/items/po-fragmentos-lixo.yml`](../server/config/oraxen/items/po-fragmentos-lixo.yml)
- [`server/config/oraxen/items/pergaminho-teleporte.yml`](../server/config/oraxen/items/pergaminho-teleporte.yml)
- [`server/config/valhallammo/recipes/fusao-po-lixo.yml`](../server/config/valhallammo/recipes/fusao-po-lixo.yml)
- [`server/config/valhallammo/recipes/ferreiro-exemplo.yml`](../server/config/valhallammo/recipes/ferreiro-exemplo.yml)

**Aviso de sintaxe**: a estrutura exata de chaves do Oraxen/ValhallaMMO pode
variar um pouco por versão do plugin — esses arquivos seguem o formato
documentado publicamente por ambos, mas confira contra a versão instalada
antes de subir em produção (às vezes uma chave muda de nome entre updates).

## Próximos blocos sugeridos

- ~~Validar/ajustar os nomes propostos acima~~ — feito nesta revisão (ver
  avisos "Validado/ajustado" nas seções de minérios e pedras de alma).
  Poções não precisaram de ajuste (6 tiers já batiam com as notas
  originais, LIXO→LENDÁRIO).
- **Bloco 5**: `PangeiaCore` — comece pelas mochilas, e inclua o listener
  do pergaminho de teleporte já modelado aqui.
- Quando o bloco 5 definir as masmorras/criaturas, voltamos aqui pra
  nomear chaves e pedras de alma extras.
- Replicar o molde de minérios/pó/fragmento pros 14 itens restantes
  (COMUM/INCOMUM/RARO/MUITO RARO) agora que a tabela está validada.
