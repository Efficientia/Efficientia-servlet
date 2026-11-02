# Landing Efficientia — fidelidade visual e Sobre Nós

Baseline inicial: `origin/main` em `0dc11ac`. Branch `fix/landing-fidelity-figma`, atualizada sem conflitos sobre `d3fcfaf` após a integração da PR de Perfil durante esta atividade.

## Diagnóstico e correções

| Área | Causa | Correção |
|---|---|---|
| Curvas | Elementos independentes, escalas locais, `transform` sobrescrito e recorte da Solução | Três SVGs originais em uma camada compartilhada. Grade com linhas compartilhadas (`subgrid`), espelhamentos reais e apenas o recorte externo do canvas |
| Hero | Container de 1350px com padding adicional, CTA 410×65 e telefone sem o brilho oficial | Canvas central de 1440px; conteúdo x=26; CTA 460×80; PNG oficial completo, considerando suas margens transparentes |
| Problema | Comparativo sem indicadores/brilhos oficiais e deslocamentos por `translateX` | PNG definitivo com proporção original; slot visual e cards posicionados pela geometria do frame |
| Solução | Pseudoelementos contidos em `overflow: clip` | Decoração removida da seção; três mockups, cards, setas e carrossel existentes preservados |
| Impacto | Margens diferentes do canvas e imagem muito abaixo da composição original | Título e texto nos offsets medidos; composição final do MacBook preservada integralmente |
| Funcionalidades | Seu canvas centralizado contrastava com margens fixas das seções vizinhas | Preservados padding de 40px, colunas 432/896 e gap de 32px do Figma; seções vizinhas passam a compartilhar o mesmo eixo central |
| Botões | Texto creme sobre verde; dimensões, peso e tamanho diferentes | Hero com azul `#0F51AE`; Próximo passo com azul escuro `#001D89` da variante V2; texto de 20–28px em negrito; secundário de Próximo passo com opacidade de 30%; foco, hover e active preservados/definidos |
| Próximo passo | Composição aumentada e telefone oculto entre 481 e 860px | Tamanhos/offsets desktop medidos e ambos os mockups presentes nas adaptações móveis |
| Menu/rodapé | Proporções e margens inconsistentes em monitores largos | Menu limitado ao canvas; navegação e destinos existentes preservados; rodapé mantém todos os links |

As superfícies, vídeo, gradientes e bordas arredondadas foram preservados. A ordem é superfície/vídeo → curvas → conteúdo; a decoração não captura eventos. As curvas não são duplicadas para esconder emendas e não recebem distorção independente dos eixos.

## Referências e decisões

- Original: [Desktop - 145, 2411:71](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=2411-71), 1440×6144.
- Final consultada: [V2 / Landing desktop, 4697:604](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=4697-604).
- [05 / Funcionalidades, 4585:215](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=4585-215): x=40; largura de texto 432; mockups 896; gap 32.
- Hero, Problema, Solução e Impacto usam x=26 no canvas original. Esses paddings não foram uniformizados cegamente.
- As três curvas usam os grupos `2411:91`, `2411:102` e `2411:82`. Os dois últimos possuem matriz horizontal negativa. Em 1440px, posições renderizadas: aproximadamente (−636,55; −270,08), (−155,78; 1374,52) e (−507,19; 2437,14). As diferenças entre bounding box do grupo e dimensão do SVG incluem os limites dos próprios vetores.
- A documentação UX1 consultada registra pendências de validação humana da V2; não comprova aprovação integral nem autoriza aplicar hipóteses editoriais. Nenhum texto foi reformulado por esse motivo.

## Assets e fontes

| Asset em uso | Fonte/decisão |
|---|---|
| `mockup_celular_hero.png` | Arquivo definitivo fornecido em `Downloads/Efficientia - inter 2026(9)/09 — V2/V2/Landing desktop/`; 1094×1821, RGBA; cópia byte a byte |
| `mockup_problema.png` | Mesmo diretório oficial; 1148×734, RGBA; cópia byte a byte |
| `landing-linhas-hero.svg` | Vetor original Figma `2411:91`, sem fundo de exportação |
| `landing-linhas-problema-solucao.svg` | Vetor original Figma `2411:102`, espelhamento aplicado conforme matriz |
| `landing-linhas-solucao-impacto.svg` | Vetor original Figma `2411:82`, espelhamento aplicado conforme matriz |
| `solucao-etapa-1/2/3.png` | Mantidos; composição final V3 consultada em `5135:3515`, `5135:3552`, `5135:3590`; 480×600 transparentes, com testes existentes |
| `mockup_macbook_impacto.png` | Mantido sem alteração; composição final `5271:1112`, 1397×744, com testes existentes |
| `funcionalidades-dashboard.png` / `funcionalidades-iphone-real.png` | Mantidos; correspondem aos slots do dashboard e Assistente V2 em `4585:215` |
| `mockup-home-motorista-realista-v3.png` | Mantido para Próximo passo; referência `4697:1154`; integração e visibilidade responsiva corrigidas |

