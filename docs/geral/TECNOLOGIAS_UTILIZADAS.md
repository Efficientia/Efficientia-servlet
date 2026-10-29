# Tecnologias Utilizadas — Projeto Efficientia

> **Sistema:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Nível Acadêmico:** 1º Ano — 2º Semestre (Projeto Interdisciplinar)  
> **Arquitetura Base:** Model-View-Controller (MVC) com Data Access Object (DAO)  
> **Última Atualização:** 24/10/2026  

---

## 1. Visão Geral do Ecossistema Tecnológico

O projeto **Efficientia** foi concebido e estruturado com tecnologias corporativas consolidadas do ecossistema Java e bancos de dados relacionais.

Abaixo é apresentada a arquitetura técnica em camadas e onde cada tecnologia atua:

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
│    [AdminServlet]      [TrajetoServlet]   [LoginServlet]                         │
│    -> Padrão Post-Redirect-Get (PRG)                                             │
│    -> Validação defensiva com [ValidadorRegex]                                   │
│    -> Captura de exceções e redirecionamento com [WebErroUtil]                   │
└───────────────────┬──────────────────────────────────────────▲───────────────────┘
                    │                                          │ Despacho (Forward)
                    ▼                                          │ Dados renderizados
┌───────────────────────────────────────┐  ┌───────────────────┴───────────────────┐
│     CAMADA DE DOMÍNIO (MODELS)        │  │     CAMADA DE VISÃO (JSP & JSTL)      │
│  [PecuaristaModel]   [MotoristaModel] │  │  JavaServer Pages + JSTL Tags Core    │
│  [CaminhaoModel]     [EmpresaModel]   │  │  <c:forEach>, <c:if>, <c:out>         │
│  [TrajetoModel]      [AdminModel]     │  │  Renderização sem scriptlets          │
└───────────────────┬───────────────────┘  └───────────────────────────────────────┘
                    │
                    ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                CAMADA DE ACESSO A DADOS (DAOs) & UTILITÁRIOS                     │
│  [*DAO] Métodos CRUD, Queries Parametrizadas, Hashing e Tratamento de Exceções   │
│  [TratadorExcecaoBanco] Mapeamento de SQLState 23505 -> [DadoDuplicadoException]  │
│  [CpfHasher] Hashing SHA-256 com Pepper / [PasswordHasher] Hashing BCrypt        │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │ Conexões JDBC seguras
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                   INFRAESTRUTURA & CONFIGURAÇÃO (DOTENV & JDBC)                  │
│  [ConnectionFactory] Pool HikariCP (6.2.0) + [dotenv-java] (.env)                │
│  Driver JDBC: [org.postgresql.Driver 42.7.5]                                     │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │ Protocolo TCP / Porta 5432
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                     BANCO DE DADOS RELACIONAL (POSTGRESQL 14+)                   │
│  Constraints CHECK, DEFAULT, FOREIGN KEY, Tipos Customizados ENUM, Integridade  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Detalhamento das Tecnologias Utilizadas

### 2.1. Linguagem de Programação: Java (JDK 21 / 25 LTS)

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Java SE (Standard Edition)** |
| **Versão** | Compilação em conformidade com Java 21 / 25 LTS (compatível com Java 17+) |
| **Contexto no Projeto** | Linguagem central de desenvolvimento de todo o ecossistema backend, abrangendo regras de negócio, algoritmos de validação, mapeamento de entidades, controladores web e testes automatizados. |
| **Motivo da Utilização** | 1. Tipagem estática forte que previne inconsistências de dados em tempo de compilação.<br>2. Paradigma de Orientação a Objetos completo (encapsulamento, herança, polimorfismo, interfaces) exigido na disciplina de POO.<br>3. Recursos modernos da plataforma (`java.time.LocalDate` / `LocalDateTime` em substituição aos legados `java.util.Date`, inferência local com `var`, estruturas imutáveis e regex nativo otimizado).<br>4. Portabilidade multiplataforma (*Write Once, Run Anywhere*). |
| **Onde é Utilizada** | Em todos os arquivos sob o diretório `src/main/java/com/efficientia/efficientia/` e `src/test/java/com/efficientia/efficientia/`. |

