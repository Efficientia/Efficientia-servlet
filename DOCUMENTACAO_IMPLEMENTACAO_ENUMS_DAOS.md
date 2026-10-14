# Documentação de Implementação: Ciclo de Vida dos Enums e Operações DAO

Este documento detalha as análises, correções e novas funcionalidades implementadas no sistema **Efficientia** referentes aos Enums (`StatusTrajeto` e `StatusAnalise`), à gestão de ciclo de vida das viagens pecuárias (`EM_ANDAMENTO` e `CONCLUIDA`), e à cobertura dos métodos de persistência no painel de testes.

---

## 1. Contexto e Premissas

O sistema opera o transporte de gado segregado nos seguintes estados de ciclo de vida:
- **`EM_ANDAMENTO`**: O transporte está ativo (embarque realizado ou caminhão em rota).
- **`CONCLUIDA`**: A viagem foi finalizada com desembarque conferido no destino.

Foram identificadas falhas de fluxo onde o status não podia ser alterado pelo operador nas telas, buscas falhavam por sensibilidade a acentos, conversões de strings no JDBC quebravam a listagem e restrições `NOT NULL` do PostgreSQL corriam risco de violação.

---

## 2. Diagnóstico dos Problemas Corrigidos

### 🔴 Erro 2: Impossibilidade Operacional de Concluir Viagens
- **Causa**: O formulário de edição (`editar-trajeto.jsp`) e o modal de edição (`teste.jsp`) possuíam o campo de status estritamente como `<input type="hidden">`, impossibilitando o usuário de alterar para `CONCLUIDA`.
- **Efeito**: Viagens com desembarque, quilometragem e assinaturas concluídas permaneciam indefinidamente como `EM_ANDAMENTO`.

### 🔴 Erro 3: Risco de Violação de Constraint `NOT NULL` no `TrajetoDAO`
- **Causa**: No método `TrajetoDAO.preencherStatement()`, a linha 746 passava `null` caso `trajeto.getStatus()` fosse nulo, enquanto a tabela no banco impõe `status status_trajeto NOT NULL`.
- **Efeito**: Tentativas de persistência sem status explícito disparavam `SQLException` no PostgreSQL.

### 🟠 Erro 4: Desserialização Frágil do Enum nos DAOs
- **Causa**: Os DAOs (`TrajetoDAO`, `ParadaImprevistaDAO`, `InfoEmbarqueDAO`) utilizavam diretamente `StatusTrajeto.valueOf(statusStr)`.
- **Efeito**: Qualquer caractere em caixa baixa, espaço em branco ou valor imprevisto no banco gerava `IllegalArgumentException` não tratada, abortando toda a listagem de registros.

### 🟠 Erro 5: Falha na Busca por Status com Acentuação
- **Causa**: O Enum foi declarado como `CONCLUIDA` (sem acento). Na busca textual do `TrajetoServlet` e no `teste.jsp`, a digitação em português com acento ("Concluída") não encontrava correspondência.
- **Efeito**: Filtros e termos de busca com "Concluída" não retornavam resultados.

---

## 3. Arquivos Modificados e Soluções Aplicadas

### 3.1. `StatusTrajeto.java`
- Adicionados métodos utilitários defensivos `from(String valor)` e `from(String valor, StatusTrajeto padrao)`:
  - Remove acentos utilizando `java.text.Normalizer`.
  - Remove espaços em branco com `trim()` e converte para caixa alta.
  - Substitui espaços por underline (`_`).
  - Aplica fallback seguro para `StatusTrajeto.EM_ANDAMENTO` caso o valor seja nulo, vazio ou inválido.

```java
public static StatusTrajeto from(String valor) {
    return from(valor, EM_ANDAMENTO);
}

public static StatusTrajeto from(String valor, StatusTrajeto padrao) {
    if (valor == null || valor.isBlank()) {
        return padrao;
    }
    String limpo = java.text.Normalizer.normalize(valor, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .trim()
            .toUpperCase()
            .replace(" ", "_");
    try {
        return StatusTrajeto.valueOf(limpo);
    } catch (IllegalArgumentException e) {
        return padrao;
    }
}
```

