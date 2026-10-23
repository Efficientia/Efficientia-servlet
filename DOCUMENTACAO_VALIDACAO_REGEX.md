# Documentação Técnica: Validação e Tratamento de Dados com Expressões Regulares (REGEX)

Este documento apresenta a especificação técnica detalhada, a arquitetura de software e a relação de todas as classes do sistema **Efficientia** que utilizam **Expressões Regulares (REGEX)** para validar, higienizar e formatar os dados inseridos pelos usuários.

---

## 1. Visão Geral e Contexto Interdisciplinar

No sistema **Efficientia**, as expressões regulares são aplicadas principalmente na camada de controle (Servlets) e utilitários (camada Util), com suporte defensivo na camada de persistência (DAOs). A abordagem adotada visa:

1. **Validação Estrita no Backend:** Toda entrada submetida via requisições HTTP (formulários de cadastro, edição e login) passa por validação no servidor Java antes de atingir o banco de dados PostgreSQL.
2. **Prevenção de Inconsistências e Falhas Relacionais:** Cumprimento rigoroso das restrições de integridade do banco de dados declaradas no arquivo de schema `Efficientia.sql` (por exemplo: `CHECK (length(cpf) = 11)`, `CHECK (length(telefone) >= 10)`, `CHECK (email LIKE '%@%')` e unicidade).
3. **Eficiência de Processamento:** Expressões regulares são pré-compiladas em constantes estáticas (`java.util.regex.Pattern`) para eliminar o custo de recompilação a cada requisição HTTP sob o Apache Tomcat.
4. **Atendimento aos Requisitos Acadêmicos:**
   * **Sistemas Operacionais (Requisito Extra):** Validação por REGEX para entradas de CPF e Celular/Telefone (sem DDI `+55`).
   * **Lógica de Programação (Requisito Extra E1):** Validação defensiva de dados de entrada, código modular e padronizado.
   * **Banco de Dados 1 & POO:** Higienização de dados antes do `INSERT`/`UPDATE` e separação em camadas segundo o padrão MVC.

---

## 2. Resumo das Expressões Regulares Centrais (`ValidadorRegex.java`)

A classe [ValidadorRegex]centraliza os padrões de regex e fornece métodos estáticos auxiliares de teste e sanitização:

| Tipo de Dado | Padrão REGEX | Formatos Aceitos | Formatos Rejeitados |
| :--- | :--- | :--- | :--- |
| **CPF** | `^(\d{3}\.\d{3}\.\d{3}-\d{2}\|\d{11})$` | `123.456.789-01`<br>`12345678901` | `1234567890` (10 dígitos)<br>`123.456.789-001`<br>`letras/especiais` |
| **Celular / Telefone** *(sem +55)* | `^(\(\d{2}\)\s?\|\d{2}\s?)(9?\d{4})[-.\s]?(\d{4})$` | `(11) 98765-4321`<br>`11 98765-4321`<br>`11987654321`<br>`(11) 3456-7890`<br>`1134567890` | `+55 (11) 98765-4321`<br>`+5511987654321`<br>`98765-4321` (sem DDD)<br>`12345` (curto) |
| **Placa de Veículo** | `^[A-Za-z]{3}-?[0-9][0-9A-Za-z][0-9]{2}$` *(Case Insensitive)* | `ABC-1234` (Padrão Antigo)<br>`ABC1234`<br>`ABC1D23` (Mercosul)<br>`abc1d23` | `AB-1234`<br>`ABC12345`<br>`123-ABCD` |
| **E-mail** | `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$` *(Case Insensitive)* | `usuario@dominio.com`<br>`contato@empresa.com.br` | `usuario@`<br>`usuario.com`<br>`@dominio.com` |
| **CNPJ** | `^(\d{2}\.\d{3}\.\d{3}/\d{4}-\d{2}\|\d{14})$` | `12.345.678/0001-90`<br>`12345678000190` | `123456780001` (incompleto)<br>`12.345.678/0001` |
| **GTA** | `^[0-9A-Za-z.\-/]{3,20}$` | `12345678`<br>`GTA-987.2026`<br>`SP/2026/001` | Menos de 3 caracteres ou símbolos inválidos |
| **Nota Fiscal** | `^[0-9.\-/]{1,44}$` | `123456`<br>`001.234.567`<br>`35260100000000000000550010000000011000000011` | Letras e caracteres especiais proibidos |

---

## 3. Relação Completa de Classes que Utilizam REGEX

Abaixo está o detalhamento de cada classe, indicando o tipo de dado validado, como o regex é executado e o impacto no fluxo da aplicação.

