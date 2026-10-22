# Documentação de Implementação: Validação com Expressões Regulares (REGEX)

Este documento descreve a especificação técnica, a arquitetura e a integração da camada de validação e sanitização de dados de entrada baseada em **Expressões Regulares (REGEX)** no sistema **Efficientia**.

---

## 1. Contexto e Enquadramento Interdisciplinar (1º Ano)

A implementação atende diretamente às demandas estabelecidas no documento de requisitos do **Projeto Interdisciplinar (1º Ano)**:

* **Sistemas Operacionais (Requisito Extra):** *"Usar REGEX para validar entrada de CPF e/ou Celular (pelo menos)"*.
* **Lógica de Programação (Requisito Extra E1):** *"Os códigos deverão apresentar adequada validação de dados de entrada e ter a nomenclatura como a indicada para os itens mencionados acima (classes, atributos e métodos)"*.
* **Banco de Dados 1 & POO:** Garantir a conformidade dos dados com as restrições de integridade do PostgreSQL declaradas no script `Efficientia.sql` (ex.: `CHECK (length(cpf) = 11)`, `CHECK (length(telefone) >= 10)` e `CHECK (email LIKE '%@%')`).

---

## 2. Decisões Arquiteturais

1. **Validação Estrita no Backend (Sem Dependência de JavaScript):**
   * A lógica de validação reside integralmente na camada de aplicação em Java. Isso elimina qualquer vulnerabilidade de bypass no cliente e atende à regra de operar diretamente via requisições HTTP gerenciadas pelos Servlets.
2. **Telefone Nacional sem Indicativo Internacional (`+55`):**
   * O formato aceito considera estritamente a numeração nacional (DDD de 2 dígitos + 8 ou 9 dígitos do assinante).
   * Números iniciados por `+` ou `00` são rejeitados de forma explícita.
3. **Padrões Pré-Compilados (`java.util.regex.Pattern`):**
   * Para otimizar a performance sob concorrência de requisições web no Apache Tomcat, todas as expressões regulares foram compiladas em constantes estáticas (`static final`), evitando recompilações desnecessárias a cada chamada HTTP.
4. **Higienização e Sanitização antes da Persistência:**
   * Entradas formatadas com pontuações (ex.: CPF com pontos e traço) são validadas quanto à conformidade de máscara e posteriormente limpas para armazenamento numérico contínuo no banco de dados.
5. **Comentários e Javadoc em Texto Limpo:**
   * Não foram utilizadas tags de marcação HTML nos comentários e cabeçalhos de métodos, mantendo a legibilidade limpa no código-fonte.

---

## 3. Especificação das Expressões Regulares

A classe utilitária `ValidadorRegex` centraliza as expressões regulares do sistema:

| Dado / Campo | Expressão Regular (Regex) | Formatos Aceitos | Formatos Rejeitados |
| :--- | :--- | :--- | :--- |
| **CPF** | `^(\d{3}\.\d{3}\.\d{3}-\d{2}\|\d{11})$` | `123.456.789-01`<br>`12345678901` | `1234567890` (10 dígitos)<br>`123.456.789-001` (máscara errada)<br>`letras/especiais` |
| **Celular / Telefone** *(sem +55)* | `^(\(\d{2}\)\s?\|\d{2}\s?)(9?\d{4})[-.\s]?(\d{4})$` | `(11) 98765-4321`<br>`11 98765-4321`<br>`11987654321`<br>`(11) 3456-7890`<br>`1134567890` | `+55 (11) 98765-4321`<br>`+5511987654321`<br>`98765-4321` (sem DDD)<br>`12345` (incompleto) |
| **Placa de Veículo** | `^[A-Za-z]{3}-?[0-9][0-9A-Za-z][0-9]{2}$` | `ABC-1234` (Tradicional)<br>`ABC1234`<br>`ABC1D23` (Mercosul)<br>`abc1d23` | `AB-1234`<br>`ABC12345`<br>`123-ABCD` |
| **E-mail** | `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$` | `usuario@dominio.com`<br>`contato@empresa.com.br` | `usuario@`<br>`usuario.com`<br>`@dominio.com` |
| **CNPJ** | `^(\d{2}\.\d{3}\.\d{3}/\d{4}-\d{2}\|\d{14})$` | `12.345.678/0001-90`<br>`12345678000190` | `123456780001` (incompleto)<br>`12.345.678/0001` |
| **GTA** | `^[0-9A-Za-z.\-/]{3,20}$` | `12345678`<br>`GTA-987.2026`<br>`SP/2026/001` | Caracteres de injeção ou menos de 3 caracteres |
| **Nota Fiscal** | `^[0-9.\-/]{1,44}$` | `123456`<br>`001.234.567`<br>`35260100000000000000550010000000011000000011` | Letras e caracteres especiais inválidos |

---

## 4. Estrutura de Arquivos e Implementação

```
src/main/java/com/efficientia/efficientia/
├── util/
│   └── ValidadorRegex.java             # Utilitário central de REGEX e sanitizadores
└── controller/
    ├── PecuaristaServlet.java          # Validação de CPF, Celular e Email (cadastro/edição)
    ├── MotoristaServlet.java           # Validação de Celular e Email (cadastro/edição)
    ├── CaminhaoServlet.java            # Validação e normalização de Placas veiculares
    └── TrajetoServlet.java             # Validação documental de GTA e Nota Fiscal
```

