# 6. Segurança, Privacidade e Responsabilidade

> **Projeto:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Nível:** 1º Ano — 2º Semestre (Projeto Interdisciplinar)  
> **Data:** 29/10/2026  
> **Documentos relacionados:** [DOCUMENTACAO_VALIDACAO_REGEX.md](DOCUMENTACAO_VALIDACAO_REGEX.md), [TECNOLOGIAS_UTILIZADAS.md](TECNOLOGIAS_UTILIZADAS.md), [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md)

Este documento responde às diretrizes de segurança da informação, privacidade de dados e uso responsável de Inteligência Artificial, considerando estritamente as tecnologias e mecanismos já implementados e ativos no código-fonte e no banco de dados do projeto **Efficientia**.

---

## 6.1. O projeto utiliza ou poderá utilizar dados pessoais?

**Sim.** O sistema **Efficientia** armazena e processa dados pessoais necessários para a operação logística do transporte pecuário e para o controle de acesso de usuários. Conforme mapeado no esquema relacional ([Efficientia.sql](../Efficientia.sql)), os dados pessoais ativos no sistema são:

| Tabela | Dados Pessoais Coletados e Armazenados | Finalidade Operacional |
| :--- | :--- | :--- |
| `pecuarista` | Nome completo, CPF, e-mail, telefone, senha e hash da assinatura | Identificação do produtor rural remetente da carga e autenticação no sistema |
| `motorista` | Nome completo, e-mail, telefone, senha e hash da assinatura | Identificação do condutor responsável pelo frete e autenticação |
| `analista` | Nome completo, CPF, e-mail, telefone, senha e hash da assinatura | Identificação do técnico responsável pela análise da viagem |
| `adm` | Nome completo, e-mail e senha | Gestão administrativa do sistema e controle de acessos |
| `endereco` / `propriedade` | Logradouro, número, bairro, CEP, cidade, estado e nome da fazenda | Localização geográfica da origem e do destino dos animais |
| `trajeto` | Horários de saída/chegada, placas, GTA e Nota Fiscal | Rastreabilidade da viagem atrelada ao motorista e ao produtor |

> [!NOTE]
> O sistema não coleta dados pessoais sensíveis definidos pelo Art. 5º, II da LGPD (origem racial ou étnica, convicção religiosa, dados de saúde ou biométricos). O CPF atua como identificador unívoco de pessoa física exigido para fins fiscais e de emissão de Guia de Trânsito Animal (GTA).

---

## 6.2. Como protegeriam essas informações?

Todas as medidas abaixo estão **efetivamente implementadas e em funcionamento no código-fonte do repositório**:

### 6.2.1. Criptografia e Hashing Robusto de Senhas (`PasswordHasher`)
- **Algoritmo PBKDF2 com HMAC-SHA-256:** As senhas dos usuários nunca são gravadas em texto claro no banco de dados. O sistema utiliza [PasswordHasher.java](../src/main/java/com/efficientia/efficientia/util/PasswordHasher.java) com derivação de chave em **600.000 iterações**.
- **Salt Criptográfico Individual de 16 Bytes:** Cada senha cadastrada gera um salt único e aleatório através de `java.security.SecureRandom`, impedindo ataques baseados em tabelas arco-íris (*rainbow tables*).
- **Comparação em Tempo Constante:** O método de autenticação utiliza `MessageDigest.isEqual()`, eliminando vulnerabilidades de ataque por temporização (*timing attacks* / canal lateral).
- **Migração Automática no Login:** Se uma conta existente ainda possuir senha em formato legado, o sistema detecta a autenticação válida e migra automaticamente para o hash seguro sem interrupção do serviço.

### 6.2.2. Prevenção Contra Injeção de SQL (*SQL Injection*)
- **100% dos DAOs com `PreparedStatement`:** Nenhuma consulta SQL concatena valores enviados pelo usuário. Todas as operações de `INSERT`, `UPDATE`, `DELETE` e `SELECT` utilizam queries parametrizadas com bind explícito (`stmt.setString()`, `stmt.setInt()`), neutralizando qualquer tentativa de injeção de comandos SQL maliciosos.

### 6.2.3. Validação Defensiva de Entrada via REGEX
- **Sanitização Prévia:** A classe [ValidadorRegex.java](../src/main/java/com/efficientia/efficientia/util/ValidadorRegex.java) valida rigorosamente as entradas antes de qualquer persistência:
  - **CPF:** Expressão regular para formato com máscara ou apenas 11 dígitos numéricos (`^(\d{3}\.\d{3}\.\d{3}-\d{2}|\d{11})$`).
  - **Telefone:** Padrão rigoroso para telefonia brasileira com DDD (`^\(?[1-9]{2}\)?\s?9?[0-9]{4}-?[0-9]{4}$`).
  - **E-mail:** Validação sintática completa com expressão regular antes de gravar.
  - **CEP:** Validação de formato de 8 dígitos numéricos.

### 6.2.4. Integridade e Restrições Nativas no Banco de Dados
- **Constraints em PostgreSQL:** O arquivo [Efficientia.sql](../Efficientia.sql) implementa travas estruturais com `CHECK` e `UNIQUE`:
  - `CHECK (email LIKE '%@%')` em todas as tabelas de usuários.
  - `CHECK (length(telefone) >= 10)` para impedir números incompletos.
  - `UNIQUE` em campos identificadores (CPF, e-mail, CNPJ) para impedir cadastros duplicados.