```
com.efficientia.efficientia/
├── util/
│   ├── ValidadorRegex.java         # Central de padrões Regex, validações e sanitizações
│   └── PasswordHasher.java         # Tokenização de hash bcrypt/PBKDF2 via split regex
├── controller/
│   ├── PecuaristaServlet.java      # Validação de CPF, Telefone e E-mail de pecuaristas
│   ├── MotoristaServlet.java       # Validação de Telefone e E-mail de motoristas
│   ├── CaminhaoServlet.java        # Validação sintática e normalização de Placas
│   ├── TrajetoServlet.java         # Validação de GTA, Nota Fiscal e normalização Unicode
│   ├── LoginServlet.java           # Validação sintática do E-mail de autenticação
│   ├── EmpresaServlet.java         # Validação e higienização de CNPJ
│   └── AdminServlet.java           # Validação de E-mail de Administradores
├── dao/impl/
│   ├── PecuaristaDAO.java          # Higienização defensiva de CPF e Telefone (replaceAll \D)
│   ├── MotoristaDAO.java           # Higienização defensiva de Telefone (replaceAll \D)
│   ├── EmpresaDAO.java             # Higienização defensiva de CNPJ (replaceAll \D)
│   ├── EnderecoDAO.java            # Higienização defensiva de CEP (replaceAll \D)
│   └── *DAO.java (Demais DAOs)     # Tokenização de termos de pesquisa via regex \s+
└── model/
    └── StatusTrajeto.java          # Normalização de diacríticos com regex \p{M}
```

---

### 3.1. [ValidadorRegex.java]
* **Pacote:** `com.efficientia.efficientia.util`
* **Tipo de Dado que Atende:** CPF, Celular/Telefone, Placa de Veículo, E-mail, CNPJ, Guia de Trânsito Animal (GTA) e Nota Fiscal (NF).
* **Como Usa o REGEX:**
  * Compila previamente 7 constantes `java.util.regex.Pattern` com as regras sintáticas oficiais do sistema.
  * Executa `PATTERN.matcher(input.trim()).matches()` nos métodos booleanos:
    * `isCpfValido(String cpf)`: checa se tem máscara `000.000.000-00` ou exatamente 11 números.
    * `isCelularValido(String telefone)`: checa DDD + 8 ou 9 dígitos, rejeitando explicitamente prefixos internacionais (`+55` ou `00`).
    * `isPlacaValida(String placa)`: avalia placas do padrão antigo (`ABC-1234` / `ABC1234`) e Mercosul (`ABC1D23`).
    * `isEmailValido(String email)`: checa formato `nome@dominio.ext`.
    * `isCnpjValido(String cnpj)`: valida formato com máscara `00.000.000/0001-00` ou 14 dígitos contínuos.
    * `isGtaValido(String gta)`: valida tamanho (3 a 20) e caracteres alfanuméricos com pontuação permitida.
    * `isNotaFiscalValida(String nf)`: valida tamanho (1 a 44) e formato de documento fiscal.
  * Executa transformações via regex:
    * `apenasDigitos(String valor)`: aplica `replaceAll("[^0-9]", "")` para remover qualquer máscara antes do envio ao banco.
    * `normalizarPlaca(String placa)`: aplica `replaceAll("[^A-Za-z0-9]", "").toUpperCase()` para remover traços e colocar em caixa alta.
    * `formatarCpf(String cpf)`: aplica `replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4")` para exibição formatada.
    * `formatarTelefone(String telefone)`: aplica regex para formatar números em `(XX) 9XXXX-XXXX` ou `(XX) XXXX-XXXX`.

---

### 3.2. [PecuaristaServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Dados cadastrais do pecuarista — **CPF**, **Celular/Telefone** e **E-mail**.
* **Como Usa o REGEX:**
  * **No Cadastro (`doPost` padrão):**
    * `!ValidadorRegex.isCpfValido(cpf)`: se inválido, interrompe o fluxo e redireciona com `?erro=cpf_invalido`.
    * `!ValidadorRegex.isCelularValido(telefone)`: se inválido, redireciona com `?erro=telefone_invalido`.
    * `!ValidadorRegex.isEmailValido(email)`: se inválido, redireciona com `?erro=email_invalido`.
    * `ValidadorRegex.apenasDigitos(cpf)`: higieniza o CPF para gravar estritamente 11 números, respeitando a restrição `CHECK (length(cpf) = 11)` do PostgreSQL.
  * **Na Atualização Completa (`acao=atualizar`):**
    * Executa a mesma tripla validação (`isCpfValido`, `isCelularValido`, `isEmailValido`) e sanitização de dígitos do CPF.
  * **Nas Ações de Atualização Específica:**
    * `acao=atualizarCpf`: valida `novoCpf` com `ValidadorRegex.isCpfValido(novoCpf)` e sanitiza antes de chamar `dao.atualizarCpf`.
    * `acao=atualizarEmail`: valida `novoEmail` com `ValidadorRegex.isEmailValido(novoEmail)` antes de chamar `dao.atualizarEmail`.
    * `acao=atualizarTelefone`: valida `novoTelefone` com `ValidadorRegex.isCelularValido(novoTelefone)` antes de chamar `dao.atualizarTelefone`.

