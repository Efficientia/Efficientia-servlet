/**
 * Gestão de Caminhões - Interatividade e Gerenciamento de Dados
 */

document.addEventListener('DOMContentLoaded', () => {
  // Lista inicial com os 10 caminhões da imagem
  let caminhoes = [
    { id: 1, empresa: 'TransBoi Logística Ltda EMP001', placaCavalo: 'BRA1A12', placaCarreta: 'BRA1A11', capacidade: 40 },
    { id: 2, empresa: 'TransBoi Logística Ltda EMP001', placaCavalo: 'BRA2B22', placaCarreta: 'BRA2B21', capacidade: 45 },
    { id: 3, empresa: 'AgroCarga Transportes EMP002', placaCavalo: 'BRA3C32', placaCarreta: 'BRA3C31', capacidade: 50 },
    { id: 4, empresa: 'AgroCarga Transportes EMP002', placaCavalo: 'BRA4D42', placaCarreta: 'BRA4D41', capacidade: 40 },
    { id: 5, empresa: 'Boiadeiro Rápido S.A. EMP003', placaCavalo: 'BRA5E52', placaCarreta: 'BRA5E51', capacidade: 38 },
    { id: 6, empresa: 'Pantanal Log Transporte Animal EMP004', placaCavalo: 'BRA6F62', placaCarreta: 'BRA6F61', capacidade: 42 },
    { id: 7, empresa: 'Rota Boiadeiro Express EMP005', placaCavalo: 'BRA7G72', placaCarreta: 'BRA7G71', capacidade: 48 },
    { id: 8, empresa: 'Gado Forte Rodoviário EMP006', placaCavalo: 'BRA8H82', placaCarreta: 'BRA8H81', capacidade: 52 },
    { id: 9, empresa: 'Centro-Oeste Cargas Vivas EMP007', placaCavalo: 'BRA9I92', placaCarreta: 'BRA9I91', capacidade: 40 },
    { id: 10, empresa: 'Estrada Real Pecuária EMP008', placaCavalo: 'BRA0J02', placaCarreta: 'BRA0J01', capacidade: 46 }
  ];

  // Elementos do DOM
  const tableBody = document.getElementById('tableBody');
  const tableEmptyState = document.getElementById('tableEmptyState');
  const totalRecordsBadge = document.getElementById('totalRecordsBadge');
  const tableSearchInput = document.getElementById('tableSearchInput');
  const btnBuscarTabela = document.getElementById('btnBuscarTabela');

  const form = document.getElementById('caminhaoForm');
  const editingIdInput = document.getElementById('editingId');
  const placaCavaloInput = document.getElementById('placaCavaloInput');
  const placaCarretaInput = document.getElementById('placaCarretaInput');
  const empresaInput = document.getElementById('empresaInput');
  const capacidadeInput = document.getElementById('capacidadeInput');
  const btnLimpar = document.getElementById('btnLimpar');
  const btnSubmit = document.getElementById('btnSubmit');
  const formCardTitle = document.getElementById('formCardTitle');
  const toastContainer = document.getElementById('toastContainer');

  // Renderizar Tabela
  function renderTable(data = caminhoes) {
    tableBody.innerHTML = '';

    if (data.length === 0) {
      tableEmptyState.style.display = 'block';
    } else {
      tableEmptyState.style.display = 'none';

      data.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
          <td class="td-id">#${item.id}</td>
          <td class="td-nome">${item.empresa}</td>
          <td><span class="plate-badge">${item.placaCavalo}</span></td>
          <td><span class="plate-badge">${item.placaCarreta}</span></td>
          <td class="td-nasc">${item.capacidade}</td>
          <td class="td-acoes">
            <div class="action-buttons-cell">
              <button class="btn-action-edit" data-id="${item.id}" title="Editar caminhão">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                Editar
              </button>
              <button class="btn-action-delete" data-id="${item.id}" title="Excluir caminhão">
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
        const item = caminhoes.find(c => c.id === id);
        if (item) {
          editingIdInput.value = item.id;
          placaCavaloInput.value = item.placaCavalo;
          placaCarretaInput.value = item.placaCarreta;
          empresaInput.value = item.empresa;
          capacidadeInput.value = item.capacidade;
          
          formCardTitle.textContent = `Editar Caminhão #${item.id}`;
          btnSubmit.textContent = 'Salvar Alterações';
          
          // Rolar suavemente até o formulário
          form.scrollIntoView({ behavior: 'smooth', block: 'center' });
          placaCavaloInput.focus();
          
          showToast(`Editando dados do caminhão ${item.placaCavalo}`, 'info');
        }
      };
    });

    // Excluir
    document.querySelectorAll('.btn-action-delete').forEach(btn => {
      btn.onclick = () => {
        const id = parseInt(btn.getAttribute('data-id'), 10);
        const item = caminhoes.find(c => c.id === id);
        if (item) {
          const confirmDelete = confirm(`Deseja realmente excluir o caminhão "${item.placaCavalo}" (#${item.id})?`);
          if (confirmDelete) {
            caminhoes = caminhoes.filter(c => c.id !== id);
            
            // Se estiver editando o mesmo que foi excluído, resetar formulário
            if (editingIdInput.value == id) {
              resetForm();
            }

            renderTable(applySearchFilter());
            showToast(`Caminhão "${item.placaCavalo}" excluído com sucesso!`, 'error');
          }
        }
      };
    });
  }

  // Busca na tabela
  function applySearchFilter() {
    const term = tableSearchInput.value.trim().toLowerCase();
    if (!term) return caminhoes;

    return caminhoes.filter(c => {
      return (
        c.placaCavalo.toLowerCase().includes(term) ||
        c.placaCarreta.toLowerCase().includes(term) ||
        c.empresa.toLowerCase().includes(term) ||
        c.capacidade.toString().includes(term) ||
        c.id.toString() === term.replace('#', '')
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
    formCardTitle.textContent = 'Cadastrar Novo Caminhão';
    btnSubmit.textContent = 'Cadastrar caminhão';
  }

  btnLimpar.addEventListener('click', () => {
    resetForm();
    showToast('Formulário limpo com sucesso.', 'info');
  });

  // Validação e Envio do Formulário
  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const placaCavalo = placaCavaloInput.value.trim().toUpperCase();
    const placaCarreta = placaCarretaInput.value.trim().toUpperCase();
    const empresa = empresaInput.value;
    const capacidade = parseInt(capacidadeInput.value, 10);
    const editingId = editingIdInput.value;

    // Validações básicas
    if (!placaCavalo || placaCavalo.length < 7) {
      showToast('Por favor, informe uma Placa do Cavalo válida.', 'error');
      placaCavaloInput.focus();
      return;
    }

    if (!placaCarreta || placaCarreta.length < 7) {
      showToast('Por favor, informe uma Placa da Carreta válida.', 'error');
      placaCarretaInput.focus();
      return;
    }

    if (!empresa) {
      showToast('Por favor, selecione a Empresa Proprietária.', 'error');
      empresaInput.focus();
      return;
    }

    if (!capacidade || capacidade <= 0) {
      showToast('Por favor, informe uma Capacidade Máxima válida.', 'error');
      capacidadeInput.focus();
      return;
    }

    if (editingId) {
      // Atualizar existente
      const idNum = parseInt(editingId, 10);
      const index = caminhoes.findIndex(c => c.id === idNum);
      if (index !== -1) {
        caminhoes[index] = {
          ...caminhoes[index],
          placaCavalo,
          placaCarreta,
          empresa,
          capacidade
        };
        showToast(`Caminhão "${placaCavalo}" atualizado com sucesso!`, 'info');
      }
    } else {
      // Inserir novo
      const nextId = caminhoes.length > 0 ? Math.max(...caminhoes.map(c => c.id)) + 1 : 1;
      const novoCaminhao = {
        id: nextId,
        empresa,
        placaCavalo,
        placaCarreta,
        capacidade
      };
      caminhoes.push(novoCaminhao);
      showToast(`Caminhão "${placaCavalo}" cadastrado com sucesso!`, 'success');
    }

    resetForm();
    renderTable(applySearchFilter());
  });

  // Caixa alta automática nos inputs de placa
  [placaCavaloInput, placaCarretaInput].forEach(inp => {
    inp.addEventListener('input', (e) => {
      e.target.value = e.target.value.toUpperCase();
    });
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
