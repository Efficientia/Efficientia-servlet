# Processo de Refinamento com Prompts e Riscos da Implementação Sem Plano

> **Projeto:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Disciplina:** Introdução à Inteligência Artificial / Projeto Interdisciplinar  
> **Nível:** 1º Ano — 2º Semestre (Instituto J&F / Germinatech)  
> **Data:** 29/10/2026  
> **Documentos relacionados:** [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md), [SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md](SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md), [EXEMPLO_IA_BD.md](EXEMPLO_IA_BD.md)

---

## 1. Contexto e Pergunta Norteadora

> *"Houve necessidade de corrigir ou complementar conteúdos?"*

**Sim, de maneira contínua.** A utilização de ferramentas de Inteligência Artificial Generativa no projeto **Efficientia** comprovou na prática que modelos de linguagem **não produzem soluções prontas para produção em uma única iteração**. A geração de saídas genéricas, suposições arquiteturais infundadas e imprecisões técnicas exigiram intervenções humanas ativas em todas as fases do ciclo de desenvolvimento.

Este documento detalha o processo de **refinamento iterativo por meio de prompts estruturados** e analisa criticamente os **impactos negativos e riscos de implementar software ou código de IA sem um plano prévio de arquitetura**.

---

## 2. Necessidade Real de Correção e Complementação

Ao longo do projeto, as respostas iniciais da IA apresentaram dois tipos recorrentes de problemas que demandaram auditoria humana obrigatória:

### 2.1. Alucinações Técnicas e Presunções Incorretas
Modelos de IA tendem a completar lacunas estatisticamente com base nos padrões mais comuns da web. No ecossistema Java, isso provocou:
* **Presunção indevida de frameworks:** Supor que o projeto utilizava Spring Boot, Hibernate ou JPA, quando o escopo acadêmico do edital de POO exige explicitamente **Jakarta EE Servlets nativos (Servlet 6.0) e persistência via JDBC puro**.
* **Divergência de algoritmos criptográficos:** A IA mencionou o uso de "BCrypt com Pepper", enquanto a implementação real do repositório ([PasswordHasher.java](../src/main/java/com/efficientia/efficientia/util/PasswordHasher.java)) utiliza **PBKDF2 com HMAC-SHA-256 em 600.000 iterações e salt criptográfico individual**.
* **Incompatibilidade de sintaxe SQL:** Proposta de constraints ou tipos de dados específicos de outros SGBDs (como MySQL ou Oracle) incompatíveis com a sintaxe rigorosa do **PostgreSQL 14+**.

### 2.2. Confusão entre Código Existente e Código Planejado
Documentações geradas em primeira versão frequentemente classificavam funcionalidades como "concluídas", quando na verdade estavam apenas no nível de rascunho ou testes unitários isolados (por exemplo, assumir que a classe `CpfHasher` já estava conectada aos DAOs, quando o banco ainda operava com `CHAR(11)` em texto claro). Foi necessária intervenção direta para que os relatórios registrassem **estritamente o que estava implementado e ativo no repositório**.

---

## 3. O Processo de Refinamento com Prompts Detalhados

Para transformar saídas imprecisas em artefatos de engenharia funcionais e auditáveis, a equipe adotou um fluxo de **engenharia de prompts iterativa** amparado pelo ciclo:

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        CICLO DE REFINAMENTO DE PROMPTS                          │
│                                                                                 │
│   [1. Prompt de Escopo] ──> [2. Leitura de Código Real] ──> [3. Rascunho IA]   │
│                                                                    │            │
│                                                                    ▼            │
│   [6. Artefato Aprovado] <── [5. Prompt Corretivo] <── [4. Auditoria Humana]    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 3.1. Transição: Prompt Ingênuo vs. Prompt Detalhado por Evidências

A qualidade do resultado dependeu diretamente do grau de especificidade e contextualização do prompt:

| Aspecto | Prompt Ingênuo (Resultado Vago/Incorreto) | Prompt Refinado (Resultado Técnico Esperado) |
| :--- | :--- | :--- |
| **Comando** | *"Escreva uma documentação sobre a segurança do sistema e como tratamos senhas."* | *"Analise os arquivos `PasswordHasher.java` e `Efficientia.sql`. Descreva a segurança de senhas detalhando o algoritmo (PBKDF2WithHmacSHA256), a quantidade de iterações (600.000), o uso de salt de 16 bytes via `SecureRandom` e a comparação em tempo constante com `MessageDigest.isEqual`. Liste apenas o que está implementado no código."* |
| **Resultado da IA** | Texto genérico citando boas práticas abstratas da internet, mencionando frameworks não usados. | Especificação técnica precisa, condizente linha por linha com as classes e testes unitários do repositório. |

