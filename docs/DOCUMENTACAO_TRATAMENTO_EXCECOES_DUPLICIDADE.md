# Documentação Acadêmica: Tratamento de Exceções de Duplicidade de Dados e Integridade Relacional

> **Projeto:** Efficientia — Gestão de Transporte e Bem-Estar Animal  
> **Tema:** Programação Orientada a Objetos (POO), Arquitetura MVC, Integridade de Banco de Dados (PostgreSQL) e Tratamento Defensivo de Exceções  
> **Status:** Implementado e Homologado  

---

## 1. Contexto e Motivação Acadêmica

### 1.1. O Desafio da Integridade de Dados no Modelo Relacional
Em sistemas corporativos que interagem com bancos de dados relacionais (SGBDs como o **PostgreSQL**), a garantia de unicidade de entidades fundamentais (tais como **CPF**, **CNPJ**, **E-mail**, **Placas de Veículos**, **Códigos Corporativos** e **Documentos Fiscais como NF-e e GTA**) é garantida através de restrições de unicidade (*UNIQUE constraints*).

Quando a camada de controle ou aplicação tenta inserir ou atualizar um registro com dados já existentes, o banco de dados aborta a instrução atômica gerando uma exceção de integridade referencial:
- **Código ANSI SQLState / PostgreSQL:** `23505` (`unique_violation`).
- **Driver JDBC:** Dispara uma [SQLException](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/SQLException.html) de baixo nível contendo detalhes técnicos de constraints internas.

### 1.2. O Problema da Captura Genérica e Silenciamento de Erros (*Exception Swallowing*)
Práticas inadequadas em arquiteturas DAO frequentemente incorrem em dois extremos:
1. **Silenciamento com retorno booleano opaco:** O método captura `SQLException`, imprime no console e devolve `false`. O Servlet chamador não consegue discernir se a falha decorreu de uma queda de rede, erro de sintaxe SQL ou se o usuário tentou cadastrar um CPF que já pertencia a outra pessoa.
2. **Propagação de `SQLException` para a camada Web:** Acopla fortemente a camada de apresentação ao JDBC e expõe mensagens internas do banco de dados ao usuário final, violando o princípio de segurança e abstração de dados (*Information Disclosure*).

### 1.3. A Solução Implementada
Implementou-se um subsistema coeso e desacoplado de tratamento de exceções de domínio no pacote `com.efficientia.efficientia.exception`:
* **Hierarquia Semântica de Exceções Unchecked:** Baseada em [DadoDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/DadoDuplicadoException.java), estendendo `RuntimeException`.
* **Tradutor Inteligente de SGBD:** O componente utilitário [TratadorExcecaoBanco](Efficientia/src/main/java/com/efficientia/efficientia/util/TratadorExcecaoBanco.java) inspeciona o `SQLState`, extrai a constraint e o valor duplicado via expressões regulares e instancia a subclasse exata de erro.
* **Comunicação Transparente com a Camada Web (Controller/JSP):** Os Servlets interceptam os erros tipados e redirecionam o usuário via padrão PRG (*Post-Redirect-Get*) com o parâmetro semântico `erro=cpf_duplicado`, `erro=cnpj_duplicado`, `erro=email_duplicado` ou `erro=placa_duplicada`.

---

## 2. Diagrama da Hierarquia de Exceções

A árvore de classes foi modelada respeitando o Princípio da Substituição de Liskov (LSP) e os conceitos fundamentais de Orientação a Objetos:

```
                  ┌──────────────────────┐
                  │   RuntimeException   │ (Java Nativo)
                  └──────────┬───────────┘
                             │
                             ▼
               ┌───────────────────────────┐
               │   DadoDuplicadoException  │ (Base de Duplicidade)
               └─────────────┬─────────────┘
                             │
       ┌──────────────┬──────┴───────┬──────────────┬──────────────┬──────────────────────────────┐
       ▼              ▼              ▼              ▼              ▼                              ▼
┌──────────────┐┌──────────────┐┌──────────────┐┌──────────────┐┌──────────────┐┌──────────────────────────────┐
│ CpfDuplicado ││ CnpjDuplicado││EmailDuplicado││PlacaDuplicada││CodigoDuplic. ││DocumentoFiscalDuplicado      │
│  Exception   ││  Exception   ││  Exception   ││  Exception   ││  Exception   ││          Exception           │
└──────────────┘└──────────────┘└──────────────┘└──────────────┘└──────────────┘└──────────────────────────────┘
```

