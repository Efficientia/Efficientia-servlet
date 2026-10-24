# Especificação Técnica (Spec): Tratamento de Exceções de Duplicidade no Banco de Dados

> **Projeto:** Efficientia — Gestão de Transporte e Bem-Estar Animal  
> **Módulo:** Camada de Persistência, Exceções de Domínio e Controle Web  
> **Autor / Equipe:** Projeto Interdisciplinar — 2º Semestre  
> **Status:** Homologado e Integrado  
> **Versão:** 1.0  

---

## 1. Identificação e Visão Geral da Especificação

### 1.1. Propósito
Esta especificação define os requisitos funcionais, arquitetura orientada a objetos, stack tecnológica e o roteiro formal de testes para o subsistema de **captura, tradução e tratamento semântico de erros de duplicidade de dados** (violações de restrições de unicidade — *UNIQUE constraints*) gerados pelo banco de dados relacional **PostgreSQL** no sistema **Efficientia**.

### 1.2. Problema Resolvido
Anteriormente, exceções de baixo nível do JDBC ([SQLException](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/SQLException.html)) eram capturadas genericamente nos blocos `catch` dos DAOs, registrando apenas mensagens no console e retornando `false`. Isso impedia que:
1. Os Servlets soubessem a causa real da falha (ex.: falha de rede vs. duplicidade de CPF ou e-mail).
2. O usuário recebesse um feedback inteligível na interface web sobre qual campo causou o conflito de cadastro.
3. As camadas superiores pudessem tomar decisões defensivas padronizadas sem acoplamento direto com o driver JDBC.

A solução implementada introduz uma hierarquia tipada de exceções de domínio e um componente tradutor de banco de dados, estabelecendo o desacoplamento e a previsibilidade da camada de dados.

---

## 2. Tecnologias e Ferramentas Utilizadas

A tabela a seguir consolida as tecnologias, bibliotecas e versões aplicadas na implementação desta especificação:

| Tecnologia | Versão | Camada / Papel | Descrição e Finalidade no Subsistema |
| :--- | :---: | :--- | :--- |
| **Java (JDK)** | `25 / 17+` | Linguagem Core | Suporte a orientação a objetos, herança, exceções não-checadas (`RuntimeException`) e Text Blocks (`"""`). |
| **PostgreSQL** | `14+` | Banco de Dados Relacional | SGBD corporativo que aplica e valida as restrições `UNIQUE NOT NULL` na camada de armazenamento físico. |
| **PostgreSQL JDBC Driver** | `42.7.5` | Driver de Conexão | Driver nativo registrado no Maven que emite o código de estado SQLState `23505` (`unique_violation`). |
| **Jakarta Servlets API** | `6.0 / 11.0` | Camada de Controle (Web) | Gerencia o ciclo de requisição HTTP, intercepta exceções nos métodos `doPost` e aplica o padrão PRG. |
| **Java Regular Expressions (Regex)**| `java.util.regex` | Utilitário / Parser | Expressões regulares pré-compiladas em constantes estáticas para extrair constraint e dados duplicados. |
| **JUnit 5 (Jupiter)** | `5.13.2` | Testes Automatizados | Framework de testes unitários para validação de classes de exceção e do componente tradutor. |
| **Apache Maven** | `3.9.6` | Build & Dependências | Automação de compilação, execução da suíte de testes e empacotamento do artefato `.war`. |
| **Apache Tomcat** | `10.1+` | Servlet Container | Servidor de aplicação web que hospeda a execução dos Servlets e processa os redirecionamentos HTTP. |

---

## 3. Arquitetura da Solução e Princípios de POO

### 3.1. Hierarquia de Exceções Unchecked
A arquitetura adota exceções não-checadas (*Unchecked Exceptions*, derivadas de `RuntimeException`).

* **Vantagem de Design:** Não obriga a alteração da assinatura dos métodos públicos nos DAOs (`throws SQLException` violaria o contrato arquitetural do [GUIA_PADRONIZACAO.md](Efficientia/GUIA_PADRONIZACAO.md)), mantendo a compatibilidade retroativa e permitindo que as camadas de controle interceptem apenas quando julgarem necessário.

