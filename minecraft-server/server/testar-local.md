# Testar no PC antes de subir pro host

Servidor de teste local, só pra validar o `PangeiaCore` antes de mexer no
servidor de verdade. **Não precisa instalar todos os plugins pra começar**:
a maior parte das mecânicas do plugin funciona com o Paper puro.

## O que você precisa

- **Java 17 ou mais novo** (o Java 21 serve). Conferir: `java -version`.
  Se não tiver: https://adoptium.net/
- O cliente do **Minecraft Java 1.20.1** (a versão do cliente precisa bater
  com a do servidor).
- ~2 GB de RAM livre.

## Montando o servidor

1. Crie uma pasta vazia, por exemplo `pangeia-teste`.

2. Baixe o **Paper 1.20.1** em https://papermc.io/downloads/paper
   (escolha 1.20.1 na lista de versões) e salve dentro dela como
   **`paper.jar`**.

3. Copie pra essa pasta o script de inicialização daqui do repositório:
   - Windows: [`scripts/iniciar-teste.bat`](./scripts/iniciar-teste.bat)
   - Linux/macOS: [`scripts/iniciar-teste.sh`](./scripts/iniciar-teste.sh)
     (rode `chmod +x iniciar-teste.sh` uma vez)

4. Rode o script. Ele vai falhar de primeira reclamando da EULA — isso é
   esperado. Abra o `eula.txt` que apareceu, troque `eula=false` por
   `eula=true`, salve e rode de novo.

5. Baixe o `.jar` do **PangeiaCore**: no GitHub, aba **Actions** →
   **Build PangeiaCore** → execução mais recente → artefato **PangeiaCore**.
   Descompacte e ponha o `.jar` na pasta `plugins/` do servidor.

6. Reinicie o servidor. No log deve aparecer o PangeiaCore carregando.

7. No Minecraft: **Multijogador → Conexão direta → `localhost`**.

8. No console do servidor, se dê permissão de admin: `op SEU_NICK`

## Roteiro de teste (em 3 etapas)

Faz sentido testar em etapas: se algo quebrar, você sabe exatamente qual
peça é a culpada.

### Etapa 1 — só Paper + PangeiaCore (é onde está o risco de verdade)

A lógica de mochila é a parte mais delicada do plugin, e dá pra testar
inteira sem nenhum outro plugin instalado.

```
/pangeia mochila dar SEU_NICK mistica_simples
/pangeia mochila dar SEU_NICK profissao_lenhador
/pangeia recursos
```

- [ ] Clique direito abre a mochila (a `mistica_simples` demora ~2s de
      propósito)
- [ ] Guardar item → fechar → reabrir: o item continua lá
- [ ] **Parar o servidor e subir de novo**: o item continua lá
- [ ] Clicar num baú segurando a mochila abre o **baú**, não a mochila
      (segurando shift, abre a mochila)
- [ ] Tentar pôr uma mochila dentro da outra → recusado
- [ ] Na `profissao_lenhador`, tentar guardar uma picareta → recusado.
      **Teste as 5 formas**: clique normal, shift-click, tecla 1-9,
      tecla F (off-hand) e arrastando o item por vários espaços
- [ ] Guardar um tronco de madeira na mesma mochila → aceito
- [ ] Com a mochila aberta, tentar arrastá-la pra outro espaço ou soltar
      com Q → recusado
- [ ] `/kill` segurando a `mistica_simples` → ela **não** cai, volta com
      você (tem maldição do ligamento)
- [ ] `/pangeia mochila dar SEU_NICK mistica_comum` e `/kill` → essa **cai**
      no chão (não tem ligamento)

### Etapa 2 — + LuckPerms (trava por cargo)

Baixe o LuckPerms (https://luckperms.net/download), ponha em `plugins/`,
reinicie e rode no console:

```
lp creategroup cargo-aurora
lp group cargo-aurora permission set pangeia.cargo.aurora true
```

- [ ] `/pangeia mochila dar SEU_NICK cargo_aurora` e tentar abrir **sem** o
      cargo → recusado com mensagem
- [ ] `lp user SEU_NICK parent add cargo-aurora`, reconectar, abrir → funciona

Se quiser já deixar todos os grupos de profissão criados, rode o
[bootstrap completo](./config/luckperms/bootstrap-commands.txt).

### Etapa 3 — + Oraxen e ValhallaMMO (itens e receitas)

Essa etapa é a mais trabalhosa e pode ficar pra depois. É o que valida o
pergaminho de teleporte e o filtro de mochila sobre itens customizados,
porque ambos dependem do ID do Oraxen gravado no item.

Ver [plugins-checklist.md](./plugins-checklist.md) para a instalação e
[docs/itens-oraxen-valhalla.md](../docs/itens-oraxen-valhalla.md) para os
itens.

## Se algo der errado

Copie o trecho do console com o erro (procure por `ERROR` ou por
`PangeiaCore`) e me manda — com o log em mãos eu consigo corrigir direto.

O detalhe que mais importa: **eu nunca rodei esse plugin**. O CI garante que
ele compila, não que se comporta como projetado. Então é esperado aparecer
ajuste nessa primeira rodada.
