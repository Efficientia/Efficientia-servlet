(() => {
  'use strict';
  const form = document.getElementById('perfil-form');
  if (!form) return;
  const button = form.querySelector('button[type="submit"]');
  let enviando = false;
  const indisponivel = button.disabled;

  form.addEventListener('submit', (event) => {
    if (enviando || indisponivel) {
      event.preventDefault();
      return;
    }
    enviando = true;
    button.disabled = true;
    button.textContent = 'Salvando…';
    form.setAttribute('aria-busy', 'true');
  });

  window.addEventListener('pageshow', () => {
    enviando = false;
    button.disabled = indisponivel;
    button.textContent = 'Salvar alterações';
    form.removeAttribute('aria-busy');
  });

  const mensagem = document.querySelector('.perfil-mensagem');
  if (mensagem) mensagem.focus();
})();