---

### 2.2. Camada Web: Jakarta Servlet API (Jakarta EE 11 / Servlet 6.0)

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Jakarta Servlet API** (`jakarta.platform:jakarta.jakartaee-web-api:11.0.0`) |
| **Contexto no Projeto** | Atua como a camada **Controller** na arquitetura MVC da aplicação, recebendo requisições HTTP do navegador, coordenando o fluxo de dados e determinando o próximo passo (renderização ou redirecionamento). |
| **Motivo da Utilização** | 1. **Exigência Direta do Edital de POO:** *"Criar um Servlet para receber e responder as requisições HTTP"*; separação de responsabilidades em pacotes `servlet`, `dao` e `model`.<br>2. Mapeamento declarativo moderno via anotação `@WebServlet` sem dependência de XML (`web.xml`).<br>3. Implementação estrita do padrão de mercado **Post-Redirect-Get (PRG)** em todas as requisições `doPost`, eliminando reenvio acidental de formulários por atualização da página (*F5*).<br>4. Suporte nativo a sessões HTTP (`HttpSession`) e decodificação explícita UTF-8 (`req.setCharacterEncoding("UTF-8")`). |
| **Onde é Utilizada** | No pacote `com.efficientia.efficientia.controller`:<br>- `PecuaristaServlet.java` (`/pecuarista`)<br>- `MotoristaServlet.java` (`/motorista`)<br>- `CaminhaoServlet.java` (`/caminhao`)<br>- `EmpresaServlet.java` (`/empresa`)<br>- `AdminServlet.java` (`/admin`)<br>- `TrajetoServlet.java` (`/trajeto`)<br>- `LoginServlet.java` (`/login` e `/logout`) |

---

### 2.3. Camada de Apresentação Dinâmica: JSP e JSTL (Jakarta Standard Tag Library)

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **JavaServer Pages (JSP)** e **JSTL 3.0** (`jakarta.servlet.jsp.jstl-api:3.0.2` e `org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1`) |
| **Contexto no Projeto** | Atua como a camada **View** no MVC, renderizando dinamicamente as telas com base nos dados preparados pelos servlets e injetados via atributos de requisição (`req.setAttribute()`). |
| **Motivo da Utilização** | 1. Eliminação total de código Java intrusivo em scriptlets (`<% ... %>`), preservando a legibilidade e manutenibilidade das páginas.<br>2. Uso de tags lógicas e de iteração da biblioteca padrão (`<c:forEach>`, `<c:if>`, `<c:choose>`, `<c:out>`).<br>3. Proteção automática contra vulnerabilidades de *Cross-Site Scripting* (XSS) através de escape de caracteres pelo `<c:out>`.<br>4. Localização segura dentro de `/WEB-INF/views/`, impedindo acesso direto de usuários via URL sem passar obrigatoriamente pela validação e filtros do Servlet. |
| **Onde é Utilizada** | - `src/main/webapp/WEB-INF/views/pecuarista.jsp`<br>- `src/main/webapp/WEB-INF/views/editar.jsp`<br>- `src/main/webapp/WEB-INF/views/editar-trajeto.jsp`<br>- `src/main/webapp/login.jsp`<br>- `src/main/webapp/erro.jsp` |

---

### 2.4. Segurança e Controle de Acesso: Jakarta Filter API (`@WebFilter`)

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Jakarta Filter API** (`jakarta.servlet.Filter`) |
| **Contexto no Projeto** | Interceptador global de segurança e autorização de requisições web. |
| **Motivo da Utilização** | 1. Proteção de rotas administrativas contra acesso anônimo não autorizado.<br>2. Verificação prévia da existência de sessão ativa com `session.getAttribute("adminLogado") != null`.<br>3. Configuração de cabeçalhos de segurança HTTP (`resp.setHeader("Cache-Control", "no-store")`) para impedir que páginas restritas fiquem salvas no cache do navegador após o logout.<br>4. Redirecionamento amigável para `/login?erro=nao_autorizado` quando o usuário tenta burlar a autenticação. |
| **Onde é Utilizada** | Na classe `com.efficientia.efficientia.filter.AuthFilter`, protegendo as rotas `/admin`, `/empresa`, `/motorista`, `/caminhao`, `/pecuarista` e `/trajeto`. |

