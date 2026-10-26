# Registro de Processo: Padronização e Naming Convention de Banco de Dados com IA

> **Projeto:** Efficientia — Gestão de Transporte e Bem-Estar Animal  
> **Arquivo Gerado:** `EXEMPLO_IA_BD.md`  
> **Data:** 14/10/2026  
> **Artefatos Produzidos/Atualizados:**
> - [NAMING_CONVENTION_DATABASE.md](NAMING_CONVENTION_DATABASE.md)
> - [Efficientia.sql](../Efficientia.sql)
> - Modelo DBML para **dbdiagram.io**

---

## 1. Contexto e Solicitação Inicial

O usuário solicitou o estabelecimento de um **Naming Convention** para o banco de dados PostgreSQL do projeto **Efficientia**, com as seguintes premissas:
1. Basear-se nos padrões de nomenclatura e arquitetura já existentes no projeto.
2. Desconsiderar os dados brutos de *dataload* existentes temporariamente durante a análise estrutural.
3. Realizar perguntas interativas de alinhamento prévio sobre campos de domínio sensíveis e não óbvios (como **CPF**, **Telefone**, **CNPJ** e **CEP**) antes de gerar o documento final.

---

## 2. Etapa de Entrevista e Alinhamento Técnico

Antes da redação das diretrizes, a IA identificou ambiguidades entre o script SQL original e a camada de validação do projeto (ex.: discrepâncias entre `CHAR` e `VARCHAR`, presença ou ausência de máscaras de formatação), submetendo ao desenvolvedor as seguintes decisões:

### Perguntas e Respostas de Definição

| Campo | Pergunta Submetida | Alternativa Selecionada pelo Desenvolvedor | Padrão Consolidado |
| :--- | :--- | :--- | :--- |
| **CPF** | Qual padrão deve ser adotado para o armazenamento de CPF? | `(Recomendado) Apenas dígitos numéricos sem pontuação (CHAR(11))` | `CHAR(11) CHECK (length(cpf) = 11)` |
| **Telefone** | Qual formato e tipo devem ser padronizados para o campo Telefone? | `(Recomendado) Apenas dígitos com DDD, ex: '11987654321' (VARCHAR(11))` | `VARCHAR(11) CHECK (length(telefone) >= 10)` |
| **CNPJ** | Qual padrão deve ser adotado para o campo CNPJ? | `(Recomendado) Apenas dígitos numéricos sem pontuação (CHAR(14))` | `CHAR(14) CHECK (length(cnpj) = 14)` |
| **CEP** | Qual padrão deve ser adotado para o campo CEP de endereço? | `(Recomendado) Apenas dígitos numéricos, ex: '01234567' (CHAR(8))` | `CHAR(8) CHECK (length(cep) = 8)` |

---

## 3. Síntese do Naming Convention Elaborado

Com base nas respostas e na análise do ecossistema Java/PostgreSQL do projeto, foi criado o arquivo oficial **`NAMING_CONVENTION_DATABASE.md`**:

### 3.1. Regras Principais de Nomenclatura
- **Tabelas:** `snake_case` estritamente no **singular** (`motorista`, `pecuarista`, `trajeto`, `empresa`).
- **Tabelas Associativas (N:N):** `[entidade1]_[entidade2]` (`caminhao_motorista`).
- **Chave Primária (PK):** Sempre a coluna simples `id` (`SERIAL PRIMARY KEY`).
- **Chaves Estrangeiras (FK):** Padrão de coluna `id_[tabela_destino]` e constraints no formato `fk_[origem]_[destino]` com `DEFERRABLE INITIALLY IMMEDIATE`.
- **Enums:** Tipos em `snake_case` (`status_trajeto`, `status_analise`) e valores em `UPPER_SNAKE_CASE` (`'EM_ANDAMENTO'`, `'CONCLUIDA'`).
- **Campos Temporais:** Prefixos semânticos `data_` para `DATE` e `data_hora_` ou `horario_` para `TIMESTAMP`.
- **Campos Quantitativos:** Prefixo `qtd_` com `INTEGER DEFAULT 0 CHECK (qtd_... >= 0)`.

