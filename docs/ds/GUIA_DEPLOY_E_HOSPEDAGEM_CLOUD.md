# Guia de Deploy, Containerização e Hospedagem em Nuvem — Efficientia

> **Disciplina:** Desenvolvimento de Sistemas (DS)  
> **Sistema:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Nível Acadêmico:** 1º Ano — 2º Semestre (Projeto Interdisciplinar)  
> **Tecnologias de Infraestrutura:** Docker (Multi-Stage Build), Apache Tomcat 11, JDK 25, Render PaaS, Aiven PostgreSQL, Dotenv & HikariCP  

---

## 1. Análise Arquitetural: Por que não utilizar o GitHub Pages?

Durante o planejamento de publicação da aplicação web **Efficientia**, avaliou-se inicialmente o **GitHub Pages**. Contudo, a análise técnica da arquitetura do projeto demonstrou que **o GitHub Pages é incompatível com o sistema completo**, pelos seguintes motivos:

| Critério Técnico | GitHub Pages (Hospedagem Estática) | Requisitos do Efficientia (Java Web Full-Stack) |
| :--- | :--- | :--- |
| **Processamento no Servidor** | Inexistente. Serve apenas arquivos estáticos (`HTML`, `CSS`, `JS` e imagens) processados no navegador do cliente. | Exige uma **JVM (Java 25)** e um **Servlet Container Jakarta EE 11 (Apache Tomcat 11)** para executar os Controllers (`@WebServlet`) e filtros de sessão (`AuthFilter`). |
| **Renderização de Visões** | Não compila nem interpreta páginas `.jsp` ou tags JSTL (`<c:forEach>`, `<c:if>`). | Utiliza **JSP 3.1 + JSTL 3.0** localizados dentro do diretório protegido `/WEB-INF/views/`, renderizados no servidor antes do envio do HTML ao cliente. |
| **Conexão com Banco de Dados** | Impossível abrir conexões TCP/JDBC diretas com o PostgreSQL a partir do navegador (além de expor credenciais publicamente). | Utiliza pool de conexões **HikariCP + Driver JDBC PostgreSQL** no backend (`ConnectionFactory`) comunicando-se via TLS/TCP com o banco remoto na nuvem (**Aiven**). |
| **Segurança de Credenciais (`.env`)** | Não possui suporte a variáveis de ambiente de runtime no lado do servidor. | Carrega credenciais sensíveis (`db.url`, `db.user`, `db.password`) de forma isolada no servidor via `dotenv-java` e variáveis de ambiente do sistema operacional. |

---

## 2. Comparativo das Melhores Opções Gratuitas para Java Web + PostgreSQL

Para hospedar gratuitamente uma aplicação empacotada como `.war` (Jakarta EE 11) integrada a um banco relacional PostgreSQL na nuvem, foram avaliadas as principais plataformas PaaS (*Platform as a Service*) e DBaaS (*Database as a Service*) do mercado:

| Plataforma | Camada Atendida | Plano Gratuito (Free Tier) | Veredito no Projeto |
| :--- | :---: | :--- | :--- |
| **Render.com** | Aplicação Web (Container Docker + Tomcat 11) | Gratuito permanente (750h/mês), HTTPS automático, integração contínua via GitHub e suporte nativo a `Dockerfile` e *Secret Files* (`.env`). *(Hiberna após 15 min de inatividade)*. | **Escolhido para o Servidor Web (Tomcat 11)** |
| **Aiven Cloud** | Banco de Dados Relacional (PostgreSQL) | Plano gratuito permanente de PostgreSQL gerenciado na nuvem com conexões seguras SSL/TLS. *(O PostgreSQL gratuito do Render expira em 30 dias, tornando o Aiven superior)*. | **Escolhido para o Banco PostgreSQL** |
| **Neon.tech / Supabase** | Banco de Dados Relacional (PostgreSQL) | Planos gratuitos permanentes de PostgreSQL Serverless na nuvem. | **Alternativas compatíveis de Banco** |

---

## 3. Arquitetura de Deploy Contínuo (CI/CD) na Nuvem

O fluxo de publicação em produção integra o repositório **GitHub**, o **Render Web Service** (executando o container Docker com Tomcat 11) e o cluster **Aiven PostgreSQL**:

```
┌─────────────────────────┐       git push        ┌──────────────────────────────────────────┐
│   Repositório GitHub    │ ────────────────────► │         Render Cloud (Web Service)       │
│                         │                       │                                          │
│ - Código Java / JSP     │                       │  ┌────────────────────────────────────┐  │
│ - pom.xml (WAR)         │                       │  │ Etapa 1: Build (Maven + JDK 25)    │  │
│ - Dockerfile            │                       │  │ Compila e gera target/*.war        │  │
│ - render.yaml           │                       │  └─────────────────┬──────────────────┘  │
└─────────────────────────┘                       │                    ▼                     │
                                                  │  ┌────────────────────────────────────┐  │
┌─────────────────────────┐     HTTPS (443)       │  │ Etapa 2: Runtime (Tomcat 11 + J25) │  │
│  Navegador do Usuário   │ ◄───────────────────► │  │ Publica em webapps/ROOT.war        │  │
│  (Desktop / Mobile)     │                       │  │ Lê .env (/etc/secrets ou ENV vars) │  │
└─────────────────────────┘                       │  └─────────────────┬──────────────────┘  │
                                                  └────────────────────┼─────────────────────┘
                                                                       │
                                                                       │ JDBC Pool (HikariCP)
                                                                       │ TCP + TLS/SSL
                                                                       ▼
                                                  ┌──────────────────────────────────────────┐
                                                  │      Aiven Cloud (PostgreSQL 14+)        │
                                                  │      Banco: dbEfficientia                │
                                                  └──────────────────────────────────────────┘
```

---

## 4. Artefatos de Infraestrutura Implementados no Repositório

### 4.1. `Dockerfile` (Multi-Stage Build)
Localizado na raiz do projeto ([`Dockerfile`](../../Dockerfile)), utiliza a estratégia **Multi-Stage Build** em duas etapas para garantir que a imagem final de produção seja enxuta, rápida e segura:

1. **Etapa 1 (`build` — `eclipse-temurin:25-jdk`):**
   * Copia primeiro a estrutura do Maven Wrapper (`.mvn/`, `mvnw` e `pom.xml`), converte quebras de linha Windows (`CRLF` $\rightarrow$ `LF`) com `sed -i 's/\r$//' mvnw` para evitar falhas em ambientes Linux e baixa as dependências em cache (`dependency:go-offline`).
   * Copia a pasta `src/` e executa `./mvnw clean package -DskipTests -B`, gerando o pacote `.war`.
2. **Etapa 2 (`runtime` — `tomcat:11.0-jdk25-temurin`):**
   * Utiliza o **Apache Tomcat 11** oficial (compatível com a especificação **Jakarta EE 11 / Servlet 6.0** declarada no `pom.xml`).
   * Remove as aplicações padrão do Tomcat e copia o pacote gerado para `webapps/ROOT.war`, fazendo com que a aplicação responda diretamente na raiz do domínio (`https://seu-app.onrender.com/`) sem exigir `/Efficientia` na URL.
   * Ajusta dinamicamente a porta HTTP do `conf/server.xml` para respeitar a variável de ambiente `$PORT` injetada pelo provedor de nuvem.

### 4.2. `.dockerignore`
Localizado na raiz ([`.dockerignore`](../../.dockerignore)), impede que pastas pesadas ou sensíveis do ambiente local (`target/`, `.git/`, `.idea/`, `.vscode/` e o arquivo `.env` local) sejam enviadas para o contexto de build da imagem Docker.

### 4.3. `render.yaml` (Infraestrutura como Código — Blueprint)
Localizado na raiz ([`render.yaml`](../../render.yaml)), automatiza a declaração do serviço web no Render (`runtime: docker`, `plan: free`) e mapeia as chaves de variáveis de ambiente esperadas (`PORT`, `DB_URL`, `DB_USER`, `DB_PASSWORD`).

---

## 5. Gestão do `.env` e Conexão com o Banco na Nuvem (`ConnectionFactory`)

Por motivos de segurança da informação e conformidade com a disciplina de Sistemas Operacionais, o arquivo `.env` real **jamais é versionado no Git** (encontra-se listado no `.gitignore`).