### 3.2. Uso Obrigatório de Restrições Negativas (*Negative Constraints*)
Descobriu-se que instruir a IA sobre **o que NÃO fazer** é tão importante quanto definir o objetivo:
- *"Não presuma bibliotecas externas que não estejam no `pom.xml`."*
- *"Não utilize caminhos absolutos locais do Windows (`file:///c:/Users/...`); formate sempre com links relativos a partir da raiz do repositório."*
- *"Não descreva funcionalidades futuras como implementadas; marque-as expressamente como planejadas ou não existentes."*

### 3.3. Refinamento Conduzido por Entrevistas Técnicas
Em decisões arquiteturais complexas (como a padronização de campos sensíveis de banco de dados em [EXEMPLO_IA_BD.md](EXEMPLO_IA_BD.md)), a IA foi orientada a **não tomar decisões arbitrárias**, mas sim formular perguntas prévias de alinhamento com opções claras para o desenvolvedor antes de gerar o código final (ex.: decidir entre `CHAR(11)` ou `VARCHAR(11)` para CPF e telefones com ou sem pontuação).

---

## 4. Os Problemas da "Implementação Sem Plano"

Tanto na codificação convencional quanto no desenvolvimento assistido por IA, a abordagem de **"ir direto para o código sem planejar"** provou ser a principal causa de retrabalho, inconsistências e falhas de sistema.

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    O CICLO VICIOSO DA IMPLEMENTAÇÃO SEM PLANO                   │
│                                                                                 │
│  [Ideia Inicial] ──> [Código Imediato/Sem Roteiro] ──> [Incompatibilidade]      │
│                                                                │                │
│                                                                ▼                │
│  [Retrabalho e Atraso] <── [Quebra de Testes / Bugs] <── [Código Desconexo]     │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 4.1. Desalinhamento entre Camadas Arquiteturais (BD × Model × Servlet × JSP)
Quando um componente foi criado isoladamente sem uma especificação prévia:
- **Exemplo de falha:** Um Servlet gerado rapidamente esperava receber parâmetros do formulário com nomes diferentes daqueles definidos na página JSP, ou tentava persistir dados com tipagens divergentes das constraints do banco de dados (ex.: tentar salvar strings longas em campos com tamanho restrito, gerando `PSQLException: value too long for type character`).
- **Consequência:** A tela quebrava silenciosamente ou redirecionava em loop, exigindo longas sessões de depuração para rastrear onde a cadeia havia se rompido.

### 4.2. Inconsistência no Padrão Post-Redirect-Get (PRG)
Sem planejamento do fluxo HTTP nos Servlets:
- Métodos `doPost` faziam despacho interno (`RequestDispatcher.forward`) diretamente para páginas de sucesso, permitindo que a atualização da página (F5) pelo usuário reenviasse o formulário e tentasse reinserir registros duplicados no banco de dados.
- O planejamento prévio da arquitetura estabeleceu o padrão **PRG obrigatório** em todas as rotas (`resp.sendRedirect("...?sucesso=true")`), eliminando reenvios acidentais.

### 4.3. Dispersão e Duplicação de Regras de Validação
Sem um plano centralizador:
- Cada Servlet passou a implementar sua própria validação com expressões regulares incompletas, gerando comportamentos divergentes entre o cadastro de pecuaristas e o de motoristas.
- A solução planejada foi isolar todas as regras de validação sintática em um componente único e reutilizável: a classe [ValidadorRegex.java](../src/main/java/com/efficientia/efficientia/util/ValidadorRegex.java), coberta por suíte completa de testes unitários.

### 4.4. O Efeito "Castelo de Cartas" e Retrabalho em Cascata
Implementar sem plano gera código descartável. Quando uma alteração estrutural no banco de dados precisava ser feita (como a introdução de novas constraints `CHECK` ou enums), todo o código implementado "às cegas" quebrava em cascata, demandando refatorações integrais de DAOs, Servlets e testes.

---

## 5. A Metodologia Adotada: "Plano Primeiro, Código Depois"

Para superar as armadilhas da implementação desordenada, o projeto **Efficientia** instituiu a obrigatoriedade de documentos de especificação e planos técnicos antes de qualquer alteração de código:

1. **Especificação Técnica Prévia:** Criação de artefatos de planejamento (como [SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md](SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md) e [SPEC_INTEGRACAO_FRONTEND_BACKEND.md](SPEC_INTEGRACAO_FRONTEND_BACKEND.md)), definindo de antemão contratos de métodos, parâmetros esperados, rotas e códigos de erro.
2. **Prompts Amparados em Arquivos Reais:** A IA só recebe autorização para codificar ou documentar após a leitura estática das classes existentes, schemas e dependências.
3. **Auditoria Humana Rigorosa em 3 Etapas:** Conforme normatizado em [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md), todo código ou texto passa pela validação de **Sintaxe**, **Conteúdo** (fidelidade com o código-fonte) e **Organização**.

Essa governança garantiu que a Inteligência Artificial atuasse como uma alavanca de produtividade e qualidade, sem jamais substituir o papel do desenvolvedor como responsável pela consistência, segurança e arquitetura do software.
