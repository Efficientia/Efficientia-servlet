# Meu Perfil

Implementação na worktree `efficientia-perfil`, branch `feat/ds/perfil`, a partir de `983c63f`.

## Referência e decisões

Frame [5119:3255](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1/?node-id=5119-3255), consultado com `get_design_context` e captura pelo MCP oficial do Figma em 01/11/2026.

Canvas 1440 × 1024; sidebar 200 px; título em x=260/y=100, Manrope 700/38 px; subtítulo IBM Plex Sans 400/17 px; cartão x=260/y=215, 1000 × 530, raio 20, fundo #093378 e borda #2E6BB5. Avatar 110 px, inicial Manrope 700/42 px. Campos 442 × 50 com intervalo 24 px. Botão salvar 190 × 48, raio 9, #4FD959. Os fundos são SVGs originais locais. O botão superior de Perfil reutiliza o componente compartilhado, com círculo de 32 × 32 px e ícone preenchido de 16 × 16 px em `assets/icons/perfil.svg`, inclusive em telas móveis.

O nome e a empresa vêm do banco; não são os textos ilustrativos do frame. Empresa sem vínculo aparece como “Nenhuma empresa vinculada”. Configurações e Notificações foram removidas da topbar conforme a restrição de integração. A busca não aparece no Perfil, pois não há lista para pesquisar. Navegação compartilhada adaptada por seletores restritos a `.perfil-page`, com links visíveis em telas pequenas. Conteúdo e cartão podem crescer com mensagens e nomes longos.

O subtítulo foi alterado de #BACFF0 para branco para garantir contraste nas quatro larguras; a medição sobre o gradiente retornou 6,24:1, 5,12:1, 6,33:1 e 5,07:1, respectivamente. O rodapé pode quebrar o nome em linhas, preservando sua leitura. Essas adaptações, o botão superior padronizado e os ícones preexistentes da sidebar são diferenças intencionais em relação à referência. O botão compartilha HTML, SVG, classes, hover e foco nativo de teclado com os demais painéis, mantendo a altura da topbar própria do Perfil para preservar o layout do conteúdo.

## Plano de execução

1. Investigar modelo, DAO, autenticação, limites e fragmentos; confirmar isolamento e obter referência do Figma antes de editar.
2. Criar testes do servlet para leitura, edição, validação, sessão, CSRF, reenvio e autorização; executar antes e depois da implementação.
3. Adicionar `AdminDAO.atualizarPerfil(int, String, String)` com uma única instrução parametrizada e criar `PerfilServlet`, JSP, CSS e JavaScript.
4. Executar Maven, validar SQL em PostgreSQL descartável, renderizar em Tomcat isolado e comparar capturas em 1440, 1024, 768 e 390 px.
5. Fazer revisão somente leitura; corrigir problemas, registrar resultados e limites. Publicação em GitHub somente após autorização explícita.

## Backend

`GET /perfil` carrega a conta pelo ID da sessão `adminLogado`. `POST /perfil` aceita somente nome, e-mail e token CSRF. Parâmetros de ID, empresa e senha não participam da atualização. O filtro de autenticação protege ambos; o servlet também valida a sessão. JSP dentro de `WEB-INF`, valores dinâmicos escapados com JSTL e respostas `Cache-Control: no-store`.

Nome obrigatório até 150 caracteres, sem controles; e-mail obrigatório, validado por `ValidadorRegex`, até 254 caracteres. Limites do modelo de banco documentado em `docs/ia/EXEMPLO_IA_BD.md` (tabela `adm`). A unicidade é a regra existente do PostgreSQL; não se introduz normalização de maiúsculas/minúsculas.

O DAO altera nome e e-mail atomicamente, preserva senha e empresa e traduz a restrição de unicidade existente. Como o login ignora caixa, o Perfil também verifica conflitos por `lower(email)`. A transação READ_COMMITTED adquire um advisory lock PostgreSQL por e-mail de destino antes de consultar e atualizar; isso impede duas chamadas concorrentes do Perfil de assumir variantes do mesmo e-mail. Sucesso atualiza a sessão com uma cópia sem senha, gira o token e usa Post/Redirect/Get com mensagem consumida uma vez. Erros mantêm os campos: 422 para validação, 409 para duplicidade, 403 para token ausente/expirado e 503 para indisponibilidade. A consulta existente `buscar` não diferencia ausência de registro de falha SQL; nesse caso o Perfil bloqueia o salvamento e apresenta indisponibilidade.

