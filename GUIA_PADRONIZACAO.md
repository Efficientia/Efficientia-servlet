# Guia de Padronização de Código — Projeto Efficientia

> **Versão:** 1.0  
> **Tecnologias:** Java 17+, Jakarta EE (Servlets, JSP, JSTL), JDBC, PostgreSQL  
> **Arquitetura:** Model-View-Controller (MVC) com Data Access Object (DAO)

---

## 1. Visão Geral e Objetivos

Este guia estabelece os padrões obrigatórios de **nomenclatura**, **estrutura de diretórios**, **orientação a objetos** e **boas práticas de codificação** para o projeto **Efficientia**. O objetivo é garantir uniformidade, legibilidade, manutenibilidade e facilidade de integração em equipe.

```
Navegador (Browser)
      │ ▲
      │ │  1. HTTP Request (GET / POST)
      │ │  6. HTML renderizado ou SendRedirect
      ▼ │
Servidor Web / Servlet Container (Apache Tomcat)
      │ ▲
      │ │  2. Roteamento de URL para doGet() / doPost()
      │ │  5. Encaminhamento via Forward para JSP
      ▼ │
Controller (Classes *Servlet)
      │ ▲
      │ │  3. Extração/Validação de parâmetros e chamada ao DAO
      │ │  4. Retorno de Models / coleções
      ▼ │
Model & DAO (*Model e *DAO)
      │ ▲
      │ │  Acesso a dados via JDBC (ConnectionFactory)
      ▼ │
Banco de Dados Relacional (PostgreSQL)
```

---

## 2. Convenções Gerais de Nomenclatura

O projeto adota as **Java Code Conventions** oficiais da Oracle/Sun, adaptadas para o ecossistema MVC do Efficientia.

| Elemento | Convenção | Exemplo Correto | Exemplo Incorreto |
| :--- | :--- | :--- | :--- |
| **Pacotes** | Minúsculas contínuas (`lowercase`) | `com.efficientia.efficientia.dao.impl` | `com.efficientia.efficientia.DAO.impl` |
| **Classes de Modelo** | UpperCamelCase + sufixo `Model` | `TrajetoModel`, `MotoristaModel` | `Trajeto`, `tb_trajeto`, `trajetoModel` |
| **Classes DAO** | UpperCamelCase + sufixo `DAO` | `TrajetoDAO`, `CaminhaoDAO` | `TrajetoDao`, `DaoTrajeto`, `TrajetoDB` |
| **Classes Controller** | UpperCamelCase + sufixo `Servlet` | `TrajetoServlet`, `AdminServlet` | `TrajetoController`, `trajetoServlet` |
| **Interfaces** | UpperCamelCase (substantivo/adjetivo) | `Model` | `IModel`, `ModelInterface` |
| **Enums** | UpperCamelCase no tipo, UPPER_SNAKE nos valores | `StatusTrajeto.EM_TRANSITO` | `StatusTrajeto.EmTransito` |
| **Métodos e Variáveis** | lowerCamelCase | `dataNascimento`, `preencherStatement()` | `DataNascimento`, `preencher_statement()` |
| **Constantes (`static final`)** | UPPER_SNAKE_CASE | `TAMANHO_MAXIMO_PAGINA` | `tamanhoMaximoPagina` |
| **Páginas JSP (Views)** | kebab-case ou lowercase sob `/WEB-INF/views/` | `editar-trajeto.jsp`, `caminhao.jsp` | `editarTrajeto.jsp`, `Editar_Trajeto.jsp` |

---

## 3. Padrão da Camada Model (`com.efficientia.efficientia.model`)

### 3.1. Interface Base `Model`
Todas as entidades persistíveis devem implementar a interface `Model`, garantindo acesso uniforme à chave primária:

```java
public interface Model {
    int getId();
}
```

### 3.2. Hierarquia de Usuários (`UsuarioModel`)
A classe abstrata `UsuarioModel` centraliza os atributos comuns a qualquer indivíduo que realiza login ou assina formulários:
- `id`, `nome`, `senha`, `email`, `telefone`, `assinatura`, `dataNascimento`, `empresaModel`.

**Regra de Herança:**
- **Herança direta de `UsuarioModel`**: `MotoristaModel` e `AnalistaModel` (entidades que compartilham o conjunto completo de atributos de usuário com empresa e assinatura).
- **Entidades com esquema dedicado**: `AdminModel` e `PecuaristaModel` implementam `Model` diretamente, mapeando fielmente as colunas do banco sem atributos artificiais ou chamadas a `super(null)`.
- **Atenção:** Como `UsuarioModel` já implementa `Model`, suas subclasses filhas **não** precisam declarar `implements Model` novamente.