---

## 4. Refatoração do Script SQL (`Efficientia.sql`)

Após a consolidação dos padrões, o arquivo SQL do projeto foi atualizado tanto no **DDL** quanto no **Dataload (Carga Inicial)**:

### 4.1. Ajustes no DDL
1. `empresa.cnpj`: Padronizado de `VARCHAR(14)` para `CHAR(14)`.
2. `motorista.telefone`, `pecuarista.telefone`, `analista.telefone`: Ajustados de `VARCHAR(20)` para `VARCHAR(11)`.
3. `analista.cpf`: Padronizado de `VARCHAR(11)` para `CHAR(11)`.
4. `endereco.cep`: Ajustado de `VARCHAR(9)` para `CHAR(8) NOT NULL CHECK (length(cep) = 8)`.
5. `endereco.estado`: Ajustado de `VARCHAR(2)` para `CHAR(2)`.

### 4.2. Ajustes no Dataload
- **Nota Fiscal Eletrônica (`numero_nota_fiscal`):** Substituição de códigos fictícios curtos (`'NF000001'`) por chaves oficiais de acesso de 44 dígitos numéricos (ex.: `'35260712345678000101550010000000011000000015'`).
- **Guia de Trânsito Animal (`numero_gta`):** Padronização dos identificadores com sigla de estado (ex.: `'GTA-MS-100001'`).
- **Hashes de Assinatura:** Uniformização de `'ass_cur_1'` para o padrão de hash `'hash_ass_cur_01'`, alinhado aos hashes de motorista e analista.

---

## 5. Atualização e Correção do Esquema DBML (dbdiagram.io)

O usuário submeteu o rascunho de especificação DBML para diagramação no **dbdiagram.io**. A IA revisou o código e realizou as seguintes correções críticas:

1. **Correção de Chave Estrangeira em `trajeto`:**  
   - O campo `id_pecuarista` estava incorretamente tipado como `varchar [not null]`.  
   - Foi corrigido para `integer [not null]`, garantindo a integridade referencial com a tabela `pecuarista(id)`.
2. **Precisão de Tipagens e Dimensões:**
   - Adicionadas as precisões de tamanho exatas (`char(14)`, `char(11)`, `char(8)`, `char(2)`, `varchar(150)`, `varchar(254)`, `varchar(44)`, `varchar(128)`).
3. **Nullability de Assinaturas:**
   - Removida a obrigatoriedade `[not null]` de campos de assinatura em viagens em andamento ou registros pendentes de submissão.

### Código DBML Final Homologado

