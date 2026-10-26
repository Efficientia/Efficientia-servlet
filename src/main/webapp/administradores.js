/**
 * Gestão de Administradores - Interatividade e Gerenciamento de Dados
 */

document.addEventListener('DOMContentLoaded', () => {
  // Lista inicial com os 11 administradores da imagem
  let administradores = [
    { id: 1, nome: 'Ricardo', email: 'admin.ricardo@empresa.com', empresa: 'TransBoi Logística Ltda' },
    { id: 2, nome: 'Lucas', email: 'admin.lucas@empresa.com', empresa: 'AgroCarga Transportes' },
    { id: 3, nome: 'Felipe', email: 'admin.felipe@empresa.com', empresa: 'Boiadeiro Rápido S.A.' },
    { id: 4, nome: 'Gabriel', email: 'admin.gabriel@empresa.com', empresa: 'Pantanal Log Transporte Animal' },
    { id: 5, nome: 'André', email: 'admin.andre@empresa.com', empresa: 'Rota Boiadeiro Express' },
    { id: 6, nome: 'Guilherme', email: 'admin.guilherme@empresa.com', empresa: 'Gado Forte Rodoviário' },
    { id: 7, nome: 'Caio', email: 'admin.caio@empresa.com', empresa: 'Centro-Oeste Cargas Vivas' },
    { id: 8, nome: 'Sérgio', email: 'admin.sergio@empresa.com', empresa: 'Estrada Real Pecuária' },
    { id: 9, nome: 'Vinicius', email: 'admin.vinicius@empresa.com', empresa: 'Sul Logística de Rebanho' },
    { id: 10, nome: 'Leonardo', email: 'admin.leonardo@empresa.com', empresa: 'Norte Boi Soluções Logísticas' },
    { id: 11, nome: 'Administrador de teste Codex', email: 'codex.teste.1701993667077@example.invalid', empresa: 'Empresa de teste Codex 1701993667077' }
  ];

  // Elementos do DOM
  const tableBody = document.getElementById('tableBody');
  const tableEmptyState = document.getElementById('tableEmptyState');
  const totalRecordsBadge = document.getElementById('totalRecordsBadge');
  const tableSearchInput = document.getElementById('tableSearchInput');
  const btnBuscarTabela = document.getElementById('btnBuscarTabela');

  const form = document.getElementById('adminForm');
  const editingIdInput = document.getElementById('editingId');
  const nomeInput = document.getElementById('nomeInput');
  const emailInput = document.getElementById('emailInput');
  const senhaInput = document.getElementById('senhaInput');
  const empresaInput = document.getElementById('empresaInput');
  const btnLimpar = document.getElementById('btnLimpar');
  const btnSubmit = document.getElementById('btnSubmit');
  const formCardTitle = document.getElementById('formCardTitle');
  const togglePasswordBtn = document.getElementById('togglePasswordBtn');
  const toastContainer = document.getElementById('toastContainer');

  // Renderizar Tabela
  function renderTable(data = administradores) {
    tableBody.innerHTML = '';

    if (data.length === 0) {
      tableEmptyState.style.display = 'block';
    } else {
      tableEmptyState.style.display = 'none';

      data.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
          <td class="td-id">#${item.id}</td>
          <td class="td-nome">${item.nome}</td>
          <td class="td-email">${item.email}</td>
          <td class="td-telefone">${item.empresa}</td>
          <td class="td-acoes">
            <div class="action-buttons-cell">
              <button class="btn-action-edit" data-id="${item.id}" title="Editar administrador">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                Editar
              </button>
              <button class="btn-action-delete" data-id="${item.id}" title="Excluir administrador">
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
        const item = administradores.find(a => a.id === id);
        if (item) {
          editingIdInput.value = item.id;
          nomeInput.value = item.nome;
          emailInput.value = item.email;
          empresaInput.value = item.empresa;
          senhaInput.value = ''; // Não carregar senha por segurança
          
          formCardTitle.textContent = `Editar Administrador #${item.id}`;
          btnSubmit.textContent = 'Salvar Alterações';
          
          // Rolar suavemente até o formulário
          form.scrollIntoView({ behavior: 'smooth', block: 'center' });
          nomeInput.focus();
          
          showToast(`Editando dados de ${item.nome}`, 'info');
        }
      };
    });

    // Excluir
    document.querySelectorAll('.btn-action-delete').forEach(btn => {
      btn.onclick = () => {
        const id = parseInt(btn.getAttribute('data-id'), 10);
        const item = administradores.find(a => a.id === id);
        if (item) {
          const confirmDelete = confirm(`Deseja realmente excluir o administrador "${item.nome}" (#${item.id})?`);
          if (confirmDelete) {
            administradores = administradores.filter(a => a.id !== id);
            
            // Se estiver editando o mesmo que foi excluído, resetar formulário
            if (editingIdInput.value == id) {
              resetForm();
            }

            renderTable(applySearchFilter());
            showToast(`Administrador "${item.nome}" excluído com sucesso!`, 'error');
          }
        }
      };
    });
  }

  // Busca na tabela
  function applySearchFilter() {
    const term = tableSearchInput.value.trim().toLowerCase();
    if (!term) return administradores;

    return administradores.filter(a => {
      return (
        a.nome.toLowerCase().includes(term) ||
        a.email.toLowerCase().includes(term) ||
        a.empresa.toLowerCase().includes(term) ||
        a.id.toString() === term.replace('#', '')
      );
    });
  }

  tableSearchInput.addEventListener('input', () => {
    renderTable(applySearchFilter());
  });

  btnBuscarTabela.addEventListener('click', () => {
    renderTable(applySearchFilter());
  });

  // SVGs para Alternar Visibilidade da Senha
  const eyeOpenSvg = `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
      <circle cx="12" cy="12" r="3"></circle>
    </svg>
  `;

  const eyeClosedSvg = `
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
      <line x1="1" y1="1" x2="23" y2="23"></line>
    </svg>
  `;

  // Alternar Visibilidade da Senha
  togglePasswordBtn.addEventListener('click', () => {
    const isPassword = senhaInput.type === 'password';
    senhaInput.type = isPassword ? 'text' : 'password';
    togglePasswordBtn.innerHTML = isPassword ? eyeClosedSvg : eyeOpenSvg;
  });

  // Limpar Formulário
  function resetForm() {
    form.reset();
    editingIdInput.value = '';
    formCardTitle.textContent = 'Cadastrar Novo Admin';
    btnSubmit.textContent = 'Cadastrar Admin';
    senhaInput.type = 'password';
    togglePasswordBtn.innerHTML = eyeOpenSvg;
  }

  btnLimpar.addEventListener('click', () => {
    resetForm();
    showToast('Formulário limpo com sucesso.', 'info');
  });

  // Validação e Envio do Formulário
  form.addEventListener('submit', (e) => {
    e.preventDefault();

    const nome = nomeInput.value.trim();
    const email = emailInput.value.trim();
    const senha = senhaInput.value;
    const empresa = empresaInput.value.trim() || 'Sem vínculo com alguma empresa';
    const editingId = editingIdInput.value;

    // Validações básicas
    if (!nome) {
      showToast('Por favor, informe o Nome Completo.', 'error');
      nomeInput.focus();
      return;
    }

    if (!email || !email.includes('@')) {
      showToast('Por favor, informe um e-mail válido.', 'error');
      emailInput.focus();
      return;
    }

    if (!editingId && (!senha || senha.length < 4)) {
      showToast('Por favor, defina uma senha de acesso com no mínimo 4 caracteres.', 'error');
      senhaInput.focus();
      return;
    }

    if (editingId) {
      // Atualizar existente
      const idNum = parseInt(editingId, 10);
      const index = administradores.findIndex(a => a.id === idNum);
      if (index !== -1) {
        administradores[index] = {
          ...administradores[index],
          nome,
          email,
          empresa
        };
        showToast(`Administrador "${nome}" atualizado com sucesso!`, 'info');
      }
    } else {
      // Inserir novo
      const nextId = administradores.length > 0 ? Math.max(...administradores.map(a => a.id)) + 1 : 1;
      const novoAdmin = {
        id: nextId,
        nome,
        email,
        empresa
      };
      administradores.push(novoAdmin);
      showToast(`Administrador "${nome}" cadastrado com sucesso!`, 'success');
    }

    resetForm();
    renderTable(applySearchFilter());
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

  // Inicializar Tabela com os 11 registros
  renderTable();
});
