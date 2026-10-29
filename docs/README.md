# Índice Geral da Documentação — Projeto Efficientia

> **Sistema:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Repositório Central de Documentação Técnica e Acadêmica**  

Bem-vindo à central de documentação do **Efficientia**. Todos os guias, especificações normativas, padrões de código, documentações de inteligência artificial e tutoriais passo a passo foram organizados nesta pasta para facilitar a navegação por avaliadores, professores e desenvolvedores.

---

## Sumário da Documentação por Categoria

### 1. Arquitetura, Tecnologias e Governança do Projeto
Documentos normativos que definem o ecossistema tecnológico, as convenções de código e o status de entrega do projeto:
* **[Tecnologias Utilizadas](TECNOLOGIAS_UTILIZADAS.md):** Especificação completa das tecnologias empregadas (Java, Jakarta Servlet, JSP/JSTL, JDBC, PostgreSQL, Dotenv, Regex, JUnit), contexto, justificativa técnica e localização no código.
* **[Análise do Código e Checklist de Entregas](ANALISE_E_CHECKLIST_ENTREGAS.md):** Comparativo detalhado das solicitações por disciplina do edital interdisciplinar (POO, BD1, Lógica, SO), mapeando o que está concluído e o plano de ação do que falta na aplicação Java.
* **[Guia de Padronização de Código](GUIA_PADRONIZACAO.md):** Normas de arquitetura MVC, separação em pacotes, padrões de nomenclatura, estilo de código Java e boas práticas.

---

### 2. Modelagem de Dados, SQL e Conformidade LGPD
Diretrizes e especificações sobre o banco de dados relacional PostgreSQL:
* **[Convenção de Nomenclatura do Banco (Naming Convention)](NAMING_CONVENTION_DATABASE.md):** Padrões obrigatórios de nomenclatura em `snake_case`, chaves primárias `id`, chaves estrangeiras `id_*`, constraints `CHECK`, `DEFAULT` e regras de modelagem.
* **[Documentação de Hashing de CPF e LGPD](DOCUMENTACAO_HASH_CPF_LGPD.md):** Mecanismo de pseudonimização do CPF com SHA-256 e salting para proteção da privacidade dos produtores rurais e motoristas.
* **[Implementação de ENUMs nos DAOs](DOCUMENTACAO_IMPLEMENTACAO_ENUMS_DAOS.md):** Guia de integração dos tipos enumerados customizados do PostgreSQL (`status_trajeto`, `status_analise`) com a camada JDBC via `Types.OTHER`.

---

### 3. Tratamento de Exceções e Integridade de Dados
Arquitetura de tolerância a falhas e conversão de erros de infraestrutura SQL para regras de negócio:
* **[Especificação de Tratamento de Exceções de Duplicidade](SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md):** Especificação arquitetural do mapeamento do código SQLState `23505` (`unique_violation`) para a hierarquia de exceções customizadas.
* **[Documentação de Tratamento de Exceções de Duplicidade](DOCUMENTACAO_TRATAMENTO_EXCECOES_DUPLICIDADE.md):** Guia prático detalhando as classes `DadoDuplicadoException`, `TratadorExcecaoBanco` e a integração com os Servlets e páginas de erro.

---

### 4. Validação de Entrada (REGEX) e Busca Dinâmica
Mecanismos de higienização de parâmetros e consulta flexível:
* **[Documentação de Validação por REGEX](DOCUMENTACAO_VALIDACAO_REGEX.md):** Documentação analítica das expressões regulares para validação de CPF, Celular sem +55, Placas (Mercosul/antiga), E-mails, CNPJ, GTA e Notas Fiscais.
* **[Documentação da Busca por Nome e Termos](DOCUMENTACAO_BUSCA_POR_NOME.md):** Mecanismo de busca flexível (nome completo, picado, insensível a maiúsculas/minúsculas e acentuação) implementado nas DAOs e Servlets.

---

### 5. Guias Didáticos Passo a Passo (Tutoriais)
Manuais práticos para desenvolvimento e manutenção contínua da aplicação:
* **[Passo a Passo: Como Implementar um DAO](passo-a-passo-dao.md):** Roteiro didático completo para criação de classes Data Access Object com JDBC puro, queries parametrizadas e tratamento de erros.
* **[Passo a Passo: Como Implementar um Servlet](passo-a-passo-servlet.md):** Roteiro prático para criação de Servlets com anotação `@WebServlet`, tratamento de GET/POST e padrão Post-Redirect-Get.

---

### 6. Evidências de Uso e Pensamento Crítico em IA
Documentação comprobatória da utilização estratégica e responsável de Inteligência Artificial:
* **[Metodologia de Documentação com IA e Protocolo de Validação Humana](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md):** Processo de geração, justificativa de necessidade e o protocolo de validação crítica em 3 passos do usuário (sintaxe, conteúdo e organização).
* **[Limitações Percebidas no Uso da IA](LIMITACOES_USO_IA.md):** Análise detalhada das limitações cognitivas, riscos de ilusão de competência/falha de aprendizado, alucinações técnicas e perda de visão global do sistema.
* **[Refinamento de Prompts e Riscos da Implementação Sem Plano](REFINAMENTO_PROMPTS_E_PLANEJAMENTO_IA.md):** Análise da necessidade real de correções e complementações, técnicas de engenharia de prompt iterativa e problemas da codificação sem planejamento prévio.
* **[Segurança, Privacidade e Responsabilidade (LGPD)](SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md):** Tratamento de dados pessoais, hashing de senhas, validação defensiva e mitigação de riscos no uso de IA.
* **[Registro de Uso de IA: Banco de Dados e Naming Convention](EXEMPLO_IA_BD.md):** Evidência de interação com IA para ideação, estruturação e refinamento crítico das regras do banco de dados PostgreSQL.
* **[Registro de Uso de IA: Validação por REGEX e Sistemas Operacionais](EXEMPLO_IA_SOP.md):** Evidência do processo de auditoria de código, varredura de dependências e validação crítica humana no utilitário de expressões regulares.

---

### 7. Interface Web e Integração Front-End × Back-End
Especificação da camada de visão da área restrita:
* **[Especificação de Integração Front-End × Back-End](SPEC_INTEGRACAO_FRONTEND_BACKEND.md):** Conversão das telas do Design System (PR #37) em JSPs protegidas e dinâmicas, mapeamento campo × parâmetro × banco, ajustes nos Servlets, estratégia de branches e roteiro de testes.

---

## 🔗 Navegação Rápida
* [Voltar para a Raiz do Projeto](../README.md)
