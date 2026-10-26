/**
 * Gestão de Empresas - Interatividade e Gerenciamento de Dados
 */

document.addEventListener('DOMContentLoaded', () => {
  // Lista inicial com 10 empresas do ramo agropecuário/logístico
  let empresas = [
    { id: 1, razaoSocial: 'TransBoi Logística Ltda', cnpj: '12345678000190', dataCadastro: '2023-01-15' },
    { id: 2, razaoSocial: 'AgroTransportes Brasil Ltda', cnpj: '23456789000101', dataCadastro: '2022-06-21' },
    { id: 3, razaoSocial: 'Boi Forte Transportes e Fretes', cnpj: '34567890000112', dataCadastro: '2021-09-03' },
    { id: 4, razaoSocial: 'Express Boiadeiro Cargas', cnpj: '45678901000123', dataCadastro: '2023-04-12' },
    { id: 5, razaoSocial: 'Carga Viva Logística Rodoviária', cnpj: '56789012000134', dataCadastro: '2020-12-01' },
    { id: 6, razaoSocial: 'Rota Rural Transportes', cnpj: '67890123000145', dataCadastro: '2022-03-18' },
    { id: 7, razaoSocial: 'Pantanal Logística Integrada', cnpj: '78901234000156', dataCadastro: '2021-08-29' },
    { id: 8, razaoSocial: 'Centro-Oeste Express Cargas', cnpj: '89012345000167', dataCadastro: '2023-05-14' },
    { id: 9, razaoSocial: 'Sul Boi Transportadora Ltda', cnpj: '90123456000178', dataCadastro: '2022-10-07' },
    { id: 10, razaoSocial: 'Estrela do Campo AgroLogística', cnpj: '01234567000189', dataCadastro: '2021-02-26' }
  ];

  // Elementos do DOM
  const tableBody = document.getElementById('tableBody');
  const tableEmptyState = document.getElementById('tableEmptyState');
  const totalRecordsBadge = document.getElementById('totalRecordsBadge');
  const tableSearchInput = document.getElementById('tableSearchInput');
  const btnBuscarTabela = document.getElementById('btnBuscarTabela');

  const form = document.getElementById('empresaForm');
  const editingIdInput = document.getElementById('editingId');
  const razaoSocialInput = document.getElementById('razaoSocialInput');
  const cnpjInput = document.getElementById('cnpjInput');
  const btnLimpar = document.getElementById('btnLimpar');
  const btnSubmit = document.getElementById('btnSubmit');
  const formCardTitle = document.getElementById('formCardTitle');
  const toastContainer = document.getElementById('toastContainer');

  // Formatar CNPJ (XX.XXX.XXX/XXXX-XX ou exibição limpa)
  function formatarCNPJ(v) {
    const raw = v.replace(/\D/g, '');
    if (raw.length === 14) {
      return `${raw.slice(0, 2)}.${raw.slice(2, 5)}.${raw.slice(5, 8)}/${raw.slice(8, 12)}-${raw.slice(12)}`;
    }
    return raw;
  }

  // Renderizar Tabela
  function renderTable(data = empresas) {
    tableBody.innerHTML = '';

    if (data.length === 0) {
      tableEmptyState.style.display = 'block';
    } else {
      tableEmptyState.style.display = 'none';

      data.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
          <td class="td-id">#${item.id}</td>
          <td class="td-nome">${item.razaoSocial}</td>
          <td class="td-cpf">${formatarCNPJ(item.cnpj)}</td>
          <td class="td-nasc">${item.dataCadastro}</td>
          <td class="td-acoes">
            <div class="action-buttons-cell">
              <button class="btn-action-edit" data-id="${item.id}" title="Editar empresa">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                Editar
              </button>
              <button class="btn-action-delete" data-id="${item.id}" title="Excluir empresa">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                  <line x1="10" y1="11" x2="10" y2="17"></line>
                  <line x1="14" y1="11" x2="14" y2="17"></line>
                </svg>
                Excluir
              </button>
            </div>
          </td>
        `;
        tableBody.appendChild(row);
      });
    }

    // Atualizar contador de registros
    totalRecordsBadge.textContent = `${data.length} ${data.length === 1 ? 'registro' : 'registros'}`;

    // Eventos dos botões da tabela
    attachTableActions();
  }

  // Vincular eventos aos botões de Editar e Excluir
  function attachTableActions() {
    // Editar
    document.querySelectorAll('.btn-action-edit').forEach(btn => {
      btn.onclick = () => {
        const id = parseInt(btn.getAttribute('data-id'), 10);
        const item = empresas.find(e => e.id === id);
        if (item) {
          editingIdInput.value = item.id;
          razaoSocialInput.value = item.razaoSocial;
          cnpjInput.value = item.cnpj;
          
          formCardTitle.textContent = `Editar Empresa #${item.id}`;
          btnSubmit.textContent = 'Salvar Alterações';
          
          // Rolar suavemente até o formulário
          form.scrollIntoView({ behavior: 'smooth', block: 'center' });
          razaoSocialInput.focus();
          
          showToast(`Editando dados de ${item.razaoSocial}`, 'info');
        }
      };
    });

    // Excluir
    document.querySelectorAll('.btn-action-delete').forEach(btn => {
      btn.onclick = () => {
        const id = parseInt(btn.getAttribute('data-id'), 10);
        const item = empresas.find(e => e.id === id);
        if (item) {
          const confirmDelete = confirm(`Deseja realmente excluir a empresa "${item.razaoSocial}" (#${item.id})?`);
          if (confirmDelete) {
            empresas = empresas.filter(e => e.id !== id);
            
            // Se estiver editando a mesma que foi excluída, resetar formulário
            if (editingIdInput.value == id) {
              resetForm();
            }

            renderTable(applySearchFilter());
            showToast(`Empresa "${item.razaoSocial}" excluída com sucesso!`, 'error');
          }
        }
      };
    });
  }

  // Busca na tabela
  function applySearchFilter() {
    const term = tableSearchInput.value.trim().toLowerCase();
    if (!term) return empresas;

    const termDigits = term.replace(/\D/g, '');

    return empresas.filter(e => {
      return (
        e.razaoSocial.toLowerCase().includes(term) ||
        e.cnpj.includes(termDigits || term) ||
        e.dataCadastro.toLowerCase().includes(term) ||
        e.id.toString() === term.replace('#', '')
      );
    });
  }

  tableSearchInput.addEventListener('input', () => {
    renderTable(applySearchFilter());
  });

  btnBuscarTabela.addEventListener('click', () => {
    renderTable(applySearchFilter());
  });

  // Limpar Formulário
  function resetForm() {
    form.reset();
    editingIdInput.value = '';
    formCardTitle.textContent = 'Cadastrar Nova Empresa';
    btnSubmit.textContent = 'Cadastrar Empresa';
  }

  btnLimpar.addEventListener('click', () => {
    resetForm();
    showToast('Formulário limpo com sucesso.', 'info');
  });

  // Validação e Envio do Formulário
  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const razaoSocial = razaoSocialInput.value.trim();
    const cnpj = cnpjInput.value.replace(/\D/g, '');
    const editingId = editingIdInput.value;

    // Validações básicas
    if (!razaoSocial) {
      showToast('Por favor, informe a Razão Social / Nome da empresa.', 'error');
      razaoSocialInput.focus();
      return;
    }

    if (!cnpj || cnpj.length < 14) {
      showToast('Por favor, informe um CNPJ válido com 14 dígitos.', 'error');
      cnpjInput.focus();
      return;
    }

    if (editingId) {
      // Atualizar existente
      const idNum = parseInt(editingId, 10);
      const index = empresas.findIndex(e => e.id === idNum);
      if (index !== -1) {
        empresas[index] = {
          ...empresas[index],
          razaoSocial,
          cnpj
        };
        showToast(`Empresa "${razaoSocial}" atualizada com sucesso!`, 'info');
      }
    } else {
      // Inserir nova
      const nextId = empresas.length > 0 ? Math.max(...empresas.map(e => e.id)) + 1 : 1;
      const today = new Date().toISOString().split('T')[0];
      const novaEmpresa = {
        id: nextId,
        razaoSocial,
        cnpj,
        dataCadastro: today
      };
      empresas.push(novaEmpresa);
      showToast(`Empresa "${razaoSocial}" cadastrada com sucesso!`, 'success');
    }

    resetForm();
    renderTable(applySearchFilter());
  });

  // Apenas dígitos no CNPJ (máximo 14)
  cnpjInput.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/\D/g, '').slice(0, 14);
  });

  // Exibir Toasts Informativos
  function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }

  // Inicializar Tabela com os 10 registros
  renderTable();
});
