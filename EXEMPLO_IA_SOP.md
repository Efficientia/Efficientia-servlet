# Registro de Interação com Inteligência Artificial: Validação de Dados por REGEX (SOP / Projeto Interdisciplinar)

Este documento registra a interação realizada entre o usuário (aluno/desenvolvedor) e o assistente de Inteligência Artificial (**Antigravity**), evidenciando o prompt de comando fornecido, a metodologia de análise da IA, as ações executadas e o processo de validação crítica humana realizado por meio da leitura do código-fonte e busca nas classes do projeto **Efficientia**.

---

## 1. Prompt Exato Enviado pelo Usuário

O comando textual enviado à IA foi:

```text
liste todas as classes que utilizão a validação pro regex e crie um .md de documentação detalhando a utilização do regex para validação dos dados inseridos pelos usuarios no projeto, nesse md precisa ter o nome das classes que usam o regex e como usam e para qual tipo de dado elas servem
```

---

## 2. Ações Executadas pela Inteligência Artificial

A IA atuou de maneira analítica e autônoma diretamente no repositório do projeto, adotando as seguintes etapas técnicas:

### 2.1. Varredura e Busca no Código-Fonte
* **Varredura por Expressões Regulares (`grep_search`):**
  * Buscou padrões de uso de regex em Java, como chamadas aos métodos `.matches()`, classes `java.util.regex.Pattern`, `.replaceAll()`, e `.split()`.
  * Localizou o utilitário central de validação: `Efficientia/src/main/java/com/efficientia/efficientia/util/ValidadorRegex.java`.
* **Rastreabilidade de Dependências e Chamadas:**
  * Realizou buscas por referências a `ValidadorRegex` em todos os pacotes do projeto (`com.efficientia.efficientia.*`).
  * Identificou que, além dos servlets óbvios (`PecuaristaServlet`, `MotoristaServlet`, `CaminhaoServlet`, `TrajetoServlet`), o utilitário também era acionado na autenticação (`LoginServlet`), no gerenciamento de transportadoras (`EmpresaServlet`) e na edição de administradores (`AdminServlet`).
  * Analisou ocorrências adicionais de sanitização por expressões regulares nas camadas DAO (`replaceAll("\\D", "")` e `split("\\s+")`) e em enumerações de modelo (`replaceAll("\\p{M}", "")`).

### 2.2. Inspeção de Arquivos (`view_file`)
* Leu e examinou linha por linha os arquivos:
  * `ValidadorRegex.java`: para extrair cada constante de regex compilada, seus métodos e finalidades de negócio.
  * `PecuaristaServlet.java`, `MotoristaServlet.java`, `CaminhaoServlet.java`, `TrajetoServlet.java`, `LoginServlet.java`, `EmpresaServlet.java`, `AdminServlet.java`: para constatar em quais métodos (`doPost`, ações pontuais) os dados eram validados e quais parâmetros eram tratados.
  * DAOs (`MotoristaDAO`, `PecuaristaDAO`, `EmpresaDAO`, `EnderecoDAO`): para identificar a camada defensiva de tratamento de dados.

### 2.3. Síntese e Geração do Documento Técnico
* Gerou e salvou o arquivo [DOCUMENTACAO_VALIDACAO_REGEX.md](Efficientia/DOCUMENTACAO_VALIDACAO_REGEX.md) contendo:
  1. O enquadramento acadêmico interdisciplinar (Sistemas Operacionais, Lógica de Programação, Banco de Dados 1 e POO).
  2. A tabela de padrões de expressões regulares (CPF, Celular sem +55, Placa Mercosul/antiga, E-mail, CNPJ, GTA, Nota Fiscal).
  3. O mapeamento detalhado de cada classe, indicando o tipo de dado tratado e a lógica aplicada.
  4. Uma tabela sinóptica consolidada relacionando Classe × Camada × Tipo de Dado × Ação Realizada.
  5. Uma matriz de testes com exemplos de entradas válidas e inválidas.

---

## 3. Como o Usuário Validou o Resultado (Validação Crítica Humana)

A geração pela IA não foi aceita passivamente; o desenvolvedor realizou um processo de auditoria e validação ativa do conteúdo:

### 3.1. Leitura e Comparação com as Classes Reais
1. **Conferência da Camada Util (`ValidadorRegex.java`):**
   * O usuário abriu e revisou a classe `ValidadorRegex.java` para confirmar se os métodos `isCpfValido`, `isCelularValido`, `isPlacaValida`, `isEmailValido`, `isCnpjValido`, `isGtaValido` e `isNotaFiscalValida` realmente existiam, correspondiam às regexes documentadas e rejeitavam o código de país `+55` (requisito de Sistemas Operacionais).
2. **Checagem dos Servlets (Controllers):**
   * Verificou a lógica nos arquivos `PecuaristaServlet.java`, `MotoristaServlet.java`, `CaminhaoServlet.java` e `TrajetoServlet.java`, confirmando que os redirecionamentos com parâmetros de erro (`?erro=cpf_invalido`, `?erro=telefone_invalido`, etc.) correspondiam exatamente às implementações do servlet.
   * Constatou a descoberta correta de classes que estendiam a validação de regex além do escopo inicial: `LoginServlet.java` (validação de e-mail antes da autenticação) e `EmpresaServlet.java` (validação e sanitização de CNPJ).
3. **Checagem das Camadas DAO e Model:**
   * Verificou nos DAOs (`MotoristaDAO`, `PecuaristaDAO`, `EmpresaDAO`, `EnderecoDAO`) se as sanitizações via regex nativa `replaceAll("\\D", "")` e divisão de palavras via `split("\\s+")` de fato ocorriam como camada defensiva antes do SQL.

### 3.2. Ajustes e Refinamento Manual no Arquivo Gerado
* O usuário inspecionou diretamente o arquivo [DOCUMENTACAO_VALIDACAO_REGEX.md](Efficientia/DOCUMENTACAO_VALIDACAO_REGEX.md).
* Realizou modificações pontuais para adequar o formato do documento aos padrões desejados para entrega acadêmica (substituindo caminhos absolutos locais do computador por referências a partir de `Efficientia/`).

---

## 4. Conclusão da Dinâmica IA-Desenvolvedor

A colaboração com a IA acelerou significativamente o mapeamento cruzado entre dezenas de arquivos do projeto em segundos, identificando pontos de validação que poderiam passar despercebidos em uma busca manual (como em `LoginServlet` e `AdminServlet`). Ao mesmo tempo, a leitura atenta e a busca direta no código pelo desenvolvedor garantiram a acurácia dos dados, a veracidade das evidências e o total domínio sobre a arquitetura do software apresentado para a disciplina de Sistemas Operacionais.
