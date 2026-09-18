# PangeiaCore — plugin próprio (Bloco 5)

Plugin Java que implementa as mecânicas do design que nenhum plugin gratuito
cobre (ver [Infraestrutura](./infraestrutura.md#o-que-não-dá-pra-resolver-só-com-plugin-grátis--vai-precisar-de-plugin-próprio)).

Código em [`minecraft-server/pangeia-core/`](../pangeia-core/).

## A decisão de arquitetura mais importante

**O conteúdo da mochila não fica dentro do item.** O item carrega apenas um
UUID no seu `PersistentDataContainer`; o conteúdo vive em
`plugins/PangeiaCore/mochilas/<uuid>.yml`.

Isso existe para matar de raiz a classe de bug mais comum em plugin de
mochila: **duplicação de itens**. Se o conteúdo morasse no NBT do item,
qualquer caminho que copiasse o item (comando de admin, modo criativo, bug
de outro plugin) copiaria junto um inventário inteiro de itens. Com o UUID,
copiar o item só cria um segundo "cartão de acesso" para o **mesmo**
armazenamento — nada é duplicado.

O mesmo raciocínio vale quando duas cópias do item existem e dois jogadores
abrem ao mesmo tempo: os dois recebem o **mesmo objeto de inventário**, então
não há como as duas telas divergirem e uma sobrescrever a outra.

Outras proteções que vieram junto:

- **Mochila dentro de mochila é bloqueada** — senão vira armazenamento
  infinito e complica rastrear de quem é o quê.
- **A mochila aberta não pode ser movida nem solta** enquanto a tela está
  aberta (impede depositar numa mochila que já não está mais com você).
- **Nunca encolher**: se alguém reduzir `linhas` na config de um tipo que já
  está em uso, a mochila abre com o tamanho antigo e registra um aviso, em
  vez de apagar os itens que não cabem mais.
- Filtro de profissão aplicado em **todos** os caminhos de entrada de item:
  clique normal, shift-click, tecla numérica (hotbar), troca com a off-hand
  e arrasto. É onde a maioria dos plugins deixa passar.
- Salvamento ao fechar + autosave periódico + salvamento síncrono no
  desligamento (para não perder conteúdo num crash ou restart).

## O que está implementado

| Módulo | Estado |
|---|---|
| Mochilas de **cargo** (só abre quem tem o cargo) | Pronto |
| Mochilas de **profissão** (só aceita itens daquela profissão) | Pronto |
| Mochilas **místicas** (menos espaço, demoram a abrir) | Pronto |
| **Maldição do ligamento** (item não cai ao morrer) | Pronto |
| **Pergaminho de teleporte** (com tempo de conjuração) | Pronto |
| Comando `/pangeia mochila dar` (usado também pelo Tebex) | Pronto |
| **Reset agendado do mundo de recursos** | Pronto (desligado por padrão) |
| **Invocações — esqueleto genérico** (chamar/dispensar entidade vinculada ao dono) | Pronto |
| Invocações — os 4 tipos de verdade, vitalidade, skins, aluguel | Não começou |
| Trava de luz/trevas no mercado negro | Não começou |
| Gate de receita por tier de profissão | Não começou (hoje o ValhallaMMO já resolve por permissão) |

### Detalhes que valem saber

- **Cargo**: a checagem é a permissão `pangeia.cargo.<nome>`, que o LuckPerms
  já concede pelo grupo criado no [bloco 3](./fundacao-permissoes.md). Não há
  dependência de compilação com o LuckPerms — é só `hasPermission`.
- **Cuidado ao testar a trava de cargo com um jogador op**: op no Bukkit
  responde `true` pra qualquer `hasPermission`, então a mochila abre mesmo
  sem o grupo do LuckPerms — não é a trava funcionando, é o teste mascarado.
  Precisa fazer `deop` no jogador de teste pra validar de verdade.
- **Itens do Oraxen**: quando existirem, o plugin lê o ID do Oraxen direto do
  PDC do item, sem depender da API dele em tempo de compilação (menos
  acoplamento, menos quebra a cada update do Oraxen). Hoje isso só é
  fallback — o Oraxen não está instalado (ver "Mudança de arquitetura"
  abaixo).
- **Mudança de arquitetura: pergaminho não depende mais do Oraxen**. O
  Oraxen deixou de ser gratuito (era a suposição do
  [bloco 2](./infraestrutura.md) quando isso foi pesquisado — mudou desde
  então). Sem substituto gratuito maduro no mercado, o pergaminho de
  teleporte passou a ser um item nativo do `PangeiaCore`, no mesmo padrão
  das mochilas: tag própria no PDC (`pergaminho_tipo`) em vez do ID do
  Oraxen, definido em `pergaminhos.tipos` no `config.yml` e entregue por
  `/pangeia pergaminho dar <jogador> <tipo>`. A "textura" por enquanto é o
  material vanilla puro (`PAPER`); o resto do catálogo do
  [bloco 4](./itens-oraxen-valhalla.md) (minérios, pó, pedras de alma,
  poções) ainda depende dessa mesma decisão — **não foi migrado**, fica em
  aberto pra quando você decidir entre comprar o Oraxen ou desenhar os
  itens na mão.
- **Pergaminho com tempo de conjuração**: proposital. Teleporte instantâneo
  num servidor com PvP e sem `/spawn` viraria rota de fuga garantida —
  levar dano cancela a leitura, e o pergaminho só é consumido no fim.
- **Invocações (bloco 6) é só o esqueleto ainda**: item que chama/dispensa
  uma entidade vinculada ao dono (`invocacoes.tipos` no `config.yml`,
  `/pangeia invocacao dar <jogador> <tipo>`), sem consumir o item — é um
  "chamador" permanente, não descartável como o pergaminho. Diferente das
  mochilas/pergaminhos, a persistência da entidade entre restart é a
  nativa do Minecraft (ela é salva no chunk como qualquer mob), não um
  arquivo próprio. **Nenhum dos 4 tipos do design** (elemental, montaria
  terrestre/voadora/submersa) tem mecânica de verdade ainda — a categoria é
  só um campo de dados por enquanto. Vitalidade, evolução, skins e aluguel
  seguem **[em aberto]**, como já estavam nas notas originais. Pesquisei
  construir isso em cima do MythicMobs (sugestão do
  [bloco 2](./infraestrutura.md)): a versão free continua grátis, mas
  **montaria não vem nela** — precisaria do addon AdvancedPet + ModelEngine
  (ModelEngine é pago, mesmo problema do Oraxen) — por isso o esqueleto é
  100% nativo no `PangeiaCore`, sem essa dependência.
- **Material-base das mochilas**: `LEATHER`, não `BUNDLE`. O bundle do
  vanilla tem armazenamento próprio e brigaria com o nosso.
- **Reset do mundo de recursos**: vem **desligado** na config. Regenerar um
  mundo apaga tudo que há nele e não tem desfazer, então preferi exigir uma
  decisão consciente a deixar ligado por padrão. O plugin também **se recusa
  a ligar** se o mundo configurado for o mundo principal ou o mundo de
  saída — um erro de digitação ali apagaria o servidor inteiro. Quem
  regenera de fato é o Multiverse (`mv regen`); o plugin só conta o tempo,
  avisa quem está no mundo e tira todo mundo de lá antes.

## Como pegar o `.jar`

Não precisa compilar nada na sua máquina (nem dá, pelo celular). O GitHub
Actions compila a cada push:

1. Vá em **Actions** → **Build PangeiaCore** no repositório.
2. Abra a execução mais recente.
3. Baixe o artefato **PangeiaCore** (o `.jar` está dentro do zip).
4. Suba o `.jar` para a pasta `plugins/` do servidor e reinicie.

O plugin é compilado contra a API **1.20.1** de propósito: o jar roda em
qualquer 1.20.x. Se compilássemos contra a 1.20.6, quebraria em servidores
1.20.1.

## Checklist de teste em jogo

### Validado em servidor local (Paper 1.20.1, 16/09/2026)

Rodado em servidor real, sem nenhum outro plugin instalado:

- [x] `/pangeia mochila dar <voce> mistica_simples` entrega a mochila
- [x] Clique direito abre (com o atraso configurado)
- [x] Clicar num baú segurando a mochila abre o **baú**, não a mochila
      (segurar shift força a mochila)
- [x] Guardar item, fechar, reabrir — o conteúdo continua lá
- [x] Reiniciar o servidor e reabrir — o conteúdo continua lá
- [x] Tentar colocar uma mochila dentro de outra → bloqueado
- [x] Na mochila de profissão, tentar guardar item de outra profissão →
      bloqueado nas 5 formas (clique, shift-click, tecla numérica,
      off-hand e arrastando)
- [x] Mover ou soltar a mochila enquanto ela está aberta → bloqueado
- [x] Morrer com a `mistica_simples` (ligamento) → a mochila **não** cai
- [x] Morrer com a `mistica_comum` → a mochila **cai** normalmente
- [x] `/pangeia recursos` responde

Com **LuckPerms** instalado:

- [x] Sem o cargo (e sem ser op), tentar abrir a `cargo_aurora` → recusada
      com mensagem
- [x] Com `lp user <voce> parent add cargo-aurora` + reconectar → abre

Pergaminho de teleporte (item nativo do `PangeiaCore`, sem Oraxen — ver
"Mudança de arquitetura" acima):

- [x] `/pangeia pergaminho dar <voce> pergaminho_teleporte` entrega o item
- [x] Clique direito → mensagem de conjuração, ~3s parado → teleporta e o
      item é consumido
- [x] Clique direito e levar dano antes de terminar → mensagem de
      cancelamento, **não** teleporta e o item **não** é consumido

Invocações (esqueleto genérico, sem mecânica por categoria ainda):

- [x] `/pangeia invocacao dar <voce> elemental_lobo_teste` entrega o item
- [x] Clique direito → chama um lobo manso vinculado a você (confirmado
      via NBT: `Owner` setado, tag `invocacao_dono` batendo com o UUID do
      jogador)
- [x] Clique direito de novo → dispensa (lobo removido do mundo)

### Ainda não validado (depende de outros plugins)

- [ ] Com `reset-mundo-recursos.ativado: true` e um `intervalo-dias` curto
      só para teste, conferir que os avisos chegam e que o mundo regenera
      (teste isso num mundo descartável, nunca no principal)

## Pontos em aberto

- **Decidido**: a mochila de profissão é **fixa por profissão** (uma de
  botânico, uma de lenhador...), não um filtro dinâmico pela profissão de
  quem carrega. É mais simples de negociar/vender e mais fácil de explicar
  ao jogador. Se um dia quiser a versão dinâmica, é uma mudança pequena.
- **Resolvido (era engano meu)**: eu tinha anotado como pendência as
  místicas que "perdem o poder ao morrer". Relendo a fala original do NPC —
  *"Elas não são dropadas, têm a 'maldição do ligamento', mas... algumas não
  têm esse poder e acabarão perdendo ela ao morrer"* — isso é exatamente o
  liga/desliga que já existe: `mistica_simples` (com ligamento, não cai) e
  `mistica_comum` (sem ligamento, cai). Não há terceiro estado a construir.
- Arquivos de mochilas cujo item foi destruído (lava, despawn) ficam órfãos
  no disco. Não é bug de correção, só lixo acumulando — dá pra limpar depois
  se incomodar.

## Próximos passos sugeridos

- Testar em servidor de teste com a checklist acima (inclui o esqueleto de
  invocações, que só rodou `mvn package` até agora).
- **Bloco 6, continuação**: escolher 1 dos 4 tipos de invocação (elemental
  de combate, montaria terrestre/voadora/submersa) e dar mecânica de
  verdade em cima do esqueleto — voadora/submersa em particular ainda não
  têm uma abordagem técnica decidida (Minecraft não tem "montaria voadora"
  nativa). Vitalidade/skins/aluguel seguem em aberto. A alternativa menor é
  a trava de luz/trevas do mercado negro, que depende da API do WorldGuard.