---

## 3. Especificação das Exceções Criadas

| Classe de Exceção | Campo Monitorado | Código de Erro Web (`tipoErro`) | Entidades Afetadas | Constraint Típica no PostgreSQL |
| :--- | :--- | :--- | :--- | :--- |
| **`CpfDuplicadoException`** | CPF | `cpf_duplicado` | Pecuarista, Analista | `pecuarista_cpf_key`, `analista_cpf_key` |
| **`CnpjDuplicadoException`** | CNPJ | `cnpj_duplicado` | Empresa | `empresa_cnpj_key` |
| **`EmailDuplicadoException`** | E-mail | `email_duplicado` | Motorista, Pecuarista, Analista, Administrador | `motorista_email_key`, `adm_email_key` |
| **`PlacaDuplicadaException`** | Placa Cavalo / Placa Carreta | `placa_duplicada` | Caminhão | `caminhao_placa_cavalo_key`, `caminhao_placa_carreta_key` |
| **`CodigoDuplicadoException`** | Código Corporativo / Integração | `codigo_duplicado` | Empresa, Analista | `empresa_codigo_key`, `analista_codigo_key` |
| **`DocumentoFiscalDuplicadoException`** | GTA ou NF-e (44 dígitos) | `documento_fiscal_duplicado` | Trajeto | `trajeto_numero_gta_key`, `trajeto_numero_nota_fiscal_key` |
| **`DadoDuplicadoException`** | Genérico / Outros | `dado_duplicado` | Todas | Qualquer UNIQUE constraint |

### 3.1. Metadados Estruturados em `DadoDuplicadoException`
Cada instância da exceção carrega atributos que enriquecem o log e a rastreabilidade:
- `entidade`: Nome da entidade envolvida (ex: `"Pecuarista"`, `"Empresa"`, `"Motorista"`).
- `campo`: Nome amigável do campo violado (ex: `"CPF"`, `"CNPJ"`, `"Placa Cavalo"`).
- `valorDuplicado`: Valor concreto que causou a colisão (ex: `"12345678901"`).
- `constraintName`: Identificador exato da restrição violada no PostgreSQL (ex: `"pecuarista_cpf_key"`).
- `tipoErro`: Identificador curto para comunicação uniforme via HTTP GET / Query String (ex: `"cpf_duplicado"`).

---

## 4. O Mecanismo do `TratadorExcecaoBanco`

O utilitário [TratadorExcecaoBanco](Efficientia/src/main/java/com/efficientia/efficientia/util/TratadorExcecaoBanco.java) centraliza o desacoplamento entre o PostgreSQL e o código Java.

### 4.1. Detecção do Erro de Duplicidade
A verificação analisa:
1. `SQLState.equals("23505")` — Código oficial ANSI SQL para violação de chave primária/única.
2. `errorCode == 1062` — Código de fornecedor para compatibilidade com outros SGBDs relacionais (MySQL).
3. Expressões textuais contidas na mensagem original do driver (`"duplicate key"`, `"violates unique constraint"`).

### 4.2. Extração de Constraints e Valores via REGEX
As mensagens de erro emitidas pelo PostgreSQL trazem o nome da constraint e os valores da linha no formato:
```text
ERROR: duplicate key value violates unique constraint "pecuarista_cpf_key"
  Detail: Key (cpf)=(12345678901) already exists.
```
O `TratadorExcecaoBanco` utiliza expressões regulares compiladas para extrair:
- **Constraint:** `(?:unique constraint|restrição de unicidade)[\"\\s]+([a-zA-Z0-9_]+)[\"\\s]?`
- **Coluna e Valor:** `Key \(([^)]+)\)=\(([^)]*)\)`

---

## 5. Fluxo de Execução e Integração nas Camadas