---

### 3.3. [MotoristaServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Dados cadastrais do motorista — **Celular/Telefone** e **E-mail**.
* **Como Usa o REGEX:**
  * **No Cadastro (`doPost` padrão):**
    * Valida `telefone` não vazio com `ValidadorRegex.isCelularValido(telefone)`. Se falhar, redireciona com `?erro=telefone_invalido`.
    * Valida `email` não vazio com `ValidadorRegex.isEmailValido(email)`. Se falhar, redireciona com `?erro=email_invalido`.
  * **Na Atualização Completa (`acao=atualizar`):**
    * Revalida celular e e-mail antes de criar o modelo `MotoristaModel` e invocar `dao.atualizar`.
  * **Nas Ações de Atualização Específica:**
    * `acao=atualizarEmail`: valida `novoEmail` com `ValidadorRegex.isEmailValido(novoEmail)`.
    * `acao=atualizarTelefone`: valida `novoTelefone` com `ValidadorRegex.isCelularValido(novoTelefone)`.

---

### 3.4. [CaminhaoServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Identificadores veiculares — **Placa do Cavalo Mecânico** e **Placa da Carreta**.
* **Como Usa o REGEX:**
  * **No Cadastro (`doPost` padrão):**
    * Executa `!ValidadorRegex.isPlacaValida(placaCavalo)` ou `!ValidadorRegex.isPlacaValida(placaCarreta)`. Se qualquer uma estiver fora do padrão, redireciona com `?erro=placa_invalida`.
    * Higieniza e padroniza as placas usando `ValidadorRegex.normalizarPlaca(placaCavalo)` e `ValidadorRegex.normalizarPlaca(placaCarreta)` para armazenamento uniforme (ex.: sem traços e em maiúsculas).
  * **Na Inserção Simplificada (`acao=inserirSimples`):**
    * Aplica validação e normalização por regex em ambas as placas.
  * **Nas Ações de Atualização Específica:**
    * `acao=atualizarPlacaCavalo`: valida `novaPlaca` com `ValidadorRegex.isPlacaValida(novaPlaca)` e salva `normalizarPlaca(novaPlaca)`.
    * `acao=atualizarPlacaCarreta`: valida `novaPlaca` com `ValidadorRegex.isPlacaValida(novaPlaca)` e salva `normalizarPlaca(novaPlaca)`.

---

### 3.5. [TrajetoServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Documentos fiscais e sanitários de transporte de carga viva — **Guia de Trânsito Animal (GTA)**, **Nota Fiscal (NF)** e **Status do Trajeto**.
* **Como Usa o REGEX:**
  * **Na Atualização Específica de GTA (`acao=atualizarGTA`):**
    * Valida a string recebida: `if (novoGta != null && !novoGta.isBlank() && !ValidadorRegex.isGtaValido(novoGta))`. Em caso de erro, redireciona para `?erro=gta_invalido`.
  * **Na Atualização Específica de Nota Fiscal (`acao=atualizarNotaFiscal`):**
    * Valida a string recebida: `if (novaNf != null && !novaNf.isBlank() && !ValidadorRegex.isNotaFiscalValida(novaNf))`. Em caso de erro, redireciona para `?erro=nf_invalida`.
  * **Na Normalização de Texto (`removerAcentos`):**
    * Aplica `Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "")` para eliminar diacríticos e acentuações antes de mapear os estados de transporte.

---

### 3.6. [LoginServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Credencial de autenticação — **E-mail do Administrador / Usuário**.
* **Como Usa o REGEX:**
  * No método `doPost`, antes de submeter a consulta de autenticação ao banco de dados:
    ```java
    if (!ValidadorRegex.isEmailValido(email) || email.length() > 254 || ...) {
        req.setAttribute("erro", "Informe um e-mail válido e sua senha.");
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
        return;
    }
    ```
  * Protege o sistema contra strings maliciosas e formatos absurdos logo na porta de entrada da aplicação.

