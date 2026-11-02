import { createServer } from 'node:http';
import { readFile, mkdir, writeFile } from 'node:fs/promises';
import { resolve, extname, sep } from 'node:path';
import { chromium } from 'playwright';

const mode = process.argv[2] || 'after';
process.chdir(resolve(import.meta.dirname, '..'));
const root = resolve('src/main/webapp');
const out = resolve(`output/fidelity/${mode}`);
await mkdir(out, { recursive: true });
const mime = { '.html': 'text/html', '.css': 'text/css', '.js': 'text/javascript', '.svg': 'image/svg+xml', '.png': 'image/png', '.jpg': 'image/jpeg', '.webm': 'video/webm' };
const server = createServer(async (req, res) => {
    const path = resolve(root, '.' + decodeURIComponent(new URL(req.url, 'http://localhost').pathname).replace(/\/$/, '/index.html'));
    if (!path.startsWith(root + sep)) { res.writeHead(403).end(); return; }
    try { res.setHeader('Content-Type', mime[extname(path)] || 'application/octet-stream'); res.end(await readFile(path)); }
    catch { res.writeHead(404).end(); }
});
await new Promise(r => server.listen(0, '127.0.0.1', r));
const origin = `http://127.0.0.1:${server.address().port}`;
const browser = await chromium.launch({ channel: 'chrome', headless: true });
const results = [];
try {
    for (const width of [1920, 1680, 1440, 1366, 1024, 860, 768, 430, 390, 360]) {
        const page = await browser.newPage({ viewport: { width, height: 1024 }, deviceScaleFactor: 1, reducedMotion: 'reduce' });
        const errors = [];
        page.on('pageerror', e => errors.push(e.message));
        await page.goto(origin, { waitUntil: 'networkidle' });
        await page.evaluate(async () => {
            document.querySelectorAll('img[loading]').forEach(i => i.loading = 'eager');
            await document.fonts.ready;
            await Promise.all([...document.images].map(i => i.decode().catch(() => {})));
            document.querySelectorAll('video').forEach(v => { v.pause(); v.currentTime = 0; });
        });
        const geometry = await page.evaluate(() => {
            const box = selector => {
                const e = document.querySelector(selector); if (!e) return null;
                const b = e.getBoundingClientRect(); const s = getComputedStyle(e);
                return { x: b.x, y: b.y + scrollY, width: b.width, height: b.height, color: s.color, background: s.backgroundColor, fontSize: parseFloat(s.fontSize), fontWeight: Number(s.fontWeight), clip: s.overflow, pointerEvents: s.pointerEvents, zIndex: s.zIndex, transform: s.transform };
            };
            return {
                viewport: { width: innerWidth, height: innerHeight, dpr: devicePixelRatio, scale: visualViewport.scale },
                scrollWidth: document.documentElement.scrollWidth,
                sections: Object.fromEntries(['.hero', '.problema', '.solucao', '.impacto', '.funcionalidades', '.proximo-passo', '.rodape'].map(s => [s, box(s)])),
                content: Object.fromEntries(['.conteudo-hero', '.conteudo-problema', '.conteudo-solucao', '.conteudo-impacto', '.conteudo-funcionalidades', '.conteudo-proximo-passo'].map(s => [s, box(s)])),
                primary: box('.botoes-hero .botao-ativo'), secondary: box('.botoes-hero .botao-desativado'),
                video: box('.video-fundo'),
                curves: [...document.querySelectorAll('.landing-decoration img')].map(e => ({ src: e.getAttribute('src'), ...box(`[data-curve="${e.dataset.curve}"]`) })),
                brokenImages: [...document.images].filter(i => !i.complete || !i.naturalWidth).map(i => i.getAttribute('src')),
                visibleMockups: [...document.querySelectorAll('.hero-imagem img,.mockup-desktop,.card-solucao.active .imagem-card-solucao img,.img-impacto,.funcionalidades-produto,#mockup-celular-proximo-passo')].map(i => ({ src: i.getAttribute('src'), natural: [i.naturalWidth,i.naturalHeight], rect: { width: i.getBoundingClientRect().width, height: i.getBoundingClientRect().height } }))
            };
        });
        const failures = [];
        if (geometry.scrollWidth > width) failures.push('overflow horizontal');
        if (geometry.viewport.scale !== 1 || geometry.viewport.dpr !== 1) failures.push('zoom/DPR inesperado');
        if (geometry.curves.length !== 3) failures.push('três grupos decorativos compartilhados ausentes');
        const curveUnit = Math.max(.5, Math.min(1, width / 1440));
        const curveY = [-270.085 * curveUnit, geometry.sections['.problema'].y + 350.531 * curveUnit, geometry.sections['.solucao'].y + 389.152 * curveUnit];
        geometry.curves.forEach((curve, i) => {
            if (Math.abs(curve.y - curveY[i]) > 1) failures.push(`${curve.src}: posição vertical incorreta`);
            if (i > 0 && curve.transform !== 'matrix(-1, 0, 0, 1, 0, 0)') failures.push(`${curve.src}: espelhamento incorreto`);
            if (curve.pointerEvents !== 'none') failures.push(`${curve.src}: decoração intercepta interação`);
        });
        if (geometry.primary.color !== 'rgb(15, 81, 174)') failures.push('CTA primário com cor divergente/contraste insuficiente');
        const luminance = color => color.match(/[\d.]+/g).slice(0, 3).map(Number).map(v => v / 255).map(v => v <= .04045 ? v / 12.92 : ((v + .055) / 1.055) ** 2.4).reduce((sum, v, i) => sum + v * [.2126, .7152, .0722][i], 0);
        const fg = luminance(geometry.primary.color), bg = luminance(geometry.primary.background);
        const contrast = (Math.max(fg, bg) + .05) / (Math.min(fg, bg) + .05);
        const largeText = geometry.primary.fontSize >= 24 || geometry.primary.fontSize >= 18.666 && geometry.primary.fontWeight >= 700;
        if (contrast < (largeText ? 3 : 4.5)) failures.push(`contraste do CTA: ${contrast.toFixed(2)}:1`);
        if (geometry.video.zIndex !== '0') failures.push('vídeo pode ficar atrás da superfície da Hero');
        if (geometry.brokenImages.length) failures.push('imagens ausentes');
        if (width === 1440 && Math.abs(geometry.primary.width - 460) > 1) failures.push('CTA desktop fora da largura Figma');
        if (width === 1440 && Math.abs(geometry.primary.height - 80) > 1) failures.push('CTA desktop fora da altura Figma');
        if (width === 1440) {
            for (const key of ['.hero', '.problema', '.solucao', '.impacto']) {
                if (Math.abs(geometry.sections[key].height - 1024) > 1) failures.push(`${key}: altura difere do canvas Figma`);
            }
            const expectedX = [-636.559, -155.785, -507.192];
            geometry.curves.forEach((curve, i) => { if (Math.abs(curve.x - expectedX[i]) > 1) failures.push(`${curve.src}: posição horizontal difere da matriz Figma`); });
        }
        for (const mockup of geometry.visibleMockups) {
            if (!mockup.rect.width || !mockup.rect.height) failures.push(`${mockup.src}: mockup oculto`);
            if (mockup.rect.width && Math.abs(mockup.rect.width / mockup.rect.height - mockup.natural[0] / mockup.natural[1]) > .01) failures.push(`${mockup.src}: proporção distorcida`);
        }
        if (width <= 860) {
            await page.locator('.btn-menu-hamburguer').click();
            if (await page.locator('.btn-menu-hamburguer').getAttribute('aria-expanded') !== 'true') failures.push('menu não abre');
            await page.locator('.menu .link-menu[href="#solucao"]').click();
            if (await page.locator('.btn-menu-hamburguer').getAttribute('aria-expanded') !== 'false') failures.push('menu não fecha ao navegar');
            await page.locator('.btn-carousel-solucao.btn-next').click();
            await page.waitForTimeout(450);
            if (await page.locator('.card-solucao.active').getAttribute('data-index') !== '1') failures.push('próxima etapa falhou');
            await page.locator('.btn-carousel-solucao.btn-prev').focus();
            await page.keyboard.press('Enter');
            await page.waitForTimeout(450);
            if (await page.locator('.card-solucao.active').getAttribute('data-index') !== '0') failures.push('etapa anterior por teclado falhou');
            await page.locator('.dot-solucao[data-index="2"]').click();
            await page.waitForTimeout(450);
            if (await page.locator('.card-solucao.active').getAttribute('data-index') !== '2') failures.push('seleção direta falhou');
            await page.locator('.dot-solucao[data-index="0"]').click();
            await page.waitForTimeout(450);
        }
        await page.evaluate(() => scrollTo(0, 0));
        await page.screenshot({ path: `${out}/landing-${width}-full.png`, fullPage: true });
        for (const section of ['problema', 'solucao', 'impacto']) {
            await page.evaluate(s => scrollTo(0, Math.max(0, document.getElementById(s).offsetTop - 280)), section);
            await page.screenshot({ path: `${out}/landing-${width}-transition-${section}.png` });
        }
        if (errors.length) failures.push(...errors);
        results.push({ width, ...geometry, contrast, errors, failures });
        console.log(`${mode} ${width}x1024 DPR=1 zoom=1: ${failures.length ? failures.join('; ') : 'PASS'}`);
        await page.close();
    }
    await writeFile(`${out}/report.json`, JSON.stringify(results, null, 2));
} finally { await browser.close(); server.close(); }
if (results.some(r => r.failures.length)) process.exitCode = 1;