### 3.3. Sobrecarga Padronizada de Construtores
Toda classe Model deve fornecer no mínimo dois construtores:
1. **Construtor Completo (com ID):** Usado pelo DAO para reconstruir o objeto a partir de um `ResultSet`.
2. **Construtor sem ID:** Usado pelos Servlets para instanciar novos registros antes da inserção no banco (onde o ID é gerado por `SERIAL` / `AUTO_INCREMENT`).

### 3.4. Encapsulamento
- Todos os atributos de instância devem ser `private` (ou `protected` em classes abstratas base).
- Getters e setters completos para todos os atributos manipuláveis.

---

## 4. Padrão da Camada DAO (`com.efficientia.efficientia.dao.impl`)

### 4.1. Estrutura Padrão da Classe
Toda classe DAO deve seguir exatamente a seguinte organização visual e de seções:

```java
package com.efficientia.efficientia.dao.impl;

import com.efficientia.efficientia.factory.ConnectionFactory;
import com.efficientia.efficientia.model.ExemploModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Exemplo.
 *
 * Gerencia a persistência e consulta na tabela 'exemplo' do PostgreSQL,
 * utilizando ConnectionFactory e fechamento automático de conexões via try-with-resources.
 */
public class ExemploDAO {

    // ==================== OPERAÇÕES CRUD ====================

    public boolean inserir(ExemploModel model) { ... }

    public List<ExemploModel> listar() { ... }

    public ExemploModel buscar(int id) { ... }

    public boolean atualizar(ExemploModel model, int id) { ... }

    public boolean excluir(int id) { ... }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    public List<ExemploModel> buscarPorNome(String termo) { ... }

    public List<ExemploModel> buscarPorEmpresa(int idEmpresa) { ... }

    // ==================== MÉTODOS AUXILIARES ====================

    private ExemploModel extrairExemplo(ResultSet rs) throws SQLException { ... }

    private void preencherStatement(PreparedStatement stmt, ExemploModel model) throws SQLException { ... }
}
```

### 4.2. Contrato Obrigatório de Métodos CRUD e Consultas

| Método | Assinatura | Retorno em Sucesso | Retorno em Falha |
| :--- | :--- | :--- | :--- |
| **Inserção** | `boolean inserir(XModel model)` | `true` se inserido | `false` |
| **Listagem** | `List<XModel> listar()` | `List<XModel>` preenchida | `List` vazia (`ArrayList`) |
| **Busca por ID** | `XModel buscar(int id)` | Objeto populado | `null` |
| **Atualização** | `boolean atualizar(XModel model, int id)` | `true` se atualizado | `false` |
| **Exclusão** | `boolean excluir(int id)` | `true` se removido | `false` |
| **Busca por Nome** | `List<XModel> buscarPorNome(String termo)` | Lista filtrada (case-insensitive / parcial) | `List` vazia |
| **Busca por Vínculo** | `List<XModel> buscarPorEmpresa(int idEmpresa)` | Lista vinculada | `List` vazia |
| **Busca por ID Único** | `XModel buscarPor<Campo>(String valor)` | Objeto correspondente | `null` |
| **Extração** | `XModel extrairX(ResultSet rs)` | Objeto populado | Lança `SQLException` |
| **Preenchimento** | `void preencherStatement(PreparedStatement stmt, XModel m)` | Mapeia `?` da query | Lança `SQLException` |

### 4.3. Regras de Ouro no JDBC e Boas Práticas do DAO
1. **Nunca propagar `throws SQLException` nos métodos públicos do DAO:** Capture a exceção internamente com `try-catch`, registre a mensagem de erro no console e devolva um valor seguro (`false`, `null` ou lista vazia).
2. **Try-with-resources Obrigatório:** Garanta o fechamento seguro de `Connection`, `PreparedStatement` e `ResultSet` declarando-os nos parênteses do `try`.
3. **Helper de Hidratação Único (`extrair<Entidade>`):** Para eliminar duplicação de código de mapeamento relacional entre `listar()`, `buscar(id)` e métodos de busca específicos, centralize a montagem do Model no método privado `extrair<Entidade>(ResultSet rs)`.
4. **Constante `BASE_SELECT`:** Para entidades com múltiplos `JOIN`s (como `TrajetoDAO` e `PropriedadeDAO`), declare uma constante `BASE_SELECT` com as projeções completas, reaproveitando-a em todas as leituras.
5. **Uso de Text Blocks (`"""`):** Queries SQL devem ser declaradas com blocos de texto multilinha, mantendo quebras de linha e indentação legíveis.
6. **Tratamento de Chaves Estrangeiras Nulas:** Sempre verifique referências a objetos associados antes de fazer `stmt.setInt(...)`. Se nulo, use `stmt.setNull(posicao, Types.INTEGER)`.

