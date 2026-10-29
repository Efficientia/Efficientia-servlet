# Desenvolvimento Autônomo e Entregas Sem Uso de IA

> **Projeto:** Efficientia — Gestão Logística e Bem-Estar no Transporte Pecuário  
> **Disciplina:** Introdução à Inteligência Artificial / Projeto Interdisciplinar  
> **Nível:** 1º Ano — 2º Semestre (Instituto J&F / Germinatech)  
> **Data:** 29/10/2026  
> **Documentos relacionados:** [FERRAMENTAS_UTILIZADAS.md](FERRAMENTAS_UTILIZADAS.md), [LIMITACOES_USO_IA.md](LIMITACOES_USO_IA.md), [METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md](METODOLOGIA_DOCUMENTACAO_IA_E_VALIDACAO.md), [SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md](SEGURANCA_PRIVACIDADE_RESPONSABILIDADE.md)

---

## 1. Visão Geral

No desenvolvimento do projeto **Efficientia**, a Inteligência Artificial atuou como ferramenta de apoio pedagógico e consulta técnica. Contudo, os pilares centrais de engenharia, regras de negócio agropecuário, validação de integridade e segurança de dados foram **100% idealizados, concebidos e implementados pela equipe humana**, sem delegação autônoma para sistemas de IA.

A seguir, detalham-se as quatro frentes do projeto concebidas inteiramente de forma humana.

---

## 2. Frentes 100% Realizadas Sem IA

### 2.1. Concepção do Tema e Regras de Negócio Pecuário
* **Definição do Problema Real:** A formulação do escopo voltado para a logística e bem-estar no transporte rodoviário de gado bovino vivo (redução de estresse e perda de peso da carga, controle de paradas para hidratação e descanso) partiu da análise direta do setor agropecuário e do direcionamento acadêmico da instituição.
* **Critérios Sanitários e Fiscais Oficiais:** As exigências e regras de integridade do agronegócio foram modeladas pela equipe:
  * Formato numérico estrito da **Guia de Trânsito Animal (GTA)** contendo exatamente 14 dígitos, regulamentada pelo MAPA.
  * Formato estrito da **Nota Fiscal Eletrônica (NFe)** com 44 dígitos numéricos vinculada à carga.
  * Tolerâncias operacionais de pesagem, registros de mortalidade e ocorrências de paradas imprevistas.

### 2.2. Arquitetura de Banco de Dados e Modelagem Relacional
* **Modelagem Conceitual e Lógica (DER):** A determinação das entidades (`Empresa`, `Veiculo`, `Motorista`, `Viagem`, `Lote`, `Parada`, `Pesagem`), bem como o mapeamento de cardinalidades (1:N, N:M), chaves primárias e chaves estrangeiras.
* **Modelo Físico e Integridade Relacional no PostgreSQL:** O script DDL (`Efficientia.sql`), a seleção rigorosa de tipos de dados (`VARCHAR`, `NUMERIC`, `TIMESTAMP`), a definição de tipos enumerados nativos (`ENUM`) para status e a criação de *constraints* de integridade (`CHECK`, `NOT NULL`, `UNIQUE`) foram definidos pela equipe de acordo com a lógica relacional estudada em Banco de Dados.

### 2.3. Validação Humana, Homologação e Conhecimento Pedagógico
* **Protocolo de Validação Crítica em 3 Etapas:** Nenhuma sugestão de código ou documentação foi incorporada sem passar pelo crivo dos integrantes:
  1. *Auditoria de Sintaxe:* Inspeção de formatação, sanitização de caminhos absolutos e conformidade Markdown.
  2. *Auditoria de Conteúdo:* Identificação e expurgo de alucinações (como tentativas da IA de sugerir anotações do ecossistema Spring ou algoritmos não condizentes).
  3. *Auditoria de Organização:* Estruturação modular de diretórios e padronização arquitetural.
* **Domínio Intelectual Obrigatório:** Diretriz interna de que nenhum integrante aceita alterações ou commita arquivos que não seja capaz de defender, explicar a causa-raiz e demonstrar o funcionamento técnico durante sabatinas da banca avaliadora.

### 2.4. Configurações Locais, Ambientes e Proteção de Segredos
* **Manipulação Exclusiva de Ambientes:** Instalação, inicialização e configuração do servidor Apache Tomcat, variáveis locais e mapeamento de portas locais de rede foram realizados manualmente.
* **Segurança e Privacidade de Dados:** Credenciais reais do banco de dados PostgreSQL e arquivos de configuração sensíveis (`.env`) foram tratados de forma restrita e estritamente local, jamais sendo compartilhados com provedores externos de IA ou incluídos em conversas de chat.
