# Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário

> **Projeto Interdisciplinar — 1º Ano / 2º Semestre**  
> **Instituto J&F — Germinatech**  
> **Tecnologias Base:** Java, Jakarta Servlets, JSP/JSTL, JDBC Puro, PostgreSQL, Dotenv, JUnit 5  

---

## Sobre o Projeto

O **Efficientia** é um sistema corporativo web desenvolvido para otimizar, auditar e monitorar a cadeia logística do transporte de gado bovino, garantindo conformidade sanitária, integridade documental (GTA e Notas Fiscais) e os mais rigorosos padrões de **bem-estar animal**.

O sistema permite a gestão integrada de:
- **Produtores Rurais / Pecuaristas:** Origem dos lotes de animais.
- **Empresas Transportadoras:** Gestão de frotas e parceiros logísticos.
- **Motoristas Condutores:** Controle de habilitação, assinaturas digitais e alocação de viagens.
- **Caminhões e Frotas:** Capacidades máximas e controle de cavalos mecânicos e carretas.
- **Viagens e Trajetos Pecuários:** Monitoramento do ciclo de vida das viagens, contagem segregada de rebanho (machos, fêmeas e marrucos), horários de embarque/desembarque, odômetros e destinação em currais.
- **Controle de Acesso Administrativo:** Autenticação segura com proteção de rotas e filtros de sessão.

---

## Arquitetura do Software

A aplicação foi desenvolvida seguindo o padrão arquitetural **Model-View-Controller (MVC)** clássico com a camada **Data Access Object (DAO)**:

```
[ Browser / Cliente ]
         │ HTTP Request
         ▼
[ AuthFilter ] ── (Interceptação de Segurança e Controle de Sessão)
         │
         ▼
[ Servlets / Controllers ] ── (Padrão Post-Redirect-Get & ValidadorRegex)
      │               │
      │ Forward       │ Persistência / Consulta
      ▼               ▼
[ Visões JSP ]    [ DAOs (JDBC) ] ── (PreparedStatement / SQL)
                      │
                      ▼
             [ Banco PostgreSQL ]
```

* **Model:** Representação das entidades de domínio (`com.efficientia.efficientia.model`).
* **View:** Páginas dinâmicas com JavaServer Pages (JSP) e JSTL Core (`src/main/webapp/WEB-INF/views/`).
* **Controller:** Jakarta Servlets tratando requisições HTTP (`com.efficientia.efficientia.controller`).
* **DAO:** Acesso a dados relacional via JDBC puro (`com.efficientia.efficientia.dao.impl`).
* **Infraestrutura:** Conexões seguras desacopladas via Dotenv e fábrica de conexões (`ConnectionFactory`).

---

## Como Configurar e Executar o Projeto

### Pré-requisitos
* **JDK:** Java 17 ou superior (Recomendado Java 21 ou 25 LTS).
* **Banco de Dados:** PostgreSQL 14 ou superior em execução.
* **Servidor Web:** Apache Tomcat 10.1+ (compatível com especificações Jakarta EE).
* **Maven:** Maven Wrapper (`./mvnw` / `mvnw.cmd`) já embutido no projeto.

### Passo 1: Configurar o Banco de Dados
1. No seu servidor PostgreSQL, crie um banco de dados (ex: `efficientia_db`).
2. Execute o script de criação de tabelas, constraints e carga inicial de dados:
   ```bash
   psql -U postgres -d efficientia_db -f Efficientia.sql
   ```

### Passo 2: Configurar as Variáveis de Ambiente (`.env`)
1. Crie uma cópia do arquivo de exemplo `.env.example` renomeando-a para `.env` na raiz do projeto:
   ```bash
   cp .env.example .env
   ```
