# Especificação Técnica (Spec): Integração do Front-End (Design System) com o Back-End MVC

> **Projeto:** Efficientia — Gestão de Transporte e Bem-Estar Animal  
> **Módulo:** Camada de Visão (JSP/JSTL), Controle Web (Servlets) e Design System da Área Restrita  
> **Autor / Equipe:** Projeto Interdisciplinar — 2º Semestre  
> **Status:** Em Implementação  
> **Versão:** 1.0  

---

## 1. Identificação e Visão Geral da Especificação

### 1.1. Propósito
Esta especificação define os requisitos funcionais, a arquitetura, a stack tecnológica, a estratégia de versionamento e o roteiro formal de testes para o **acoplamento das telas de cadastro da área restrita** (entregues pelo time de Design no Pull Request #37, commit `701e682`) às funcionalidades já existentes no back-end do **Efficientia** (Servlets, DAOs, validação por REGEX e tratamento de duplicidade sobre PostgreSQL).

### 1.2. Problema Resolvido
O PR #37 adicionou à raiz de `src/main/webapp/` cinco telas **HTML estáticas** (`pecuaristas`, `motoristas`, `caminhoes`, `empresas`, `administradores`), cada uma acompanhada de um `.js` que **simula o CRUD em memória** com arrays de dados fictícios (`let pecuaristas = [...]`). Isso gerava:
1. **Dados fictícios no cliente:** a tela não refletia o banco de dados PostgreSQL.
2. **Nenhuma persistência:** inserir, editar e excluir apenas manipulavam o DOM; recarregar a página descartava tudo.
3. **Acesso sem autenticação:** os `.html` na raiz do `webapp` não passam pelo [AuthFilter](../src/main/java/com/efficientia/efficientia/filter/AuthFilter.java), podendo ser abertos diretamente pela URL.
4. **Chaves estrangeiras fixas:** listas de empresas escritas manualmente no HTML (`<option>` e `<datalist>`) em vez de refletir a tabela `empresa`.

A solução converte cada tela em uma **JSP renderizada no servidor**, protegida em `/WEB-INF/views/`, alimentada pelos atributos que os Servlets já injetam via `req.setAttribute()`, preservando integralmente a identidade visual do Design System.

### 1.3. Escopo e Fora de Escopo

| Item | Dentro do escopo | Observação |
| :--- | :---: | :--- |
| Conversão das 5 telas de cadastro para JSP + JSTL | Sim | Pecuarista, Motorista, Caminhão, Empresa, Administrador |
| Reaproveitamento de `style.css` e ícones SVG | Sim | Folha movida para `css/painel.css` |
| Remoção de dados mockados e CRUD em memória | Sim | Arquivos `*.html` e `*.js` de mock removidos |
| Modo de edição integrado ao formulário | Sim | Substitui as telas `editar*.jsp` separadas |
| Ajustes mínimos nos Servlets (edição, senha, feedback) | Sim | Ver seção 5 |
| Tela de Trajetos | Não | Sem design entregue; a UI antes hospedada em `teste.jsp` deixa de existir (ver nota abaixo) |
| Implementação da Visão Geral (Home / Dashboard) | Sim | Nova tela `home.jsp` servida por `HomeServlet` (`/home`), tornando-se a rota inicial pós-login |
| Redesenho da tela `erro.jsp` | Não | Mantida como está |

---

## 2. Tecnologias e Ferramentas Utilizadas

| Tecnologia | Versão | Camada / Papel | Descrição e Finalidade no Subsistema |
| :--- | :---: | :--- | :--- |
| **Java (JDK)** | `25 / 17+` | Linguagem Core | Servlets, DAOs e Models consumidos pelas views. |
| **Jakarta Servlets API** | `6.0 / 11.0` | Camada de Controle (Web) | `doGet` injeta atributos e faz `forward`; `doPost` aplica o padrão PRG. |
| **JSP (Jakarta Server Pages)** | `3.1` | Camada de Visão | Renderização server-side das telas sob `/WEB-INF/views/`. |
| **JSTL Core** | `3.0` | Camada de Visão | `<c:forEach>`, `<c:if>`, `<c:choose>`, `<c:out>` para listagem e escape de HTML. |
| **Expression Language (EL)** | `5.0` | Camada de Visão | Acesso aos atributos (`${pecuaristaModels}`) e à sessão (`${sessionScope.adminLogado}`). |
| **CSS3 (Vanilla)** | — | Design System | Paleta escura, cards, tabelas e toasts em `css/painel.css`. |
| **JavaScript (Vanilla)** | ES2020 | Interatividade de UI | Apenas utilitários de interface (máscaras, olho de senha, toasts); **sem dados**. |
| **PostgreSQL** | `14+` | Banco de Dados | Fonte única da verdade para as listagens. |
| **Apache Tomcat** | `10.1+` | Servlet Container | Hospeda e compila as JSPs. |
| **Apache Maven** | `3.9.6` | Build | Empacotamento `.war` e execução da suíte de testes. |

---

## 3. Arquitetura da Solução e Princípios MVC

### 3.1. Estado Atual vs. Estado Alvo

```
        ESTADO ATUAL (PR #37)                              ESTADO ALVO (MVC)
  ┌──────────────────────────────┐                ┌────────────────────────────────────┐
  │ /pecuaristas.html (estático) │                │ GET /pecuarista  (PecuaristaServlet)│
  │ /pecuaristas.js   (mock)     │                │        │ dao.listar() / buscar...   │
  │   let pecuaristas = [ ... ]  │      ──►       │        ▼ req.setAttribute(...)      │
  │   CRUD simulado no DOM       │                │ /WEB-INF/views/pecuarista.jsp       │
  │ (sem sessão, sem banco)      │                │   <c:forEach> + formulário POST     │
  └──────────────────────────────┘                └────────────────────────────────────┘
```

### 3.2. Diagrama de Sequência (Listagem, Cadastro e Edição)

```
[Browser]                [AuthFilter]        [Servlet]              [DAO / PostgreSQL]       [JSP]
    │ GET /pecuarista         │                  │                         │                    │
    ├────────────────────────►│ sessão adminLogado?                        │                    │
    │                         ├─────────────────►│ dao.listar()            │                    │
    │                         │                  ├────────────────────────►│                    │
    │                         │                  │◄── List<PecuaristaModel>┤                    │
    │                         │                  │ setAttribute + forward ─────────────────────►│
    │◄──────────────────────────────────────────────────── HTML renderizado (tabela + form) ───┤
    │                         │                  │                         │                    │
    │ POST /pecuarista (acao=inserir | atualizar | excluir)                 │                    │
    ├────────────────────────►├─────────────────►│ valida (REGEX) → dao.*  │                    │
    │◄──── 302 /pecuarista?sucesso=cadastrado (PRG)  ◄───────────────────────┤                    │
    │ GET /pecuarista?acao=editar&id=7                                       │                    │
    ├────────────────────────►├─────────────────►│ dao.buscar(7) + listar()│                    │
    │◄──── mesma JSP em MODO EDIÇÃO (form preenchido, acao=atualizar) ──────────────────────────┤
```

### 3.3. Decisão de Design: Modo de Edição Integrado
O design entregue já previa edição **no próprio formulário** (campo `editingId`, título "Editar …" e botão "Salvar Alterações"). Em vez de criar cinco novas telas `editar-*.jsp`, cada JSP possui **dois modos** definidos pela presença do atributo de edição enviado pelo Servlet:

| Modo | Condição EL | `acao` enviada | Título do card | Botão |
| :--- | :--- | :--- | :--- | :--- |
| **Cadastro** | `empty pecuaristaModel` | `inserir` (rota padrão do Servlet) | Cadastrar Novo Pecuarista | Cadastrar pecuarista |
| **Edição** | `not empty pecuaristaModel` | `atualizar` + `id` oculto | Editar Pecuarista #ID | Salvar Alterações |

---

## 4. Especificação dos Componentes Implementados

### 4.1. Design System e Recursos Estáticos

| Artefato | Origem | Destino | Ação |
| :--- | :--- | :--- | :--- |
| Folha de estilos | `webapp/style.css` | [css/painel.css](../src/main/webapp/css/painel.css) | `git mv` (preserva histórico; evita colisão com `css/style.css` da landing page) |
| Ícones SVG | `webapp/assets/icons/*.svg` | (mantido) | Referenciados via `${pageContext.request.contextPath}/assets/icons/` |
| Utilitários de UI | trechos de `*.js` mock | [js/painel.js](../src/main/webapp/js/painel.js) | Novo arquivo único e compartilhado, sem dados |

### 4.2. Fragmentos JSP Compartilhados (`/WEB-INF/views/fragments/`)
Eliminam a duplicação de ~100 linhas de layout presentes em cada HTML do design. Incluídos com a diretiva estática `<%@ include file="..." %>`, de modo que as variáveis de página (`paginaAtiva`, `paginaServlet`) são visíveis.

| Fragmento | Responsabilidade |
| :--- | :--- |
| `sidebar.jspf` | Logo, menu lateral com item 'Visão geral' (`/home`) e módulos administrativos (item ativo via `paginaAtiva`), saudação com `${sessionScope.adminLogado.nome}` e botão de logout (`/logout`) |
| `topbar.jspf` | Barra superior com busca contextual (local na Home, server-side `GET` nos cadastros) e botões de ação (Configurações, Notificações com indicador e Perfil) |
| `toasts.jspf` | Converte os parâmetros `sucesso` e `erro` da URL em toasts estilizados |
| `head.jspf` | `<head>` comum: metadados, título (`tituloPagina`), fonte IBM Plex Sans e `css/painel.css` |
| `waves.jspf` | Linhas de onda decorativas de fundo |

### 4.3. Utilitário de Interface: [painel.js](../src/main/webapp/js/painel.js)
Responsabilidades **exclusivamente visuais** (nenhum dado de negócio):
- Alternância de visibilidade de senha (`#togglePasswordBtn`).
- Máscaras e saneamento: apenas dígitos (CPF, CNPJ, telefone), data `DD/MM/AAAA`, placa em maiúsculas.
- Conversão `DD/MM/AAAA` ⇄ `AAAA-MM-DD` entre o campo visível e o campo oculto `dataNascimento` (o `parseLocalDate` dos Servlets exige formato ISO).
- Confirmação de exclusão (`confirm`) e auto-dispensa dos toasts.
- Botão "Limpar Formulário" (em modo edição, retorna à rota de cadastro).

### 4.4. Páginas JSP

| Tela de Design (removida) | JSP Protegida | Rota (Servlet) | Atributos consumidos |
| :--- | :--- | :--- | :--- |
| `home.html` | `/WEB-INF/views/home.jsp` | `/home` ([HomeServlet](../src/main/java/com/efficientia/efficientia/controller/HomeServlet.java)) | `sessionScope.adminLogado` |
| `pecuaristas.html` | `/WEB-INF/views/pecuarista.jsp` | `/pecuarista` ([PecuaristaServlet](../src/main/java/com/efficientia/efficientia/controller/PecuaristaServlet.java)) | `pecuaristaModels`, `pecuaristaModel` (edição), `termoBusca` |
| `caminhoes.html` | `/WEB-INF/views/caminhao.jsp` | `/caminhao` ([CaminhaoServlet](../src/main/java/com/efficientia/efficientia/controller/CaminhaoServlet.java)) | `caminhaoModels`, `empresaModels`, `caminhaoModel` |
| `empresas.html` | `/WEB-INF/views/empresa.jsp` | `/empresa` ([EmpresaServlet](../src/main/java/com/efficientia/efficientia/controller/EmpresaServlet.java)) | `empresaModels`, `empresaModel` |
| `motoristas.html` | `/WEB-INF/views/motorista.jsp` | `/motorista` ([MotoristaServlet](../src/main/java/com/efficientia/efficientia/controller/MotoristaServlet.java)) | `motoristaModels`, `empresaModels`, `motoristaModel` |
| `administradores.html` | `/WEB-INF/views/admin.jsp` | `/admin` ([AdminServlet](../src/main/java/com/efficientia/efficientia/controller/AdminServlet.java)) | `adminModels`, `empresas`, `adminModel` |

---

## 5. Ajustes Necessários nos Servlets

| ID | Ajuste | Servlets | Justificativa |
| :---: | :--- | :--- | :--- |
| **S-01** | No `GET ?acao=editar`, **não** fazer `forward` para `editar*.jsp`; apenas definir o atributo `*Model` e **seguir o fluxo normal de listagem** | Os 5 | Habilita o modo de edição integrado (seção 3.3). |
| **S-02** | Em `atualizar`, **senha vazia mantém a senha atual** (`dao.buscar(id).getSenha()`) | Pecuarista, Motorista, Admin | O DAO grava `senha = ?` sempre; sem o ajuste, editar sem redigitar a senha a apagaria. A tela não pré-carrega a senha (segurança). |
| **S-03** | Redirecionamentos de sucesso passam a incluir `?sucesso=cadastrado\|atualizado\|excluido` | Caminhão, Empresa, Motorista, Admin | Padroniza o feedback já existente em `PecuaristaServlet` e alimenta `toasts.jspf`. |
| **S-04** | `ROTA_AREA_RESTRITA` (`LoginServlet`) configurada para `/home`; `/home` incluído nos `urlPatterns` do `AuthFilter`; `AuthServletTest` atualizado | LoginServlet, AuthFilter | A tela Visão Geral (`home.jsp`) centraliza o acesso a todos os módulos como destino inicial pós-login. |

> [!WARNING]
> `teste.jsp` era a única tela que consumia o `TrajetoServlet` (que encaminha para `/WEB-INF/views/trajeto.jsp`, inexistente). Sem design de Trajetos entregue, a rota `/trajeto` fica sem interface até que essa tela seja desenhada e convertida seguindo este mesmo padrão.

> [!NOTE]
> Os fluxos de erro de duplicidade continuam redirecionando para [erro.jsp](../../src/main/webapp/erro.jsp) via [WebErroUtil](../../src/main/java/com/efficientia/efficientia/util/WebErroUtil.java), conforme [SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md](../logica/SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md). Erros de formato (`cpf_invalido`, `email_invalido`, `telefone_invalido`, `placa_invalida`, `cnpj_invalido`) voltam à própria tela como toast.

---

## 6. Matriz de Mapeamento: Campo do Design × Parâmetro do Servlet × Coluna do Banco

| Tela | Campo (Design) | `name` enviado ao Servlet | Atributo do Model | Observação |
| :--- | :--- | :--- | :--- | :--- |
| **Pecuarista** | Nome Completo | `nome` | `getNome()` | — |
| | CPF | `cpf` | `getCpf()` | Servlet valida por REGEX e sanitiza para 11 dígitos |
| | Data de Nascimento | `dataNascimento` (oculto, ISO) | `getDataNascimento()` | Campo visível `DD/MM/AAAA` sem `name` |
| | E-mail / Telefone / Senha | `email` / `telefone` / `senha` | `getEmail()` / `getTelefone()` | Senha é *hashed* no DAO |
| **Motorista** | Nome, Nascimento, E-mail, Telefone, Senha | `nome`, `dataNascimento`, `email`, `telefone`, `senha` | `UsuarioModel` | — |
| | Empresa *(novo campo, opcional)* | `idEmpresa` | `getEmpresa()` | `<select>` dinâmico de `${empresaModels}` |
| **Caminhão** | Placa Cavalo / Carreta | `placaCavalo` / `placaCarreta` | `getPlacaCavalo()` / `getPlacaCarreta()` | Servlet normaliza e valida (Mercosul/antiga) |
| | Capacidade Máxima | `capacidadeMaxima` | `getCapacidadeMaxima()` | — |
| | Empresa Proprietária | `idEmpresa` | `getEmpresa()` | `<select>` dinâmico |
| **Empresa** | Razão Social / Nome | `nome` | `getNome()` | — |
| | CNPJ | `cnpj` | `getCnpj()` | Servlet valida e sanitiza para 14 dígitos |
| | *(código)* | `codigo` (oculto, só na edição) | `getCodigo()` | Exibido como coluna; não digitável |
| **Administrador** | Nome, E-mail, Senha | `nome`, `email`, `senha` | `AdminModel` | — |
| | Empresa Vinculada | `idEmpresa` | `getEmpresa()` | `<select>` com opção "Sem vínculo" (valor vazio) |

### 6.1. Divergências Intencionais entre o Design Entregue e o Modelo de Dados

| Tela | Design original | Decisão | Motivo |
| :--- | :--- | :--- | :--- |
| Motorista | Coluna **CPF** na tabela | Substituída por **EMPRESA** | `MotoristaModel`/tabela `motorista` não possuem CPF. |
| Empresa | Coluna **DATA DE CADASTRO** | Substituída por **CÓDIGO** | A tabela `empresa` não registra data de criação. |
| Empresa / Caminhão / Admin | Listas de empresas fixas no HTML | `<select>` populado do banco | Evita IDs inexistentes e reflete cadastros reais. |
| Todas | Busca client-side por `input` | Busca server-side (`GET ?busca=`) | Reaproveita as buscas dos DAOs ([DOCUMENTACAO_BUSCA_POR_NOME.md](DOCUMENTACAO_BUSCA_POR_NOME.md)). |

---

## 7. Estratégia de Versionamento (Git)

### 7.1. Estrutura de Branches

| Nível | Branch | Conteúdo |
| :--- | :--- | :--- |
| **Base (épico)** | `feat/ds-jsp/acoplamento-frontend-backend` | Spec, CSS/JS compartilhados, fragmentos e integração das features |
| **Feature 1** | `feat/POO/EFFICIENTI22/integra-jsp-pecuarista` | `pecuarista.jsp` + ajustes S-01/S-02/S-03 em `PecuaristaServlet` |
| **Feature 2** | `feat/POO/EFFICIENTI22/integra-jsp-caminhao-empresa` | `caminhao.jsp`, `empresa.jsp` |
| **Feature 3** | `feat/POO/EFFICIENTI22/integra-jsp-motorista-admin` | `motorista.jsp`, `admin.jsp` |
| **Limpeza** | `refactor/POO/EFFICIENTI22/remove-mocks-html-e-js` | Remoção de `*.html`/`*.js` mockados e `editar.jsp` órfão |

### 7.2. Fluxo e Convenção de Commits
`main` → épico → features (merge `--no-ff` no épico) → *push* de cada branch → Pull Request único do épico para `main`. Padrão de mensagem: `<tipo>/<DISCIPLINA>/<TASK-ID>/<descricao-em-kebab-case>`.

---

## 8. Sequência Completa de Testes da Implementação

### Fase 1: Verificação Estática e Testes Automatizados
```powershell
$env:JAVA_HOME = "C:\Users\20260095-ieg\.jdks\openjdk-25.0.2"
./mvnw clean test
./mvnw clean package -DskipTests   # compila o WAR e valida as JSPs empacotadas
```

| ID | Verificação | Resultado Esperado |
| :---: | :--- | :--- |
| **ST-01** | Suíte JUnit existente (`ValidadorRegexTest`, `TratadorExcecaoBancoTest`) | Todos **PASS** (nenhum Servlet testado foi alterado em assinatura) |
| **ST-02** | `grep` por `pecuaristas.js`, `let pecuaristas`, `.html"` nos JSPs | Nenhuma ocorrência |
| **ST-03** | Nenhum arquivo de gestão fora de `/WEB-INF/views/` | Somente `login.jsp`, `erro.jsp`, `esqueci-senha.jsp` e `index.html` na raiz |

### Fase 2: Teste de Integração (Servlet × DAO × PostgreSQL)

| Passo | Ação | Resultado Esperado |
| :---: | :--- | :--- |
| 1 | `GET /pecuarista` autenticado | Tabela exibe exatamente os registros de `SELECT * FROM pecuarista` |
| 2 | `GET /pecuarista?busca=<nome parcial>` | Tabela filtrada pela busca do DAO; campo de busca mantém o termo |
| 3 | `GET /caminhao` | `<select>` de empresas contém todas as linhas de `empresa` |
| 4 | `GET /pecuarista?acao=editar&id=N` | Mesma JSP em modo edição, campos preenchidos, senha vazia |
| 5 | `POST atualizar` com senha em branco | Senha anterior preservada (login do usuário continua válido) |

### Fase 3: Roteiro End-to-End na Interface Web

```
+----+-------------------+-----------------------------------+------------------------------------------+---------------------------------------------+
| Et | Tela              | Ação do Usuário                   | Dados Submetidos                         | Comportamento Esperado                      |
+----+-------------------+-----------------------------------+------------------------------------------+---------------------------------------------+
| 01 | Qualquer tela     | Abrir URL sem estar logado        | —                                        | 302 → /login?erro=nao_autorizado            |
| 02 | Qualquer tela     | Acessar /pecuaristas.html direto  | —                                        | 404 (arquivo removido)                      |
| 03 | Pecuarista        | Cadastrar com dados válidos       | CPF 11 dígitos, nascimento DD/MM/AAAA    | 302 /pecuarista?sucesso=cadastrado + toast  |
| 04 | Pecuarista        | Cadastrar com CPF já existente    | CPF repetido                             | Redireciona para /erro.jsp (cpf_duplicado)  |
| 05 | Pecuarista        | Clicar em Editar e salvar         | Alterar telefone, senha vazia            | sucesso=atualizado; senha preservada        |
| 06 | Pecuarista        | Excluir com confirmação           | —                                        | sucesso=excluido; linha some da tabela      |
| 07 | Caminhão          | Cadastrar com placa inválida      | 'XXXX'                                   | Toast de erro placa_invalida                |
| 08 | Caminhão/Motorista| Escolher empresa no select        | Empresa real do banco                    | Vínculo gravado em id_empresa               |
| 09 | Empresa           | Cadastrar CNPJ inválido           | 13 dígitos                               | Toast de erro cnpj_invalido                 |
| 10 | Administrador     | Cadastrar sem empresa             | "Sem vínculo"                            | Registro criado com id_empresa NULL         |
| 11 | Sidebar           | Clicar em Sair                    | —                                        | /logout → sessão invalidada → /login        |
+----+-------------------+-----------------------------------+------------------------------------------+---------------------------------------------+
```

---

## 9. Protocolo de Validação Humana em 3 Passos

Conforme [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md):

- [ ] **Sintaxe:** tags JSTL balanceadas, caminhos com `${pageContext.request.contextPath}`, `mvn clean package` sem erros.
- [ ] **Conteúdo:** todos os `name` dos formulários coincidem com os parâmetros lidos pelos Servlets (seção 6); saída sempre escapada com `<c:out>` (prevenção de XSS); senha jamais renderizada no HTML.
- [ ] **Organização:** nenhuma tela de gestão na raiz do `webapp`; mocks removidos; histórico de commits no padrão do time.

---

## 10. Conclusão

Esta especificação transforma o protótipo visual do PR #37 em telas **funcionais, seguras e orientadas a dados**: o banco PostgreSQL passa a ser a única fonte de informação, as rotas passam pelo `AuthFilter`, a validação por REGEX e o tratamento de duplicidade já homologados são reaproveitados sem reescrita, e o JavaScript fica restrito ao que lhe cabe — a experiência de interface.
