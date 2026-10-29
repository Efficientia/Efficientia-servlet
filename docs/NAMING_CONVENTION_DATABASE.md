# Convenção de Nomenclatura e Padrões de Banco de Dados — Projeto Efficientia

> **SGBD Alvo:** PostgreSQL 14+  
> **Versão do Documento:** 1.0  
> **Status:** Aprovado  
> **Arquivo de Referência DDL:** `Efficientia.sql` (schema local)

---

## 1. Visão Geral

Este documento define os padrões obrigatórios de **nomenclatura**, **tipagem de dados**, **definição de constraints** e **boas práticas de modelagem relacional** para o banco de dados do projeto **Efficientia**.

O objetivo desta padronização é garantir integridade semântica, facilidade de manutenção no mapeamento objeto-relacional (JDBC/DAO) e uniformidade arquitetural em todo o ciclo de desenvolvimento.

---

## 2. Regras Gerais de Nomenclatura

Todas as identificações no banco de dados devem seguir rigorosamente o padrão **`snake_case` em letras minúsculas**, sem acentuação, espaços ou caracteres especiais.

| Elemento | Padrão Sintático | Formato Gramatical | Exemplo Correto | Exemplo Incorreto |
| :--- | :--- | :--- | :--- | :--- |
| **Tabelas (Entidades)** | `snake_case` minúsculo | Substantivo no **singular** | `motorista`, `pecuarista`, `trajeto` | `motoristas`, `tb_motorista`, `Motorista` |
| **Tabelas Associativas (N:N)** | `[entidade1]_[entidade2]` | Nomes das entidades no singular | `caminhao_motorista` | `caminhoes_motoristas`, `rel_caminhao` |
| **Chave Primária (PK)** | Sempre `id` | Nome fixo simples | `id` | `id_motorista`, `motorista_id`, `cod_motorista` |
| **Chave Estrangeira (FK)** | `id_[tabela_referenciada]` | Prefixo `id_` + nome da tabela singular | `id_empresa`, `id_trajeto` | `empresa_id`, `fk_empresa`, `idEmpresa` |
| **Colunas Comuns** | `snake_case` minúsculo | Descritivo e contextual | `data_nascimento`, `nome` | `dtNasc`, `nome_completo_motorista` |
| **Flags / Booleanos** | `snake_case` minúsculo | Adjetivo ou particípio de estado | `ativo`, `finalizado` | `is_ativo`, `fl_ativo`, `status` (se bool) |
| **Tipos ENUM Customizados** | `snake_case` minúsculo | Prefixo descritivo (ex: `status_*`) | `status_trajeto`, `status_analise` | `StatusTrajeto`, `enum_status` |
| **Valores de ENUM** | `UPPER_SNAKE_CASE` | Texto representativo do estado | `'EM_ANDAMENTO'`, `'CONCLUIDA'` | `'em_andamento'`, `'Concluida'` |
| **Constraints PK** | `[tabela]_pkey` | Padrão nativo do PostgreSQL | `motorista_pkey` | `pk_motorista` |
| **Constraints FK** | `fk_[origem]_[destino]` | Prefixo `fk_` + tabelas | `fk_trajeto_motorista` | `FK_TRAJETO_1`, `fk_mot` |
| **Constraints CHECK** | `chk_[regra]` | Prefixo `chk_` + contexto | `chk_periodo_trajeto` | `check_data`, `valida_periodo` |
| **Índices Secundários** | `idx_[tabela]_[coluna(s)]` | Prefixo `idx_` + tabela + coluna(s) | `idx_trajeto_status` | `index1`, `trajeto_status_idx` |

---

## 3. Padrões de Colunas e Campos Não Óbvios

Definições de tipagem e formatação consolidadas para campos sensíveis e de domínio específico:

### 3.1. Documentos e Identificadores Civis/Fiscais

* Todos os identificadores oficiais são armazenados **estritamente numéricos (sem pontos, traços ou barras)**, garantindo menor uso de armazenamento, facilidade de indexação e validações via regex consistentes.