```
[Formulário JSP]
       │
       │ (1) POST /pecuarista (CPF: 12345678901)
       ▼
[PecuaristaServlet]
       │
       │ (2) dao.inserir(novoPecuarista)
       ▼
[PecuaristaDAO]
       │
       │ (3) executeUpdate()
       ▼
[PostgreSQL Database]
       │
       │ (4) Lança SQLException (SQLState: 23505)
       ▼
[PecuaristaDAO - Bloco catch]
       │
       │ (5) TratadorExcecaoBanco.tratarSeDuplicado(e, "Pecuarista")
       │     └─► Identifica SQLState 23505 e constraint 'pecuarista_cpf_key'
       │     └─► Instancia e lança CpfDuplicadoException
       ▼
[PecuaristaServlet - Bloco catch(DadoDuplicadoException)]
       │
       │ (6) Captura CpfDuplicadoException
       │ (7) sendRedirect("/pecuarista?erro=" + e.getTipoErro())
       ▼
[Navegador do Usuário / View JSP]
       │
       │ (8) GET /pecuarista?erro=cpf_duplicado
       ▼
[Exibição Amigável de Alerta de Duplicidade]
```

### 5.1. Código no DAO (Exemplo em `PecuaristaDAO`)
```java
try (Connection connection = ConnectionFactory.getConnection();
     PreparedStatement stmt = connection.prepareStatement(sql)) {

    preencherStatement(stmt, pecuaristaModel);
    return stmt.executeUpdate() > 0;

} catch (SQLException e) {
    // Intercepta e dispara a exceção especializada caso seja duplicidade
    TratadorExcecaoBanco.tratarSeDuplicado(e, "Pecuarista");
    
    // Se for outro erro de banco (ex: timeout de conexão), mantém o log e retorna false
    System.out.println("Erro ao inserir pecuarista: " + e.getMessage());
    return false;
}
```

### 5.2. Código no Servlet (Exemplo em `PecuaristaServlet`)
```java
try {
    boolean inserido = dao.inserir(novoPecuarista);
    if (!inserido) {
        WebErroUtil.redirecionarErroGeral(req, resp, "Não foi possível cadastrar o pecuarista. Verifique os dados informados.", "/pecuarista");
        return;
    }
} catch (DadoDuplicadoException e) {
    // Redireciona para a tela de erro geral (/erro.jsp) com os metadados da duplicidade e link de retorno
    WebErroUtil.redirecionarErroDuplicidade(req, resp, e, "/pecuarista");
    return;
} catch (Exception e) {
    System.out.println("Erro ao cadastrar pecuarista: " + e.getMessage());
    WebErroUtil.redirecionarErroGeral(req, resp, "Erro ao cadastrar pecuarista: " + e.getMessage(), "/pecuarista");
    return;
}
resp.sendRedirect(req.getContextPath() + "/pecuarista?sucesso=cadastrado");
```

---

## 6. Cobertura de Testes Automatizados (JUnit 5)

A classe [TratadorExcecaoBancoTest](Efficientia/src/test/java/com/efficientia/efficientia/util/TratadorExcecaoBancoTest.java) homologa 100% dos cenários de teste unitário:
- Identificação correta de violação por `SQLState 23505`.
- Identificação por código MySQL `1062`.
- Não classificação de erros alheios (ex: falha de conexão `08001` ou tabela inexistente `42P01`).
- Mapeamento correto de CPF para [CpfDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/CpfDuplicadoException.java).
- Mapeamento de CNPJ para [CnpjDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/CnpjDuplicadoException.java).
- Mapeamento de E-mail para [EmailDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/EmailDuplicadoException.java).
- Mapeamento de Placa (Carreta/Cavalo) para [PlacaDuplicadaException](Efficientia/src/main/java/com/efficientia/efficientia/exception/PlacaDuplicadaException.java).
- Mapeamento de Código Corporativo para [CodigoDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/CodigoDuplicadoException.java).
- Mapeamento de Documentos de Viagem (GTA/NF-e) para [DocumentoFiscalDuplicadoException](Efficientia/src/main/java/com/efficientia/efficientia/exception/DocumentoFiscalDuplicadoException.java).
- Comportamento seletivo de `tratarSeDuplicado`: lança exceção em caso de duplicidade e mantém a execução fluida para outros tipos de falha de banco.
