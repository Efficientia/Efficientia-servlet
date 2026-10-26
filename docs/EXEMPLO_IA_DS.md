# Registro de uso de IA — Desenvolvimento de Sistemas (DS1)

- **Projeto:** Efficientia
- **Ferramentas:** Codex e geração de imagens integrada
- **Atividade registrada:** atualização do mockup da seção do problema da landing page, em 24/10/2026
- **Registro elaborado em:** 26/10/2026

## 1. Contexto e pedido

A landing page utilizava uma imagem com um relatório em papel e um computador com uma versão antiga do dashboard. O objetivo era atualizar a tela exibida sem perder a comparação entre o registro físico e o digital.

O pedido do usuário foi:

> a ideia dessa imagem é metade ser o relatório e a outra metade uma parte do computador com o dashboard aberto nele, tenta recriar do jeito certo e me mostra para ver se eu aprovo

A referência visual e a tela atual do dashboard vieram do projeto da equipe. Não foram criadas pela IA para substituir o design do sistema.

## 2. Como a IA foi utilizada

O Codex usou a imagem anterior como referência de composição e a captura do dashboard atual como referência da interface. A ferramenta de geração de imagens produziu um PNG com fundo transparente: papel à esquerda e apenas parte do computador à direita.

O processo teve uma etapa de aprovação visual antes da aplicação. Depois da aprovação no Figma, o usuário solicitou:

> altera no html

No código, o Codex adicionou o PNG aos assets, trocou o endereço da imagem no HTML e atualizou seu texto alternativo. O CSS, os espaçamentos e o funcionamento do menu e do carrossel não foram alterados nessa entrega.

## 3. Revisão humana e correção

A geração não acertou a composição de primeira. Uma tentativa mostrou o papel ao lado de um computador inteiro, em vez de uma única peça dividida. O usuário explicou a divisão desejada e pediu outra versão.

Na versão seguinte, o recorte da base do computador ficou deslocado para a direita. O usuário apontou que esse detalhe também deveria acompanhar a divisão ao meio. A imagem foi corrigida, deixando parte do recorte escondida pelo papel.

Somente depois dessa correção o usuário respondeu:

> perfeito aprovado

A escolha da composição e a aprovação foram humanas. A IA executou a geração e os ajustes a partir desse retorno.

## 4. Resultado e evidências

| Evidência | Onde conferir |
| --- | --- |
| Imagem aprovada, com fundo transparente | [mockup-comparativo-dashboard-atual.png](../src/main/webapp/assets/mockup-comparativo-dashboard-atual.png) |
| Uso da imagem e texto alternativo | [Seção do problema no HTML](../src/main/webapp/index.html#L96) |
| Alteração versionada | Commit `938aef6` — `fix/DS/EFFICIENTI-10/atualiza-comparativo-de-papel-e-dashboard` |
| Composição aplicada no protótipo público V2 | [Comparação no Figma](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1/Efficientia---inter-2026?node-id=4780-754) |

Na implementação, os hashes SHA-256 do PNG aprovado e do arquivo copiado para o projeto foram comparados e eram iguais. O HTML foi aberto no navegador para conferir a imagem renderizada, e `git diff --check` não apontou erros de formatação. Isso comprova essa substituição pontual, não uma nova validação de toda a aplicação.

## 5. Limites e cuidados

A IA ajudou a preparar um recurso visual e aplicá-lo ao front-end. Ela não criou um dashboard funcional nessa imagem nem alterou o back-end. Um mockup gerado pode reinterpretar letras e detalhes; a tela original do Figma continua sendo a referência do produto.

O exemplo mostra uma limitação concreta: a IA precisou de correções humanas para representar a divisão física do computador. A aprovação visual não deve ser apresentada como teste de usabilidade ou comprovação da utilidade do sistema.

Este registro utiliza pedidos e artefatos do projeto, sem reproduzir senhas, conteúdo do `.env` ou respostas individuais de participantes.