---

## 5. Padrão da Camada Controller (`com.efficientia.efficientia.controller`)

### 5.1. Definição do Servlet
- Cada Servlet deve estender `HttpServlet` e conter a anotação `@WebServlet` definindo `name` e rota (`value`):
  ```java
  @WebServlet(name = "ExemploServlet", value = "/exemplo")
  public class ExemploServlet extends HttpServlet {
      private ExemploDAO dao;

      @Override
      public void init() {
          this.dao = new ExemploDAO();
      }
  }
  ```

### 5.2. Separação de Responsabilidades HTTP

#### Método `doGet` (Leitura e Navegação)
- Roteia com base no parâmetro `acao`:
  - Se `acao == "editar"`: busca o registro por ID via `dao.buscar(id)`, armazena no `req.setAttribute(...)` e faz o `forward` para o JSP de formulário (ex: `/WEB-INF/views/editar-exemplo.jsp`).
  - Se sem ação: lista todos os registros via `dao.listar()`, armazena na requisição e faz o `forward` para a listagem (ex: `/WEB-INF/views/exemplo.jsp`).

#### Método `doPost` (Mutações - Create, Update, Delete)
- Define a codificação: `req.setCharacterEncoding("UTF-8");`.
- Roteia com base no parâmetro `acao`:
  - `"excluir"`: lê o ID e executa `dao.excluir(id)`.
  - `"atualizar"`: lê o ID e os novos dados, monta o objeto Model e executa `dao.atualizar(model, id)`.
  - Padrão (novo cadastro): lê os parâmetros, instancia o Model (construtor sem ID) e executa `dao.inserir(model)`.
- **Padrão Post-Redirect-Get (PRG):** Sempre finalize mutações no `doPost` com `resp.sendRedirect(req.getContextPath() + "/rota")` para evitar reenvio acidental de formulário ao recarregar a página (F5).

### 5.3. Métodos Auxiliares Defensivos
Para evitar quebras com `NullPointerException` ou `NumberFormatException`, inclua helpers privados nos Servlets:
```java
private int parseInt(String valor, int valorPadrao) {
    if (valor == null || valor.isBlank()) return valorPadrao;
    try {
        return Integer.parseInt(valor.trim());
    } catch (NumberFormatException e) {
        return valorPadrao;
    }
}

private String obterParametro(HttpServletRequest req, String nome) {
    String valor = req.getParameter(nome);
    return (valor != null) ? valor.trim() : "";
}
```

---

## 6. Padrão da Camada View (`src/main/webapp/WEB-INF/views`)

1. **Localização Segura:** Todas as páginas JSP de renderização do sistema devem ficar sob `/WEB-INF/views/`. Essa pasta é inacessível diretamente pela URL do navegador, forçando toda requisição a passar pelo Servlet correspondente.
2. **Padrão de Nomenclatura das Páginas:**
   - Tela principal/listagem: `[entidade].jsp` (ex: `trajeto.jsp`, `caminhao.jsp`).
   - Tela de edição: `editar-[entidade].jsp` (ex: `editar-trajeto.jsp`, `editar-caminhao.jsp`).
3. **Uso de JSTL e Expression Language (EL):**
   - Evitar blocos de código Java (`<% ... %>` / Scriptlets).
   - Utilizar tags JSTL `<c:forEach>`, `<c:if>`, `<c:choose>` e expressões `${objeto.propriedade}`.

---

## 7. Checklist de Validação para Code Review

Antes de enviar ou aprovar um novo código, valide esta lista:

- [ ] O pacote está escrito em letras minúsculas?
- [ ] A classe possui o sufixo correto (`*Model`, `*DAO`, `*Servlet`)?
- [ ] O Model implementa `Model` e estende `UsuarioModel` (se for ator de login/assinatura)?
- [ ] O Model possui construtor com ID e sem ID?
- [ ] O DAO implementa todos os 5 métodos CRUD com os nomes padronizados?
- [ ] O DAO utiliza `try-with-resources` para conexões e prepared statements?
- [ ] O DAO não propaga `SQLException` em métodos públicos?
- [ ] O Servlet faz `sendRedirect` após operações de escrita no `doPost` (Post-Redirect-Get)?
- [ ] A página JSP está localizada sob `/WEB-INF/views/` e com nome em kebab-case?
- [ ] Métodos e classes estão documentados com Javadoc explicativo?