```
                            ┌────────────────────────┐
                            │    java.lang.Throwable │
                            └───────────┬────────────┘
                                        │
                            ┌───────────▼────────────┐
                            │   java.lang.Exception  │
                            └───────────┬────────────┘
                                        │
                            ┌───────────▼────────────┐
                            │ java.lang.RuntimeExcep.│
                            └───────────┬────────────┘
                                        │
                         ┌──────────────▼─────────────┐
                         │   DadoDuplicadoException   │ ◄── [Base de Duplicidade]
                         └──────────────┬─────────────┘
                                        │
        ┌──────────────┬────────────────┼──────────────┬──────────────┬──────────────────────────────┐
        ▼              ▼                ▼              ▼              ▼                              ▼
 ┌─────────────┐┌─────────────┐  ┌─────────────┐┌─────────────┐┌─────────────┐┌──────────────────────────────┐
 │ CpfDuplicado││CnpjDuplicado│  │EmailDuplicad││PlacaDuplicad││CodigoDuplic.││DocumentoFiscalDuplicado      │
 │  Exception  ││  Exception  │  │  Exception  ││  Exception  ││  Exception  ││          Exception           │
 └─────────────┘└─────────────┘  └─────────────┘└─────────────┘└─────────────┘└──────────────────────────────┘
```

### 3.2. Diagrama de Sequência e Fluxo de Execução

```
[Cliente Web / Browser]        [Servlet (Controller)]           [DAO (Persistência)]            [PostgreSQL (SGBD)]
           │                              │                               │                              │
           │ 1. POST /pecuarista (Dados)  │                               │                              │
           ├─────────────────────────────►│                               │                              │
           │                              │ 2. dao.inserir(novoPecuarista)│                              │
           │                              ├──────────────────────────────►│                              │
           │                              │                               │ 3. executeUpdate()           │
           │                              │                               ├─────────────────────────────►│
           │                              │                               │                              │
           │                              │                               │ 4. Lança SQLException        │
           │                              │                               │    (SQLState: 23505)         │
           │                              │                               │◄─────────────────────────────┤
           │                              │                               │                              │
           │                              │                               │ 5. TratadorExcecaoBanco      │
           │                              │                               │    .tratarSeDuplicado(e)     │
           │                              │                               │    (Reconhece constraint)    │
           │                              │ 6. Dispara CpfDuplicadoExcep. │                              │
           │                              │◄──────────────────────────────┤                              │
           │                              │                               │                              │
           │                              │ 7. catch (DadoDuplicadoExcep) │                              │
           │                              │    extrai tipoErro            │                              │
           │ 8. HTTP 302: Redirecionamento│                               │                              │
           │    GET /pecuarista?erro=...  │                               │                              │
           │◄─────────────────────────────┤                               │                              │
           │                              │                               │                              │
```

---

## 4. Especificação dos Componentes Implementados

### 4.1. Classe Base: [DadoDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/DadoDuplicadoException.java)
- **Pacote:** `com.efficientia.efficientia.exception`
- **Atributos Encapsulados:**
  - `entidade` (`String`): Nome da entidade de negócio afetada (ex.: `"Pecuarista"`, `"Empresa"`).
  - `campo` (`String`): Nome semântico do atributo em duplicidade (ex.: `"CPF"`, `"CNPJ"`, `"E-mail"`).
  - `valorDuplicado` (`String`): Valor tentado que foi rejeitado pelo banco.
  - `constraintName` (`String`): Nome técnico da constraint no PostgreSQL (ex.: `"pecuarista_cpf_key"`).
  - `tipoErro` (`String`): Código padronizado para feedback em URLs e Views (ex.: `"cpf_duplicado"`).

### 4.2. Exceções Especializadas

| Classe | Construtor Principal | Campo Associado | Código Web (`tipoErro`) |
| :--- | :--- | :--- | :--- |
| [CpfDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/CpfDuplicadoException.java) | `(entidade, cpf, constraint, cause)` | CPF | `cpf_duplicado` |
| [CnpjDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/CnpjDuplicadoException.java) | `(entidade, cnpj, constraint, cause)` | CNPJ | `cnpj_duplicado` |
| [EmailDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/EmailDuplicadoException.java) | `(entidade, email, constraint, cause)` | E-mail | `email_duplicado` |
| [PlacaDuplicadaException](Efficientia/src/main/java/com/efficientia/efficientia/exception/PlacaDuplicadaException.java) | `(entidade, tipoPlaca, placa, constraint, cause)` | Placa Carreta / Cavalo | `placa_duplicada` |
| [CodigoDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/CodigoDuplicadoException.java) | `(entidade, codigo, constraint, cause)` | Código Corporativo | `codigo_duplicado` |
| [DocumentoFiscalDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/DocumentoFiscalDuplicadoException.java) | `(tipoDoc, numeroDoc, constraint, cause)` | Guia GTA / NF-e | `documento_fiscal_duplicado` |