---

### 2.5. Acesso a Dados e Persistência: JDBC Puro (`java.sql.*`)

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Java Database Connectivity (JDBC)** |
| **Contexto no Projeto** | Camada de persistência relacional responsável pela execução de comandos SQL no banco de dados. |
| **Motivo da Utilização** | 1. **Exigência Explícita do Edital de POO:** *"Acessar um Banco de Dados relacional Postgres usando JDBC"*, sem o uso de frameworks ORM de alto nível (como JPA/Hibernate, que são escopo do 2º Ano).<br>2. Uso obrigatório de `PreparedStatement` em todas as operações com parâmetros externos, garantindo imunidade a ataques de **SQL Injection**.<br>3. Uso de `try-with-resources` para fechamento garantido e determinístico de cursores (`ResultSet`), statements e conexões, prevenindo vazamentos de recursos de rede.<br>4. Obtenção de chaves primárias autoincrementadas via `Statement.RETURN_GENERATED_KEYS` / `getGeneratedKeys()`. |
| **Onde é Utilizada** | Em todos os DAOs do pacote `com.efficientia.efficientia.dao.impl`:<br>- `PecuaristaDAO.java`, `MotoristaDAO.java`, `CaminhaoDAO.java`, `EmpresaDAO.java`<br>- `AdminDAO.java`, `TrajetoDAO.java`, `PropriedadeDAO.java`, `EnderecoDAO.java`<br>- `InfoEmbarqueDAO.java`, `ParadaImprevistaDAO.java`, `AnalistaDAO.java` |

---

### 2.6. Banco de Dados: PostgreSQL 14+ & Driver JDBC Oficial

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **PostgreSQL** e Driver JDBC Oficial (`org.postgresql:postgresql:42.7.5`) |
| **Contexto no Projeto** | Sistema Gerenciador de Banco de Dados Relacional (SGBD) da aplicação. |
| **Motivo da Utilização** | 1. **Exigência Curricular de Banco de Dados 1:** Criação de script SQL a partir do modelo conceitual/lógico com independência de banco por série.<br>2. Cumprimento dos critérios **Extra de Banco de Dados 1:** Uso intensivo de `DEFAULT` (ex.: contadores de carga `qtd_macho DEFAULT 0`) e restrições `CHECK` (ex.: validação de período `chk_periodo_trajeto`, tamanho exato de CPF `length(cpf) = 11`, capacidade positiva de caminhão `capacidade_maxima > 0`).<br>3. Tipos enumerados customizados (`CREATE TYPE status_trajeto AS ENUM ('EM_ANDAMENTO', 'CONCLUIDA')`).<br>4. Suporte a integridade referencial com chaves estrangeiras (`DEFERRABLE INITIALLY IMMEDIATE`).<br>5. O driver PostgreSQL expõe códigos de estado de erro padronizados (**SQLState `23505`** para `unique_violation`), permitindo o tratamento inteligente de duplicidades no Java. |
| **Onde é Utilizada** | - Script DDL/DML: `Efficientia.sql`<br>- Inicialização do driver: `com.efficientia.efficientia.factory.ConnectionFactory.java`<br>- Mapeamento de tipos especiais: `PreparedStatement.setObject(index, enumValue.name(), Types.OTHER)` nos DAOs. |

---