Para permitir que a aplicação funcione de maneira transparente tanto no ambiente de desenvolvimento local (IntelliJ IDEA + Tomcat local) quanto em containers Docker na nuvem, a classe [`ConnectionFactory.java`](../../src/main/java/com/efficientia/efficientia/factory/ConnectionFactory.java) implementa uma cadeia de resolução inteligente em 4 níveis:

1. **Arquivo `.env` no diretório de execução (`./.env`):** Usado em execuções locais diretas.
2. **Secret File do Render (`/etc/secrets/.env`):** Suporte nativo ao recurso *Secret Files* do Render, que monta arquivos confidenciais em `/etc/secrets/.env` dentro do container sem expô-los no repositório.
3. **Varredura ascendente na árvore de diretórios:** Cobre execuções via WAR explodido no IntelliJ IDEA / Tomcat local.
4. **Variáveis de Ambiente do Sistema Operacional (`System.getenv` / `Dotenv`):** Através do método utilitário `getConfig(dotKey, envKey)`, a fábrica aceita tanto a nomenclatura com ponto do arquivo `.env` (`db.url`, `db.user`, `db.password`) quanto o padrão POSIX de variáveis de ambiente de nuvem (`DB_URL`, `DB_USER`, `DB_PASSWORD`).

```java
String url = getConfig("db.url", "DB_URL");
String user = getConfig("db.user", "DB_USER");
String password = getConfig("db.password", "DB_PASSWORD");
```

---

## 6. Passo a Passo para Publicar o Site no Render

Como o banco de dados do projeto já está hospedado na nuvem (**Aiven PostgreSQL**) e todos os arquivos de infraestrutura já estão prontos no repositório, siga apenas os passos abaixo:

### Passo 1: Enviar a branch para o GitHub
No terminal da raiz do projeto, envie a branch para o repositório remoto (e faça o merge na `main` quando desejar publicar a partir dela):
```bash
git push -u origin feat/DS/EFFICIENTI22/configura-deploy-docker-render
```

### Passo 2: Criar o Web Service no Render
1. Acesse [https://render.com](https://render.com) e faça login com sua conta do **GitHub**.
2. No painel principal (Dashboard), clique em **New +** e selecione **Web Service**.
3. Conecte e selecione o repositório **Efficientia**.
4. Preencha as configurações básicas do serviço:
   * **Name:** `efficientia`
   * **Branch:** selecione a branch desejada (ex.: `main` ou `feat/DS/EFFICIENTI22/configura-deploy-docker-render`)
   * **Runtime / Language:** `Docker` (o Render detectará o `Dockerfile` automaticamente)
   * **Instance Type:** `Free` ($0/mês)

### Passo 3: Configurar as Credenciais do Banco (`.env` na Nuvem)
Role a página de criação do serviço até a seção **Environment Variables** (ou **Secret Files**) e escolha **uma** das duas opções abaixo copiando os valores do seu arquivo `.env` local (que aponta para o banco Aiven):

#### Opção Recomendada A — Via Environment Variables:
Adicione as 3 variáveis abaixo clicando em **Add Environment Variable**:
| Key | Value (copie do seu `.env` local) |
| :--- | :--- |
| `DB_URL` | `jdbc:postgresql://pg-2eb87185-institutojef-9d35.h.aivencloud.com:17645/dbEfficientia` |
| `DB_USER` | *(o valor de `db.user` do seu `.env`)* |
| `DB_PASSWORD` | *(o valor de `db.password` do seu `.env`)* |

#### Opção Alternativa B — Via Secret File (`.env` idêntico ao local):
Em **Secret Files**, clique em **Add Secret File**:
* **Filename:** `.env`
* **Contents:** cole exatamente as 3 linhas do seu arquivo `.env` local (`db.url=...`, `db.user=...`, `db.password=...`).

### Passo 4: Finalizar e Acessar o Site
1. Clique no botão **Create Web Service**.
2. Acompanhe o log de build no painel do Render (a primeira compilação do Maven + Docker leva cerca de 3 a 5 minutos).
3. Assim que o status mudar para **Live**, seu site estará disponível publicamente na URL gerada no topo do painel (ex.: `https://efficientia.onrender.com`), abrindo a Landing Page na raiz (`/`) e conectando o Login (`/login`) e os módulos administrativos diretamente ao banco Aiven PostgreSQL!