Não havia CSRF compartilhado. O Perfil usa token aleatório de 256 bits por sessão, comparação constante e rotação após sucesso. A gravação e a rotação são serializadas por sessão para impedir reenvios concorrentes. A proteção não foi estendida às outras funcionalidades. JavaScript bloqueia o envio repetido e restaura o botão ao retornar pelo histórico; a proteção do servidor funciona sem JavaScript.

## Validação

Ambiente: JDK 25.0.2, Maven Wrapper 3.9.6, Tomcat 11.0.25 em `127.0.0.1:8189`, PostgreSQL 16.6 descartável em `127.0.0.1:55439`, Chrome por sessão dedicada de agent-browser 0.41.1. Tudo de execução foi colocado em `target/qa`; nenhum servidor ou banco compartilhado foi usado. A instalação do Tomcat existente foi somente lida. Contas nas capturas são fixtures sintéticos persistidos nesse banco, não contas de produção nem valores fixos da JSP.

Comandos executados:

```powershell
$env:JAVA_HOME='C:\Users\osmarcarvalho-ieg\.jdks\openjdk-25.0.2'
.\mvnw.cmd '-Dtest=PerfilServletTest' test
.\mvnw.cmd test
# Após criar o PostgreSQL descartável e aplicar src/test/resources/perfil-qa.sql:
[Environment]::SetEnvironmentVariable('db.url','jdbc:postgresql://127.0.0.1:55439/efficientia_perfil_qa','Process')
[Environment]::SetEnvironmentVariable('db.user','perfil_qa','Process')
[Environment]::SetEnvironmentVariable('db.password','qa-descartavel','Process')
.\mvnw.cmd '-Dperfil.db.test=true' '-Dtest=AdminPerfilPersistenciaTest' test
.\mvnw.cmd '-Dperfil.db.test=true' package
.\mvnw.cmd '-DskipTests' package # Reempacotamento após o último ajuste exclusivamente de CSS
node --check src/main/webapp/js/perfil.js
rtk git diff --check
```

- Primeiro teste do servlet falhou por ausência de implementação; após implementar: 13 testes aprovados.
- Primeira suíte sem banco: 50 testes executados, 47 aprovados; `ConnectionFactoryTest` teve 1 falha e 2 erros por ausência de configuração.
- PostgreSQL: os testes reproduziram duplicidade por caixa e corrida entre contas antes da correção (2 falhas); após a correção, os 4 testes de persistência passaram.
- Suíte completa com PostgreSQL: **54 testes, 0 falhas, 0 erros, 0 ignorados**; WAR gerado. Sem `-Dperfil.db.test=true`, os quatro testes novos de persistência são ignorados intencionalmente e os testes antigos de conexão continuam exigindo banco configurado.
- Navegador: login real, link de Perfil da topbar, redirect anônimo, leitura de nome/e-mail/empresa, edição persistida, saudação sincronizada, sucesso PRG, duplicidade sem distinguir caixa com preenchimento preservado, HTTP 422 para e-mail inválido, HTTP 403 sem CSRF e HTML malicioso exibido como texto sem execução.
- Capturas atuais inspecionadas em 1440, 1024, 768 e 390 px: sem overflow horizontal; campos em duas colunas até tablet e uma coluna no celular. Em 1440, cartão x=260/y=215,05, 1000 × 530; campos x=306 e x=772/y=510,05, 442 × 50; botão x=1024/y=670,05, 190 × 48.
- axe-core 4.12.1, WCAG 2 A/AA: 0 violações e 1 verificação incompleta, referente a contraste de título/subtítulo sobre SVG (2 nós). O subtítulo foi ajustado e conferido por medição manual; o título branco em tamanho grande também manteve contraste suficiente. A medição é uma amostragem de pixels de fundo imediatamente acima das linhas de texto, sem substituir avaliação assistiva humana.
- Revisão independente somente leitura: conflito de caixa encontrado e corrigido; releitura do DAO e dos testes sem novos achados concretos.
- Padronização final do botão: comparação da Home e do Perfil em 1440 e 390 px, nos estados normal, hover e foco por teclado. Geometria, SVG e estilos idênticos; clique na Home e Enter no próprio Perfil navegam para `/Efficientia/perfil`. Posição e dimensões do cartão preservadas. O relatório está em `botao-comparacao.json`.
- Ao terminar, Tomcat, PostgreSQL e navegador dedicados foram encerrados e `target/qa` removido. Capturas e medições permanecem em `docs/qa/perfil`; WAR e relatórios Maven permanecem em `target`. Não há dependências de teste adicionadas ao `pom.xml`.

