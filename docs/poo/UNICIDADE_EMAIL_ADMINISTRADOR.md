# Unicidade do e-mail do administrador

O login compara e-mails sem diferenciar maiúsculas e minúsculas. A constraint
`UNIQUE(email)` do schema de referência distingue caixa. Assim, dois valores
como `contato@example.invalid` e `CONTATO@example.invalid` podem identificar
contas diferentes no banco e a mesma identidade no login.

## Limite da proteção do Perfil

`AdminDAO.atualizarPerfil` usa transação, consulta parametrizada por `lower(email)`
e advisory lock transacional sobre o e-mail de destino. Isso serializa as
atualizações que passam por esse método. A proteção não alcança escritores que
não adquirem o mesmo lock, como `inserir`, `atualizar` e `atualizarEmail`, nem
acessos SQL externos. Uma atualização administrativa pode introduzir a colisão
mesmo após uma atualização válida do Perfil.

Os testes de concorrência entre dois Perfis não demonstram unicidade global.
A garantia deve existir no PostgreSQL para cobrir todos os escritores. Alterar
apenas a consulta do Perfil ou o nível de isolamento não resolve o contrato dos
demais escritores.

## Referência para uma demanda futura — fora do escopo atual

Em 02/11/2026, o responsável retirou explicitamente a migração de unicidade
global do escopo da finalização atual. Nenhum índice, constraint ou regra global
será alterado nesta entrega. A referência abaixo permanece como conhecimento
técnico para uma demanda futura, que exigirá autorização própria e confirmação
do schema efetivo.

1. Confirmar que o domínio e o login usam a mesma normalização, inclusive a
   política para espaços. O índice proposto usa `lower(email)`, como o login
   atual, e não altera os valores armazenados.
2. Levantar colisões no banco de destino, com acesso restrito por conter dados
   pessoais:

   ```sql
   SELECT lower(email) AS email_normalizado, array_agg(id ORDER BY id) AS contas
   FROM adm
   GROUP BY lower(email)
   HAVING count(*) > 1;
   ```

3. Resolver cada colisão com os responsáveis pelas contas. Não excluir contas,
   unificá-las ou escolher um e-mail automaticamente. Manter backup e um plano
   de reversão das correções de dados.
4. Validar a alteração em homologação com inserção, edição administrativa,
   edição do Perfil e duas conexões concorrentes. Os conflitos devem continuar
   sendo traduzidos para `DadoDuplicadoException` pelos DAOs.
5. Em janela de manutenção, interromper temporariamente os escritores de
   administradores e criar o índice em uma transação controlada:

   ```sql
   CREATE UNIQUE INDEX adm_email_lower_unique ON adm (lower(email));
   ```

   A criação normal bloqueia escritas durante a construção. Avaliar previamente
   o volume e o tempo de bloqueio. Se for necessário manter escritas, planejar
   separadamente `CREATE UNIQUE INDEX CONCURRENTLY`, fora de transação explícita:
   novas colisões podem impedir a conclusão e deixar um índice inválido.
   Não tratar `IF NOT EXISTS` como comprovação de um índice correto e válido.

6. Conferir `pg_index.indisunique`, `pg_index.indisvalid` e a definição em
   `pg_indexes`; repetir os cenários de duplicidade antes de liberar os
   escritores. Preservar a constraint anterior durante esta mudança para
   reduzir o escopo do rollback.

Uma falha na criação impede a liberação da migração. Remover um índice inválido
ou reverter um índice válido exige decisão do responsável pelo banco; remover
a garantia de unicidade reabre o risco de colisão. Não reverter correções de
dados ou executar DDL automaticamente durante o deploy da aplicação.

A limitação entre escritores permanece registrada, mas a criação desse índice
não é uma pendência da entrega atual. Não executar esta proposta como parte da
preparação das PRs ou do deploy desta versão.