### 4.3. Utilitário Tradutor: [TratadorExcecaoBanco](Efficientia/src/main/java/com/efficientia/efficientia/util/TratadorExcecaoBanco.java)
- **Pacote:** `com.efficientia.efficientia.util`
- **Constantes de Estado:**
  - `SQL_STATE_UNIQUE_VIOLATION = "23505"` (PostgreSQL / ANSI SQL).
  - `MYSQL_ERROR_DUPLICATE_ENTRY = 1062` (MySQL Fallback).
- **Padrões Regex Pré-compilados:**
  - `CONSTRAINT_PATTERN = Pattern.compile("(?:unique constraint|restrição de unicidade)[\"\\s]+([a-zA-Z0-9_]+)[\"\\s]?", Pattern.CASE_INSENSITIVE)`
  - `KEY_DETAIL_PATTERN = Pattern.compile("Key \\(([^)]+)\\)=\\(([^)]*)\\)", Pattern.CASE_INSENSITIVE)`
- **Métodos Públicos Estáticos:**
  - `boolean isDuplicidade(SQLException e)`: Avalia o SQLState e texto da mensagem.
  - `void tratarSeDuplicado(SQLException e, String entidadePadrao)`: Dispara a exceção tipada se for duplicidade; se for erro alheio (ex: timeout), não dispara nada.
  - `DadoDuplicadoException traduzir(SQLException e, String entidadePadrao)`: Inspeciona constraint e detalhes para instanciar a subclasse especializada.

---

## 5. Matriz de Mapeamento: Banco de Dados x Exceção x Servlet

Esta tabela consolida o mapeamento entre a modelagem relacional de [Efficientia.sql](Efficientia/Efficientia.sql) e os controladores HTTP:

| Tabela | Coluna UNIQUE | Constraint Padrão PostgreSQL | Exceção Lançada | Servlet Tratador | Parâmetro de Redirecionamento |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **`pecuarista`** | `cpf` | `pecuarista_cpf_key` | `CpfDuplicadoException` | [PecuaristaServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/PecuaristaServlet.java) | `?erro=cpf_duplicado` |
| **`pecuarista`** | `email` | `pecuarista_email_key` | `EmailDuplicadoException` | [PecuaristaServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/PecuaristaServlet.java) | `?erro=email_duplicado` |
| **`empresa`** | `cnpj` | `empresa_cnpj_key` | `CnpjDuplicadoException` | [EmpresaServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/EmpresaServlet.java) | `?erro=cnpj_duplicado` |
| **`empresa`** | `nome` | `empresa_nome_key` | `DadoDuplicadoException` | [EmpresaServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/EmpresaServlet.java) | `?erro=nome_duplicado` |
| **`empresa`** | `codigo` | `empresa_codigo_key` | `CodigoDuplicadoException` | [EmpresaServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/EmpresaServlet.java) | `?erro=codigo_duplicado` |
| **`motorista`** | `email` | `motorista_email_key` | `EmailDuplicadoException` | [MotoristaServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/MotoristaServlet.java) | `?erro=email_duplicado` |
| **`caminhao`** | `placa_carreta` | `caminhao_placa_carreta_key`| `PlacaDuplicadaException` | [CaminhaoServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/CaminhaoServlet.java) | `?erro=placa_duplicada` |
| **`caminhao`** | `placa_cavalo` | `caminhao_placa_cavalo_key` | `PlacaDuplicadaException` | [CaminhaoServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/CaminhaoServlet.java) | `?erro=placa_duplicada` |
| **`trajeto`** | `numero_gta` | `trajeto_numero_gta_key` | `DocumentoFiscalDuplicadoException` | [TrajetoServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/TrajetoServlet.java) | `?erro=documento_fiscal_duplicado` |
| **`trajeto`** | `numero_nota_fiscal`| `trajeto_numero_nota_fiscal_key`| `DocumentoFiscalDuplicadoException`| [TrajetoServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/TrajetoServlet.java) | `?erro=documento_fiscal_duplicado` |
| **`adm`** | `email` | `adm_email_key` | `EmailDuplicadoException` | [AdminServlet](Efficientia/src/main/java/com/efficientia/efficientia/controller/AdminServlet.java) | `?erro=email_duplicado` |
| **`analista`** | `cpf`, `codigo`, `email` | `analista_*_key` | `Cpf/Codigo/EmailDuplicadoException`| DAO preparado | — |

