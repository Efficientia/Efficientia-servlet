/**
 * Gestão de Pecuaristas - Interatividade e Gerenciamento de Dados
 */

document.addEventListener('DOMContentLoaded', () => {
  // Lista inicial com os 10 pecuaristas exatamente como na imagem
  let pecuaristas = [
    { id: 1, nome: 'Antonio', cpf: '22233344401', email: 'antonio.dono@email.com', telefone: '67965432101', nascimento: '1970-01-15' },
    { id: 2, nome: 'Benedito', cpf: '22233344402', email: 'benedito.dono@email.com', telefone: '65965432102', nascimento: '1968-06-21' },
    { id: 3, nome: 'Claudio', cpf: '22233344403', email: 'claudio.dono@email.com', telefone: '62965432103', nascimento: '1975-09-03' },
    { id: 4, nome: 'Domingos', cpf: '22233344404', email: 'domingos.dono@email.com', telefone: '34965432104', nascimento: '1980-04-12' },
    { id: 5, nome: 'Eustaquio', cpf: '22233344405', email: 'eustaquio.dono@email.com', telefone: '17965432105', nascimento: '1965-12-01' },
    { id: 6, nome: 'Francisco', cpf: '22233344406', email: 'francisco.dono@email.com', telefone: '18965432106', nascimento: '1972-03-18' },
    { id: 7, nome: 'Geraldo', cpf: '22233344407', email: 'geraldo.dono@email.com', telefone: '44965432107', nascimento: '1978-08-29' },
    { id: 8, nome: 'Helio', cpf: '22233344408', email: 'helio.dono@email.com', telefone: '45965432108', nascimento: '1963-05-14' },
    { id: 9, nome: 'Ismael', cpf: '22233344409', email: 'ismael.dono@email.com', telefone: '11965432109', nascimento: '1974-10-07' },
    { id: 10, nome: 'Joaquim', cpf: '22233344410', email: 'joaquim.dono@email.com', telefone: '21965432110', nascimento: '1969-02-26' }
  ];

  // Elementos do DOM
  const tableBody = document.getElementById('tableBody');
  const tableEmptyState = document.getElementById('tableEmptyState');
  const totalRecordsBadge = document.getElementById('totalRecordsBadge');
  const tableSearchInput = document.getElementById('tableSearchInput');
  const btnBuscarTabela = document.getElementById('btnBuscarTabela');

  const form = document.getElementById('pecuaristaForm');
  const editingIdInput = document.getElementById('editingId');
  const nomeInput = document.getElementById('nomeInput');
  const cpfInput = document.getElementById('cpfInput');
  const nascimentoInput = document.getElementById('nascimentoInput');
  const emailInput = document.getElementById('emailInput');
  const telefoneInput = document.getElementById('telefoneInput');
  const senhaInput = document.getElementById('senhaInput');
  const btnLimpar = document.getElementById('btnLimpar');
  const btnSubmit = document.getElementById('btnSubmit');
  const formCardTitle = document.getElementById('formCardTitle');
  const togglePasswordBtn = document.getElementById('togglePasswordBtn');
  const toastContainer = document.getElementById('toastContainer');

  // Renderizar Tabela
  function renderTable(data = pecuaristas) {
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
          <td class="td-cpf">${item.cpf}</td>
          <td class="td-email">${item.email}</td>
          <td class="td-telefone">${item.telefone}</td>
          <td class="td-nasc">${item.nascimento}</td>
          <td class="td-acoes">
            <div class="action-buttons-cell">
              <button class="btn-action-edit" data-id="${item.id}" title="Editar pecuarista">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                Editar
              </button>
              <button class="btn-action-delete" data-id="${item.id}" title="Excluir pecuarista">
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
        const item = pecuaristas.find(p => p.id === id);
        if (item) {
          editingIdInput.value = item.id;
          nomeInput.value = item.nome;
          cpfInput.value = item.cpf;
          
          // Formatar data para DD/MM/AAAA se for YYYY-MM-DD
          if (item.nascimento.includes('-')) {
            const parts = item.nascimento.split('-');
            nascimentoInput.value = `${parts[2]}/${parts[1]}/${parts[0]}`;
          } else {
            nascimentoInput.value = item.nascimento;
          }

          emailInput.value = item.email;
          telefoneInput.value = item.telefone;
          senhaInput.value = ''; // Não carregar senha por segurança
          
          formCardTitle.textContent = `Editar Pecuarista #${item.id}`;
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
        const item = pecuaristas.find(p => p.id === id);
        if (item) {
          const confirmDelete = confirm(`Deseja realmente excluir o pecuarista "${item.nome}" (#${item.id})?`);
          if (confirmDelete) {
            pecuaristas = pecuaristas.filter(p => p.id !== id);
            
            // Se estiver editando o mesmo que foi excluído, resetar formulário
            if (editingIdInput.value == id) {
              resetForm();
            }

            renderTable(applySearchFilter());
            showToast(`Pecuarista "${item.nome}" excluído com sucesso!`, 'error');
          }
        }
      };
    });
  }

  // Busca na tabela
  function applySearchFilter() {
    const term = tableSearchInput.value.trim().toLowerCase();
    if (!term) return pecuaristas;

    return pecuaristas.filter(p => {
      return (
        p.nome.toLowerCase().includes(term) ||
        p.cpf.toLowerCase().includes(term) ||
        p.email.toLowerCase().includes(term) ||
        p.telefone.toLowerCase().includes(term) ||
        p.nascimento.toLowerCase().includes(term) ||
        p.id.toString() === term.replace('#', '')
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
    formCardTitle.textContent = 'Cadastrar Novo Pecuarista';
    btnSubmit.textContent = 'Cadastrar pecuarista';
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
    const cpf = cpfInput.value.replace(/\D/g, '');
    let nascimento = nascimentoInput.value.trim();
    const email = emailInput.value.trim();
    const telefone = telefoneInput.value.replace(/\D/g, '');
    const senha = senhaInput.value;
    const editingId = editingIdInput.value;

    // Validações básicas
    if (!nome) {
      showToast('Por favor, informe o Nome Completo.', 'error');
      nomeInput.focus();
      return;
    }

    if (!cpf || cpf.length < 11) {
      showToast('Por favor, informe um CPF válido com 11 dígitos.', 'error');
      cpfInput.focus();
      return;
    }

    if (!nascimento) {
      showToast('Por favor, informe a Data de Nascimento.', 'error');
      nascimentoInput.focus();
      return;
    }

    if (!email || !email.includes('@')) {
      showToast('Por favor, informe um e-mail válido.', 'error');
      emailInput.focus();
      return;
    }

    if (!telefone || telefone.length < 10) {
      showToast('Por favor, informe um telefone válido com DDD.', 'error');
      telefoneInput.focus();
      return;
    }

    if (!editingId && (!senha || senha.length < 4)) {
      showToast('Por favor, defina uma senha de acesso com no mínimo 4 caracteres.', 'error');
      senhaInput.focus();
      return;
    }

    // Normalizar formato de data para YYYY-MM-DD para consistência com a tabela
    let formattedDate = nascimento;
    if (nascimento.includes('/')) {
      const parts = nascimento.split('/');
      if (parts.length === 3) {
        formattedDate = `${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}`;
      }
    }

    if (editingId) {
      // Atualizar existente
      const idNum = parseInt(editingId, 10);
      const index = pecuaristas.findIndex(p => p.id === idNum);
      if (index !== -1) {
        pecuaristas[index] = {
          ...pecuaristas[index],
          nome,
          cpf,
          email,
          telefone,
          nascimento: formattedDate
        };
        showToast(`Pecuarista "${nome}" atualizado com sucesso!`, 'info');
      }
    } else {
      // Inserir novo
      const nextId = pecuaristas.length > 0 ? Math.max(...pecuaristas.map(p => p.id)) + 1 : 1;
      const novoPecuarista = {
        id: nextId,
        nome,
        cpf,
        email,
        telefone,
        nascimento: formattedDate
      };
      pecuaristas.push(novoPecuarista);
      showToast(`Pecuarista "${nome}" cadastrado com sucesso!`, 'success');
    }

    resetForm();
    renderTable(applySearchFilter());
  });

  // Máscaras de Digitação Simples
  // Máscara Data DD/MM/AAAA
  nascimentoInput.addEventListener('input', (e) => {
    let v = e.target.value.replace(/\D/g, '').slice(0, 8);
    if (v.length >= 5) {
      e.target.value = `${v.slice(0, 2)}/${v.slice(2, 4)}/${v.slice(4)}`;
    } else if (v.length >= 3) {
      e.target.value = `${v.slice(0, 2)}/${v.slice(2)}`;
    } else {
      e.target.value = v;
    }
  });

  // CPF: Apenas dígitos
  cpfInput.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/\D/g, '').slice(0, 11);
  });

  // Telefone: Apenas dígitos
  telefoneInput.addEventListener('input', (e) => {
    e.target.value = e.target.value.replace(/\D/g, '').slice(0, 11);
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