### 2.7. Segurança e Gestão de Variáveis de Ambiente: Dotenv Java

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Dotenv Java** (`io.github.cdimascio:dotenv-java:3.0.0`) |
| **Contexto no Projeto** | Carregamento desacoplado de credenciais de infraestrutura e banco de dados a partir de arquivo `.env`. |
| **Motivo da Utilização** | 1. **Exigência Explícita de Sistemas Operacionais:** *"Estruturar variáveis do ambiente (.env) para o sistema (segurança de código): ENVs para banco de dados e API externas"*; inclusão no `.gitignore`.<br>2. Impede o vazamento de senhas e credenciais em repositórios públicos do GitHub.<br>3. Algoritmo resiliente de localização do arquivo `.env` implementado na `ConnectionFactory`, buscando tanto no diretório de trabalho raiz quanto subindo a árvore de diretórios do WAR compilado em servidores Tomcat.<br>4. Fornecimento de arquivo modelo `/.env.example` para replicação segura por outros membros da equipe. |
| **Onde é Utilizada** | - `com.efficientia.efficientia.factory.ConnectionFactory.java`<br>- Arquivo de configuração: `.env` (ignorado no git)<br>- Arquivo de template: `.env.example`<br>- Declaração de dependência: `pom.xml` |

---

### 2.8. Validação e Higienização de Dados: Expressões Regulares (REGEX)

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Expressões Regulares Java** (`java.util.regex.Pattern` e `Matcher`) |
| **Contexto no Projeto** | Camada utilitária transversal de validação defensiva sintática e higienização de dados de entrada do usuário. |
| **Motivo da Utilização** | 1. **Exigência Extra de Sistemas Operacionais:** *"Usar REGEX para validar entrada de CPF e/ou Celular (pelo menos)"*.<br>2. **Exigência de Lógica de Programação:** *"Os códigos deverão apresentar adequada validação de dados de entrada"*.<br>3. Otimização de performance através de compilação estática (`Pattern.compile()`), reaproveitando o bytecode dos padrões em milhares de requisições simultâneas sem realocação.<br>4. Cobertura ampla que vai além do mínimo exigido: validação de CPF (com/sem máscara), Celular/Telefone com DDD (sem +55 nacional), Placas veiculares (Mercosul e Tradicional), E-mail institucional (RFC 5322), CNPJ (14 dígitos), GTA e Notas Fiscais.<br>5. Funções de normalização e sanitização (`apenasDigitos()`, `normalizarPlaca()`). |
| **Onde é Utilizada** | - Classe central: `com.efficientia.efficientia.util.ValidadorRegex.java`<br>- Controladores: `PecuaristaServlet`, `MotoristaServlet`, `CaminhaoServlet`, `EmpresaServlet`, `AdminServlet`, `TrajetoServlet`, `LoginServlet`<br>- DAOs: `MotoristaDAO`, `PecuaristaDAO`, `EmpresaDAO`, `EnderecoDAO` |

---

### 2.9. Criptografia, Segurança e LGPD: Hashing SHA-256 e BCrypt

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Java Cryptography Architecture** (`java.security.MessageDigest`) e Utilitários de Hash |
| **Contexto no Projeto** | Anonimização e pseudonimização de dados pessoais sensíveis e proteção de senhas de acesso. |
| **Motivo da Utilização** | 1. **Conformidade com a LGPD (Lei Geral de Proteção de Dados - Lei nº 13.709/2018):** Proteção do CPF de produtores e motoristas ao gerar assinaturas de conferência digital sem expor o dado bruto.<br>2. O `CpfHasher` aplica SHA-256 com salting/pepper fixo para produzir um hash determinístico de 64 caracteres hexadecimais, inviabilizando ataques por *Rainbow Tables*.<br>3. O `PasswordHasher` assegura que nenhuma senha administrativa seja persistida em texto plano no banco de dados. |
| **Onde é Utilizada** | - `com.efficientia.efficientia.util.CpfHasher.java`<br>- `com.efficientia.efficientia.util.PasswordHasher.java`<br>- Mapeamento nas entidades: `PecuaristaModel`, `MotoristaModel`, `AdminModel` |

---

