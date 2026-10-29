# Especificação Técnica (Spec): Padronização do Tratamento de Exceções nos DAOs Auxiliares

> **Projeto:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Módulo:** Camada de Persistência, Exceções de Domínio e DAOs Auxiliares  
> **Disciplina:** Lógica de Programação (M3) e Programação Orientada a Objetos (POO)  
> **Branch de Trabalho:** `feat/POO/EFFICIENTI22/padroniza-excecoes-daos-auxiliares`  
> **Status:** Especificado e Pronto para Implementação  
> **Versão:** 1.0  

---

## 1. Identificação e Visão Geral da Especificação

### 1.1. Propósito
Esta especificação técnica formaliza a padronização e extensão da arquitetura de **captura, tradução e lançamento semântico de exceções de banco de dados** (notadamente violações de chave primária e restrições de unicidade — *UNIQUE constraints*, código `SQLSTATE 23505`) para todos os **DAOs auxiliares** do ecossistema Efficientia:
1. `PropriedadeDAO`
2. `EnderecoDAO`
3. `InfoEmbarqueDAO`
4. `ParadaImprevistaDAO`
5. `AnalistaDAO` (complementação de métodos de atualização específicos)

### 1.2. Problema Resolvido
Enquanto os DAOs principais (`PecuaristaDAO`, `MotoristaDAO`, `CaminhaoDAO`, `EmpresaDAO`, `AdminDAO`, `TrajetoDAO`) já utilizam o componente utilitário [`TratadorExcecaoBanco.java`](../src/main/java/com/efficientia/efficientia/util/TratadorExcecaoBanco.java), os DAOs auxiliares mantinham blocos `catch (SQLException e)` legados que apenas imprimiam a mensagem no console (`System.out.println`) e retornavam `false`.

Esse comportamento gerava três deficiências graves:
1. **Engolimento Silencioso de Exceções (*Exception Swallowing*):** Violação direta do requisito M3 de Lógica de Programação (*"Tratamento de exceções, sempre que couber"*).
2. **Inconsistência Arquitetural:** Duas convenções concorrentes na mesma camada de persistência.
3. **Ausência de Rastreabilidade Semântica:** Impedimento de feedback amigável caso haja conflito de dados (exemplo: vincular um mesmo endereço já ocupado a uma nova propriedade rural ou duplicar embarque para o mesmo trajeto).

---

## 2. Fundamentação no Banco de Dados (PostgreSQL)

O esquema relacional do banco de dados (`Efficientia.sql`) estabelece restrições de unicidade explícitas para as tabelas envolvidas:

| Tabela | Coluna com Restrição | Tipo de Restrição no DDL | Impacto de Negócio ao Duplicar |
| :--- | :--- | :--- | :--- |
| `propriedade` | `id_endereco` | `INTEGER UNIQUE NOT NULL` | Um endereço físico só pode pertencer a uma única propriedade rural cadastrada. |
| `info_embarque` | `id_trajeto` | `INTEGER UNIQUE NOT NULL` | Uma viagem pecuária possui apenas uma ficha consolidada de informações de embarque. |
| `analista` | `cpf` | `CHAR(11) UNIQUE NOT NULL` | O CPF do analista é único nacionalmente. |
| `analista` | `codigo` | `VARCHAR(20) UNIQUE NOT NULL` | O código funcional do analista na empresa contratante é estritamente exclusivo. |
| `endereco` | `id` (PK) | `SERIAL PRIMARY KEY` | Integridade referencial do endereço geográfico. |
| `parada_imprevista`| `id` (PK) | `SERIAL PRIMARY KEY` | Integridade do registro cronológico de incidentes da viagem. |

---

## 3. Arquitetura da Solução e Padrão de Projeto

### 3.1. Princípio de Tratamento Centralizado
Em todas as operações de mutação (`INSERT` e `UPDATE`), o bloco de tratamento de erro do DAO deve delegar imediatamente a análise ao utilitário estático:

```java
try (Connection connection = ConnectionFactory.getConnection();
     PreparedStatement stmt = connection.prepareStatement(sql)) {

    // Preenchimento e execução
    return stmt.executeUpdate() > 0;

} catch (SQLException e) {
    // 1. Dispara exceção de domínio se for erro de duplicidade (SQLSTATE 23505)
    TratadorExcecaoBanco.tratarSeDuplicado(e, "NomeEntidade");
    
    // 2. Log defensivo para falhas não-duplicadas (ex.: queda de conexão ou timeout)
    System.out.println("Erro ao persistir NomeEntidade: " + e.getMessage());
    return false;
}
```

Caso o erro seja uma violação de duplicidade (`23505`), o método `tratarSeDuplicado` interrompe o fluxo normal lançando uma subclasse não-checada de `DadoDuplicadoException` (`RuntimeException`), preservando a assinatura do método (`public boolean`) sem gerar acoplamento checked.

