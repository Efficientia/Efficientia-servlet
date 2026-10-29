# Limitações Percebidas no Uso da Inteligência Artificial

> **Projeto:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Disciplina:** Introdução à Inteligência Artificial / Projeto Interdisciplinar  
> **Nível:** 1º Ano — 2º Semestre (Instituto J&F / Germinatech)  
> **Data:** 29/10/2026  
> **Documentos relacionados:** [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md), [REFINAMENTO_PROMPTS_E_PLANEJAMENTO_IA.md](REFINAMENTO_PROMPTS_E_PLANEJAMENTO_IA.md), [SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md](SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md)

---

## 1. Contexto e Pergunta Norteadora

> *"Quais limitações perceberam no uso da IA?"*

A experiência prática com Inteligência Artificial Generativa no projeto **Efficientia** evidenciou que, apesar de acelerar o desenvolvimento, a ferramenta possui limitações cognitivas, técnicas e pedagógicas severas. O uso da IA sem governança e sem pensamento crítico introduz fragilidades tanto no produto de software quanto na formação acadêmica do estudante.

Este documento sintetiza as principais limitações identificadas durante o ciclo de engenharia do projeto.

---

## 2. Limitações Pedagógicas e Cognitivas

### 2.1. O Risco de Ilusão de Competência e Falha no Aprendizado
A principal limitação pedagógica observada foi o **risco de atrofia do raciocínio lógico e da consolidação conceitual**.

- **Acomodação cognitiva (*Copy-Paste* acrítico):** Quando a IA entrega uma solução pronta e funcional em segundos (por exemplo, um método JDBC complexo com múltiplos `JOIN`s ou uma expressão regular intricada), o desenvolvedor pode cair na "ilusão de competência" — achar que domina o assunto apenas porque o código compilou e executou com sucesso.
- **Déficit na resolução autônoma de problemas:** Se o aluno delega à IA a depuração de cada exceção ou o desenho inicial de cada algoritmo, ele não desenvolve a musculatura cognitiva necessária para investigar logs, entender mensagens de erro do compilador ou analisar a causa-raiz de falhas em tempo de execução.
- **Mitigação aplicada no Efficientia:** A equipe adotou o princípio do **"domínio do que faz e para que serve"** (conforme exposto em [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md)): nenhum membro commita código gerado por IA que não seja capaz de explicar em detalhes técnicos durante uma sabatina ou defesa de banca. A IA atuou como tutora explicativa, e não como substituta do raciocínio.

---

## 3. Limitações Técnicas e Arquiteturais da IA

### 3.1. Falta de Visão Holística e Compreensão do Todo
Embora os modelos de linguagem processem grandes volumes de texto, sua atuação tende a ser **local e probabilística**, carecendo de real compreensão da integridade do sistema:
- **Efeito túnel:** Ao ser solicitada a criar ou alterar um Servlet, a IA costuma focar unicamente naquele arquivo isolado, esquecendo de avaliar impactos colaterais na camada de visualização (JSP), nas chaves do banco de dados (`Efficientia.sql`) ou nas regras de encoding de filtros (`AuthFilter`).
- **Incompatibilidade silenciosa entre camadas:** Sugestões que funcionavam de forma isolada frequentemente quebravam o contrato da arquitetura global (por exemplo, esperar campos de formulário com nomes diferentes daqueles já consolidados no front-end).

### 3.2. Tendência a Alucinações Convincentes
A IA é treinada para gerar respostas plausíveis e persuasivas, o que se torna perigoso quando a resposta está tecnicamente errada:
- **Alucinação de contexto:** O modelo presumiu repetidamente o uso de anotações e bibliotecas de ecossistemas populares (Spring Boot, Hibernate, Lombok) inexistentes no projeto, ignorando que o edital exigia estritamente **Jakarta Servlets nativos e JDBC puro**.
- **Confusão de detalhes de baixo nível:** Mencionou algoritmos diferentes dos implementados no código (como citar BCrypt em vez de PBKDF2), exigindo leitura atenta do código-fonte para desmentir o texto gerado.

