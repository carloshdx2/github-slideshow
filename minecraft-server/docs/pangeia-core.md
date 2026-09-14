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
| Invocações (vitalidade, skins, aluguel) | Não começou |
| Trava de luz/trevas no mercado negro | Não começou |
| Reset agendado do mundo de recursos | Não começou |
| Gate de receita por tier de profissão | Não começou (hoje o ValhallaMMO já resolve por permissão) |

### Detalhes que valem saber

- **Cargo**: a checagem é a permissão `pangeia.cargo.<nome>`, que o LuckPerms
  já concede pelo grupo criado no [bloco 3](./fundacao-permissoes.md). Não há
  dependência de compilação com o LuckPerms — é só `hasPermission`.
- **Itens do Oraxen**: o plugin lê o ID do Oraxen direto do PDC do item, sem
  depender da API dele em tempo de compilação (menos acoplamento, menos
  quebra a cada update do Oraxen).
- **Pergaminho com tempo de conjuração**: proposital. Teleporte instantâneo
  num servidor com PvP e sem `/spawn` viraria rota de fuga garantida —
  levar dano cancela a leitura, e o pergaminho só é consumido no fim.
- **Material-base das mochilas**: `LEATHER`, não `BUNDLE`. O bundle do
  vanilla tem armazenamento próprio e brigaria com o nosso.

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

Ainda **não testei nada disso em servidor rodando** — não tenho um
Minecraft aqui. A compilação é verificada pelo CI; o comportamento precisa
ser conferido por você:

- [ ] `/pangeia mochila dar <voce> mistica_simples` entrega a mochila
- [ ] Clique direito abre (com o atraso configurado)
- [ ] Clicar num baú segurando a mochila abre o **baú**, não a mochila
      (segurar shift força a mochila)
- [ ] Guardar item, fechar, reabrir — o conteúdo continua lá
- [ ] Reiniciar o servidor e reabrir — o conteúdo continua lá
- [ ] Tentar colocar uma mochila dentro de outra → bloqueado
- [ ] Na mochila de botânico, tentar guardar uma picareta → bloqueado
      (testar também com shift-click, tecla numérica e arrastando)
- [ ] Morrer com a `mistica_simples` (ligamento) → a mochila **não** cai
- [ ] Morrer com a `mistica_comum` → a mochila **cai** normalmente
- [ ] Sem o cargo, tentar abrir a `cargo_aurora` → recusa
- [ ] Com `lp user <voce> parent add cargo-aurora`, abre
- [ ] Pergaminho: levar dano durante a leitura cancela o teleporte

## Pontos em aberto

- **[em aberto]** A mochila de profissão hoje é **fixa por profissão** (uma
  de botânico, uma de lenhador...). A frase original das notas ("só aceita
  itens relacionados à profissão *de quem a usa*") também poderia significar
  um filtro dinâmico pelo dono. Escolhi a fixa por ser mais simples de
  negociar/vender; se preferir a dinâmica, é uma mudança pequena.
- **[em aberto]** Mochilas místicas sem ligamento que "perdem o poder ao
  morrer" (citadas nas notas) ainda não existem — hoje o ligamento é
  ligado/desligado, sem esse terceiro estado.
- Arquivos de mochilas cujo item foi destruído (lava, despawn) ficam órfãos
  no disco. Não é bug de correção, só lixo acumulando — dá pra limpar depois
  se incomodar.

## Próximos passos sugeridos

- Testar em servidor de teste com a checklist acima.
- **Bloco 6**: invocações (o outro módulo grande) ou o reset agendado do
  mundo de recursos (bem menor, fecha uma pendência do bloco 3).