Evidências versionáveis em [docs/qa/perfil](../qa/perfil): `figma-5119-3255.png`, `perfil-1440.png`, `perfil-1024.png`, `perfil-768.png`, `perfil-390.png`, `medidas-1440.json`, `medidas-1024.json`, `medidas-768.json`, `medidas-390.json`, `contraste.json`, `botao-comparacao.json`.

## Arquivos e responsabilidades

| Arquivo | Responsabilidade |
| --- | --- |
| `src/main/java/com/efficientia/efficientia/controller/PerfilServlet.java` | GET/POST, validação, CSRF, mensagens, identificação e sincronização de sessão |
| `src/main/java/com/efficientia/efficientia/dao/impl/AdminDAO.java` | Atualização transacional de nome/e-mail, unicidade e preservação dos demais atributos |
| `src/main/java/com/efficientia/efficientia/filter/AuthFilter.java` | Proteção da rota `/perfil` |
| `src/main/webapp/WEB-INF/views/perfil.jsp` | Formulário acessível e saída dinâmica escapada |
| `src/main/webapp/WEB-INF/views/fragments/topbar.jspf` | Link contextual para Perfil, estado atual, ausência de busca no Perfil e remoção dos dois controles |
| `src/main/webapp/css/perfil.css` | Estilos da página e adaptações responsivas restritas ao Perfil |
| `src/main/webapp/js/perfil.js` | Bloqueio de reenvio, restauração pelo histórico e foco em mensagens |
| `src/main/webapp/assets/perfil-fundo.svg` | Gradiente original do Figma |
| `src/main/webapp/assets/perfil-ondas.svg` | Ondas originais do Figma |
| `src/test/java/com/efficientia/efficientia/controller/PerfilServletTest.java` | 13 testes de controle, autorização, dados, sessão, validação, CSRF e falhas |
| `src/test/java/com/efficientia/efficientia/dao/AdminPerfilPersistenciaTest.java` | 4 testes reais de persistência/atomicidade/concorrência, opt-in e com guarda de banco |
| `src/test/resources/perfil-qa.sql` | Schema mínimo e contas sintéticas para PostgreSQL descartável |
| `docs/poo/PERFIL.md` | Decisões, validação, reprodução, inventário e limites |
| `docs/qa/perfil/*.png` | As cinco capturas listadas acima |
| `docs/qa/perfil/medidas-*.json` | As quatro medições listadas acima, incluindo geometrias, assets e overflow |
| `docs/qa/perfil/contraste.json` | Medições de contraste nas quatro larguras |
| `docs/qa/perfil/botao-comparacao.json` | Igualdade do botão entre Home e Perfil, incluindo estados e navegação |

## Limites e atenção na integração

- Os fluxos administrativos antigos de gravação de e-mail não participam do advisory lock. Uma garantia universal de unicidade sem distinguir caixa exige avaliar e migrar para um índice único em `lower(email)`, com tratamento prévio de conflitos existentes; nenhuma migration foi introduzida neste escopo.
- O banco descartável usa somente o schema de conta documentado no repositório. Não foi testado o schema de produção nem a totalidade dos fluxos CRUD preexistentes.
- Manrope e IBM Plex Sans carregaram durante a validação e seguem a estratégia existente de Google Fonts. Ambientes sem acesso externo usam os fallbacks declarados.
- Não houve validação manual com leitor de tela nem execução em múltiplos navegadores; a evidência visual é do Chrome e do frame desktop fornecido.
- A branch de remoção de controles da topbar ainda precisa ser conciliada no momento da integração, preservando o link de Perfil e a ausência de Configurações/Notificações.
- Nenhum merge faz parte desta entrega. A publicação da branch e da PR foi autorizada pelo solicitante após a validação local.