Os arquivos antigos de Hero e Problema foram preservados no repositório; apenas suas referências em `index.html` foram substituídas. A busca prévia não encontrou uso desses dois arquivos em outras telas. Nenhuma interface interna dos mockups foi editada.

SHA-256 dos PNGs oficiais, iguais na origem e no destino:

```text
mockup_celular_hero.png  0B796104136291745566B74BA0002DB65A6FDDCF6901EA565D695DD9808D5CE3
mockup_problema.png      B4F03FCFA33B283DEF970C061531CF216E43BE6A763FADE66FDED6220B3C71C1
```

## Sobre Nós no Figma

Não foi encontrado um frame existente de Sobre Nós nas páginas `Landing page`, `UX — Pesquisa e avaliação` e `Mobile/WEB - UI`. Os novos frames reproduzem o HTML/CSS existente, sem redesenhar a página:

- [Desktop 1440×2317](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=5374-3481).
- [Mobile 390×2019](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=5374-3604).
- [Mobile / menu aberto](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=5386-3651).
- [Seção documental](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=5374-3478).
- [Entrada no índice geral UX1](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1?node-id=5386-3789).

São 34/27/34 textos editáveis, além de containers, ícones e fotos. Nenhum frame usa screenshot da página inteira como preenchimento. História, missão, visão, valores e equipe foram conferidos nos três frames. O menu desktop V2 recebe o link Sobre Nós; os novos frames possuem retornos à landing/login, e o menu mobile abre/fecha no protótipo. Os quadros históricos, o original `2411:71`, a apresentação final de 25 quadros e UX2 foram preservados. As capturas intermediárias desta atividade foram removidas. O HTML/CSS de Sobre Nós permanece idêntico à main.

## Validação e evidências

| Viewport real | DPR/zoom | Resultado |
|---|---|---|
| 1920×1024 | 1 / 100% | Passou |
| 1680×1024 | 1 / 100% | Passou |
| 1440×1024 | 1 / 100% | Passou |
| 1366×1024 | 1 / 100% | Passou |
| 1024×1024 | 1 / 100% | Passou |
| 860×1024 | 1 / 100% | Passou |
| 768×1024 | 1 / 100% | Passou |
| 430×1024 | 1 / 100% | Passou |
| 390×1024 | 1 / 100% | Passou |
| 360×1024 | 1 / 100% | Passou |

Foram produzidas dez capturas completas e trinta transições por rodada. A baseline reproduziu os defeitos de camada e CTA; a rodada final passou em todas as larguras. Houve inspeção visual das páginas completas e revisão independente, seguida de conferência das capturas regeneradas após os últimos ajustes.

O QA verifica overflow, imagens carregadas/proporcionais, geometria e espelhamento das curvas, interação do menu, seleção por setas/dots e retorno por teclado no carrossel. O CTA da Hero tem contraste calculado de 4,064:1 e fonte em negrito de 20–28px, atendendo ao limiar de 3:1 para texto grande. Próximo passo conserva a variante V2 com texto azul escuro, altura desktop de 82,45px e fundo secundário a 30%.

Evidências locais não versionadas: `output/fidelity/comparacao.html`, `before/`, `after/`, `after/report.json`, `figma-sobre-*-final.png`, `maven-package.log` e `evidencias.zip`. A galeria compara página inteira e transições por largura. Para reproduzir, consulte [scripts/README.md](../scripts/README.md).

- Maven/JDK 25: `./mvnw.cmd clean "-Dtest=!ConnectionFactoryTest" package` — 69 testes contabilizados: 65 passaram e 4 de `AdminPerfilPersistenciaTest` foram ignorados por dependerem do ambiente de persistência. Zero falhas/erros; WAR gerado e conferido sem scripts temporários.
- Antes da atualização de main, o mesmo conjunto local executou 52 testes, todos aprovados. A integração de Perfil acrescentou os demais casos, preservados sem alteração.
- Baseline completa: 55 testes; `ConnectionFactoryTest` apresentou uma falha e dois erros sem o ambiente PostgreSQL. Essa dependência foi explicitamente excluída do comando acima; não foi alterada nesta correção.
- `node --check scripts/qa-landing.mjs`, `node --check src/main/webapp/js/carousel-solucao.js` e `git diff --check` sem erros.
- Dependências de QA fixadas em lockfile: `npm audit --prefix scripts` não identificou vulnerabilidades. Nenhuma dependência de produção foi adicionada.

## Limites

O navegador de QA serve o frontend estático: não comprova autenticação, acesso ao banco ou implantação no servidor. Esses destinos e o JavaScript funcional foram preservados, e os testes locais aplicáveis passaram. A conferência visual usa Chromium/Chrome, DPR 1 e zoom 100%; não constitui teste em todos os navegadores ou em todos os níveis de zoom. Não houve merge, alteração de status no Jira ou mudança em demandas do segundo ano. O aceite visual final continua sob revisão da equipe.