| Campo | Tipo PostgreSQL | Validação (CHECK) | Exemplo | Observações |
| :--- | :--- | :--- | :--- | :--- |
| **CPF** | `CHAR(11)` | `CHECK (length(cpf) = 11)` | `'12345678901'` | Apenas dígitos. `UNIQUE NOT NULL`. |
| **CNPJ** | `CHAR(14)` | `CHECK (length(cnpj) = 14)` | `'12345678000101'` | Apenas dígitos. `UNIQUE NOT NULL`. |
| **CEP** | `CHAR(8)` | `CHECK (length(cep) = 8)` | `'01234567'` | Apenas dígitos. Sem hífen. |
| **Telefone** | `VARCHAR(11)` | `CHECK (length(telefone) >= 10)` | `'11987654321'` ou `'1133334444'` | Apenas dígitos com DDD (10 dígitos fixo, 11 dígitos celular). |
| **Nota Fiscal (NF-e)** | `VARCHAR(44)` | `CHECK (length(numero_nota_fiscal) = 44)` | `'352107...44digitos'` | Chave de acesso eletrônica da NF-e (44 dígitos). |
| **Guia Trânsito Animal (GTA)**| `VARCHAR(20)` | — | `'GTA-MG-10293'` | Código alfanumérico único da GTA. |
| **Placas de Veículos** | `VARCHAR(10)` | — | `'BRA1A12'` ou `'ABC1234'` | Placa Carreta e Placa Cavalo (formato Mercosul ou cinza). |
| **Código Interno** | `VARCHAR(20)` | — | `'EMP001'`, `'ANA-002'` | Identificadores de login e vínculo rápido. |

### 3.2. Datas e Horários

* Não utilizar abreviações genéricas como `dt`. Utilizar prefixos semânticos:
  * **`data_`**: Para valores de calendário sem componente horário (`DATE`).
  * **`data_hora_`** ou **`horario_`**: Para momentos precisos no tempo (`TIMESTAMP` ou `TIMESTAMP WITH TIME ZONE`).

| Campo | Tipo | Descrição |
| :--- | :--- | :--- |
| `data_nascimento` | `DATE NOT NULL` | Data de nascimento do usuário. |
| `data_inicio`, `data_fim` | `DATE` | Vigência ou período de alocação de frota. |
| `data_hora_inicio`, `data_hora_fim` | `TIMESTAMP` | Início e conclusão de trajetos e paradas. |
| `horario_embarque`, `horario_desembarque` | `TIMESTAMP` | Momentos de carga e descarga de animais. |
| `data_analise` | `TIMESTAMP NOT NULL` | Momento da emissão da avaliação pelo analista. |

### 3.3. Quantidades e Métricas

* Campos quantitativos contáveis devem utilizar o prefixo **`qtd_`** e validação defensiva contra números negativos:
  * `qtd_macho INTEGER DEFAULT 0 CHECK (qtd_macho >= 0)`
  * `qtd_femea INTEGER DEFAULT 0 CHECK (qtd_femea >= 0)`
  * `qtd_marruco INTEGER DEFAULT 0 CHECK (qtd_marruco >= 0)`
* Métricas de hodômetro e capacidade:
  * `km_saida INTEGER CHECK (km_saida >= 0)`
  * `km_chegada INTEGER CHECK (km_chegada >= km_saida)`
  * `capacidade_maxima INTEGER NOT NULL CHECK (capacidade_maxima > 0)`

### 3.4. Credenciais, Autenticação e Segurança

| Campo | Tipo | Descrição |
| :--- | :--- | :--- |
| `email` | `VARCHAR(254) UNIQUE NOT NULL` | Tamanho máximo conforme RFC 5321. `CHECK (email LIKE '%@%')`. |
| `senha` | `VARCHAR(100) NOT NULL` | Armazenamento de hash de senha criptografada (ex: BCrypt / PBKDF2). |
| `assinatura` | `VARCHAR(128)` | Hash ou token de verificação da assinatura eletrônica do responsável. |

### 3.5. Localização e Endereço

| Campo | Tipo | Descrição |
| :--- | :--- | :--- |
| `cep` | `CHAR(8) NOT NULL` | 8 dígitos numéricos. |
| `estado` | `CHAR(2) NOT NULL` | Sigla da Unidade Federativa em 2 caracteres maiúsculos (ex: `'SP'`, `'MG'`). |
| `cidade` | `VARCHAR(60) NOT NULL` | Nome do município. |
| `rua` | `VARCHAR(120) NOT NULL` | Logradouro. |
| `numero` | `VARCHAR(20) NOT NULL` | Número ou identificação (aceita `'S/N'`). |
| `complemento` | `VARCHAR(100)` | Informação adicional de endereço. |
| `tipo` | `VARCHAR(30) NOT NULL` | Tipo do imóvel (ex: `'FAZENDA'`, `'FRIGORIFICO'`, `'SEDE'`). |