---

### 3.7. [EmpresaServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Identificador de pessoa jurídica — **CNPJ da Empresa Transportadora / Frigorífico**.
* **Como Usa o REGEX:**
  * **Na Inserção Simplificada (`acao=inserirSimples`):**
    * Valida se o CNPJ fornecido bate com o padrão nacional com máscara ou 14 dígitos contínuos via `ValidadorRegex.isCnpjValido(cnpj)`. Rejeita com `?erro=cnpj_invalido`.
    * Sanitiza os dígitos através de `ValidadorRegex.apenasDigitos(cnpj)`.
  * **Na Atualização Específica de CNPJ (`acao=atualizarCnpj`):**
    * Valida o novo CNPJ com `ValidadorRegex.isCnpjValido(novoCnpj)` e sanitiza com `apenasDigitos(novoCnpj)` antes de persistir no banco.

---

### 3.8. [AdminServlet.java]
* **Pacote:** `com.efficientia.efficientia.controller`
* **Tipo de Dado que Atende:** Cadastro e manutenção de operadores do sistema — **E-mail do Administrador**.
* **Como Usa o REGEX:**
  * Na ação de atualização pontual de e-mail (`acao=atualizarEmail`):
    * Valida se a nova conta informada cumpre o formato correto:
      ```java
      if (novoEmail != null && !novoEmail.isBlank() && !ValidadorRegex.isEmailValido(novoEmail)) {
          resp.sendRedirect(req.getContextPath() + "/admin?erro=email_invalido");
          return;
      }
      ```

---

### 3.9. Classes da Camada de Persistência (DAOs)
Além dos Controllers e do utilitário central, classes DAO utilizam expressões regulares nativas em Java (`replaceAll` e `split`) como camada defensiva final de sanitização dos dados antes de executar comandos SQL parametrizados:

1. **[PecuaristaDAO.java]:**
   * **Dados:** CPF e Telefone.
   * **Regex:** `cpf.replaceAll("\\D", "").trim()` e `telefone.replaceAll("\\D", "").trim()` para expurgar pontuações antes das queries.
   * **Busca:** `termo.trim().split("\\s+")` para quebrar pesquisas multi-palavras em filtros SQL.
2. **[MotoristaDAO.java]:**
   * **Dados:** Telefone e termos de pesquisa.
   * **Regex:** `telefone.replaceAll("\\D", "")` e `termo.trim().split("\\s+")`.
3. **[EmpresaDAO.java]:**
   * **Dados:** CNPJ e termos de pesquisa.
   * **Regex:** `cnpj.replaceAll("\\D", "").trim()` e `termo.trim().split("\\s+")`.
4. **[EnderecoDAO.java]:**
   * **Dados:** CEP (Código de Endereçamento Postal).
   * **Regex:** `cep.replaceAll("\\D", "").trim()` para garantir conformidade estrita de 8 dígitos numéricos.
5. **[AdminDAO.java][PropriedadeDAO.java][InfoEmbarqueDAO.java]:**
   * **Dados:** Termos de busca fornecidos pelo usuário em campos de pesquisa textual.
   * **Regex:** `termo.trim().split("\\s+")` para segmentar termos separados por um ou múltiplos espaços em branco.

---

### 3.10. Classes de Modelo e Segurança
1. **[StatusTrajeto.java]:**
   * **Dados:** Status do trajeto informado pelo usuário ou sistema (ex.: "Em Trânsito", "Concluído", "Pendente").
   * **Regex:** `.replaceAll("\\p{M}", "")` que usa propriedades de blocos Unicode para remover marcas de acentuação antes de converter para a enumeração interna.
2. **[PasswordHasher.java]:**
   * **Dados:** Hash e salt de senhas de usuários.
   * **Regex:** `stored.split("\\$", -1)` para decodificar os blocos do algoritmo criptográfico seguro.

---

## 4. Tabela Sinóptica: Classe x Tipo de Dado x Operação Regex

