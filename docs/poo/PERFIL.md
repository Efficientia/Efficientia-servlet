# Meu Perfil

## Referência e decisões

Referência de interface: frame [5119:3255](https://www.figma.com/design/EBSgjcvj39hdmO1wZNIAr1/?node-id=5119-3255).

Canvas 1440 × 1024; sidebar 200 px; título em x=260/y=100, Manrope 700/38 px; subtítulo IBM Plex Sans 400/17 px; cartão x=260/y=215, 1000 × 530, raio 20, fundo #093378 e borda #2E6BB5. Avatar 110 px, inicial Manrope 700/42 px. Campos 442 × 50 com intervalo 24 px. Botão salvar 190 × 48, raio 9, #4FD959. Os fundos são SVGs originais locais. O botão superior de Perfil reutiliza o componente compartilhado, com círculo de 32 × 32 px e ícone preenchido de 16 × 16 px em `assets/icons/perfil.svg`, inclusive em telas móveis.

O nome e a empresa vêm do banco; não são os textos ilustrativos do frame. Empresa sem vínculo aparece como “Nenhuma empresa vinculada”. Configurações e Notificações foram removidas da topbar conforme a restrição de integração. A busca não aparece no Perfil, pois não há lista para pesquisar. Navegação compartilhada adaptada por seletores restritos a `.perfil-page`, com links visíveis em telas pequenas. Conteúdo e cartão podem crescer com mensagens e nomes longos.

O subtítulo foi alterado de #BACFF0 para branco para garantir contraste nas quatro larguras; a medição sobre o gradiente retornou 6,24:1, 5,12:1, 6,33:1 e 5,07:1, respectivamente. O rodapé pode quebrar o nome em linhas, preservando sua leitura. Essas adaptações, o botão superior padronizado e os ícones preexistentes da sidebar são diferenças intencionais em relação à referência. O botão compartilha HTML, SVG, classes, hover e foco nativo de teclado com os demais painéis, mantendo a altura da topbar própria do Perfil para preservar o layout do conteúdo.

## Backend

`GET /perfil` carrega a conta pelo ID da sessão `adminLogado`. `POST /perfil` aceita somente nome, e-mail e token CSRF. Parâmetros de ID, empresa e senha não participam da atualização. O filtro de autenticação protege ambos; o servlet também valida a sessão. JSP dentro de `WEB-INF`, valores dinâmicos escapados com JSTL e respostas `Cache-Control: no-store`.

Nome obrigatório até 150 caracteres, sem controles; e-mail obrigatório, validado por `ValidadorRegex`, até 254 caracteres. Limites do modelo de banco documentado em `docs/ia/EXEMPLO_IA_BD.md` (tabela `adm`). A unicidade é a regra existente do PostgreSQL; não se introduz normalização de maiúsculas/minúsculas.

O DAO altera nome e e-mail atomicamente, preserva senha e empresa e traduz a restrição de unicidade existente. Como o login ignora caixa, o Perfil também verifica conflitos por `lower(email)`. A transação READ_COMMITTED adquire um advisory lock PostgreSQL por e-mail de destino antes de consultar e atualizar; isso impede duas chamadas concorrentes do Perfil de assumir variantes do mesmo e-mail. Sucesso atualiza a sessão com uma cópia sem senha, gira o token e usa Post/Redirect/Get com mensagem consumida uma vez. Erros mantêm os campos: 422 para validação, 409 para duplicidade, 403 para token ausente/expirado e 503 para indisponibilidade. A consulta existente `buscar` não diferencia ausência de registro de falha SQL; nesse caso o Perfil bloqueia o salvamento e apresenta indisponibilidade.

Não havia CSRF compartilhado. O Perfil usa token aleatório de 256 bits por sessão, comparação constante e rotação após sucesso. A gravação e a rotação são serializadas por sessão para impedir reenvios concorrentes. A proteção não foi estendida às outras funcionalidades. JavaScript bloqueia o envio repetido e restaura o botão ao retornar pelo histórico; a proteção do servidor funciona sem JavaScript.

## Reprodução dos testes

Use JDK 25 e o Maven Wrapper do projeto. Para testes de persistência, prepare um PostgreSQL local em `127.0.0.1:55439`, com o banco vazio `efficientia_perfil_qa` e o usuário `perfil_qa`.

Aplique `src/test/resources/perfil-qa.sql` nesse banco descartável. O fixture cria apenas o schema de conta necessário aos testes e contas sintéticas de demonstração. Não execute o fixture em bancos compartilhados ou de produção.

Configure `JAVA_HOME` para o JDK 25. Em PowerShell:

```powershell
.\mvnw.cmd '-Dtest=PerfilServletTest' test
psql -h 127.0.0.1 -p 55439 -U perfil_qa -d efficientia_perfil_qa -v ON_ERROR_STOP=1 -f src/test/resources/perfil-qa.sql
[Environment]::SetEnvironmentVariable('db.url','jdbc:postgresql://127.0.0.1:55439/efficientia_perfil_qa','Process')
[Environment]::SetEnvironmentVariable('db.user','perfil_qa','Process')
[Environment]::SetEnvironmentVariable('db.password','<senha-do-banco-local>','Process')
.\mvnw.cmd '-Dperfil.db.test=true' '-Dtest=AdminPerfilPersistenciaTest' test
.\mvnw.cmd '-Dperfil.db.test=true' package
node --check src/main/webapp/js/perfil.js
rtk git diff --check
```

`PerfilServletTest` cobre leitura da conta, autorização, validação, sessão, CSRF e prevenção de reenvio. `AdminPerfilPersistenciaTest` cobre preservação de senha/empresa, atomicidade e concorrência na atualização do e-mail.

Sem `-Dperfil.db.test=true`, os quatro testes de persistência do Perfil são ignorados intencionalmente. Os testes existentes de `ConnectionFactoryTest` continuam exigindo banco configurado. O teste de persistência verifica a URL local exata antes de gravar.

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
| `docs/poo/PERFIL.md` | Contratos, decisões de interface, reprodução dos testes e limites |

## Limites e atenção na integração

- Os fluxos administrativos antigos de gravação de e-mail não participam do advisory lock. Uma garantia universal de unicidade sem distinguir caixa exige avaliar e migrar para um índice único em `lower(email)`, com tratamento prévio de conflitos existentes; nenhuma migration foi introduzida neste escopo.
- O banco descartável usa somente o schema de conta documentado no repositório. Não foi testado o schema de produção nem a totalidade dos fluxos CRUD preexistentes.
- Manrope e IBM Plex Sans carregaram durante a validação e seguem a estratégia existente de Google Fonts. Ambientes sem acesso externo usam os fallbacks declarados.
- Não houve validação manual com leitor de tela nem execução em múltiplos navegadores; a evidência visual é do Chrome e do frame desktop fornecido.
- A branch de remoção de controles da topbar ainda precisa ser conciliada no momento da integração, preservando o link de Perfil e a ausência de Configurações/Notificações.
