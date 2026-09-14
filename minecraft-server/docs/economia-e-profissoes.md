# Economia e Profissões

## Moedas

- **Gemas**: moeda "premium", comprada com dinheiro real (reais). É o modelo
  de monetização do servidor ("nosso ganha pão"). Funciona quase como um NFT
  em conceito — **[em aberto]** possível adaptação para NFTs de verdade no
  futuro.
- **Ouro / dinheiro do jogo ("K")**: moeda interna que circula entre
  profissões pela compra e venda de recursos e serviços.

Algumas profissões (ex.: alfaiate) vendem itens **por gemas** diretamente na
loja do console, o que as conecta ao lado de dinheiro real da economia.

## Sistema de níveis de profissão

As profissões são organizadas em **níveis (0, 3, 5, 7)**, cada um com um papel
diferente na cadeia econômica:

| Nível | Papel |
|---|---|
| **0** | **Aventureiro** — profissões ligadas diretamente aos reencarnados desde o início (permanente). |
| **3** | **Coleta de recursos** — quem extrai a matéria-prima do mundo. |
| **5** | **Construção / serviços especiais** — únicos que alteram o mundo ou vendem por gemas. |
| **7** | **Profissões de saída / produto final** — o que todo mundo quer no fim da cadeia; oferecem serviços. |

### Nível 5 — construção e serviços especiais

- **Pedreiro**: o único que pode **construir ou destruir blocos** nos reinos
  do futuro (**[em aberto]** — mencionado como algo do "reino do futuro",
  sem detalhes ainda). Responsável por construir/consertar as casas dos
  reencarnados — profissão essencial para moradia. Sem pré-requisito.