---

### 3.2. Camada DAO (`TrajetoDAO`, `ParadaImprevistaDAO`, `InfoEmbarqueDAO`)
1. **Leitura Defensiva (Erro 4)**:
   - Substituído `StatusTrajeto.valueOf(statusStr)` por `StatusTrajeto.from(statusStr)` nos três DAOs:
     - `TrajetoDAO.extrairTrajeto(ResultSet rs)`
     - `ParadaImprevistaDAO.extrairParada(ResultSet rs)`
     - `InfoEmbarqueDAO.extrairInfoEmbarque(ResultSet rs)`
2. **Proteção `NOT NULL` (Erro 3)**:
   - Em `TrajetoDAO.preencherStatement()`:
     ```java
     stmt.setString(4, trajeto.getStatus() != null 
             ? trajeto.getStatus().name() 
             : StatusTrajeto.EM_ANDAMENTO.name());
     ```

---

### 3.3. `TrajetoServlet.java`
1. **Normalização de Busca Textual (Erro 5)**:
   - Criado método estático `normalizar(String str)` com `Normalizer`.
   - Comparação na busca textual agora cruza os valores do enum e o termo digitado de forma agnóstica a acentos e maiúsculas/minúsculas.
2. **Novas Ações no `doPost` (Mapeamento de Métodos do DAO)**:
   - `atualizarStatus`: executa `dao.atualizarStatus(id, novoStatus)`.
   - `inserirSimples`: executa `dao.inserirSimples(...)`.
   - `atualizarGTA`: executa `dao.atualizarGTA(id, gta)`.
   - `atualizarNotaFiscal`: executa `dao.atualizarNotaFiscal(id, nf)`.
   - `atualizarKm`: executa `dao.atualizarKm(id, kmSaida, kmChegada)`.
   - `atualizarCurral`: executa `dao.atualizarCurral(id, curral, curraleiro)`.
3. **Consistência de Encerramento (Erro 2)**:
   - Em `atualizar` e `cadastro`, caso o status seja `CONCLUIDA` e `dataHoraFim` não tenha sido informada, o sistema assume automaticamente o `horarioDesembarque` ou o momento atual (`LocalDateTime.now()`).

---

### 3.4. `editar-trajeto.jsp` (Formulário Oficial)
- Substituído o `<input type="hidden" name="status">` por um seletor visual estilizado:
```html
<div class="doc-row" style="background: rgba(30, 41, 59, 0.04); border-bottom: 2px solid #000; padding: 0.5rem 0.75rem; align-items: center; justify-content: space-between;">
    <div style="display: flex; align-items: center; gap: 0.75rem;">
        <span class="doc-cell-label" style="margin: 0; font-weight: 700; color: #1e293b;">STATUS DO TRANSPORTE:</span>
        <select name="status" class="doc-cell-select" style="font-weight: 700; font-size: 0.9rem; padding: 0.35rem 0.75rem; border-radius: 4px; border: 2px solid #000; background: #fff; cursor: pointer;" required>
            <option value="EM_ANDAMENTO" ${trajetoModel.status == 'EM_ANDAMENTO' || trajetoModel.status == null ? 'selected' : ''}>EM ANDAMENTO</option>
            <option value="CONCLUIDA" ${trajetoModel.status == 'CONCLUIDA' ? 'selected' : ''}>CONCLUÍDA</option>
        </select>
    </div>
    <span style="font-size: 0.8rem; color: #475569; font-style: italic;">
        ${trajetoModel.status == 'CONCLUIDA' ? '✓ Viagem finalizada no frigorífico' : '⚡ Caminhão em rota de transporte'}
    </span>
</div>
```

---