### 3.3. Amnésia Contextual e Limites de Janela de Contexto
Em conversas longas ou repositórios com muitos arquivos:
- A IA pode "esquecer" diretrizes e restrições acordadas em mensagens anteriores (como voltar a usar caminhos absolutos do Windows ou regredir na padronização de nomenclatura já consolidada no [NAMING_CONVENTION_DATABASE.md](NAMING_CONVENTION_DATABASE.md)).
- Esse comportamento exigiu que a equipe frequentemente reinjetasse restrições negativas nos prompts para manter a coerência das entregas.

### 3.4. Dificuldade com Validação de Regras de Domínio Específicas
A IA compreende padrões sintáticos universais (como a gramática do Java ou do SQL), mas tem dificuldade em interpretar regras de negócio muito particulares do agronegócio e transporte de animais vivos:
- A IA não compreende intuitivamente por que uma parada imprevista impacta diretamente o lote de animais ou por que uma Guia de Trânsito Animal (GTA) e uma Nota Fiscal possuem chaves numéricas com formatos estritos exigidos por órgãos fiscalizadores reais (MAPA/SEFAZ). Todas as regras de negócio pecuário tiveram que ser minuciosamente especificadas e validadas por humanos.

---

## 4. Limitações de Segurança e Privacidade

### 4.1. Impossibilidade de Submeter Dados Sensíveis e Segredos Reais
- Modelos de IA baseados em nuvem representam um risco caso o desenvolvedor compartilhe dados de ambiente (`.env`), senhas de banco ou dados pessoais reais em prompts.
- A equipe precisou impor a **restrição rígida de isolamento**: trabalhar apenas com dados fictícios do *dataload* e manter credenciais de banco estritamente locais e fora do alcance da IA.

---

## 5. Quadro Resumo: Limitações Identificadas vs. Ação Mitigadora da Equipe

| Categoria | Limitação Observada na IA | Impacto Potencial | Como a Equipe Mitigou no Projeto |
| :--- | :--- | :--- | :--- |
| **Pedagógica** | Risco de dependência acrítica e ilusão de aprendizado | Não aprender os conceitos fundamentais de POO, BD e SO | Estudo obrigatório do "o que faz / para que serve" e domínio de cada método commitado. |
| **Arquitetura** | Visão fragmentada de arquivos sem integridade global | Quebra de contratos entre JSP, Servlets e PostgreSQL | Especificação prévia em documentos (`SPEC_*`) antes de codificar. |
| **Técnica** | Alucinações de frameworks (Spring/Hibernate) | Código incompatível com o edital de Jakarta EE e JDBC | Prompts com restrições negativas rígidas amparados no `pom.xml`. |
| **Memória** | Perda de contexto e regras prévias em conversas longas | Regressão de padrões e convenções de código | Criação de documentos normativos fixos (`NAMING_CONVENTION`, `GUIA_PADRONIZACAO`). |
| **Negócio** | Desconhecimento de regras do domínio pecuário | Validações e fluxos logísticos irrealistas | Especificação detalhada humana das regras de GTA, bem-estar e transporte. |

---

## 6. Conclusão

A principal lição aprendida no projeto **Efficientia** foi que **a Inteligência Artificial deve atuar como copiloto acelerador, e nunca como piloto autônomo**. 

Se utilizada de forma passiva, a IA sabota o aprendizado do estudante e produz sistemas frágeis, desconexos e repletos de inconsistências sutis. Quando utilizada com postura crítica, planejamento arquitetural prévio e auditoria humana rigorosa em etapas (sintaxe, conteúdo e organização), a IA torna-se uma ferramenta transformadora, permitindo que estudantes do primeiro ano compreendam arquiteturas robustas e entreguem projetos com padrão profissional de engenharia.