### 6.2.5. Isolamento de Credenciais de Banco (`dotenv-java`)
- As credenciais de conexão com o banco de dados (host, porta, banco, usuário e senha do Aiven PostgreSQL) são carregadas a partir de variáveis de ambiente gerenciadas pela biblioteca `dotenv-java`.
- O arquivo `.env` é expressamente ignorado pelo controle de versão (`.gitignore`), garantindo que nenhuma senha de infraestrutura seja exposta no repositório GitHub. Apenas o modelo seguro `.env.example` é versionado.

### 6.2.6. Autenticação, Proteção de Sessão e Cache (`AuthFilter`)
- **Filtro de Segurança (`AuthFilter`):** Intercepta todas as requisições destinadas à área restrita da aplicação (`/area-restrita/*`).
- **Validação de Sessão Ativa:** Impede acesso direto por URL a páginas JSP sem login prévio autenticado.
- **Expiração Automática:** A sessão HTTP é configurada com tempo limite de inatividade de **30 minutos** (`session.setMaxInactiveInterval(30 * 60)`).
- **Invalidação no Logout:** Ação explícita de encerramento através de `session.invalidate()`.
- **Proteção de Cache (`Cache-Control: no-store`):** O filtro injeta o cabeçalho HTTP que proíbe o navegador de armazenar em cache páginas restritas, evitando que dados pessoais sejam visualizados ao pressionar o botão "Voltar" após o logout.

### 6.2.7. Tratamento Seguro de Erros e Exceções
- A camada utilitária `WebErroUtil` e o manipulador `TratadorExcecaoBanco` capturam violações de integridade relacional (`SQLState 23505`) e retornam mensagens amigáveis ao usuário via parâmetros seguros de redirecionamento, sem nunca expor traces de pilha (*stack traces*), nomes de tabelas ou estruturas internas do servidor no navegador.

---

## 6.3. Existe preocupação com LGPD?

**Sim.** A arquitetura do sistema **Efficientia** foi desenhada incorporando diretamente princípios da **Lei Geral de Proteção de Dados (Lei nº 13.709/2018 - LGPD)**:

1. **Princípio da Finalidade (Art. 6º, I):** A coleta dos dados de pecuaristas, motoristas e analistas restringe-se estritamente à execução do contrato de transporte, comprovação sanitária (GTA) e cumprimento de obrigações fiscais da carga pecuária.
2. **Princípio da Necessidade e Minimização (Art. 6º, III):** O sistema não realiza coleta de dados excedentes ou desnecessários. Campos como religião, gênero ou estado civil não existem no modelo de dados.
3. **Princípio da Segurança (Art. 6º, VII):** Implementação prática de medidas técnicas de proteção da informação:
   - Hashing com PBKDF2 e salts exclusivos para todas as senhas;
   - Sessões web com controle de inatividade e destruição completa no logout;
   - Bloqueio de cache local para dados de formulários cadastrais;
   - Proteção de queries via JDBC parametrizado contra vazamento por injeção.
4. **Princípio da Prevenção (Art. 6º, VIII):** Aplicação de validações defensivas em múltiplas camadas (REGEX na entrada do Controller e constraints `CHECK` no PostgreSQL), evitando corrupção de dados ou gravação de informações inconsistentes.
5. **Privacidade desde a Concepção (*Privacy by Design* - Art. 46):** A proteção criptográfica e as validações ocorrem nas camadas de utilitários e filtros (`util` e `filter`) antes mesmo que os dados alcancem a camada de persistência.

---

## 6.4. Existem riscos relacionados ao uso inadequado da IA na solução proposta?

O projeto **Efficientia** utiliza a Inteligência Artificial **exclusivamente como ferramenta de auxílio ao desenvolvimento de software (Pair Programming, geração de testes e auxílio na documentação técnica)**, não havendo modelos de IA rodando como agentes autônomos tomadores de decisão dentro do sistema web final. 

Portanto, os riscos e as respectivas mitigações implementadas concentram-se no ciclo de desenvolvimento:

| Risco Mapeado | Descrição do Risco | Mitigação Implementada no Projeto |
| :--- | :--- | :--- |
| **Vazamento de Segredos e Dados em Prompts** | Enviar credenciais de banco, chaves privadas ou dados de usuários reais para modelos de IA públicos. | **Política de Zero Exposição:** O arquivo `.env` nunca foi compartilhado com modelos de IA. Todo o desenvolvimento e validação utiliza exclusivamente dados fictícios estruturados na carga inicial de testes (`Efficientia.sql`). |
| **Geração de Código Inseguro ou Vulnerável** | Ferramentas de IA sugerirem códigos desatualizados, concatenação vulnerável de SQL ou algoritmos de hash fracos (como MD5 ou SHA-1). | **Auditoria de Código Humana e Testes:** Nenhuma sugestão é adotada sem validação. Foi padronizado o uso de `PreparedStatement` e algoritmos robustos recomendados pelo NIST (PBKDF2-HMAC-SHA256). A integridade do código é validada por testes unitários com JUnit 5. |
| **Alucinação Técnica em Documentação** | A IA afirmar capacidades inexistentes ou documentar assinaturas de métodos de forma equivocada. | **Protocolo de Validação em 3 Etapas:** Conforme estabelecido em [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md), todo conteúdo gerado por IA passa por validação humana de **Sintaxe**, **Conteúdo** (checagem direta contra o código-fonte) e **Organização**. |
| **Dependência Acrítica de Código** | Aceitação passiva de sugestões sem compreensão técnica dos desenvolvedores. | **Metodologia de Pair Programming:** A IA atua como assistente; as decisões arquiteturais, estrutura de pacotes MVC e regras de negócio de bem-estar animal foram definidas e revisadas pela equipe técnica. |