---

## 6. Sequência Completa de Testes da Implementação

Para validar a implementação de ponta a ponta, foram estruturados **três níveis de teste**:
1. **Fase 1:** Testes Unitários Automatizados com JUnit 5 / Maven.
2. **Fase 2:** Sequência de Teste de Integração (Camada DAO + Banco PostgreSQL).
3. **Fase 3:** Sequência de Teste na Interface Web / Servlets (Navegador e Requisições HTTP).

---

### Fase 1: Testes Unitários Automatizados (JUnit 5 & Maven)

#### Procedimento de Execução
No terminal do projeto (PowerShell no Windows), execute o comando com o `JAVA_HOME` configurado:

```powershell
$env:JAVA_HOME = "C:\Users\20260095-ieg\.jdks\openjdk-25.0.2"
./mvnw test -Dtest=TratadorExcecaoBancoTest
```

Para rodar a suíte completa de todos os testes do projeto:
```powershell
./mvnw test
```

#### Relação dos Casos de Teste Homologados em [TratadorExcecaoBancoTest](Efficientia/src/test/java/com/efficientia/efficientia/util/TratadorExcecaoBancoTest.java)

| ID | Cenário Testado | Entrada Simulada (`SQLException`) | Validação Asserida | Resultado |
| :---: | :--- | :--- | :--- | :---: |
| **UT-01** | Reconhecimento de SQLState `23505` | `sqlState = "23505"`, erro pecuarista_cpf_key | `isDuplicidade(e) == true` | **PASS** |
| **UT-02** | Reconhecimento de Código MySQL `1062` | `errorCode = 1062`, duplicate entry | `isDuplicidade(e) == true` | **PASS** |
| **UT-03** | Rejeição de erro SQL genérico | `sqlState = "08001"` (Connection refused) | `isDuplicidade(e) == false` | **PASS** |
| **UT-04** | Tradução para CPF Duplicado | Mensagem com `pecuarista_cpf_key` e `Key (cpf)=(12345678901)` | Instância de `CpfDuplicadoException`, campo `"CPF"` e valor `"12345678901"` | **PASS** |
| **UT-05** | Tradução para E-mail Duplicado | Mensagem com `motorista_email_key` e `Key (email)=(joao@transportes.com)` | Instância de `EmailDuplicadoException`, campo `"E-mail"` | **PASS** |
| **UT-06** | Tradução para CNPJ Duplicado | Mensagem com `empresa_cnpj_key` e `Key (cnpj)=(12345678000101)` | Instância de `CnpjDuplicadoException`, campo `"CNPJ"` | **PASS** |
| **UT-07** | Tradução para Placa Duplicada | Mensagem com `caminhao_placa_carreta_key` e `Key (placa_carreta)=(BRA1A12)`| Instância de `PlacaDuplicadaException`, campo `"Placa Carreta"` | **PASS** |
| **UT-08** | Tradução para Código Duplicado | Mensagem com `analista_codigo_key` e `Key (codigo)=(ANA-001)` | Instância de `CodigoDuplicadoException`, campo `"Código"` | **PASS** |
| **UT-09** | Tradução de Guia GTA / NF-e | Mensagem com `trajeto_numero_gta_key` e `Key (numero_gta)=(GTA-12345)` | Instância de `DocumentoFiscalDuplicadoException` | **PASS** |
| **UT-10** | Comportamento seletivo de disparo | Execução de `tratarSeDuplicado` com `23505` vs. erro de sintaxe `42P01` | Lança exceção apenas no primeiro; não dispara no segundo | **PASS** |

---

### Fase 2: Sequência de Teste de Integração (DAO x Banco PostgreSQL)

Esta sequência valida o comportamento das classes DAO ao executarem instruções SQL reais no PostgreSQL com dados conflitantes:

#### Passo 1: Teste de Duplicidade de CPF no Pecuarista
1. **Ação:** Instanciar um objeto `PecuaristaModel` com CPF `"12345678901"` e cadastrar via `pecuaristaDAO.inserir(p1)`.
2. **Resultado Esperado 1:** O primeiro registro é inserido com sucesso, retornando `true`.
3. **Ação:** Instanciar um segundo objeto `PecuaristaModel` com dados diferentes, mas o **mesmo CPF** `"12345678901"`, e chamar `pecuaristaDAO.inserir(p2)`.
4. **Resultado Esperado 2:**
   - O PostgreSQL rejeita com `SQLState 23505` (`pecuarista_cpf_key`).
   - O `PecuaristaDAO` aciona `TratadorExcecaoBanco.tratarSeDuplicado(e, "Pecuarista")`.
   - O método dispara imediatamente uma instância de `CpfDuplicadoException`.

#### Passo 2: Teste de Duplicidade de CNPJ na Empresa
1. **Ação:** Inserir uma empresa com CNPJ `"12345678000101"` via `empresaDAO.inserir(emp1)`.
2. **Ação:** Tentar atualizar uma segunda empresa com o mesmo CNPJ via `empresaDAO.atualizarCnpj(id2, "12345678000101")`.
3. **Resultado Esperado:** O método dispara `CnpjDuplicadoException` com `campo = "CNPJ"`.

#### Passo 3: Teste de Duplicidade de Placa no Caminhão
1. **Ação:** Inserir um caminhão com placa cavalo `"BRA1A12"` via `caminhaoDAO.inserir(c1)`.
2. **Ação:** Tentar inserir um novo caminhão com a mesma placa cavalo `"BRA1A12"`.
3. **Resultado Esperado:** O método dispara `PlacaDuplicadaException` com `tipoPlaca = "Placa Cavalo"`.

#### Passo 4: Teste de Resiliência a Outros Erros de Banco
1. **Ação:** Desconectar a base de dados ou apontar temporariamente para credencial inválida no `.env`.
2. **Ação:** Executar `pecuaristaDAO.inserir(p1)`.
3. **Resultado Esperado:**
   - O erro não é de duplicidade (`SQLState != 23505`).
   - `TratadorExcecaoBanco.tratarSeDuplicado` identifica que não é duplicidade e silencia a exceção customizada.
   - O DAO executa o log padrão `System.out.println` e devolve `false` normalmente, sem quebrar o fluxo.

---

### Fase 3: Sequência de Teste End-to-End na Interface Web (HTTP / Servlets)

Roteiro de teste via navegador no Apache Tomcat (ex.: `http://localhost:8080/Efficientia`):

```
+----+----------------------+---------------------------------+------------------------------------------+-------------------------------------------------+
| Et | Formulário Web       | Ação do Usuário                 | Dados Submetidos                         | Comportamento Esperado                          |
+----+----------------------+---------------------------------+------------------------------------------+-------------------------------------------------+
| 01 | Cadastro Pecuarista  | Preencher e clicar em Salvar    | CPF já cadastrado (ex: '12345678901')    | Redirecionamento: /pecuarista?erro=cpf_duplicado|
| 02 | Edição Pecuarista    | Alterar e-mail para já em uso   | E-mail existente de outro pecuarista     | Redirecionamento: /pecuarista?erro=email_duplic.|
| 03 | Cadastro Empresa     | Preencher e submeter formulário | CNPJ já cadastrado (ex: '12345678000101')| Redirecionamento: /empresa?erro=cnpj_duplicado  |
| 04 | Cadastro Empresa     | Informar código já atribuído    | Código corporativo (ex: 'EMP001')        | Redirecionamento: /empresa?erro=codigo_duplicado|
| 05 | Cadastro Motorista   | Informar e-mail já em uso       | E-mail existente (ex: 'mot@empresa.com') | Redirecionamento: /motorista?erro=email_duplic. |
| 06 | Cadastro Caminhão    | Cadastrar com placa já em uso   | Placa Cavalo/Carreta (ex: 'BRA2E19')     | Redirecionamento: /caminhao?erro=placa_duplicada|
| 07 | Cadastro Trajeto     | Submeter viagem com mesma GTA   | Número de GTA repetido (ex: 'GTA-001')   | Redirecionamento: /trajeto?erro=documento_fiscal|
+----+----------------------+---------------------------------+------------------------------------------+-------------------------------------------------+
```

---

## 7. Conclusão

Esta especificação assegura o isolamento de responsabilidades entre as restrições relacionais do **PostgreSQL** e as camadas de aplicação do **Efficientia**. Ao transformar códigos de erro técnicos e crípticos em exceções de domínio ricas e estruturadas, o sistema garante manutenibilidade, conformidade com os princípios da Orientação a Objetos e uma experiência de usuário defensiva e amigável.