### 2.10. Tratamento Inteligente de Exceções de Banco: Hierarquia de Exceções Unchecked

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Hierarquia Customizada de Exceções** baseada em `RuntimeException` |
| **Contexto no Projeto** | Mapeamento e tradução de falhas de integridade do SGBD PostgreSQL para exceções semânticas de negócio. |
| **Motivo da Utilização** | 1. **Exigência de Lógica de Programação:** *"Deve haver tratamento de exceções, sempre que couber"*; *"código bem organizado"*; *"mensagens amigáveis ao usuário"*, evitando que *stacktraces* cruas poluam a tela do navegador.<br>2. Captura cirúrgica do código de erro **SQLState `23505`** (`unique_violation`).<br>3. Identificação algorítmica da constraint violada (ex.: `pecuarista_cpf_key`, `empresa_cnpj_key`, `caminhao_placa_cavalo_key`, etc.).<br>4. Disparo de exceções especializadas com mensagens contextualizadas em português (`CpfDuplicadoException`, `CnpjDuplicadoException`, `PlacaDuplicadaException`, `EmailDuplicadoException`, etc.).<br>5. O `WebErroUtil` serializa a mensagem amigável e redireciona de volta ao formulário ou página de erro amigável (`/erro.jsp`). |
| **Onde é Utilizada** | - Pacote `com.efficientia.efficientia.exception`: `DadoDuplicadoException`, `CpfDuplicadoException`, `CnpjDuplicadoException`, `EmailDuplicadoException`, `PlacaDuplicadaException`, `CodigoDuplicadoException`, `DocumentoFiscalDuplicadoException`<br>- Utilitários: `com.efficientia.efficientia.util.TratadorExcecaoBanco.java`, `com.efficientia.efficientia.util.WebErroUtil.java`<br>- DAOs: `AdminDAO`, `CaminhaoDAO`, `EmpresaDAO`, `MotoristaDAO`, `PecuaristaDAO`, `TrajetoDAO`, `AnalistaDAO`<br>- Servlets: Interceptação nos blocos `catch` de `doPost()` |

---

### 2.11. Testes Automatizados e Qualidade: JUnit 5 Jupiter

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **JUnit 5 Jupiter** (`org.junit.jupiter:junit-jupiter-api` e `junit-jupiter-engine:5.13.2`) |
| **Contexto no Projeto** | Bateria de testes de unidade e testes de integração executados durante o ciclo de build. |
| **Motivo da Utilização** | 1. Garantia de não-regressão do software durante refatorações e manutenções evolutivas.<br>2. Verificação de integridade da fábrica de conexões (`ConnectionFactoryTest`).<br>3. Validação dos algoritmos de hashing e salting (`CpfHasherTest`, `PasswordHasherTest`).<br>4. Validação completa da matriz de mapeamento de constraints de duplicidade (`TratadorExcecaoBancoTest`).<br>5. Teste da integridade e regras de negócio de autenticação (`AuthServletTest`).<br>6. Teste exaustivo de expressões regulares (`ValidadorRegexTest`). |
| **Onde é Utilizada** | No diretório `src/test/java/com/efficientia/efficientia/`: <br>- `factory/ConnectionFactoryTest.java`<br>- `util/CpfHasherTest.java`<br>- `util/PasswordHasherTest.java`<br>- `util/TratadorExcecaoBancoTest.java`<br>- `util/ValidadorRegexTest.java`<br>- `controller/AuthServletTest.java` |

---

### 2.12. Gerenciamento de Dependências e Build: Apache Maven & Maven Wrapper

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **Apache Maven** (`pom.xml`) e **Maven Wrapper** (`mvnw`, `mvnw.cmd`) |
| **Contexto no Projeto** | Ferramenta declarativa de build, resolução de dependências e empacotamento em arquivo `.war`. |
| **Motivo da Utilização** | 1. Padronização absoluta do ciclo de compilação, testes e empacotamento em qualquer máquina da equipe sem necessidade de pré-instalação manual do Maven global.<br>2. Gerenciamento centralizado de bibliotecas e plugins (`maven-compiler-plugin`, `maven-war-plugin`, `maven-surefire-plugin`).<br>3. Facilidade para futura integração em pipelines de integração contínua (CI/CD via GitHub Actions). |
| **Onde é Utilizada** | - `pom.xml`<br>- `mvnw` (Linux/macOS)<br>- `mvnw.cmd` (Windows)<br>- `.mvn/wrapper/maven-wrapper.properties` |

