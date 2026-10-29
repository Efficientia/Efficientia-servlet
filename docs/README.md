# Índice Geral da Documentação — Projeto Efficientia

> **Sistema:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Repositório Central de Documentação Técnica e Acadêmica por Matéria**  

Bem-vindo à central de documentação do **Efficientia**. Todos os guias, especificações normativas, padrões de código, documentações de inteligência artificial e tutoriais passo a passo estão organizados em subpastas dedicadas por matéria do Projeto Interdisciplinar:

---

## Sumário da Documentação por Matéria

### 1. Programação Orientada a Objetos (POO) (`docs/poo/`)
Documentos, tutoriais e especificações de backend Java, arquitetura MVC, DAOs, Servlets e camada de visão Web:
* **[Passo a Passo: Como Implementar um DAO](poo/passo-a-passo-dao.md):** Roteiro didático completo para criação de classes Data Access Object com JDBC puro, queries parametrizadas e tratamento de erros.
* **[Passo a Passo: Como Implementar um Servlet](poo/passo-a-passo-servlet.md):** Roteiro prático para criação de Servlets com anotação `@WebServlet`, tratamento de GET/POST e padrão Post-Redirect-Get.
* **[Documentação da Busca por Nome e Termos](poo/DOCUMENTACAO_BUSCA_POR_NOME.md):** Mecanismo de busca flexível (nome completo, particionado, insensível a maiúsculas/minúsculas e acentuação) implementado nas DAOs e Servlets.
* **[Especificação de Integração Front-End × Back-End](poo/SPEC_INTEGRACAO_FRONTEND_BACKEND.md):** Conversão das telas do Design System em JSPs protegidas e dinâmicas, mapeamento campo × parâmetro × banco, ajustes nos Servlets e roteiro de testes.

---

### 2. Banco de Dados 1 (BD1) (`docs/bd1/`)
Diretrizes e especificações sobre o banco relacional PostgreSQL, modelagem, constraints e conformidade LGPD:
* **[Convenção de Nomenclatura do Banco (Naming Convention)](bd1/NAMING_CONVENTION_DATABASE.md):** Padrões obrigatórios de nomenclatura em `snake_case`, chaves primárias `id`, chaves estrangeiras `id_*`, constraints `CHECK`, `DEFAULT` e regras de modelagem.
* **[Implementação de ENUMs nos DAOs](bd1/DOCUMENTACAO_IMPLEMENTACAO_ENUMS_DAOS.md):** Guia de integração dos tipos enumerados customizados do PostgreSQL (`status_trajeto`, `status_analise`) com a camada JDBC via `Types.OTHER`.
* **[Documentação de Hashing de CPF e LGPD](bd1/DOCUMENTACAO_HASH_CPF_LGPD.md):** Mecanismo de pseudonimização do CPF com SHA-256 e salting para proteção da privacidade dos produtores rurais e motoristas.

---

### 3. Lógica de Programação (`docs/logica/`)
Arquitetura de tolerância a falhas, tratamento de exceções de integridade e conversão de erros de infraestrutura SQL para regras de negócio:
* **[Especificação de Tratamento de Exceções de Duplicidade](logica/SPEC_TRATAMENTO_EXCECOES_DUPLICIDADE.md):** Especificação arquitetural do mapeamento do código SQLState `23505` (`unique_violation`) para a hierarquia de exceções customizadas.
* **[Documentação de Tratamento de Exceções de Duplicidade](logica/DOCUMENTACAO_TRATAMENTO_EXCECOES_DUPLICIDADE.md):** Guia prático detalhando as classes `DadoDuplicadoException`, `TratadorExcecaoBanco` e a integração com os Servlets e páginas de erro.
* **[Especificação de Padronização de Exceções nos DAOs Auxiliares](logica/SPEC_PADRONIZACAO_EXCECOES_DAOS_AUXILIARES.md):** Especificação técnica para estender o tratamento centralizado aos DAOs auxiliares.
* **[Plano de Implementação de Exceções nos DAOs Auxiliares](logica/PLANO_IMPLEMENTACAO_EXCECOES_DAOS_AUXILIARES.md):** Checklist de execução e testes para os DAOs auxiliares.

---

### 4. Sistemas Operacionais (SO) (`docs/so/`)
Mecanismos de validação textual via expressões regulares e integração com ambiente/infraestrutura:
* **[Documentação de Validação por REGEX](so/DOCUMENTACAO_VALIDACAO_REGEX.md):** Documentação analítica das expressões regulares para validação de CPF, Celular nacional sem +55, Placas (Mercosul e antiga), E-mails, CNPJ, CEP, GTA e Notas Fiscais.

---

### 5. Inteligência Artificial (IA) (`docs/ia/`)
Documentação comprobatória da utilização estratégica, metodologia de validação humana e pensamento crítico no uso de IA:
* **[Metodologia de Documentação com IA e Protocolo de Validação Humana](ia/METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md):** Processo de geração, justificativa de necessidade e o protocolo de validação crítica em 3 passos do usuário (sintaxe, conteúdo e organização).
* **[Limitações Percebidas no Uso da IA](ia/LIMITACOES_USO_IA.md):** Análise detalhada das limitações cognitivas, riscos de ilusão de competência/falha de aprendizado, alucinações técnicas e perda de visão global do sistema.
* **[Refinamento de Prompts e Riscos da Implementação Sem Plano](ia/REFINAMENTO_PROMPTS_E_PLANEJAMENTO_IA.md):** Análise da necessidade real de correções e complementações, técnicas de engenharia de prompt iterativa e problemas da codificação sem planejamento prévio.
* **[Segurança, Privacidade e Responsabilidade (LGPD)](ia/SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md):** Tratamento de dados pessoais, hashing de senhas, validação defensiva e mitigação de riscos no uso de IA.
* **[Registro de Uso de IA: Banco de Dados e Naming Convention](ia/EXEMPLO_IA_BD.md):** Evidência de interação com IA para ideação, estruturação e refinamento crítico das regras do banco de dados PostgreSQL.
* **[Registro de Uso de IA: Validação por REGEX e Sistemas Operacionais](ia/EXEMPLO_IA_SOP.md):** Evidência do processo de auditoria de código, varredura de dependências e validação crítica humana no utilitário de expressões regulares.

---

### 6. Governança e Arquitetura Geral (`docs/geral/`)
Visão holística, conformidade curricular e padronização global do projeto:
* **[Tecnologias Utilizadas](geral/TECNOLOGIAS_UTILIZADAS.md):** Especificação completa das tecnologias empregadas (Java, Jakarta Servlet, JSP/JSTL, JDBC, PostgreSQL, Dotenv, Regex, JUnit), contexto, justificativa técnica e localização no código.
* **[Guia de Padronização de Código](geral/GUIA_PADRONIZACAO.md):** Normas de arquitetura MVC, separação em pacotes, padrões de nomenclatura, estilo de código Java e boas práticas.
* **[Análise do Código e Checklist de Entregas](geral/ANALISE_E_CHECKLIST_ENTREGAS.md):** Comparativo detalhado das solicitações por disciplina do edital interdisciplinar (POO, BD1, Lógica, SO), mapeando o que está concluído e o plano de ação do que falta na aplicação Java.

---

## 🔗 Navegação Rápida
* [Voltar para a Raiz do Projeto](../README.md)

