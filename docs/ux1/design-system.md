# UI Kit e design system — Efficientia

Registro da implementação da landing page em 03/10/2026. Escopo: **UX1**; UX2 e DAD não fazem parte desta entrega. Este documento descreve o código atual, não propõe alterações de layout.

## Requisito e evidências

Atende à documentação solicitada em [EFFICIENTI-299 — Documentar UI Kit e design system](https://efficientia-team-inter-2026.atlassian.net/browse/EFFICIENTI-299), vinculada a [EFFICIENTI-44 — Evolução do Design System](https://efficientia-team-inter-2026.atlassian.net/browse/EFFICIENTI-44).

Critério de aceite informado na demanda: artefato revisado, com evidência acessível e ligação ao requisito.

| Evidência | Fonte |
| --- | --- |
| Tokens, componentes, responsividade e estados visuais | [style.css](../../src/main/webapp/css/style.css) |
| Estrutura e uso real dos componentes | [index.html](../../src/main/webapp/index.html) |
| Navegação do carrossel | [carousel-solucao.js](../../src/main/webapp/js/carousel-solucao.js) |

O artefato e a ligação ao requisito estão registrados aqui. A revisão humana para aceite ainda está pendente. A documentação não altera o status no Jira e não comprova testes com usuários, avaliação heurística ou entrega de protótipo V2.

## Fundamentos

### Cores

Azul, verde e **creme são cores da marca**. Use os tokens existentes antes de adicionar outra cor. As superfícies translúcidas dependem do fundo sobre o qual são aplicadas.

| Token | Valor |
| --- | --- |
| `--color-primary` | `#0F51AE` |
| `--color-primary-dark` | `#001D89` |
| `--color-accent` | `#56D867` |
| `--color-cream` | `#FFF8E1` |
| `--color-white` | `#FFFFFF` |
| `--color-text` | `#121212` |
| `--color-border` | `#B5B5B5` |
| `--color-surface-subtle` | `rgba(217, 217, 217, 0.20)` |
| `--color-surface-glass` | `rgba(255, 255, 255, 0.15)` |
| `--color-overlay-menu` | `rgba(0, 21, 75, 0.65)` |
| `--color-overlay-menu-strong` | `rgba(0, 21, 75, 0.75)` |
| `--color-surface-white-05` | `rgba(255, 255, 255, 0.05)` |
| `--color-surface-white-12` | `rgba(255, 255, 255, 0.12)` |
| `--color-surface-white-25` | `rgba(255, 255, 255, 0.25)` |
| `--color-surface-white-30` | `rgba(255, 255, 255, 0.30)` |
| `--color-surface-white-70` | `rgba(255, 255, 255, 0.70)` |
| `--color-surface-cream-10` | `rgba(255, 248, 225, 0.10)` |
| `--color-surface-cream-12` | `rgba(255, 248, 225, 0.12)` |
| `--color-surface-cream-20` | `rgba(255, 248, 225, 0.20)` |
| `--color-border-cream` | `rgba(255, 248, 225, 0.30)` |
| `--color-surface-muted` | `rgba(153, 149, 135, 0.10)` |
| `--color-accent-35` | `rgba(86, 216, 103, 0.35)` |
| `--color-accent-60` | `rgba(86, 216, 103, 0.60)` |

### Tipografia

Manrope é a família dos títulos e IBM Plex Sans é a dos textos. As fontes são carregadas pelo Google Fonts, com `sans-serif` como alternativa se o carregamento falhar.

| Token | Valor |
| --- | --- |
| `--font-heading` | `"Manrope", sans-serif` |
| `--font-body` | `"IBM Plex Sans", sans-serif` |
| `--font-size-14` | `14px` |
| `--font-size-16` | `16px` |
| `--font-size-20` | `20px` |
| `--font-size-22` | `22px` |
| `--font-size-24` | `24px` |
| `--font-size-42` | `42px` |
| `--font-size-48` | `48px` |
| `--font-weight-medium` | `500` |
| `--font-weight-semibold` | `600` |
| `--font-weight-bold` | `700` |
| `--font-weight-extrabold` | `800` |
| `--line-height-31` | `31px` |
| `--line-height-default` | `normal` |

No estilo global, `h1`, `h2` e `h3` usam Manrope; `h1` e `h2` partem de 42px. `p`, `a`, `span` e `button` usam IBM Plex Sans. Isso inclui os `span` dentro de títulos; o menu possui sua própria definição de Manrope. Regras específicas e media queries podem sobrescrever os tamanhos globais.

### Raios, sombras e opacidades

Os nomes numéricos dos raios indicam diretamente seu valor. `radius-pill` é o raio de 40px utilizado nos botões; não equivale a um arredondamento infinito. `radius-circle` produz um círculo quando largura e altura são iguais.

| Token | Valor |
| --- | --- |
| `--radius-10` | `10px` |
| `--radius-16` | `16px` |
| `--radius-20` | `20px` |
| `--radius-25` | `25px` |
| `--radius-30` | `30px` |
| `--radius-pill` | `40px` |
| `--radius-circle` | `50%` |
| `--shadow-card` | `0 4px 30px rgba(0, 0, 0, 0.10)` |
| `--shadow-menu` | `0 4px 15px rgba(0, 0, 0, 0.30)` |
| `--shadow-accent-soft` | `0 0 15px rgba(86, 216, 103, 0.40)` |
| `--shadow-accent-strong` | `0 0 10px rgba(86, 216, 103, 0.70)` |
| `--opacity-subtle` | `0.5` |
| `--opacity-hover` | `0.8` |

## UI Kit: componentes existentes

### Botões e links de ação

| Classe | Papel | Aparência e estados |
| --- | --- | --- |
| `.botao-ativo` | Ação primária | Fundo verde, texto creme, raio 40px; hover aumenta o brilho para 1.1 |
| `.botao-desativado` | Ação secundária | Fundo creme com opacidade 12%, borda creme 30%; hover aumenta o fundo para 20% |

Ambos usam IBM Plex Sans, 22px, peso 700 e padding de 18px × 25px na regra base. São alinhados por `inline-flex`.

**Atenção aos nomes:** `botao-ativo` é a variante primária, não um estado selecionado. `botao-desativado` continua interativo: não representa um botão realmente desabilitado. Não existe uma variante desabilitada padronizada documentada no código atual.

Exemplo de uso das classes existentes:

```html
<a class="botao-ativo" href="#solucao">Conheça a solução</a>
<a class="botao-desativado" href="#problema">Veja o problema</a>
```

Use link para navegar e botão para executar uma ação. Links e botões recebem foco visível; não remova o contorno sem fornecer um substituto acessível.

### Badge

`.badge` identifica a seção, com fundo sutil, borda translúcida e raio 30px. O texto base usa 16px, peso 700, caixa alta e azul; as variantes da solução e próximo passo usam verde.

```html
<div class="badge"><p>O problema</p></div>
```

Badge é informativo: **não recebe hover nem foco**.

### Cards

| Seletor | Uso | Estilo base |
| --- | --- | --- |
| `.card-problema` | Problemas do processo manual | Superfície branca 70%, borda neutra, raio 20px, ícone e texto lado a lado |
| `.conteudo-card-solucao` | Legenda dos celulares da solução | Gradiente translúcido, borda branca 30%, sombra de card e cantos inferiores de 20px |

O card de problema usa título azul de 24px/700 e parágrafo escuro de 16px/500. Sua regra base possui altura de 130px, largura máxima de 445px, padding de 40px e gap de 20px. Os ajustes responsivos permanecem no CSS.

Cards são conteúdo, não controles: **não adicionar hover, foco ou `tabindex`** apenas por aparência. Se houver uma ação futura, ela deve ter um link ou botão próprio.

### Menu

`.menu` é a navegação fixa, com fundo azul translúcido, desfoque e raio 25px. `.link-menu` usa Manrope, creme, 24px/700 na regra base. Hover aplica sublinhado, opacidade 0.8 e deslocamento vertical de 3px; foco usa contorno verde.

Até 860px, a navegação usa o botão de menu. O JavaScript alterna `.menu.open` e `aria-expanded`; fecha ao selecionar um link ou clicar fora. O estado expandido deve continuar coerente com o conteúdo exibido.

### Carrossel da solução

Até **860px inclusive**, um card fica ativo por vez. Setas, indicadores e gesto horizontal permitem navegar; avançar após o último retorna ao primeiro, e voltar do primeiro leva ao último. Acima de 860px, os cards aparecem juntos e os controles ficam ocultos.

O CSS e `matchMedia('(max-width: 860px)')` devem manter o mesmo limite. As setas usam fundo azul, borda translúcida e raio circular. Hover usa verde translúcido, escala 1.08 e sombra suave; pressionar usa escala 0.95. O indicador ativo fica verde, com largura de 28px e brilho.

O gesto considera deslocamento mínimo de 45px; a troca animada possui duração de 400ms. Esses valores são implementação atual, não novos tokens.

## Responsividade e movimento

Breakpoints existentes: `max-width` de 1392, 1300, 1200, 961, 925, 860, 659, 636, 480 e 390px; também há `min-width: 1400px`. Não foram criados nem alterados por esta documentação.

Não há escala de espaçamento tokenizada. Ainda existem tamanhos de fonte e valores visuais literais em regras específicas e media queries: a adoção dos tokens é parcial. Padronizar isso futuramente exige comparação visual, sem alterar margens e proporções por consequência.

`prefers-reduced-motion` reduz animações e transições para 0.01ms e desativa a rolagem suave. O temporizador do carrossel continua em 400ms no JavaScript, mesmo com movimento reduzido. Linhas orgânicas são decorativas e não capturam eventos de ponteiro.

## Acessibilidade: situação atual

- Idioma `pt-BR`, textos alternativos nas imagens e rótulos de controles estão presentes no HTML.
- Menu comunica expansão por `aria-expanded`; controles do carrossel possuem rótulos acessíveis.
- Links e botões previstos no CSS recebem `:focus-visible`, contorno verde de 3px e afastamento de 4px.
- Preferência por movimento reduzido possui tratamento em CSS.
- Contraste não foi auditado: verificar especialmente texto creme sobre verde. Não considerar a página conforme à WCAG apenas por possuir tokens e foco.
- Revisar destinos de navegação: há referência a `#funcionalidades` sem seção correspondente e link “Ver fluxo completo” com destino vazio na implementação analisada.

Esses pontos são registro de pendências, não correções realizadas nesta entrega.

## Uso e manutenção

```css
/* Exemplo de consumo dos fundamentos; não é uma nova classe implementada. */
.exemplo {
    color: var(--color-primary);
    font-family: var(--font-body);
    font-size: var(--font-size-16);
    border-radius: var(--radius-20);
}
```

Antes de criar um token, procurar um equivalente e confirmar se o valor precisa variar de forma centralizada. Não criar variáveis sem uso. Antes de remover um token, pesquisar referências em todo o projeto. Não tratar uma variável como autorização para mudar o layout.

## Checklist de revisão para UX1

- [x] Documentar os 51 tokens atuais e os componentes existentes.
- [x] Disponibilizar o documento pelo README e ligar às fontes e ao requisito.
- [ ] Revisão humana do artefato e registro da evidência de aceite.
- [ ] Validar contraste e navegação por teclado.
- [ ] Conferir visual em desktop, tablet e celular, incluindo 1300px e 860px.
- [ ] Testar menu, setas, indicadores, gesto e movimento reduzido.
- [ ] Definir quais pendências serão tratadas em demandas próprias.

Os testes visuais e interativos acima não foram executados como parte desta alteração somente documental. Nenhuma alteração foi feita no Jira.