| Classe | Camada | Tipo de Dado do Usuário | Método / Padrão Utilizado | Ação Realizada |
| :--- | :--- | :--- | :--- | :--- |
| **`ValidadorRegex`** | Util | CPF, Celular, Placa, E-mail, CNPJ, GTA, NF | `Pattern.compile(...)`, `matches(...)`, `replaceAll(...)` | Centraliza definições de Regex, validações e funções de limpeza/máscara |
| **`PecuaristaServlet`** | Controller | CPF, Telefone/Celular, E-mail | `isCpfValido`, `isCelularValido`, `isEmailValido`, `apenasDigitos` | Validação e sanitização no cadastro e edições de pecuaristas |
| **`MotoristaServlet`** | Controller | Telefone/Celular, E-mail | `isCelularValido`, `isEmailValido` | Validação no cadastro e edições de motoristas |
| **`CaminhaoServlet`** | Controller | Placa Cavalo Mecânico, Placa Carreta | `isPlacaValida`, `normalizarPlaca` | Validação de padrão Mercosul/antigo e normalização para maiúsculas |
| **`TrajetoServlet`** | Controller | GTA, Nota Fiscal, Status Trajeto | `isGtaValido`, `isNotaFiscalValida`, `replaceAll("\\p{M}", "")` | Validação de guias/NFs e normalização de acentos de status |
| **`LoginServlet`** | Controller | E-mail de Autenticação | `isEmailValido` | Bloqueio precoce de logins com formato inválido de e-mail |
| **`EmpresaServlet`** | Controller | CNPJ da Empresa | `isCnpjValido`, `apenasDigitos` | Validação de máscara/dígitos e sanitização para o banco |
| **`AdminServlet`** | Controller | E-mail do Administrador | `isEmailValido` | Validação de formato de e-mail na alteração de perfil |
| **`PecuaristaDAO`** | DAO | CPF, Telefone, Busca | `replaceAll("\\D", "")`, `split("\\s+")` | Higienização defensiva de números e tokenização de pesquisa |
| **`MotoristaDAO`** | DAO | Telefone, Busca | `replaceAll("\\D", "")`, `split("\\s+")` | Higienização defensiva de telefone e tokenização de pesquisa |
| **`EmpresaDAO`** | DAO | CNPJ, Busca | `replaceAll("\\D", "")`, `split("\\s+")` | Higienização defensiva de CNPJ e tokenização de pesquisa |
| **`EnderecoDAO`** | DAO | CEP | `replaceAll("\\D", "")` | Higienização defensiva de CEP numérico |
| **`StatusTrajeto`** | Model | Status do Trajeto | `replaceAll("\\p{M}", "")` | Normalização Unicode para reconhecimento de status sem acentos |
| **`PasswordHasher`** | Util | Hash de Senha | `split("\\$", -1)` | Decomposição estruturada do hash de senha |

---

## 5. Matriz de Testes e Evidências Práticas

| Dado de Entrada | Valor Testado | Validador Acionado | Resultado | Tratamento Final |
| :--- | :--- | :--- | :---: | :--- |
| **CPF formatado** | `123.456.789-01` | `isCpfValido` | Válido | Sanitizado para `12345678901` |
| **CPF contínuo** | `12345678901` | `isCpfValido` | Válido | Persistido com 11 dígitos |
| **CPF inválido** | `123.456.789-1` | `isCpfValido` | Inválido | Redirecionado com `?erro=cpf_invalido` |
| **Celular com DDD** | `(11) 98765-4321` | `isCelularValido` | Válido | Aceito sem código de país |
| **Telefone Fixo** | `(11) 3456-7890` | `isCelularValido` | Válido | Aceito formato nacional de 8 dígitos |
| **Telefone com +55** | `+5511987654321` | `isCelularValido` | Inválido | Rejeitado (estritamente sem DDI) |
| **Placa Mercosul** | `bra2e19` | `isPlacaValida` | Válido | Normalizado para `BRA2E19` |
| **Placa Tradicional** | `ABC-1234` | `isPlacaValida` | Válido | Normalizado para `ABC1234` |
| **E-mail de Login** | `admin@efficientia.com`| `isEmailValido` | Válido | Liberado para checagem de hash |
| **E-mail mal formatado**| `admin_sem_arroba.com` | `isEmailValido` | Inválido | Rejeitado na tela de login |
| **CNPJ formatado** | `12.345.678/0001-90`| `isCnpjValido` | Válido | Sanitizado para `12345678000190` |
| **GTA animal** | `SP-2026/0123` | `isGtaValido` | Válido | Gravado no trajeto do gado |
| **Nota Fiscal** | `3526010000000000` | `isNotaFiscalValida` | Válido | Gravado no trajeto do gado |

---

## 6. Conclusão

A arquitetura de validação por expressões regulares do **Efficientia** garante a integridade dos dados desde o momento em que o usuário preenche o formulário até a persistência no PostgreSQL, atendendo com rigor técnico tanto aos requisitos funcionais da aplicação quanto aos critérios acadêmicos do Projeto Interdisciplinar.
