# Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário

> **Projeto Interdisciplinar — 1º Ano / 2º Semestre**  
> **Instituto J&F — Germinatech**  
> **Tecnologias Base:** Java 25, Jakarta EE 11 (Servlets 6.0, JSP 3.1, JSTL 3.0), JDBC Puro, PostgreSQL, Dotenv, HikariCP, Docker, JUnit 5  

[![Status](https://img.shields.io/badge/Status-Online%20no%20Render-success?style=for-the-badge&logo=render)](https://efficientia.onrender.com/)
[![Java](https://img.shields.io/badge/Java-25%20LTS-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-11.0-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14%2B-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

---

## 🌐 Aplicação Web em Produção

O sistema **Efficientia** está publicado e operando em nuvem com integração contínua (CI/CD):

👉 **URL de Acesso:** [https://efficientia.onrender.com/](https://efficientia.onrender.com/)

### Credenciais de Demonstração / Teste Imediato:
| Papel | E-mail de Acesso | Senha Padrão |
| :--- | :--- | :--- |
| **Administrador (Principal)** | `admin.ricardo@empresa.com` | `adminpass1` |
| **Administrador (Secundário)** | `admin.lucas@empresa.com` | `adminpass2` |

> [!NOTE]
> **Sobre o plano gratuito de hospedagem (Render Cloud):**  
> Por utilizar o plano gratuito do Render, o container entra em modo de hibernação (*spin down*) após alguns minutos de inatividade. Caso o site esteja hibernando, a **primeira requisição pode levar cerca de 30 a 50 segundos** para inicializar o container Docker com Tomcat 11. Uma vez acordado, o sistema responde com alta performance através do pool de conexões pré-aquecidas HikariCP integrado ao banco **Aiven PostgreSQL**.

---

## 📖 Sobre o Projeto

O **Efficientia** é um sistema corporativo web desenvolvido para otimizar, auditar e monitorar a cadeia logística do transporte de gado bovino, garantindo conformidade sanitária, integridade documental (Guias de Trânsito Animal - GTA e Notas Fiscais) e os mais rigorosos padrões de **bem-estar animal**.

O sistema gerencia de forma integrada:
* **Produtores Rurais / Pecuaristas:** Gestão de propriedades rurais, origens dos lotes e rastreabilidade sanitária.
* **Empresas Transportadoras:** Gestão de frotas e parceiros logísticos do agronegócio.
* **Motoristas Condutores:** Controle de habilitação, conformidade de identificação e alocação de viagens.
* **Caminhões e Frotas:** Controle de capacidades máximas, compatibilidade entre cavalos mecânicos e carretas (placas antigas e Mercosul).
* **Viagens e Trajetos Pecuários:** Monitoramento completo do ciclo de vida das viagens, contagem segregada de rebanho (machos, fêmeas e marrucos), horários de embarque/desembarque, registro de odômetros e destinação em currais.
* **Controle de Acesso Administrativo:** Autenticação segura com proteção de rotas via filtros de sessão (`AuthFilter`), proteção contra repetição de formulários (PRG - *Post-Redirect-Get*) e hashing criptográfico de senhas e dados sensíveis (LGPD).

---

## 🏗️ Arquitetura do Software

A aplicação foi estruturada seguindo o padrão arquitetural **Model-View-Controller (MVC)** com a camada **Data Access Object (DAO)** em JDBC puro e pool HikariCP:

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                             CAMADA CLIENTE (BROWSER)                            │
│                 Interface Web (HTML5, CSS3, JavaScript, Formulários)             │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │ HTTP Request (GET / POST)
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                     CAMADA DE SEGURANÇA E FILTROS (JAKARTA EE)                   │
│         [AuthFilter] Interceptação de Sessão, Cache-Control, Proteção de Rotas   │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │ Requisição Autorizada
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                   CAMADA DE CONTROLE - CONTROLLERS (JAKARTA SERVLETS)            │
│    [PecuaristaServlet] [MotoristaServlet] [CaminhaoServlet] [EmpresaServlet]     │
│    [AdminServlet]      [TrajetoServlet]   [LoginServlet]     [PerfilServlet]     │
│    -> Padrão Post-Redirect-Get (PRG)                                             │
│    -> Validação defensiva com [ValidadorRegex]                                   │
│    -> Tratamento de duplicidade com [TratadorExcecaoBanco]                       │
└───────────────────┬──────────────────────────────────────────▲───────────────────┘
                    │                                          │ Despacho (Forward)
                    ▼                                          │ Dados renderizados
┌───────────────────────────────────────┐  ┌───────────────────┴───────────────────┐
│     CAMADA DE DOMÍNIO (MODELS)        │  │     CAMADA DE VISÃO (JSP & JSTL)      │
│  [PecuaristaModel]   [MotoristaModel] │  │  JavaServer Pages + JSTL Tags Core    │
│  [CaminhaoModel]     [EmpresaModel]   │  │  <c:forEach>, <c:if>, <c:out>         │
│  [TrajetoModel]      [AdminModel]     │  │  Páginas protegidas em /WEB-INF/views/│
└───────────────────┬───────────────────┘  └───────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                CAMADA DE ACESSO A DADOS (DAOs) & UTILITÁRIOS                     │
│  [*DAO] Métodos CRUD, Queries Parametrizadas, Hashing e Tratamento de Exceções   │
│  [TratadorExcecaoBanco] Mapeamento de SQLState 23505 -> [DadoDuplicadoException]  │
│  [CpfHasher] Hashing SHA-256 com Salting (LGPD) / [PasswordHasher]               │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │ Conexões JDBC seguras
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                   INFRAESTRUTURA & CONFIGURAÇÃO (DOTENV & JDBC)                  │
│  [ConnectionFactory] Pool HikariCP (6.2.0) + [dotenv-java] (.env / ENV vars)     │
│  Driver JDBC: [org.postgresql.Driver 42.7.5]                                     │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │ Protocolo TCP / Porta 5432
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                         BANCO DE DADOS (POSTGRESQL 14+)                          │
│               Esquema relacional, ENUMs nativos, Triggers e Constraints          │
└──────────────────────────────────────────────────────────────────────────────────┘
```

* **Model (`com.efficientia.efficientia.model`):** Classes POJO puras representando as entidades de negócio.
* **View (`src/main/webapp/WEB-INF/views/`):** Páginas dinâmicas renderizadas no servidor com JavaServer Pages (JSP 3.1) e JSTL Core 3.0, protegidas contra acesso direto via URL.
* **Controller (`com.efficientia.efficientia.controller`):** Servlets Jakarta (`@WebServlet`) tratando requisições HTTP, validações defensivas com expressões regulares e redirecionamentos seguros (Post-Redirect-Get).
* **DAO (`com.efficientia.efficientia.dao.impl`):** Acesso a dados relacional via JDBC puro com `PreparedStatement` e mapeamento de ENUMs nativos do PostgreSQL (`Types.OTHER`).
* **Infraestrutura e Segurança:** Fábrica de conexões resiliente (`ConnectionFactory`) com pool de alta performance **HikariCP**, suporte a arquivos de ambiente (`.env`), variáveis de sistema e pseudonimização de dados pessoais (SHA-256 com salting para CPF).

---

## 🚀 Como Configurar e Rodar o Sistema Localmente

Existem **três maneiras** de executar o sistema em ambiente local:
1. **Via Docker** *(Recomendada — rápida e não exige configuração de Tomcat ou JDK local)*;
2. **Via IntelliJ IDEA / Eclipse** *(Ideal para desenvolvimento e depuração)*;
3. **Via Tomcat Standalone** *(Execução manual via terminal e Maven)*.

### 📋 Pré-requisitos Gerais
* **Git** instalado.
* **PostgreSQL 14 ou superior** em execução (localmente ou via serviço em nuvem como Aiven/Neon/Supabase).
* **JDK 17 ou superior** (Recomendado **JDK 21 ou 25 LTS** para compilação local).
* **Apache Tomcat 10.1+ ou 11.0+** (compatível com especificações Jakarta EE 11).
* *Ou apenas* **Docker** instalado e rodando.

---

### Passo 1: Clonar o Repositório

```bash
git clone https://github.com/Efficientia/Efficientia.git
cd Efficientia
```

---

### Passo 2: Configurar o Banco de Dados PostgreSQL

1. Conecte-se ao seu servidor PostgreSQL e crie uma nova base de dados:
   ```sql
   CREATE DATABASE efficientia_db;
   ```
2. Execute o script de criação de tabelas, tipos ENUM, triggers e carga inicial de dados ([`Efficientia.sql`](Efficientia.sql)):
   ```bash
   # Via terminal (Linux / macOS / Git Bash / PowerShell):
   psql -U postgres -d efficientia_db -f Efficientia.sql
   ```
   *(Alternativamente, abra o arquivo `Efficientia.sql` no **DBeaver**, **pgAdmin** ou **IntelliJ Database Tool** e execute o script completo na base criada).*

> [!TIP]
> **Subindo o PostgreSQL rapidamente via Docker:**  
> Caso não tenha o PostgreSQL instalado na máquina:
> ```bash
> docker run --name pg-efficientia -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=efficientia_db -p 5432:5432 -d postgres:16
> docker exec -i pg-efficientia psql -U postgres -d efficientia_db < Efficientia.sql
> ```

---

### Passo 3: Configurar as Variáveis de Ambiente (`.env`)

1. Crie uma cópia do arquivo de exemplo [`.env.example`](.env.example) renomeando-a para `.env` na raiz do projeto:
   ```bash
   # Windows PowerShell:
   Copy-Item .env.example .env

   # Linux / macOS / Bash:
   cp .env.example .env
   ```
2. Abra o arquivo `.env` e configure com as credenciais do seu banco PostgreSQL:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/efficientia_db
   db.user=postgres
   db.password=sua_senha_aqui
   ```
   *(Nota: O sistema também reconhece automaticamente a nomenclatura em caixa-alta `DB_URL`, `DB_USER` e `DB_PASSWORD`, tanto no `.env` quanto em variáveis de ambiente do sistema operacional).*

---

### 🐳 Opção A: Execução via Docker (Recomendada)

Com o Docker instalado e o banco rodando (seja local ou nuvem), execute a aplicação sem precisar instalar Java ou Tomcat na sua máquina:

1. **Construa a imagem Docker multi-stage:**
   ```bash
   docker build -t efficientia .
   ```
2. **Execute o container:**
   ```bash
   docker run -d -p 8080:8080 --env-file .env --name efficientia-app efficientia
   ```
3. **Acesse a aplicação no seu navegador:**
   ```
   http://localhost:8080/
   ```

---

### 💻 Opção B: Execução no IntelliJ IDEA (Ambiente de Desenvolvimento)

1. Abra a pasta do projeto no **IntelliJ IDEA**. A IDE reconhecerá automaticamente o projeto Maven (`pom.xml`).
2. Certifique-se de configurar a versão do JDK do projeto em **File -> Project Structure -> Project -> SDK** (selecione Java 21 ou 25).
3. Vá em **Run -> Edit Configurations...** e clique no botão **`+` (Add New Configuration)**.
4. Selecione **Tomcat Server -> Local**:
   * Configure o caminho da sua instalação local do **Apache Tomcat 10.1+** ou **11.0+** em *Application server*.
   * Na aba **Deployment**, clique em `+` e selecione **Artifact -> `Efficientia:war exploded`**.
   * No campo **Application context**, defina `/` (para responder na raiz) ou `/Efficientia`.
5. Clique em **Apply** e em **Run / Debug**.
6. A aplicação será iniciada e estará acessível em:
   ```
   http://localhost:8080/ (ou http://localhost:8080/Efficientia/)
   ```

---

### ⚙️ Opção C: Compilação Manual e Execução via Tomcat Standalone

1. **Compile e empacote o projeto utilizando o Maven Wrapper embutido:**
   ```powershell
   # No Windows:
   .\mvnw.cmd clean package -DskipTests

   # No Linux / macOS:
   ./mvnw clean package -DskipTests
   ```
2. O arquivo empacotado será gerado em `target/Efficientia-1.0-SNAPSHOT.war`.
3. Renomeie o arquivo gerado para `ROOT.war` (para responder na raiz `/`) e copie-o para o diretório `webapps/` do seu Apache Tomcat 10.1+/11.0:
   ```bash
   cp target/Efficientia-1.0-SNAPSHOT.war /caminho/para/tomcat/webapps/ROOT.war
   ```
4. Inicie o servidor Tomcat:
   ```bash
   # Windows:
   /caminho/para/tomcat/bin/startup.bat

   # Linux / macOS:
   /caminho/para/tomcat/bin/startup.sh
   ```
5. Abra o navegador em `http://localhost:8080/`.

---

## 🧪 Suíte de Testes Automatizados

O projeto possui uma robusta suíte de testes unitários e de integração desenvolvida com **JUnit 5**, cobrindo regras de negócio, persistência, segurança e validações:

```powershell
# Execução da suíte completa via Maven Wrapper:
.\mvnw.cmd clean test    # Windows
./mvnw clean test        # Linux / macOS
```

### Principais Componentes Testados:
* **`ConnectionFactoryTest`:** Valida a resiliência do pool de conexões HikariCP, leitura em camadas do `.env` e variáveis de ambiente.
* **`CpfHasherTest`:** Valida o algoritmo de hashing irreversível SHA-256 com salting para conformidade com a LGPD.
* **`PasswordHasherTest`:** Valida o hashing seguro de senhas, verificação e compatibilidade de migração.
* **`ValidadorRegexTest`:** Testa exaustivamente as expressões regulares de CPF, celular nacional sem +55, placas Mercosul/antigas, CNPJ, e-mail e CEP.
* **`TratadorExcecaoBancoTest` & `DaosAuxiliaresExcecaoTest`:** Valida o mapeamento do código de erro PostgreSQL `23505` (`unique_violation`) para a exceção de domínio `DadoDuplicadoException`.
* **`AuthServletTest` & `PerfilServletTest`:** Valida fluxos de login, encerramento de sessão, controle de acesso e atualização de perfis de usuário.

---

## 🗺️ Rotas e Navegação da Aplicação

| Rota / Endpoint | Descrição / Papel | Proteção de Sessão |
| :--- | :--- | :---: |
| `/` | **Landing Page Institucional:** Apresentação da solução, dores do transporte pecuário e proposta de valor. | Pública |
| `/sobre-nos.html` | **Página Sobre Nós:** Informações institucionais, propósitos e equipe de desenvolvimento. | Pública |
| `/login` | **Tela e Endpoint de Login:** Autenticação de administradores com credenciais criptografadas. | Pública |
| `/logout` | **Encerramento de Sessão:** Invalidação da sessão ativa do usuário e redirecionamento seguro. | Autenticada |
| `/home` | **Dashboard Geral:** Painel principal administrativo com métricas e atalhos operacionais. | 🔒 Autenticada |
| `/pecuarista` | **Módulo de Produtores Rurais:** Cadastro, listagem, busca fonética/insensível e edição de pecuaristas. | 🔒 Autenticada |
| `/motorista` | **Módulo de Motoristas:** Gestão de condutores habilitados, controle de identificação e status. | 🔒 Autenticada |
| `/caminhao` | **Módulo de Veículos:** Controle de carretas, cavalos mecânicos e capacidade volumétrica/peso de gado. | 🔒 Autenticada |
| `/trajeto` | **Módulo de Trajetos e Viagens:** Ciclo de vida das viagens, contagem segregada de animais e odômetros. | 🔒 Autenticada |
| `/empresa` | **Módulo de Transportadoras:** Gestão das empresas logísticas parceiras e dados cadastrais. | 🔒 Autenticada |
| `/admin` | **Módulo de Administradores:** Gerenciamento dos usuários com privilégios de gestão no sistema. | 🔒 Autenticada |
| `/perfil` | **Perfil do Usuário Logado:** Visualização e atualização cadastral do administrador em sessão. | 🔒 Autenticada |

---

## 📚 Central de Documentação Técnica (`/docs`)

Toda a documentação técnica por disciplina, normas arquiteturais, registros de auditoria de IA e guias detalhados estão centralizados na pasta [docs/](docs/README.md):

| Documento | Disciplina | Descrição |
| :--- | :---: | :--- |
| **[docs/ds/GUIA_DEPLOY_E_HOSPEDAGEM_CLOUD.md](docs/ds/GUIA_DEPLOY_E_HOSPEDAGEM_CLOUD.md)** | DS | Guia de containerização Docker (Tomcat 11), configuração do `.env` e deploy gratuito na nuvem (Render + Aiven PostgreSQL). |
| **[docs/geral/TECNOLOGIAS_UTILIZADAS.md](docs/geral/TECNOLOGIAS_UTILIZADAS.md)** | Geral | Lista exaustiva das tecnologias utilizadas, contexto, justificativa e localização no código-fonte. |
| **[docs/geral/ANALISE_E_CHECKLIST_ENTREGAS.md](docs/geral/ANALISE_E_CHECKLIST_ENTREGAS.md)** | Geral | Análise completa dos requisitos do edital interdisciplinar (POO, BD1, Lógica, SO) e matriz de conformidade. |
| **[docs/geral/GUIA_PADRONIZACAO.md](docs/geral/GUIA_PADRONIZACAO.md)** | Geral | Guia normativo de boas práticas, arquitetura em camadas e estilo de código Java. |
| **[docs/poo/passo-a-passo-dao.md](docs/poo/passo-a-passo-dao.md)** | POO | Tutorial didático de implementação de Data Access Objects com JDBC puro. |
| **[docs/poo/passo-a-passo-servlet.md](docs/poo/passo-a-passo-servlet.md)** | POO | Tutorial didático de implementação de Jakarta Servlets e padrão Post-Redirect-Get. |
| **[docs/poo/DOCUMENTACAO_BUSCA_POR_NOME.md](docs/poo/DOCUMENTACAO_BUSCA_POR_NOME.md)** | POO | Mecanismo de busca flexível, particionada e insensível a acentuação. |
| **[docs/poo/SPEC_INTEGRACAO_FRONTEND_BACKEND.md](docs/poo/SPEC_INTEGRACAO_FRONTEND_BACKEND.md)** | POO | Especificação de integração das telas e JSPs à camada de controle. |
| **[docs/bd1/NAMING_CONVENTION_DATABASE.md](docs/bd1/NAMING_CONVENTION_DATABASE.md)** | BD1 | Convenção oficial de nomenclatura de tabelas, colunas, constraints e ENUMs no PostgreSQL. |
| **[docs/bd1/DOCUMENTACAO_HASH_CPF_LGPD.md](docs/bd1/DOCUMENTACAO_HASH_CPF_LGPD.md)** | BD1 | Pseudonimização de CPF com SHA-256 e salting para conformidade com a LGPD. |
| **[docs/bd1/DOCUMENTACAO_IMPLEMENTACAO_ENUMS_DAOS.md](docs/bd1/DOCUMENTACAO_IMPLEMENTACAO_ENUMS_DAOS.md)** | BD1 | Integração de ENUMs nativos do PostgreSQL com JDBC puro (`Types.OTHER`). |
| **[docs/logica/DOCUMENTACAO_TRATAMENTO_EXCECOES_DUPLICIDADE.md](docs/logica/DOCUMENTACAO_TRATAMENTO_EXCECOES_DUPLICIDADE.md)** | Lógica | Arquitetura de captura de duplicidade (SQLState `23505`) e hierarquia de exceções customizadas. |
| **[docs/logica/SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md](docs/logica/SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md)** | Lógica | Especificação técnica do mapeamento e fluxo de erros de duplicidade. |
| **[docs/so/DOCUMENTACAO_VALIDACAO_REGEX.md](docs/so/DOCUMENTACAO_VALIDACAO_REGEX.md)** | SO | Especificação analítica de expressões regulares para CPF, celular nacional sem +55, placas, etc. |
| **[docs/ia/METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](docs/ia/METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md)** | IA | Metodologia de geração com IA e protocolo de validação humana (sintaxe, conteúdo e organização). |
| **[docs/ia/LIMITACOES_USO_IA.md](docs/ia/LIMITACOES_USO_IA.md)** | IA | Análise crítica das limitações, riscos e salvaguardas cognitivas no uso de IA. |
| **[docs/ia/EXEMPLO_IA_BD.md](docs/ia/EXEMPLO_IA_BD.md)** | IA / BD1 | Evidência de uso crítico e estratégico de IA em modelagem de Banco de Dados. |
| **[docs/ia/EXEMPLO_IA_SOP.md](docs/ia/EXEMPLO_IA_SOP.md)** | IA / SO | Evidência de auditoria e validação humana de IA em Sistemas Operacionais. |

---

## 👥 Equipe do Projeto Interdisciplinar

Desenvolvido pelos alunos do **1º Ano / 2º Semestre** do curso de Tecnologia — **Instituto J&F / Germinatech**:
* **Equipe Efficientia**
* Conheça a equipe completa e o propósito do projeto em: [`/sobre-nos.html`](src/main/webapp/sobre-nos.html) ou online em [https://efficientia.onrender.com/sobre-nos.html](https://efficientia.onrender.com/sobre-nos.html).

---

## 📄 Licença

Este projeto é distribuído sob a licença **MIT**. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.
