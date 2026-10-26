/**
 * Área Restrita — utilitários de interface (SEM dados de negócio).
 *
 * Os dados vêm do banco via Servlets/JSP. Este script cuida apenas de experiência de uso:
 * máscaras, olho de senha, conversão de data DD/MM/AAAA <-> AAAA-MM-DD, confirmação de
 * exclusão e toasts. Veja docs/SPEC_INTEGRACAO_FRONTEND_BACKEND.md.
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
    const alternar = () => {
      senhaInput.type = senhaInput.type === 'password' ? 'text' : 'password';
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
  document.querySelectorAll('form[data-confirm]').forEach(f => {
    f.addEventListener('submit', e => {
      if (!window.confirm(f.getAttribute('data-confirm'))) e.preventDefault();
    });
  });

  // Em modo edição, leva o usuário até o formulário
  const editando = document.querySelector('form.cadastro-form[data-modo="edicao"]');
  if (editando) editando.scrollIntoView({ behavior: 'smooth', block: 'center' });
});
