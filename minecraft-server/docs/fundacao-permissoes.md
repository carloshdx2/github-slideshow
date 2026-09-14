# Fundação: Permissões, Economia e Mundos — Bloco 3

Configuração da base do servidor: LuckPerms (grupos/permissões), Vault +
EssentialsX (economia "K" e comandos), e Multiverse-Core (mundos).

## Modelo de grupos no LuckPerms

O nível de profissão (0/3/5/7, ver
[Economia e Profissões](./economia-e-profissoes.md)) **não é uma progressão
linear que o jogador sobe dentro de uma profissão** — é uma classificação
fixa: cada profissão já nasce em um nível específico. O nível 0
(aventureiro) é a base permanente de todo reencarnado; profissões são
**adicionadas** por cima dela, não a substituem.

```
default (= aventureiro, nível 0 — todo mundo sempre tem)
 ├─ lenhador    (nível 3) ──┬─▶ escultor    (nível 7)
 │                          └─▶ marceneiro  (nível 5)
 ├─ minerador   (nível 3) ──┬─▶ ferreiro    (nível 7)
 │                          └─▶ joalheiro   (nível 7)
 ├─ pescador    (nível 3) ──┬─▶ taverneiro  (nível 7)
 │                          └─▶ curtidor    (nível 5)
 ├─ botanico    (nível 3) ──┬─▶ alquimista  (nível 7)
 │                          └─▶ tintureiro  (nível 5)
 ├─ pedreiro    (nível 5, sem pré-requisito)
 ├─ alfaiate    (nível 5, sem pré-requisito)
 ├─ agricultor  (nível 7, sem pré-requisito)
 └─ encantador  (nível 7, sem pré-requisito)
```

Cada grupo de profissão carrega duas permissões-tag, usadas depois pelo
`PangeiaCore` e pelas receitas do ValhallaMMO/Oraxen pra travar o que cada
profissão pode fazer:

- `pangeia.tier.<0|3|5|7>` — nível da profissão.
- `pangeia.profissao.<nome>` — identifica a profissão em si.

### Árvore de evolução no LuckPerms