```dbml
// ==========================================
// 1. ENUMS
// ==========================================

enum status_trajeto {
  EM_ANDAMENTO
  CONCLUIDA
}

enum status_analise {
  EM_ANDAMENTO
  CONCLUIDA
}

// ==========================================
// 2. TABELAS
// ==========================================

Table empresa {
  id integer [primary key, increment]
  nome varchar(150) [not null, unique]
  cnpj char(14) [not null, unique]
  codigo varchar(20) [unique]
}

Table motorista {
  id integer [primary key, increment]
  id_empresa integer [not null]
  assinatura varchar(128)
  nome varchar(150) [not null]
  data_nascimento date [not null]
  senha varchar(100) [not null]
  email varchar(254) [not null, unique]
  telefone varchar(11) [not null]
}

Table caminhao {
  id integer [primary key, increment]
  id_empresa integer [not null]
  capacidade_maxima integer [not null]
  placa_carreta varchar(10) [not null, unique]
  placa_cavalo varchar(10) [not null, unique]
}

Table caminhao_motorista {
  id integer [primary key, increment]
  id_motorista integer [not null]
  id_caminhao integer [not null]
  data_inicio date
  data_fim date
  ativo boolean [not null]
}

Table pecuarista {
  id integer [primary key, increment]
  cpf char(11) [not null, unique]
  assinatura varchar(128)
  data_nascimento date [not null]
  nome varchar(150) [not null]
  senha varchar(100) [not null]
  email varchar(254) [not null, unique]
  telefone varchar(11) [not null]
}

Table trajeto {
  id integer [primary key, increment]
  id_motorista integer [not null]
  id_caminhao integer [not null]
  id_pecuarista integer [not null]

  status status_trajeto [not null]
  data_hora_inicio timestamp
  data_hora_fim timestamp
  km_saida integer
  km_chegada integer
  
  numero_gta varchar(20) [not null, unique]
  numero_nota_fiscal varchar(44) [not null, unique]
  horario_embarque timestamp [not null]
  
  qtd_macho integer [default: 0]
  qtd_femea integer [default: 0]
  qtd_marruco integer [default: 0]
  
  horario_desembarque timestamp 
  numero_curral varchar(20)
  nome_curraleiro varchar(150)
  nome_manobrista varchar(150)
  assinatura_curraleiro varchar(128)
  assinatura_manobrista varchar(128)
  assinatura_motorista varchar(128)
}

Table info_embarque {
  id integer [primary key, increment]
  id_trajeto integer [not null, unique]
  nome varchar(100) [not null]
}

Table parada_imprevista {
  id integer [primary key, increment]
  id_trajeto integer [not null]
  data_hora_inicio timestamp [not null]
  data_hora_fim timestamp
  motivo varchar(100) [not null]
  observacao varchar(500)
}

Table analista {
  id integer [primary key, increment]
  id_empresa integer [not null]
  cpf char(11) [not null, unique]
  assinatura varchar(128)
  codigo varchar(20) [not null, unique]
  data_nascimento date [not null]
  nome varchar(150) [not null]
  senha varchar(100) [not null]
  email varchar(254) [not null, unique]
  telefone varchar(11) [not null]
}

Table analise {
  id integer [primary key, increment]
  id_analista integer [not null]
  id_trajeto integer [not null]
  data_analise timestamp [not null]
  status_analise status_analise [not null]
  observacao varchar(500)
}

Table endereco {
  id integer [primary key, increment]
  cep char(8) [not null]
  tipo varchar(30) [not null]
  numero varchar(20) [not null]
  rua varchar(120) [not null]
  cidade varchar(60) [not null]
  estado char(2) [not null]
  pais varchar(50) [not null]
  complemento varchar(100)
}

Table propriedade {
  id integer [primary key, increment]
  id_pecuarista integer [not null]
  id_endereco integer [not null, unique]
  nome varchar(120) [not null]
}

Table adm {
  id integer [primary key, increment]
  id_empresa integer [not null]
  senha varchar(100) [not null]
  email varchar(254) [not null, unique]
  nome varchar(150) [not null]
}

// ==========================================
// 3. RELACIONAMENTOS (REFS)
// ==========================================

Ref: caminhao.id_empresa > empresa.id
Ref: motorista.id_empresa > empresa.id
Ref: analista.id_empresa > empresa.id
Ref: adm.id_empresa > empresa.id

Ref: caminhao_motorista.id_motorista > motorista.id 
Ref: caminhao_motorista.id_caminhao > caminhao.id

Ref: trajeto.id_motorista > motorista.id
Ref: trajeto.id_caminhao > caminhao.id
Ref: trajeto.id_pecuarista > pecuarista.id

Ref: info_embarque.id_trajeto - trajeto.id
Ref: parada_imprevista.id_trajeto > trajeto.id

Ref: analise.id_analista > analista.id
Ref: analise.id_trajeto > trajeto.id

Ref: propriedade.id_pecuarista > pecuarista.id
Ref: propriedade.id_endereco - endereco.id
```

---

## 6. Conclusão

O fluxo de colaboração permitiu alinhar decisões de engenharia de software e banco de dados de ponta a ponta:
1. **Semântica:** Decisões conscientes sobre armazenamento de identificadores sem máscara (`CHAR(11)`, `CHAR(14)`, `CHAR(8)`).
2. **Qualidade:** Prevenção de inconsistências relacionais (ex.: FK de pecuarista corrigida).
3. **Documentação Viva:** Alinhamento contínuo entre o documento normativo (`NAMING_CONVENTION_DATABASE.md`), o script de deploy (`Efficientia.sql`) e a ferramenta visual de modelagem (`dbdiagram.io`).