---

### 2.13. Pool de Conexões de Alta Performance: HikariCP

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologia** | **HikariCP** (`com.zaxxer:HikariCP:6.2.0`) e ponte de log `org.slf4j:slf4j-jdk14:1.7.36` |
| **Contexto no Projeto** | Gerenciador de pool de conexões JDBC com o banco remoto na nuvem (Aiven PostgreSQL). |
| **Motivo da Utilização** | 1. Elimina a sobrecarga crítica de múltiplos segundos de negociação TCP, TLS/SSL e autenticação SCRAM a cada requisição HTTP.<br>2. Mantém conexões pré-aquecidas prontas para uso imediato em memória (`minimumIdle = 2`, `maximumPoolSize = 10`), reduzindo o tempo de obtenção de conexão para menos de 5 ms.<br>3. Totalmente transparente para a camada DAO, que continua utilizando o padrão idiomático `try (Connection c = ConnectionFactory.getConnection())`.<br>4. Mecanismo de heartbeat preventivo (`keepaliveTime = 60000`) para evitar quedas por inatividade impostas por firewalls ou NATs da nuvem. |
| **Onde é Utilizada** | - `com.efficientia.efficientia.factory.ConnectionFactory.java`<br>- `src/test/java/com/efficientia/efficientia/factory/ConnectionFactoryTest.java`<br>- Declaração em `pom.xml` |

---

### 2.14. Inteligência Artificial: Ferramentas de Assistência, Automação e Documentação

O desenvolvimento do sistema **Efficientia** incorporou ferramentas de Inteligência Artificial Generativa sob uma metodologia de *Pair Programming* e engenharia assistida por agentes, com validação humana em três etapas (sintaxe, conteúdo e organização).

| Aspecto | Descrição Detalhada |
| :--- | :--- |
| **Tecnologias Utilizadas** | **Antigravity AI Agent** (Google DeepMind / Gemini 2.5 e 3.8), **Google Gemini** (Gemini Pro/Advanced), **dbdiagram.io AI** (Prompt-to-DBML) e **GitHub Copilot** |
| **Contexto no Projeto** | Atuou transversalmente ao longo do ciclo de vida da aplicação: ideação de arquitetura, alinhamento de decisões por entrevistas interativas, refatoração de código Java e SQL, conformidade com a LGPD e geração de documentação viva. |
| **Motivo da Utilização** | 1. **Antigravity AI Agent:** Autonomia para varredura da AST e arquivos do projeto, formulação de perguntas interativas de alinhamento antes de alterações estruturais sensíveis (CPF, telefone, CEP) e geração de documentação técnica com rastreabilidade cruzada entre disciplinas.<br>2. **Google Gemini:** Consulta teórica, validação de compatibilidade com Jakarta EE 11 e definição da estratégia de segurança defensiva (hashing com salt/pepper e anonimização/pseudonimização LGPD).<br>3. **dbdiagram.io AI:** Geração de código DBML e validação estrutural de relacionamentos, integridade referencial e tipos do DER.<br>4. **GitHub Copilot:** Aceleração na escrita de código repetitivo (*boilerplate* JDBC, getters/setters e templates de testes JUnit 5). |
| **Onde é Utilizada** | - Documentações técnicas em `docs/` (`EXEMPLO_IA_BD.md`, `METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md`, `NAMING_CONVENTION_DATABASE.md`, etc.).<br>- Refatoração de DAOs, Servlets, Models e Utilitários (`CpfHasher.java`, `TratadorExcecaoBanco.java`, `ValidadorRegex.java`).<br>- Refatoração do script de banco `Efficientia.sql` e modelo DBML do DER.<br>- Configuração de testes automatizados e suíte de testes unitários. |

