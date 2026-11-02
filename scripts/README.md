# Conferência visual da landing

Requisitos: Node.js 22 ou superior e Google Chrome instalado. A ferramenta é exclusiva de QA; não participa do WAR.

```powershell
npm ci --prefix scripts
node scripts/qa-landing.mjs after
```

O script inicia e encerra um servidor estático local e abre uma única aba por vez. Registra viewport, DPR 1, zoom 100%, imagens carregadas, medidas, contraste, erros de JavaScript, navegação e carrossel. Captura a página inteira e as três transições em 1920, 1680, 1440, 1366, 1024, 860, 768, 430, 390 e 360px, com altura de viewport de 1024px.

Os arquivos ficam em `output/fidelity/after/`, ignorados pelo Git. Use outro argumento para separar uma baseline: `node scripts/qa-landing.mjs before`. As verificações numéricas são complementares à revisão das capturas completas.

Os links de autenticação exigem o servlet: este servidor verifica o frontend estático e não simula login ou conexão com o banco. Para compilar o projeto, use o Maven Wrapper com JDK 25. A suíte `ConnectionFactoryTest` requer o ambiente PostgreSQL configurado.
