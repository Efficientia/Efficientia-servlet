# Plano de Implementação: Padronização de Exceções nos DAOs Auxiliares (Lógica - M3)

> **Projeto:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Disciplina:** Lógica de Programação (Requisito M3: Tratamento de Exceções) e POO  
> **Branch:** `feat/POO/EFFICIENTI22/padroniza-excecoes-daos-auxiliares`  
> **Documento de Referência:** [SPEC_PADRONIZACAO_EXCECOES_DAOS_AUXILIARES.md](SPEC_PADRONIZACAO_EXCECOES_DAOS_AUXILIARES.md)  
> **Data:** 29/10/2026  

---

## 1. Visão Geral e Objetivos

O objetivo deste plano é executar passo a passo a padronização do tratamento de exceções em todos os DAOs auxiliares que ainda não utilizam o utilitário [`TratadorExcecaoBanco`](../src/main/java/com/efficientia/efficientia/util/TratadorExcecaoBanco.java).

Ao final da execução deste plano:
- `PropriedadeDAO`, `EnderecoDAO`, `InfoEmbarqueDAO`, `ParadaImprevistaDAO` e os métodos complementares de `AnalistaDAO` estarão 100% integrados à arquitetura de exceções de domínio tipadas.
- Uma suíte de testes automatizados com JUnit 5 validará o comportamento esperado tanto para SQLState `23505` (duplicidade) quanto para outras falhas de banco de dados.
- O build do projeto passará com `0 failures` e `0 errors` via Maven Wrapper.

---

## 2. Cronograma de Execução em Fases

```
┌────────────────────────────────────────────────────────────────────────┐
│ FASE 1: Preparação do Ambiente e Branch                                │
│ [x] Criação da branch: feat/POO/EFFICIENTI22/padroniza-excecoes-daos  │
│ [x] Redação da Especificação Técnica (Spec)                            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ FASE 2: Implementação nos DAOs com UNIQUE constraints explícitas       │
│ [ ] PropriedadeDAO (UNIQUE id_endereco)                                │
│ [ ] InfoEmbarqueDAO (UNIQUE id_trajeto)                                │
│ [ ] AnalistaDAO (atualizarNome, atualizarSenha, atualizarEmpresa)      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ FASE 3: Implementação nos DAOs de Infraestrutura Geográfica/Logs       │
│ [ ] EnderecoDAO (inserir, atualizar, inserirSimples, atualizações)     │
│ [ ] ParadaImprevistaDAO (inserir, atualizar)                           │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ FASE 4: Testes Automatizados com JUnit 5                               │
│ [ ] Criar classe de teste DaosAuxiliaresExcecaoTest.java               │
│ [ ] Testar captura de duplicidade (SQLSTATE 23505)                     │
│ [ ] Testar resiliência com falhas genéricas de conexão                 │
│ [ ] Executar suíte completa: .\mvnw.cmd test                           │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ FASE 5: Commit, Auditoria e Checklist de Entrega                       │
│ [ ] Git add e commit semântico padronizado                             │
│ [ ] Atualização do checklist de entregas (Lógica M3)                   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 3. Checklist Técnico de Alterações por Arquivo

### 3.1. `PropriedadeDAO.java`
- [ ] Importar `com.efficientia.efficientia.util.TratadorExcecaoBanco;`
- [ ] Método `inserir(PropriedadeModel)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade");`
- [ ] Método `atualizar(PropriedadeModel, int)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade");`
- [ ] Método `inserirSimples(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade");`
- [ ] Método `atualizarNome(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade", "Nome", novoNome);`
- [ ] Método `atualizarPecuarista(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade", "Pecuarista Vinculado", String.valueOf(idPecuarista));`
- [ ] Método `atualizarEndereco(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Propriedade", "Endereço Vinculado", String.valueOf(idEndereco));`

### 3.2. `InfoEmbarqueDAO.java`
- [ ] Importar `com.efficientia.efficientia.util.TratadorExcecaoBanco;`
- [ ] Método `inserir(InfoEmbarqueModel)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "InfoEmbarque");`
- [ ] Método `atualizar(InfoEmbarqueModel, int)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "InfoEmbarque");`

### 3.3. `EnderecoDAO.java`
- [ ] Importar `com.efficientia.efficientia.util.TratadorExcecaoBanco;`
- [ ] Método `inserir(EnderecoModel)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço");`
- [ ] Método `atualizar(EnderecoModel, int)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço");`
- [ ] Método `inserirSimples(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço");`
- [ ] Método `atualizarCep(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "CEP", novoCep);`
- [ ] Método `atualizarCidade(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Cidade", novaCidade);`
- [ ] Método `atualizarBairro(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Bairro", novoBairro);`
- [ ] Método `atualizarRua(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Rua", novaRua);`
- [ ] Método `atualizarNumero(...)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Endereço", "Número", String.valueOf(novoNumero));`

### 3.4. `ParadaImprevistaDAO.java`
- [ ] Importar `com.efficientia.efficientia.util.TratadorExcecaoBanco;`
- [ ] Método `inserir(ParadaImprevistaModel)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "ParadaImprevista");`
- [ ] Método `atualizar(ParadaImprevistaModel, int)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "ParadaImprevista");`

### 3.5. `AnalistaDAO.java`
- [ ] Método `atualizarNome(int id, String novoNome)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Analista", "Nome", novoNome);`
- [ ] Método `atualizarSenha(int id, String novaSenha)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Analista");`
- [ ] Método `atualizarEmpresa(int id, Integer idEmpresa)`: adicionar `TratadorExcecaoBanco.tratarSeDuplicado(e, "Analista", "Empresa", String.valueOf(idEmpresa));`

---

## 4. Matriz de Riscos e Mitigações

| Risco Identificado | Severidade | Probabilidade | Mitigação Arquitetural |
| :--- | :---: | :---: | :--- |
| Quebra de assinatura pública dos DAOs (`throws`) | Alta | Baixa | `TratadorExcecaoBanco` lança subclasses de `RuntimeException`. A assinatura `public boolean` permanece inalterada. |
| Exceções disparadas em cenários normais de falha de rede | Média | Nula | O `TratadorExcecaoBanco` filtra estritamente pelo SQLState `23505` (`unique_violation`). Falhas de conexão continuam caindo no log e retornando `false`. |
| Regressão nos testes unitários existentes | Alta | Baixa | Execução automatizada com Maven (`mvn test`) antes de consolidar o commit. |

---

## 5. Critérios de Conclusão e Entrega
1. 100% dos métodos de inserção e atualização de todos os 5 DAOs auxiliares chamando `TratadorExcecaoBanco.tratarSeDuplicado`.
2. Criação do arquivo de teste `src/test/java/com/efficientia/efficientia/dao/DaosAuxiliaresExcecaoTest.java`.
3. Validação com `mvn test` passando 100% dos testes.
