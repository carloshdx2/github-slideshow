# Infraestrutura Técnica — Bloco 2

Decisões de infraestrutura para tirar Pangeia do papel, sem depender de
plugins pagos nem de "versão grátis" de plugin pago (essas costumam ser
builds piratas desatualizadas e instáveis — evitamos por completo).

## Decisões confirmadas

- **Minecraft 1.20.x**, servidor **Paper** (fork do Spigot focado em
  performance/estabilidade — é o que praticamente todo host gerenciado
  oferece pronto pra selecionar).
- **Hospedagem gerenciada** (tipo Shockbyte/Pebblehost/Apex) — sem Docker.
  Isso significa: sem `docker-compose`, o deploy é via **upload de arquivos**
  (jars de plugin + configs) pelo painel do host ou FTP/SFTP.
- **Estratégia de plugins: híbrida** — priorizar plugins gratuitos e de
  código aberto; onde não existir opção gratuita boa o suficiente, construir
  plugin próprio (Java) em vez de usar crack/leak de plugin pago.

## Stack de plugins recomendada

| Camada | Plugin | Grátis? | Cobre no design |
|---|---|---|---|
| Permissões | **LuckPerms** | Sim, open source | Base para todo controle de acesso por profissão/cargo |
| Economia (API) | **Vault** | Sim | Ponte entre plugins de economia e o "K" (moeda interna) |
| Comandos essenciais | **EssentialsX** | Sim, open source | Comandos básicos — **sem** usar `/spawn`/`/warp` livre, já que o design não tem spawn fixo |
| Múltiplos mundos | **Multiverse-Core** | Sim | Vilarejos como mundos/regiões separados; mundo de recursos que reseta periodicamente |
| Itens e receitas customizadas | **Oraxen** | Sim, 100% open source | Minérios/pó/fragmentos por tier, pedras de alma, poções, chaves, pergaminhos — texturas e itens próprios do servidor |
| Profissões / progressão / bancadas | **ValhallaMMO** | Sim (núcleo grátis) | Sistema de receitas por bancada própria (ferreiro na bigorna, etc — é literalmente o plugin citado nas suas notas como "baseado no Valhalla"), skills que sobem de nível |
| Classes/raças (addon do Valhalla) | **ValhallaRaces** | Verificar licença antes de instalar | Pode mapear para os níveis de profissão — avaliar no bloco 3 |
| Mobs e chefes de dungeon | **MythicMobs (versão free)** | Sim | Criaturas customizadas por região, drops de pedra de alma |
| Dungeons/masmorras com chave | **TheDungeons** ou **DungeonInstances** | Sim | Portais + chaves com instância própria por grupo (bate com "ativação individual" e "sair morrendo/portal/chefe") |
| Regiões e proteção | **WorldGuard** + **WorldEdit** | Sim | Zonas seguras nas vilas, restrição de PvP, base do mercado negro |
| Monetização (Gemas) | **Tebex** (webstore) | Sim (grátis pra criar, cobra % da venda, não mensalidade) | Compra de Gemas com dinheiro real, entrega automática in-game |

## O que **não** dá pra resolver só com plugin grátis — vai precisar de plugin próprio

Baseado na pesquisa, essas mecânicas do seu design não têm um plugin gratuito
maduro que cubra exatamente o que foi descrito. Para essas, o caminho é
plugin Java customizado (podendo usar a API do Oraxen/ValhallaMMO/MythicMobs
como base, sem reinventar a roda toda):

1. **Mochilas de cargo e de profissão com restrição de item** — os plugins
   de backpack gratuitos (Minepacks, UltimateBackpack) não têm o conceito de
   "só abre quem tem o mesmo cargo" nem "só aceita item da própria
   profissão". Isso é 100% específico do seu design.
2. **Sistema de níveis de profissão 0/3/5/7** — ValhallaMMO tem
   níveis/skills, mas a lógica de "tier 0 = aventureiro, 3 = coleta, 5 =
   construção/especial, 7 = saída" com bloqueio de receita por tier é
   customizada.
3. **"Maldição do ligamento"** (item não cai ao morrer) — dá pra fazer com
   NBT tag customizada (via Oraxen) + um listener de morte próprio.
4. **Invocações (elementais/montarias) com vitalidade, evolução e skins
   raras/limitadas/sazonais, aluguel por 3 dias** — não achei um plugin
   gratuito maduro e confiável que cubra tudo isso junto sem risco de ser
   build pirata. Melhor construir por cima do **MythicMobs free** (que já
   suporta montaria/disguise de graça) com uma camada própria pra
   vitalidade/skins/aluguel.
5. **Pergaminhos de teleporte** — item consumível com cooldown, simples de
   fazer: Oraxen pro item + plugin próprio pequeno pro efeito de teleporte.
6. **Mercado negro de itens de luz/trevas** — WorldGuard cuida da região,
   mas a trava "esse item só pode ser vendido/usado dentro dessa região"
   precisa de um listener próprio checando a tag do item.
7. **Reset periódico do mundo de recursos** — Multiverse cuida da estrutura
   de mundos, mas o agendamento do reset automático (ex.: a cada 13 dias)
   precisa de uma tarefa (scheduler) própria.

**Itens 2, 3 e 5–7 acima são pequenos** (podem virar um único plugin
"PangeiaCore" com vários módulos). O item 1 (mochilas) e o item 4
(invocações) são os mais trabalhosos e devem ser os primeiros focos de
desenvolvimento.

## Estrutura de pastas no repositório

Como o deploy é manual (upload no painel do host, não Docker), o repositório
guarda **configuração versionada**, não os `.jar` dos plugins (binários de
terceiros não devem ir pro git):

```
minecraft-server/
  docs/                     # já existe — documentação de design
  server/
    README.md               # como fazer o deploy no host gerenciado
    server.properties        # template de configuração do servidor
    plugins-checklist.md     # checklist de instalação, um item por plugin
    config/                  # configs (.yml) versionadas de cada plugin
      luckperms/
      valhallammo/
      oraxen/
      mythicmobs/
      worldguard/
    pangeia-core/            # (futuro) plugin Java próprio — módulos do item acima
```

Vou criar agora o esqueleto (`server/README.md`, `server.properties` e o
checklist). As pastas de config de cada plugin ficam vazias por enquanto —
preenchemos conforme formos configurando cada plugin nos próximos blocos.

## Próximos blocos sugeridos

- **Bloco 3**: configurar LuckPerms + Vault + EssentialsX + Multiverse
  (fundação) e definir os grupos de permissão por profissão/nível.
- **Bloco 4**: modelar os itens no Oraxen (minérios, pó, fragmentos, pedras
  de alma, poções, chaves, pergaminhos) e as receitas no ValhallaMMO.
- **Bloco 5**: começar o plugin Java `PangeiaCore` pelas mochilas (o item
  mais citado e mais específico do design).