---

## 3. Matriz de Rastreabilidade: Disciplinas × Tecnologias

A tabela a seguir demonstra formalmente a aderência de cada tecnologia às solicitações das disciplinas do Projeto Interdisciplinar:

| Disciplina | Requisito do Edital | Tecnologia Aplicada | Onde está no Código |
| :--- | :--- | :--- | :--- |
| **Programação Orientada a Objetos** | CRUD administrativo de pelo menos 3 tabelas (Mínimo 7,0) | Java, Jakarta Servlet API, JDBC, Models | `com.efficientia.efficientia.controller.*`, `com.efficientia.efficientia.dao.impl.*` |
| **Programação Orientada a Objetos** | Separação em pacotes `servlet`, `dao` e `model` | Arquitetura MVC com pacotes padronizados | `controller/`, `dao/`, `model/`, `factory/`, `util/`, `exception/` |
| **Programação Orientada a Objetos** | CRUD em mais 3 tabelas (Extra 10,0 - Total de 6 tabelas) | Java, Servlets, DAOs para 6 entidades | `pecuarista`, `caminhao`, `motorista`, `adm`, `empresa`, `trajeto` |
| **Programação Orientada a Objetos** | Acessar BD Postgres usando JDBC | JDBC Puro (`PreparedStatement`), PostgreSQL Driver | `com.efficientia.efficientia.dao.impl.*`, `ConnectionFactory.java` |
| **Programação Orientada a Objetos** | Pool de conexões e performance de persistência | HikariCP (`HikariDataSource`), alta performance JDBC | `ConnectionFactory.java`, `ConnectionFactoryTest.java` |
| **Programação Orientada a Objetos** | Diversificar métodos para alterar e excluir | Métodos específicos de atualização e exclusão | DAOs e Servlets (`atualizar*`, `excluir*`) |
| **Programação Orientada a Objetos** | Filtros diversos correlacionando tabelas | DAOs com INNER JOINs, filtros dinâmicos e busca | `TrajetoDAO.buscar`, `TrajetoServlet.doGet`, busca insensível a acentos |
| **Banco de Dados 1** | Modelo Lógico e Script SQL de criação | PostgreSQL DDL | `Efficientia.sql` |
| **Banco de Dados 1** | Uso de DEFAULT e CHECKS (Extra 10,0) | PostgreSQL Constraints (`CHECK`, `DEFAULT`, `ENUM`) | `Efficientia.sql` |
| **Banco de Dados 1** | Script DataLoad com dados iniciais (Extra 10,0) | PostgreSQL DML (`INSERT INTO`) | `Efficientia.sql` |
| **Banco de Dados 1** | Documento Naming Convention (Extra 10,0) | Markdown normativo de banco | `docs/NAMING_CONVENTION_DATABASE.md` |
| **Sistemas Operacionais** | Variáveis de ambiente (`.env`) para o banco | `dotenv-java`, `.env`, `.env.example` | `ConnectionFactory.java`, `.env.example` |
| **Sistemas Operacionais** | Validação por REGEX de CPF e Celular (Extra 10,0) | `java.util.regex.Pattern` / `Matcher` | `com.efficientia.efficientia.util.ValidadorRegex.java` |
| **Lógica de Programação** | Boas práticas de programação e nomenclatura | Java Code Conventions, Nomenclatura descritiva | Todo o código Java sob `com.efficientia.efficientia` |
| **Lógica de Programação** | Tratamento de exceções, sempre que couber | `TratadorExcecaoBanco`, `DadoDuplicadoException` | `exception/`, `TratadorExcecaoBanco.java`, `WebErroUtil.java` |
| **Lógica de Programação** | Validação adequada de dados de entrada | `ValidadorRegex`, validações defensivas nos Servlets | `ValidadorRegex.java`, Servlets |
