const assert = require('node:assert/strict');
const { execFile } = require('node:child_process');
const { readFile } = require('node:fs/promises');
const http = require('node:http');
const path = require('node:path');
const { promisify } = require('node:util');
const { setTimeout: esperar } = require('node:timers/promises');
const test = require('node:test');

const executar = promisify(execFile);
const navegador = process.env.AGENT_BROWSER_BIN;

test('confirmação de exclusão no navegador', { skip: !navegador, timeout: 60000 }, async t => {
  const envios = [];
  const script = await readFile(path.join(__dirname, '../../main/webapp/js/painel.js'));
  const html = `<!doctype html><html lang="pt-BR"><meta charset="utf-8"><title>Teste de exclusão</title>
    <form id="primeiro" action="/excluir" method="post" data-confirm="Excluir &lt;b&gt;registro&lt;/b&gt;?">
      <input name="id" value="1" type="hidden"><button name="acao" value="excluir">Excluir primeiro</button>
    </form>
    <form id="segundo" action="/excluir" method="post" data-confirm="Excluir segundo?">
      <input name="id" value="2" type="hidden"><button name="acao" value="excluir">Excluir segundo</button>
    </form>
    <form id="invalido" data-confirm="Excluir inválido?">
      <input name="obrigatorio" required aria-label="Obrigatório"><button>Excluir inválido</button>
    </form>
    <script>document.addEventListener('submit', e => { if (window.cancelarEnvio) e.preventDefault(); });</script>
    <script src="/painel.js"></script></html>`;
  const servidor = http.createServer((req, res) => {
    if (req.url === '/painel.js') {
      res.writeHead(200, { 'Content-Type': 'text/javascript; charset=UTF-8' }).end(script);
    } else if (req.method === 'POST') {
      let corpo = '';
      req.on('data', parte => { corpo += parte; });
      req.on('end', () => {
        envios.push(Object.fromEntries(new URLSearchParams(corpo)));
        res.writeHead(204).end();
      });
    } else {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=UTF-8' }).end(html);
    }
  });
  await new Promise(resolve => servidor.listen(0, '127.0.0.1', resolve));
  const sessao = `exclusao-teste-${process.pid}`;
  async function browser(...args) {
    const { stdout } = await executar(navegador, ['--session', sessao, '--json', ...args], { timeout: 15000 });
    const resposta = JSON.parse(stdout);
    assert.equal(resposta.success, true, stdout);
    return resposta.data.result;
  }
  async function abrir() {
    envios.length = 0;
    await browser('open', `http://127.0.0.1:${servidor.address().port}/`);
  }
  async function aguardarEnvio() {
    for (let tentativa = 0; tentativa < 40 && envios.length === 0; tentativa++) await esperar(25);
    assert.equal(envios.length, 1);
  }
  t.after(async () => {
    await browser('close');
    servidor.closeAllConnections();
    await new Promise(resolve => servidor.close(resolve));
  });

  await t.test('reutiliza o diálogo, escapa a mensagem e devolve o foco ao cancelar', async () => {
    await abrir();
    await browser('click', '#primeiro button');
    assert.equal(await browser('eval', "document.querySelectorAll('dialog').length"), 1);
    assert.equal(await browser('eval', "document.querySelector('#mensagem-modal-exclusao').textContent"), 'Excluir <b>registro</b>?');
    assert.equal(await browser('eval', "document.activeElement.matches('.modal-exclusao-cancelar')"), true);
    await browser('click', '.modal-exclusao-cancelar');
    assert.equal(await browser('eval', "document.activeElement.matches('#primeiro button')"), true);
    await browser('click', '#segundo button');
    await browser('press', 'Escape');
    assert.equal(await browser('eval', "document.querySelector('dialog').open"), false);
    assert.equal(envios.length, 0);
  });

  await t.test('preserva a validação nativa e o cancelamento de outros listeners', async () => {
    await abrir();
    await browser('click', '#invalido button');
    assert.equal(await browser('eval', "document.querySelector('dialog').open"), false);
    await browser('eval', 'window.cancelarEnvio = true');
    await browser('click', '#primeiro button');
    await browser('click', '.modal-exclusao-confirmar');
    assert.equal(envios.length, 0);
    await browser('eval', 'window.cancelarEnvio = false');
    await browser('click', '#primeiro button');
    assert.equal(await browser('eval', "document.querySelector('dialog').open"), true);
    await browser('click', '.modal-exclusao-confirmar');
    await aguardarEnvio();
    assert.deepEqual(envios[0], { id: '1', acao: 'excluir' });
  });

  await t.test('envia uma vez, preserva o submitter e bloqueia novos envios até pageshow', async () => {
    await abrir();
    await browser('click', '#segundo button');
    await browser('eval', `document.querySelector('.modal-exclusao-confirmar').click();
      document.querySelector('#segundo').requestSubmit(document.querySelector('#segundo button'));`);
    await aguardarEnvio();
    assert.deepEqual(envios[0], { id: '2', acao: 'excluir' });
    assert.equal(await browser('eval', "document.querySelector('dialog').open"), false);
    await browser('eval', "window.dispatchEvent(new PageTransitionEvent('pageshow', { persisted: true }))");
    await browser('click', '#segundo button');
    assert.equal(await browser('eval', "document.querySelector('dialog').open"), true);
  });
});
