# Servidor Minecraft — Pangeia

Documentação de design do servidor, organizada a partir das anotações originais
(Notion: "COMEÇO DE TUDO"). Este é o **primeiro bloco** do projeto: consolidar a
ideia em documentos estruturados antes de partir para infraestrutura e plugins.

## Conceito central

- O mundo se chama **Pangeia**. Jogadores são **"reencarnados"** — um tema tipo
  isekai: personagens chegam em Pangeia vindos de outro lugar.
- Ao redor da vila inicial **Mary Geoise** existem 5 vilarejos vizinhos: **Brisa
  Boreal**, **Condado das Colinas da Lua**, **Governança das Planícies da
  Desolação** e **Planalto da Colina Cinzenta** (+ a própria Mary Geoise = base
  do mundo).
- **Não existe `/spawn`.** Locomoção é só na raça: a pé, montarias/invocações ou
  pergaminhos de teleporte. Isso torna teleporte e montarias itens centrais da
  economia.
- **Dificuldade dinâmica por local e horário:** em Mary Geoise o nível de perigo
  vai de 0–2 durante o dia e de 5–10 durante a noite. Quanto mais longe do
  centro do mapa (a base do mundo), mais perigoso o local, o tempo todo. Ou
  seja: dia = mais fácil / noite = mais difícil, e essa curva fica mais dura
  conforme o jogador se afasta do centro.
- **Economia com dinheiro real ("nosso ganha pão")**: existem **Gemas**,
  moeda comprada com dinheiro real (reais), funcionando como a moeda "premium"
  do servidor — o equivalente a algo "quase NFT", podendo evoluir para NFTs de
  verdade no futuro. Convive com uma economia interna (ouro/"K" do jogo) que
  circula entre profissões.
- Mundos de recursos (zona compartilhada onde mineradores, lenhadores e
  botânicos coletam e vendem) **resetam periodicamente** (a última rodada
  durou 13 dias antes de fechar, e já houve uma "rebelião" quando isso
  aconteceu). Não há ainda uma forma permanente de manter esse mundo aberto —
  aparentemente de propósito, para não deixar essas profissões acumularem
  recursos infinitos; jogadores mais espertos estocam para vender mais caro
  quando o mundo fecha.

## Índice dos documentos

1. [Economia e Profissões](./economia-e-profissoes.md) — níveis de profissão
   (0/3/5/7), lista de profissões e como se ganham/gastam recursos.
2. [Minérios, Pó e Fragmentos](./itens-e-recursos.md) — tiers de minério,
   como viram pó/fragmento e como alimentam receitas exclusivas.
3. [Mochilas](./mochilas.md) — mochilas de cargo, de profissão e místicas.
4. [Invocações e Locomoção](./invocacoes-e-locomocao.md) — elementais,
   montarias terrestres/voadoras/submersas, skins e vitalidade.
5. [Pedras de Alma](./pedras-de-alma.md) — drops de criaturas e purificação.
6. [Poções e Frascos](./pocoes-e-frascos.md) — tiers de poção e produção.
7. [Dungeons e Chaves](./dungeons-e-masmorras.md) — masmorras, chaves e drops.
8. [Ideias Adicionais](./ideias-adicionais.md) — baús interligados, itens de
   luz/trevas, mercado negro, e outras notas soltas.
9. [Infraestrutura Técnica](./infraestrutura.md) — **bloco 2**: server
   software, stack de plugins gratuitos, o que precisa de plugin próprio, e
   estrutura de deploy no host gerenciado.

## Status

Isso é uma tradução 1:1 das anotações originais para um formato navegável —
ainda não é uma especificação técnica (não define comandos, plugins,
fórmulas exatas de dano/preço, etc). Vários pontos nas anotações originais
já eram dúvidas em aberto do próprio autor ("acredito que pode ser
alterado...", "acho que dá pra...") — mantive essas ressalvas nos documentos
correspondentes marcadas como **[em aberto]**.