Confirmado: **um jogador pode (e deve) ter mais de uma profissão ao mesmo
tempo** — ver [Economia e Profissões](./economia-e-profissoes.md#árvore-de-evolução-de-profissões)
para o design completo. Tecnicamente isso é simples no LuckPerms: evoluir
**não troca de grupo, só adiciona um novo** por cima do que o jogador já
tem.

```
lp user <nome> parent add lenhador     # jogador vira lenhador
# ... depois de cumprir os requisitos de evolução (ver doc de economia) ...
lp user <nome> parent add escultor     # soma escultor, sem tirar lenhador
```

O jogador termina com `default` + `lenhador` + `escultor` simultaneamente,
herdando as permissões dos três. Isso já resolve sozinho a regra de "só
quem já é experiente no recurso base consegue evoluir" — o `escultor`
**não** tem `lenhador` como pré-requisito *estrutural* no LuckPerms (não é
inerência de grupo-pra-grupo); é uma checagem **processual**: antes de
rodar o `parent add escultor`, confirme que o jogador já tem
`pangeia.profissao.lenhador` (comando `/lp user <nome> permission check
pangeia.profissao.lenhador`). Por enquanto isso é manual (staff confere e
roda o comando); vira um módulo do `PangeiaCore` mais pra frente.

### Cargos (ranks pagos)

Separado do sistema de profissão — "cargo" é o rank ligado a compras com
dinheiro real (ex.: cargo "Aurora", citado nas notas originais junto da
mochila de cargo). Criei só **um grupo de exemplo** (`cargo-aurora`) no
bootstrap; os cargos reais (nomes, preços, benefícios) ainda não foram
definidos nas notas — quando você decidir a lista, é só repetir o padrão.

### Política de locomoção (sem `/spawn`)

Já que o design não tem `/spawn` e locomoção é "na raça" (montarias/
invocações/pergaminhos), o grupo `default` **não** recebe permissão para
os comandos de teletransporte "gratuito" do EssentialsX:

| Comando | Permissão | Status no `default` |
|---|---|---|
| `/spawn` | `essentials.spawn` | negado |
| `/home` , `/sethome` | `essentials.home` , `essentials.sethome` | negado |
| `/warp` | `essentials.warp` | negado |
| `/tpa` , `/tpahere` , `/tpaccept` | `essentials.tpa*` | negado |
| `/back` | `essentials.back` | negado |

Um grupo `staff` (moderação/admin) tem esses comandos liberados de volta,
pra fins de suporte/gestão do servidor. Comandos sociais/econômicos normais
(`/msg`, `/pay`, `/balance`, `/baltop`, `/mail`, `/kit`) continuam liberados
para todo mundo.

**[em aberto]**: decidir se `/tpa` (teleporte entre jogadores, não pra um
ponto fixo) deveria continuar liberado — ele não quebra tanto a dificuldade
de locomoção quanto `/spawn`/`/warp`/`/home`, já que ainda depende de outro
jogador estar no destino. Deixei bloqueado por padrão pra manter a tensão
do "só anda na raça/monta/teleporte por pergaminho", mas é fácil reverter.

## Economia (Vault + EssentialsX)

- **EssentialsX Economy** é o provedor de economia por trás do Vault —
  não precisa de plugin de economia à parte. Ele já é o "K" citado no
  design (moeda interna que circula entre profissões).
- **Gemas** (moeda premium, dinheiro real) **não** passam pelo Vault/K —
  isso é tratado pelo Tebex na loja web, creditando um saldo separado (via
  comando customizado executado na compra). Ainda não implementado — entra
  quando configurarmos o Tebex.

## Mundos (Multiverse-Core)

Dois mundos para começar:

- **`world`** — o continente de Pangeia em si (permanente). As 5 vilas
  (Mary Geoise, Brisa Boreal, Condado das Colinas da Lua, Governança das
  Planícies da Desolação, Planalto da Colina Cinzenta) são **regiões dentro
  desse mesmo mundo** (via WorldGuard), não mundos separados — afinal os
  jogadores andam a pé entre elas.
- **`recursos_pangeia`** — o mundo de recursos compartilhado (mineradores/
  lenhadores/botânicos), que deve ser **resetado periodicamente** (a
  referência era ~13 dias). O reset automático **já está implementado** no
  `PangeiaCore` (ver [PangeiaCore](./pangeia-core.md)), mas vem **desligado**
  na config: regenerar um mundo apaga tudo e não tem desfazer, então ligar é
  uma decisão consciente sua depois de conferir o nome do mundo.

Ver [server/config/multiverse-core/bootstrap-commands.txt](../server/config/multiverse-core/bootstrap-commands.txt)
para os comandos de criação.

## Como aplicar

1. Suba **LuckPerms**, **Vault**, **EssentialsX** e **Multiverse-Core** no
   servidor de teste (ver
   [plugins-checklist.md](../server/plugins-checklist.md)) e deixe bootar
   uma vez pra gerar os arquivos padrão.
2. Execute, **em ordem**, os comandos de
   [server/config/luckperms/bootstrap-commands.txt](../server/config/luckperms/bootstrap-commands.txt)
   no console (ou via RCON).
3. Execute os comandos de
   [server/config/multiverse-core/bootstrap-commands.txt](../server/config/multiverse-core/bootstrap-commands.txt).
4. Mescle as chaves de
   [server/config/essentialsx/config-overrides.yml](../server/config/essentialsx/config-overrides.yml)
   no `config.yml` gerado pelo EssentialsX (não é pra substituir o arquivo
   inteiro — ele tem centenas de linhas; só ajuste essas chaves específicas).
5. Reinicie o servidor.

## Próximos blocos sugeridos

- **Bloco 4**: modelar itens no Oraxen e receitas no ValhallaMMO,
  amarrando as permissões `pangeia.tier.*` / `pangeia.profissao.*` já
  criadas aqui como requisito de cada receita.
- **Bloco 5**: começar o `PangeiaCore` (mochilas primeiro).