2. Abra o arquivo `.env` e configure com as suas credenciais locais do PostgreSQL:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/efficientia_db
   db.user=postgres
   db.password=sua_senha_aqui
   ```

### Passo 3: Compilar e Executar Testes Automatizados
Utilize o Maven Wrapper para validar a compilação e rodar a suíte de testes:
```powershell
# No Windows PowerShell:
.\mvnw.cmd clean test
```

### Passo 4: Executar no Servidor Tomcat
1. Abra o projeto no **IntelliJ IDEA**.
2. Adicione uma configuração de execução do tipo **Tomcat Server -> Local**.
3. Na aba *Deployment*, adicione o artefato `Efficientia:war exploded`.
4. Inicie o servidor. A aplicação estará acessível em:
   ```
   http://localhost:8080/Efficientia/
   ```

---

## Central de Documentação Técnica (`/docs`)

Toda a documentação técnica, normas arquiteturais, registros de IA e guias passo a passo estão centralizados na pasta [docs/](docs/README.md):

| Documento | Disciplina | Descrição |
| :--- | :---: | :--- |
| **[docs/geral/TECNOLOGIAS_UTILIZADAS.md](docs/geral/TECNOLOGIAS_UTILIZADAS.md)** | Geral | Lista exaustiva das tecnologias utilizadas, contexto, justificativa e localização no código. |
| **[docs/geral/ANALISE_E_CHECKLIST_ENTREGAS.md](docs/geral/ANALISE_E_CHECKLIST_ENTREGAS.md)** | Geral | Análise completa dos requisitos das disciplinas (POO, BD1, Lógica, SO) e o que falta na aplicação Java. |
| **[docs/geral/GUIA_PADRONIZACAO.md](docs/geral/GUIA_PADRONIZACAO.md)** | Geral | Guia normativo de boas práticas, arquitetura em camadas e estilo de código Java. |
| **[docs/poo/passo-a-passo-dao.md](docs/poo/passo-a-passo-dao.md)** | POO | Tutorial didático de implementação de Data Access Objects. |
| **[docs/poo/passo-a-passo-servlet.md](docs/poo/passo-a-passo-servlet.md)** | POO | Tutorial didático de implementação de Jakarta Servlets. |
| **[docs/poo/DOCUMENTACAO_BUSCA_POR_NOME.md](docs/poo/DOCUMENTACAO_BUSCA_POR_NOME.md)** | POO | Mecanismo de busca flexível, particionada e insensível a acentuação. |
| **[docs/poo/SPEC_INTEGRACAO_FRONTEND_BACKEND.md](docs/poo/SPEC_INTEGRACAO_FRONTEND_BACKEND.md)** | POO | Especificação de integração das telas e JSPs à camada de controle. |
| **[docs/bd1/NAMING_CONVENTION_DATABASE.md](docs/bd1/NAMING_CONVENTION_DATABASE.md)** | BD1 | Convenção oficial de nomenclatura de tabelas, colunas, constraints e ENUMs no PostgreSQL. |
| **[docs/bd1/DOCUMENTACAO_HASH_CPF_LGPD.md](docs/bd1/DOCUMENTACAO_HASH_CPF_LGPD.md)** | BD1 | Pseudonimização de CPF com SHA-256 e salting para conformidade com a LGPD. |
| **[docs/bd1/DOCUMENTACAO_IMPLEMENTACAO_ENUMS_DAOS.md](docs/bd1/DOCUMENTACAO_IMPLEMENTACAO_ENUMS_DAOS.md)** | BD1 | Integração de ENUMs nativos do PostgreSQL com JDBC puro. |
| **[docs/logica/DOCUMENTACAO_TRATAMENTO_EXCECOES_DUPLICIDADE.md](docs/logica/DOCUMENTACAO_TRATAMENTO_EXCECOES_DUPLICIDADE.md)** | Lógica | Arquitetura de captura de duplicidade (SQLState 23505) e hierarquia de exceções customizadas. |
| **[docs/logica/SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md](docs/logica/SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md)** | Lógica | Especificação técnica do mapeamento e fluxo de erros de duplicidade. |
| **[docs/so/DOCUMENTACAO_VALIDACAO_REGEX.md](docs/so/DOCUMENTACAO_VALIDACAO_REGEX.md)** | SO | Especificação de expressões regulares para CPF, celular nacional sem +55, placas, etc. |
| **[docs/ia/METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](docs/ia/METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md)** | IA | Metodologia de geração com IA e protocolo de validação humana (sintaxe, conteúdo e organização). |
| **[docs/ia/LIMITACOES_USO_IA.md](docs/ia/LIMITACOES_USO_IA.md)** | IA | Análise crítica das limitações, riscos e salvaguardas cognitivas no uso de IA. |
| **[docs/ia/EXEMPLO_IA_BD.md](docs/ia/EXEMPLO_IA_BD.md)** | IA / BD1 | Evidência de uso crítico e estratégico de IA em Banco de Dados. |
| **[docs/ia/EXEMPLO_IA_SOP.md](docs/ia/EXEMPLO_IA_SOP.md)** | IA / SO | Evidência de auditoria e validação humana de IA em Sistemas Operacionais. |

---

## Suíte de Testes Automatizados
O projeto conta com suíte de testes em JUnit 5 cobrindo:
* Testes de conexão resiliente com o banco PostgreSQL (`ConnectionFactoryTest`).
* Testes do algoritmo de hashing SHA-256 com salting (`CpfHasherTest`).
* Testes do algoritmo de hashing de senhas (`PasswordHasherTest`).
* Testes de tradução de violação de constraints para exceções (`TratadorExcecaoBancoTest`).
* Testes de autenticação e proteção de credenciais (`AuthServletTest`).