---

## 4. Estrutura de Integridade Referencial e Constraints

### 4.1. Chaves Primárias (PK)
Toda tabela deve ter como chave primária uma coluna única chamada `id`, sequencial e auto-gerada:
```sql
CREATE TABLE exemplo (
    id SERIAL PRIMARY KEY
);
```

### 4.2. Chaves Estrangeiras (FK)
As restrições de chave estrangeira devem ser declaradas preferencialmente via blocos `ALTER TABLE` ou ao final do `CREATE TABLE`, garantindo rastreabilidade e suporte a ordenação de carga:
* **Nomenclatura:** `fk_[tabela_origem]_[tabela_destino]`
* **Modo de Verificação:** `DEFERRABLE INITIALLY IMMEDIATE` para flexibilidade em transações complexas.

Exemplo:
```sql
ALTER TABLE trajeto 
  ADD CONSTRAINT fk_trajeto_motorista 
  FOREIGN KEY (id_motorista) REFERENCES motorista (id) 
  DEFERRABLE INITIALLY IMMEDIATE;
```

### 4.3. Constraints de Período e Consistência (CHECK)
Regras relacionando múltiplos campos de uma mesma tabela devem receber nome formal iniciando com `chk_`:
```sql
CONSTRAINT chk_periodo_trajeto CHECK (data_hora_fim > data_hora_inicio)
CONSTRAINT chk_periodo_parada  CHECK (data_hora_fim > data_hora_inicio)
```

---

## 5. Mapeamento Direto: Banco de Dados ↔ Java (Efficientia)

| Banco de Dados (PostgreSQL) | Camada Model (Java) | Camada DAO (JDBC) |
| :--- | :--- | :--- |
| `id SERIAL PRIMARY KEY` | `private int id;` | `rs.getInt("id")` / `stmt.setInt(...)` |
| `CHAR(11)` / `VARCHAR(n)` | `private String [campo];` | `rs.getString("...")` / `stmt.setString(...)` |
| `INTEGER` (not null) | `private int [campo];` | `rs.getInt("...")` / `stmt.setInt(...)` |
| `INTEGER` (nullable / FK) | Referência a outro Model ou `Integer` | `stmt.setNull(..., Types.INTEGER)` se nulo |
| `BOOLEAN` | `private boolean [campo];` | `rs.getBoolean("...")` / `stmt.setBoolean(...)` |
| `DATE` | `private LocalDate [campo];` | `rs.getDate("...").toLocalDate()` |
| `TIMESTAMP` | `private LocalDateTime [campo];` | `rs.getTimestamp("...").toLocalDateTime()` |
| `ENUM (status_*)` | `private Status[X] status;` | `Status[X].valueOf(rs.getString("..."))` |

---

## 6. Checklist de Validação para Criação/Alteração de Tabelas

Antes de aprovar ou commitar modificações de banco no script SQL:

- [ ] O nome da tabela está no **singular** e em **`snake_case`**?
- [ ] A chave primária se chama exclusivamente **`id`** (`SERIAL PRIMARY KEY`)?
- [ ] As chaves estrangeiras utilizam a convenção **`id_[tabela_destino]`**?
- [ ] A constraint da FK segue o padrão **`fk_[origem]_[destino]`**?
- [ ] Campos de documento (CPF, CNPJ, CEP) estão definidos como **números puros (`CHAR(11)`, `CHAR(14)`, `CHAR(8)`)**?
- [ ] O campo de telefone está padronizado como **`VARCHAR(11)`** (apenas dígitos com DDD)?
- [ ] Campos com restrição de preenchimento possuem `CHECK` de tamanho ou conteúdo condizente?
- [ ] As colunas temporais usam os prefixos corretos (`data_` para DATE, `data_hora_`/`horario_` para TIMESTAMP)?
- [ ] Nomes de status/estados utilizam tipos ENUM com valores em `UPPER_SNAKE_CASE`?