---

## 4. Matriz Detalhada de Mutações por DAO Auxiliar

### 4.1. `PropriedadeDAO`
* **Entidade Padrão:** `"Propriedade"`
* **Métodos a padronizar:**
  1. `public boolean inserir(PropriedadeModel propriedadeModel)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade");`
  2. `public boolean atualizar(PropriedadeModel propriedadeModel, int id)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade");`
  3. `public boolean inserirSimples(String nome, Integer idPecuarista, Integer idEndereco)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade");`
  4. `public boolean atualizarNome(int id, String novoNome)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade", "Nome", novoNome);`
  5. `public boolean atualizarPecuarista(int id, Integer idPecuarista)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade", "Pecuarista Vinculado", String.valueOf(idPecuarista));`
  6. `public boolean atualizarEndereco(int id, Integer idEndereco)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade", "Endereço Vinculado", String.valueOf(idEndereco));`

### 4.2. `EnderecoDAO`
* **Entidade Padrão:** `"Endereço"`
* **Métodos a padronizar:**
  1. `public boolean inserir(EnderecoModel enderecoModel)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço");`
  2. `public boolean atualizar(EnderecoModel enderecoModel, int id)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço");`
  3. `public boolean inserirSimples(String rua, int numero, String bairro, String cidade, String cep)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço");`
  4. `public boolean atualizarCep(int id, String novoCep)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "CEP", novoCep);`
  5. `public boolean atualizarCidade(int id, String novaCidade)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Cidade", novaCidade);`
  6. `public boolean atualizarBairro(int id, String novoBairro)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Bairro", novoBairro);`
  7. `public boolean atualizarRua(int id, String novaRua)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Rua", novaRua);`
  8. `public boolean atualizarNumero(int id, int novoNumero)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Número", String.valueOf(novoNumero));`

### 4.3. `InfoEmbarqueDAO`
* **Entidade Padrão:** `"InfoEmbarque"`
* **Métodos a padronizar:**
  1. `public boolean inserir(InfoEmbarqueModel infoEmbarque)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "InfoEmbarque");`
  2. `public boolean atualizar(InfoEmbarqueModel infoEmbarque, int id)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "InfoEmbarque");`

### 4.4. `ParadaImprevistaDAO`
* **Entidade Padrão:** `"ParadaImprevista"`
* **Métodos a padronizar:**
  1. `public boolean inserir(ParadaImprevistaModel parada)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "ParadaImprevista");`
  2. `public boolean atualizar(ParadaImprevistaModel parada, int id)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "ParadaImprevista");`

### 4.5. `AnalistaDAO`
* **Entidade Padrão:** `"Analista"`
* **Status Atual:** Métodos gerais (`inserir`, `atualizar`, `inserirSimples`, `atualizarEmail`) já contêm o tratamento.
* **Métodos a complementar para 100% de cobertura:**
  1. `public boolean atualizarNome(int id, String novoNome)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Analista", "Nome", novoNome);`
  2. `public boolean atualizarSenha(int id, String novaSenha)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Analista");`
  3. `public boolean atualizarEmpresa(int id, Integer idEmpresa)`:
     - Adicionar: `TratadorExcecaoBanco.tratarSeDuplicado(e, "Analista", "Empresa", String.valueOf(idEmpresa));`

---

## 5. Estratégia de Testes Automatizados (JUnit 5)

Para comprovar a conformidade com a disciplina de **Lógica de Programação**, deve ser implementada a classe de testes automatizados:
* **Arquivo:** `src/test/java/com/efficientia/efficientia/dao/DaosAuxiliaresExcecaoTest.java`
* **Cenários Cobertos:**
  1. Lançamento de `DadoDuplicadoException` quando `PropriedadeDAO` recebe SQLSTATE 23505 na vinculação de endereço.
  2. Lançamento de `DadoDuplicadoException` quando `InfoEmbarqueDAO` recebe SQLSTATE 23505 por trajeto duplicado.
  3. Comportamento não-duplicado (SQLSTATE genérico como `08001` de conexão) retornando `false` sem lançar exceção de duplicidade.
  4. Execução de suíte via `mvn test` garantindo regressão zero nos 31 testes existentes.

---

## 6. Critérios de Aceitação (DoD)
1. Todos os 5 DAOs auxiliares importam e utilizam `TratadorExcecaoBanco`.
2. Nenhuma operação de mutação em DAOs auxiliares consome `SQLException` silenciosamente quando o código for `23505`.
3. Todos os testes unitários compilam e passam via Maven Wrapper (`mvn test`).
4. Documentação técnica atualizada e versionada na branch de feature.
