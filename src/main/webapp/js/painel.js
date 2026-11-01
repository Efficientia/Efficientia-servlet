/**
 * Área Restrita — utilitários de interface (SEM dados de negócio).
 *
 * Os dados vêm do banco via Servlets/JSP. Este script cuida apenas de experiência de uso:
 * busca interativa nos cards da Home, máscaras, alternância de visibilidade de senha com ícones,
 * conversão de data DD/MM/AAAA <-> AAAA-MM-DD, confirmação de exclusão e toasts informativos.
 * Veja docs/poo/SPEC_INTEGRACAO_FRONTEND_BACKEND.md.
 */
document.addEventListener('DOMContentLoaded', () => {
  const toastContainer = document.getElementById('toastContainer');

  // ---------------------------------------------------------------- Toasts
  function dispensarToast(toast) {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }

  function showToast(message, type = 'success') {
    if (!toastContainer) return;
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.setAttribute('role', type === 'error' ? 'alert' : 'status');
    toast.textContent = message;
    toastContainer.appendChild(toast);
    setTimeout(() => dispensarToast(toast), 3500);
  }

  // Toasts renderizados pelo servidor (fragments/toasts.jspf) também somem sozinhos
  if (toastContainer) {
    toastContainer.querySelectorAll('.toast').forEach(t => setTimeout(() => dispensarToast(t), 4500));
  }

  // Remove ?sucesso= / ?erro= da barra de endereço para o toast não reaparecer ao atualizar a página
  const url = new URL(window.location.href);
  if (url.searchParams.has('sucesso') || url.searchParams.has('erro')) {
    url.searchParams.delete('sucesso');
    url.searchParams.delete('erro');
    window.history.replaceState({}, '', url.pathname + url.search);
  }

  // ------------------------------------------------ Busca nos cards da Home
  const topSearchInput = document.getElementById('topSearchInput');
  const homeCards = document.querySelectorAll('.home-card');
  if (topSearchInput && homeCards.length > 0) {
    topSearchInput.addEventListener('input', (e) => {
      const query = e.target.value.toLowerCase().trim();

      homeCards.forEach((card) => {
        const cardText = card.textContent.toLowerCase();
        const cardTags = card.getAttribute('data-card-title') || '';

        if (!query || cardText.includes(query) || cardTags.includes(query)) {
          card.style.display = 'flex';
        } else {
          card.style.display = 'none';
        }
      });
    });
  }

  // --------------------------------------------------------------- Máscaras
  document.querySelectorAll('[data-mask="digits"]').forEach(input => {
    const max = parseInt(input.getAttribute('data-maxlen'), 10) || 20;
    input.addEventListener('input', () => {
      input.value = input.value.replace(/\D/g, '').slice(0, max);
    });
  });

  document.querySelectorAll('[data-mask="placa"]').forEach(input => {
    input.addEventListener('input', () => {
      input.value = input.value.toUpperCase().replace(/[^A-Z0-9-]/g, '').slice(0, 8);
    });
  });

  const dataVisivel = document.getElementById('nascimentoInput');
  const dataOculta = document.getElementById('dataNascimentoHidden');

  function isoParaBr(iso) {
    const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(iso || '');
    return m ? `${m[3]}/${m[2]}/${m[1]}` : '';
  }

  function brParaIso(br) {
    const m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(br || '');
    if (!m) return '';
    const [, d, mo, a] = m;
    const dt = new Date(`${a}-${mo}-${d}T00:00:00`);
    const valida = dt.getFullYear() === +a && dt.getMonth() + 1 === +mo && dt.getDate() === +d;
    return valida ? `${a}-${mo}-${d}` : '';
  }

  if (dataVisivel && dataOculta) {
    // Modo edição: o servidor envia a data em ISO no campo oculto
    if (dataOculta.value) dataVisivel.value = isoParaBr(dataOculta.value);

    dataVisivel.addEventListener('input', () => {
      const v = dataVisivel.value.replace(/\D/g, '').slice(0, 8);
      if (v.length >= 5) dataVisivel.value = `${v.slice(0, 2)}/${v.slice(2, 4)}/${v.slice(4)}`;
      else if (v.length >= 3) dataVisivel.value = `${v.slice(0, 2)}/${v.slice(2)}`;
      else dataVisivel.value = v;
    });
  }

  // ------------------------------------------------------------ Olho de senha
  const toggleBtn = document.getElementById('togglePasswordBtn');
  const senhaInput = document.getElementById('senhaInput');
  if (toggleBtn && senhaInput) {
    const eyeOpenSvg = `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
        <circle cx="12" cy="12" r="3"></circle>
      </svg>
    `;
    const eyeClosedSvg = `
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16">
        <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
        <line x1="1" y1="1" x2="23" y2="23"></line>
      </svg>
    `;

    const alternar = () => {
      const isPassword = senhaInput.type === 'password';
      senhaInput.type = isPassword ? 'text' : 'password';
      toggleBtn.innerHTML = isPassword ? eyeClosedSvg : eyeOpenSvg;
    };
    toggleBtn.addEventListener('click', alternar);
    toggleBtn.addEventListener('keydown', e => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        alternar();
      }
    });
  }

  // ------------------------------------------------ Validação leve no cliente
  // (a validação definitiva por REGEX continua no Servlet — ValidadorRegex)
  const form = document.querySelector('form.cadastro-form');
  if (form) {
    form.addEventListener('submit', e => {
      const faltando = Array.from(form.querySelectorAll('[required]'))
        .find(el => !el.value || !el.value.trim());
      if (faltando) {
        e.preventDefault();
        const rotulo = form.querySelector(`label[for="${faltando.id}"]`);
        const nome = rotulo ? rotulo.textContent.replace('*', '').trim() : 'campo obrigatório';
        showToast(`Preencha o campo: ${nome}.`, 'error');
        faltando.focus();
        return;
      }

      if (dataVisivel && dataOculta) {
        const iso = brParaIso(dataVisivel.value.trim());
        if (!iso) {
          e.preventDefault();
          showToast('Informe uma data de nascimento válida (DD/MM/AAAA).', 'error');
          dataVisivel.focus();
          return;
        }
        dataOculta.value = iso;
      }
    });
  }

  // --------------------------------------------------- Limpar formulário (cadastro)
  const btnLimpar = document.getElementById('btnLimpar');
  if (btnLimpar && btnLimpar.tagName === 'BUTTON' && form) {
    btnLimpar.addEventListener('click', () => {
      form.reset();
      if (dataOculta) dataOculta.value = '';
      showToast('Formulário limpo.', 'info');
    });
  }

  // -------------------------------------------------- Confirmação de exclusão
  const formulariosExclusao = document.querySelectorAll('form[data-confirm]');

  if (formulariosExclusao.length > 0) {
    const modal = document.createElement('dialog');
    modal.className = 'modal-confirmacao-exclusao';
    modal.setAttribute('aria-labelledby', 'titulo-modal-exclusao');
    modal.setAttribute('aria-describedby', 'mensagem-modal-exclusao');

    modal.innerHTML = `
      <div class="modal-exclusao-conteudo">
        <div class="modal-exclusao-icone" aria-hidden="true"></div>
        <h2 id="titulo-modal-exclusao">Confirmar exclusão</h2>
        <p id="mensagem-modal-exclusao"></p>
        <div class="modal-exclusao-acoes">
          <button type="button" class="modal-exclusao-cancelar">Cancelar</button>
          <button type="button" class="modal-exclusao-confirmar">Excluir</button>
        </div>
      </div>
    `;

    document.body.appendChild(modal);

    const mensagem = modal.querySelector('#mensagem-modal-exclusao');
    const cancelar = modal.querySelector('.modal-exclusao-cancelar');
    const confirmar = modal.querySelector('.modal-exclusao-confirmar');

    const formulariosAutorizados = new WeakSet();

    let formularioPendente = null;
    let botaoPendente = null;

    formulariosExclusao.forEach(formulario => {
      formulario.addEventListener('submit', evento => {
        if (formulariosAutorizados.has(formulario) || evento.defaultPrevented) {
          return;
        }

        evento.preventDefault();

        formularioPendente = formulario;
        botaoPendente = evento.submitter;

        mensagem.textContent = formulario.dataset.confirm ||
          'Deseja realmente excluir este registro?';

        modal.showModal();
        cancelar.focus();
      });
    });

    cancelar.addEventListener('click', () => modal.close());

    modal.addEventListener('close', () => {
      formularioPendente = null;
      botaoPendente = null;
    });

    confirmar.addEventListener('click', () => {
      if (!formularioPendente) return;

      const formulario = formularioPendente;
      const botao = botaoPendente;

      modal.close();

      formularioPendente = null;
      botaoPendente = null;

      formulariosAutorizados.add(formulario);

      try {
        if (botao && botao.form === formulario) {
          formulario.requestSubmit(botao);
        } else {
          formulario.requestSubmit();
        }
      } finally {
        formulariosAutorizados.delete(formulario);
      }
    });
  }

  // Em modo edição, leva o usuário até o formulário
  const editando = document.querySelector('form.cadastro-form[data-modo="edicao"]');
  if (editando) editando.scrollIntoView({ behavior: 'smooth', block: 'center' });
});