### 3.5. `teste.jsp` e `teste.css`
1. **Modal de Edição (`#modalEdicaoTrajeto`)**:
   - O campo `#editTraj-status` deixou de ser `hidden` e passou a ser um dropdown interativo integrado à função `abrirModalEdicaoTrajeto()`.
2. **Botão Rápido de Status na Tabela de Trajetos**:
   - Cada linha de trajeto agora possui um botão dinâmico:
     - Se o trajeto está `EM_ANDAMENTO`: exibe botão verde **`✓ Concluir`**.
     - Se o trajeto está `CONCLUIDA`: exibe botão amarelo **`⚡ Reabrir`**.
   - Ao clicar, a função JavaScript `atualizarStatusRapido(id, novoStatus)` despacha via `fetch` a atualização direta para o Servlet.
3. **Novas Abas Funcionais no Painel**:
   - **Aba "Cadastro Simples (inserirSimples)"**: Permite cadastrar viagens via método simplificado do DAO.
   - **Aba "Atualizações Específicas (DAO)"**: Painel com 4 sub-formulários dedicados para testar `atualizarGTA()`, `atualizarNotaFiscal()`, `atualizarKm()` e `atualizarCurral()`.
4. **Generalização de `switchTab()`**:
   - A função JavaScript `switchTab()` foi generalizada para suportar qualquer aba criada dinamicamente nas seções.
5. **Estilos CSS (`teste.css`)**:
   - Adicionadas as classes `.btn-sm-success` e `.btn-sm-warning`.

---

## 4. Auditoria de Restrições `NOT NULL` nos Demais DAOs (Diagnóstico Erro 3)

Abaixo está o levantamento de todos os DAOs em relação ao padrão do Erro 3:

| DAO | Colunas `NOT NULL` no Banco | Comportamento Atual no `preencherStatement` | Risco |
| :--- | :--- | :--- | :---: |
| **AdminDAO** | `id_empresa`, `senha`, `email`, `nome` | Se `empresaModel` for nula, executa `stmt.setNull(1, Types.INTEGER)`. | Alto |
| **AnalistaDAO** | `id_empresa`, `data_nascimento`, `cpf`, etc. | Se `empresaModel` for nula, faz `setNull(1)`. Se data for nula, faz `null`. | Alto |
| **CaminhaoDAO** | `id_empresa`, `capacidade_maxima`, placas | Se `empresaModel` for nula, executa `stmt.setNull(1, Types.INTEGER)`. | Alto |
| **MotoristaDAO** | `id_empresa`, `data_nascimento`, etc. | Se `empresaModel` for nula, faz `setNull(1)`. Se data for nula, faz `setNull(3)`. | Alto |
| **ParadaImprevistaDAO** | `id_trajeto`, `data_hora_inicio`, `motivo` | Se `trajetoModel` for nulo, faz `stmt.setNull(1, Types.INTEGER)`. | Alto |
| **InfoEmbarqueDAO** | `id_trajeto`, `nome` | Se `trajetoModel` for nulo, faz `stmt.setNull(2, Types.INTEGER)`. | Alto |
| **PecuaristaDAO** | `data_nascimento`, `cpf`, `nome` | Se `dataNascimento` for nula, faz `stmt.setNull(3, Types.DATE)`. | Alto |
| **PropriedadeDAO** | `id_pecuarista`, `id_endereco`, `nome` | Se `pecuarista` ou `endereco` forem nulos, faz `setNull()`. | Alto |
| **EmpresaDAO** | `nome`, `cnpj` | Envia strings diretamente (necessita validação não-vazia). | Médio |
| **EnderecoDAO** | `cep`, `rua`, `cidade`, `estado`, `pais` | Envia strings diretamente (necessita validação não-vazia). | Médio |

---

## 5. Validação de Compilação

O projeto foi validado utilizando o Maven Wrapper (`mvnw.cmd compile`) apontando para o JDK 25:
- **Fontes compilados**: 38 arquivos Java.
- **Resultado**: `BUILD SUCCESS` (0 erros de compilação).
