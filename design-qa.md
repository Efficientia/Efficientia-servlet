# Conferência — seção de funcionalidades

## Referência e escopo

- Figma: [Desktop 145 / Funcionalidades](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1/Efficientia---inter-2026?node-id=4585-215).
- Implementação: `src/main/webapp/index.html#funcionalidades` e `css/funcionalidades.css`.
- Apenas a nova seção foi alterada; login, área restrita, CSS global e JavaScript do carrossel foram preservados.
- Os mockups são imagens ilustrativas das telas atuais do Figma, não controles interativos.

## Evidências visuais

As capturas locais ficam fora do Git, na pasta de artefatos desta tarefa:

`C:/Users/osmarcarvalho-ieg/.codex/visualizations/2026/09/29/01a0ed80-71d2-7571-a9fe-24bdf056e0a8/`

| Arquivo | Conteúdo |
| --- | --- |
| `funcionalidades-figma.png` | Referência, 1440 × 992 |
| `funcionalidades-desktop-final.png` | Seção no navegador, aproximadamente 1425 × 992 |
| `funcionalidades-comparacao.png` | Figma à esquerda, HTML à direita, 1440 × 496 |
| `funcionalidades-mobile-final.png` | Seção no celular, aproximadamente 375 × 1301 |

Desktop: viewport de 1440 × 1200 CSS px; celular: 390 × 1600 CSS px.
A barra de rolagem reduz a área útil horizontal. Densidade capturada próxima de 1.
Na comparação, ambas as imagens foram ajustadas para 720 × 496, sem cortar conteúdo.
Ícones, títulos e mockups também foram conferidos ampliados nas capturas individuais.

## Comparação e correções

1. A primeira comparação encontrou quebras de linha diferentes nas descrições e mockups um pouco acima da referência.
2. Foram ajustadas as larguras dos textos e as posições proporcionais dos mockups.
3. A nova captura foi comparada lado a lado com o Figma. Não restaram diferenças de composição que impeçam a entrega.
4. No tablet, o espaço dos mockups passou a acompanhar a largura do próprio bloco, evitando uma área vazia excessiva.

## Resultado por aspecto

- **Fontes:** Manrope nos títulos e IBM Plex Sans nos textos; pesos, tamanhos e quebras conferidos. Pequenas diferenças de renderização entre Figma e navegador são esperadas.
- **Espaçamento:** duas colunas no desktop e uma até 860px; margens equilibradas, sem conteúdo cortado na seção.
- **Cores:** tokens existentes da landing, fundo branco e destaque verde conforme a referência. Isso não constitui uma certificação de contraste WCAG; o verde do título foi mantido como aprovado.
- **Imagens:** oito assets locais, íntegros e carregados; proporções preservadas, sem substituir as telas atuais por mockups antigos. A seção não foi achatada em uma imagem única.
- **Conteúdo:** os cinco recursos e seus textos estão presentes, sem benefícios repetidos, botões fictícios ou números de pesquisa inventados.

## Testes

- Larguras solicitadas: 360, 390, 430, 860, 861, 1300, 1440 e 1920px. O navegador arredondou a largura próxima de 861 para 862px em uma leitura; os lados mobile e desktop da transição foram conferidos.
- Nenhum elemento da nova seção ultrapassou horizontalmente a área útil nas larguras verificadas.
- Todos os assets da seção carregaram; cinco subtítulos de recursos encontrados.
- Menu mobile: abre, fecha ao escolher uma opção e navega para a nova âncora.
- Carrossel em 860px: seta seguinte foi à etapa 2, indicador foi à etapa 3 e seta anterior retornou à etapa 2.
- Console do navegador: nenhum erro registrado durante a conferência.
- HTML: âncora única, oito referências de assets válidas e todos os links internos com destino.
- `git diff --check`: sem erros.

## Limites

- Não houve alteração nem validação de backend/autenticação nesta entrega estática.
- A versão mobile é uma adaptação do desktop aprovado; não havia uma referência mobile específica desta seção.
- Problemas preexistentes de outras seções não foram incluídos neste ajuste.

final result: passed