- **Alfaiate**: trabalha diretamente com **skins**. Só quem tem essa
  profissão consegue lidar com ela — a loja vende recursos/itens **por
  gemas**, ligando o alfaiate ao dinheiro real. Sem pré-requisito. Ver
  também a seção dedicada em
  [Ideias Adicionais](./ideias-adicionais.md#alfaiate).
- **Marceneiro** *(requer: Lenhador — evolução)*: móveis e itens
  decorativos de madeira mais elaborados que o lenhador cru não produz
  (portas trabalhadas, baús especiais, mobília) — principal fornecedor do
  **pedreiro** para equipar as casas.
- **Tintureiro** *(requer: Botânico — evolução)*: corantes e tinturas a
  partir de plantas coletadas pelo botânico — usado pelo **alfaiate**
  (cor das roupas/cosméticos), **escultor** e **joalheiro** (acabamento de
  itens detalhados).
- **Curtidor** *(requer: Pescador — evolução)*: couro e escama de
  criaturas aquáticas — usado pelo **ferreiro** (armaduras leves) e pelo
  **alfaiate** (roupas resistentes à água).

### Nível 7 — profissões de saída (o que todo mundo deseja)

Essas profissões têm uma "saída de item maior" — são o topo da cadeia de
consumo:

- **Taverneiro** *(requer: Pescador — evolução)*: faz comida e bebidas —
  usa o peixe do pescador como insumo principal.
- **Agricultor**: tem fazendas, vende produtos e fornece itens importantes
  para outras profissões. Sem pré-requisito (não deriva de nenhum
  coletor específico).
- **Joalheiro** *(requer: Minerador — evolução)*: profissão mais complexa
  do mundo — trabalha com anéis, colares e itens usados por aventureiros,
  a partir dos minérios/gemas do minerador.
- **Ferreiro** *(requer: Minerador — evolução)*: trabalha com forjas —
  voltado para ferramentas, espadas, escudos e armaduras. É o **maior
  comprador do minerador**. Baseado no plugin **ValhallaMMO**.
- **Escultor** *(requer: Lenhador — evolução)*: trabalha com itens mais
  detalhados — arcos, flechas, frascos, potes e também farinhas, a partir
  da madeira do lenhador.
- **Alquimista** *(requer: Botânico — evolução)*: profissão básica de
  sobrevivência. Cria poções que ajudam os reencarnados a enfrentar as
  criaturas do mundo, e é o **maior comprador do botânico**. Também cria
  pergaminhos, com base nos itens disponíveis no servidor.
- **Encantador**: cria encantamentos que fortalecem e melhoram itens, e
  trabalha diretamente com pedras da alma (ver
  [Pedras de Alma](./pedras-de-alma.md)). Sem pré-requisito (não deriva de
  nenhum coletor específico — a matéria-prima vem de combate, não de
  coleta).

### Profissões de coleta / suporte (nível 3 e afins)

- **Lenhador**: profissão "coringa" — é a **única fonte de madeira** e vende
  para praticamente todas as outras profissões, sendo por isso um dos
  maiores geradores de recurso da economia.
- **Minerador**: minera com base no próprio nível — nível 1 = 1 minério por
  bloco, e a proporção sobe conforme o nível aumenta.
- **Pescador**: tem locais específicos para pescar, possivelmente
  restritos por nível, já que peixes de alta qualidade/lendários ficam em
  áreas específicas.
- **Botânico**: coleta recursos usados principalmente por alquimistas.
- **Domador de feras** (possível profissão futura, **[em aberto]**): evolui
  pets e montarias.

## Árvore de evolução de profissões

Os 4 coletores (nível 3) são a raiz de uma árvore de evolução — cada um
pode evoluir para **duas profissões diferentes**, dando ao jogador uma
escolha real de caminho em vez de um único destino óbvio:

| Coletor (nível 3) | Evolução A | Evolução B |
|---|---|---|
| **Lenhador** | Escultor (nível 7) | Marceneiro (nível 5) |
| **Minerador** | Ferreiro (nível 7) | Joalheiro (nível 7) |
| **Botânico** | Alquimista (nível 7) | Tintureiro (nível 5) |
| **Pescador** | Taverneiro (nível 7) | Curtidor (nível 5) |

**[em aberto]**: o minerador hoje só ramifica pra duas profissões de nível
7 (nenhuma nova de nível 5) — se quiser um terceiro braço mais "menor" tipo
os outros três coletores, é só definir o conceito (ex.: um "gemologista"
ou "fundidor") que eu encaixo no mesmo padrão.

### Regra: evolução soma, não substitui

Ao evoluir, o jogador **fica com as duas profissões ao mesmo tempo** — ex.:
um lenhador que evolui pra escultor continua sendo lenhador também. Isso é
proposital: os locais que dão os materiais raros usados pelas profissões
evoluídas são perigosos (muitos monstros, difícil acesso), então só quem já
é experiente no próprio recurso base (já lenhador, já pescador, etc.)
consegue ir lá coletar — a evolução não troca a coleta por outra coisa, ela
soma uma camada de processamento mais avançada em cima.

### Requisitos para "upar" a profissão

1. **Já ter a profissão base** (coletor correspondente).
2. **Entregar um material raro** ligado àquele recurso, obtido em uma zona
   de risco (área perigosa/masmorra correspondente — ver
   [Dungeons e Masmorras](./dungeons-e-masmorras.md)). É o que prova que o
   jogador já é "experiente" o bastante pra sobreviver coletando ali.
3. Concluir a **quest do NPC** da evolução escolhida (mesmo padrão de
   quests que já desbloqueiam receitas — ver seção abaixo).

**[em aberto]**: qual material raro específico e qual masmorra/zona exata
para cada uma das 8 evoluções — depende de termos os itens/tiers definidos
no Oraxen (bloco 4) e as masmorras definidas (bloco 4/5).

### Como isso vira permissão (LuckPerms)

Ver [Fundação: Permissões](./fundacao-permissoes.md#árvore-de-evolução-no-luckperms)
para o detalhe técnico — resumindo: o jogador recebe o grupo da profissão
evoluída **em adição** ao grupo da profissão base (nunca no lugar dele).
Hoje esse processo é manual (staff roda o comando depois de conferir a
entrega do material + quest); automatizar essa checagem é candidato a
módulo do `PangeiaCore` (bloco 5).

## Como se tornar uma profissão / aprender receitas

- Para fabricar um item, **não basta saber a receita** — é preciso ter a
  **habilidade** (a profissão) para poder craftar, mesmo sabendo os
  materiais.
- Por ser reencarnado, algumas receitas básicas já vêm salvas no
  "subconsciente" do personagem. Receitas mais avançadas só aparecem se você
  tiver os itens na mão (não aparecem como mágica/automático).
- **Cada item tem sua própria bancada de criação** — por exemplo, uma espada
  não é feita na mesa do ferreiro, e sim na bigorna. Preste atenção na
  bancada correta indicada pelo NPC.
- Receitas exclusivas do servidor (não encontráveis "na internet",
  diferente das receitas vanilla) são desbloqueadas via **quests com NPCs**,
  que instruem sobre os materiais necessários.

## Notas de design

- O sistema de níveis (0/3/5/7) pode ter os **nomes alterados no futuro**,
  mas a estrutura de níveis deve ser mantida — **[em aberto]**.
- Itens relacionados a **luz** e **trevas** não circulam no mercado normal —
  ver [Ideias Adicionais](./ideias-adicionais.md#mercado-negro-luz-e-trevas)
  para a economia separada que isso cria.