### 4.1. Classe Utilitária: `ValidadorRegex.java`

* **Pacote:** `com.efficientia.efficientia.util`
* **Principais Métodos:**
  * `boolean isCpfValido(String cpf)`: avalia a sintaxe do CPF com ou sem máscara.
  * `boolean isCelularValido(String telefone)`: assegura formato nacional com DDD sem prefixo internacional.
  * `boolean isPlacaValida(String placa)`: valida placas Mercosul ou tradicionais.
  * `boolean isEmailValido(String email)`: valida estrutura de e-mail.
  * `boolean isCnpjValido(String cnpj)`: valida CNPJ com ou sem máscara.
  * `boolean isGtaValido(String gta)`: valida código de Guia de Trânsito Animal.
  * `boolean isNotaFiscalValida(String nf)`: valida número documental de Nota Fiscal.
  * `String apenasDigitos(String valor)`: higieniza o texto, retornando apenas caracteres numéricos `[0-9]`.
  * `String normalizarPlaca(String placa)`: limpa pontuações e converte para letras maiúsculas.
  * `String formatarCpf(String cpf)`: converte 11 dígitos para a representação visual `000.000.000-00`.
  * `String formatarTelefone(String telefone)`: converte números para `(XX) 9XXXX-XXXX` ou `(XX) XXXX-XXXX`.

---

## 5. Integração na Camada de Controle (Controllers)

### 5.1. `PecuaristaServlet.java`
No método `doPost`, antes de instanciar o modelo ou persistir via `dao.inserir()` ou `dao.atualizar()`:
1. Valida o CPF através de `ValidadorRegex.isCpfValido(cpf)`. Se inválido, redireciona para `?erro=cpf_invalido`.
2. Valida o celular através de `ValidadorRegex.isCelularValido(telefone)`. Se inválido, redireciona para `?erro=telefone_invalido`.
3. Valida o e-mail através de `ValidadorRegex.isEmailValido(email)`. Se inválido, redireciona para `?erro=email_invalido`.
4. Aplica `ValidadorRegex.apenasDigitos(cpf)` gerando a string de 11 caracteres para cumprir a constraint `CHECK (length(cpf) = 11)` do banco.

### 5.2. `MotoristaServlet.java`
No método `doPost` (cadastro e edição):
1. Valida o telefone/celular via `ValidadorRegex.isCelularValido(telefone)`. Se inválido, redireciona para `?erro=telefone_invalido`.
2. Valida o e-mail via `ValidadorRegex.isEmailValido(email)`. Se inválido, redireciona para `?erro=email_invalido`.

### 5.3. `CaminhaoServlet.java`
No método `doPost` (cadastro e edição):
1. Valida se as placas do cavalo mecânico e da carreta cumprem o padrão Mercosul ou antigo via `ValidadorRegex.isPlacaValida(...)`.
2. Padroniza as placas com `ValidadorRegex.normalizarPlaca(...)` em caixa alta antes da persistência.

### 5.4. `TrajetoServlet.java`
Nas ações de despacho e atualizações pontuais:
1. Na ação `atualizarGTA`, valida o número informado via `ValidadorRegex.isGtaValido(novoGta)`.
2. Na ação `atualizarNotaFiscal`, valida o documento fiscal via `ValidadorRegex.isNotaFiscalValida(novaNf)`.

---

## 6. Matriz de Testes e Evidências

| Caso de Teste | Entrada Fornecida | Resultado Obtido | Status |
| :--- | :--- | :--- | :---: |
| CPF formatado válido | `123.456.789-01` | Aprovado no REGEX e sanitizado para `12345678901` | Aprovado |
| CPF contínuo válido | `12345678901` | Aprovado no REGEX e persistido diretamente | Aprovado |
| CPF com tamanho incorreto | `123.456.789-1` | Rejeitado; redirecionado com `?erro=cpf_invalido` | Aprovado |
| Celular nacional com máscara | `(11) 98765-4321` | Aprovado no REGEX | Aprovado |
| Celular nacional contínuo | `11987654321` | Aprovado no REGEX | Aprovado |
| Telefone com prefixo `+55` | `+5511987654321` | Rejeitado pelo REGEX (estritamente sem +55) | Aprovado |
| Telefone fixo nacional | `(11) 3456-7890` | Aprovado no REGEX | Aprovado |
| Placa Mercosul minúscula | `bra2e19` | Aprovado no REGEX e normalizado para `BRA2E19` | Aprovado |
| Placa Tradicional com hífen | `ABC-1234` | Aprovado no REGEX e normalizado para `ABC1234` | Aprovado |
| E-mail fora do padrão RFC | `usuario_sem_arroba.com` | Rejeitado; redirecionado com `?erro=email_invalido` | Aprovado |

---

## 7. Rastreabilidade para Avaliação das Disciplinas

* **Sistemas Operacionais:** Atendimento ao critério de nota extra mediante uso de REGEX para validação de CPF e Celular.
* **Lógica de Programação:** Atendimento ao critério extra de validação de dados de entrada, código modular, tipado e com tratamento defensivo.
* **Banco de Dados 1 & POO:** Integração com as restrições relacionais e arquitetura MVC do sistema.
