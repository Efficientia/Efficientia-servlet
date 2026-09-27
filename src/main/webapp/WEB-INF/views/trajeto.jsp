<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.efficientia.efficientia.model.TrajetoModel" %>

<%
    DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    DateTimeFormatter dtfInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    request.setAttribute("dtfDateTime", dtfDateTime);
    request.setAttribute("dtfInput", dtfInput);
%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatórios de Embarque e Desembarque | Efficientia</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600;700&family=Manrope:wght@500;600;700;800;900&family=Caveat:wght@600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teste.css">
</head>
<body>

<div class="container" style="max-width: 1040px;">
    <!-- Barra Superior -->
    <header class="header">
        <div class="header-title-box">
            <h1>Efficientia <span>| Gestão de Transporte</span></h1>
            <p>Emissão e Controle de Relatórios de Embarque e Desembarque</p>
        </div>
        <div style="display: flex; gap: 0.8rem;">
            <a href="${pageContext.request.contextPath}/teste.jsp?servlet=trajeto" class="btn-voltar">
                ⚡ Painel de Testes
            </a>
            <a href="${pageContext.request.contextPath}/" class="btn-voltar">
                &larr; Página Principal
            </a>
        </div>
    </header>

    <!-- Abas Internas -->
    <div class="tabs-header">
        <button type="button" id="tabBtnCriarDoc" class="tab-button active" onclick="alternarAbaTrajeto('criar')">
            <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Preencher Novo Relatório
        </button>
        <button type="button" id="tabBtnListarDoc" class="tab-button" onclick="alternarAbaTrajeto('listar')">
            <svg width="18" height="18" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 10h16M4 14h16M4 18h16" />
            </svg>
            Relatórios Cadastrados (${not empty trajetoModels ? trajetoModels.size() : 0})
        </button>
    </div>

    <!-- ==================================================================== -->
    <!-- ABA 1: FORMULÁRIO ESTILO DOCUMENTO FÍSICO JBS -->
    <!-- ==================================================================== -->
    <div id="abaCriarDoc">
        <form action="${pageContext.request.contextPath}/trajeto" method="post" id="formNovoDocumento">
            <input type="hidden" name="status" value="EM_ANDAMENTO">

            <div class="doc-sheet-wrapper">
                <div class="doc-sheet">
                    
                    <!-- CABEÇALHO DO DOCUMENTO -->
                    <div class="doc-header">
                        <div class="doc-logo-box">
                            <span class="doc-logo-pill">(( EFFICIENTIA ))</span>
                        </div>

                        <div class="doc-vias-box">
                            1ª Via Branca - TRP<br>
                            2ª Via Rosa - Frigorífico<br>
                            3ª Via Azul - Pecuarista
                        </div>

                        <div class="doc-title-box">
                            <h1>Relatório de Embarque e Desembarque</h1>
                        </div>

                        <div class="doc-number-box">
                            33235
                        </div>
                    </div>

                    <!-- LINHA 1: PECUARISTA -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Nome pecuarista:</span>
                            <select name="idPecuarista" class="doc-cell-select" required>
                                <option value="" disabled selected>Selecione o pecuarista...</option>
                                <c:forEach var="pec" items="${pecuaristaModels}">
                                    <option value="${pec.id}">${pec.nome} (CPF: ${pec.cpf})</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <!-- LINHA 2: PLACAS CAVALO E CARRETA -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Caminhão (Placa cavalo):</span>
                            <select id="novo-select-caminhao" name="idCaminhao" class="doc-cell-select" required onchange="atualizarPlacasNovo()">
                                <option value="" disabled selected>Selecione o caminhão...</option>
                                <c:forEach var="cam" items="${caminhaoModels}">
                                    <option value="${cam.id}" data-carreta="${cam.placaCarreta}">
                                        Cavalo: ${cam.placaCavalo} | Carreta: ${cam.placaCarreta}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Placa carreta:</span>
                            <input type="text" id="novo-placa-carreta" class="doc-cell-input" readonly placeholder="Preenchimento automático">
                        </div>
                    </div>

                    <!-- LINHA 3: DATAS E HORÁRIOS DE EMBARQUE -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Data e Hora embarque:</span>
                            <input type="datetime-local" name="horarioEmbarque" class="doc-cell-input" required>
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Horário de saída da propriedade:</span>
                            <input type="datetime-local" name="dataHoraInicio" class="doc-cell-input">
                        </div>
                    </div>

                    <!-- LINHA 4: MOTORISTA E DOCUMENTAÇÃO -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1.2;">
                            <span class="doc-cell-label">Código/ Motorista:</span>
                            <select name="idMotorista" class="doc-cell-select" required>
                                <option value="" disabled selected>Selecione o motorista...</option>
                                <c:forEach var="mot" items="${motoristaModels}">
                                    <option value="${mot.id}">Cód. #${mot.id} - ${mot.nome}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="doc-cell" style="flex: 0.9;">
                            <span class="doc-cell-label">Nº GTA:</span>
                            <input type="text" name="numeroGTA" class="doc-cell-input" placeholder="Ex: GTA-98741" required>
                        </div>
                        <div class="doc-cell" style="flex: 0.9;">
                            <span class="doc-cell-label">Nº nota fiscal:</span>
                            <input type="text" name="numeroNotaFiscal" class="doc-cell-input" placeholder="Ex: NF-45892" required>
                        </div>
                    </div>

                    <!-- LINHA 5: QUILOMETRAGEM -->
                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Km saída do embarcadouro:</span>
                            <input type="number" name="kmSaida" class="doc-cell-input" placeholder="Ex: 120000">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Km chegada desembarcadouro:</span>
                            <input type="number" name="kmChegada" class="doc-cell-input" placeholder="Ex: 120450">
                        </div>
                    </div>

                    <!-- SEÇÃO 2: ANIMAIS / EMBARQUE -->
                    <div class="doc-section-header">ANIMAIS / EMBARQUE</div>
                    
                    <div class="doc-row" style="background: #fafafa;">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Machos:</span>
                            <input type="number" id="novoQtdMacho" name="qtdMacho" class="doc-cell-input calc-total" min="0" value="0" oninput="calcularTotalAnimaisNovo()">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Fêmeas:</span>
                            <input type="number" id="novoQtdFemea" name="qtdFemea" class="doc-cell-input calc-total" min="0" value="0" oninput="calcularTotalAnimaisNovo()">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Marrucos:</span>
                            <input type="number" id="novoQtdMarruco" name="qtdMarruco" class="doc-cell-input calc-total" min="0" value="0" oninput="calcularTotalAnimaisNovo()">
                        </div>
                        <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                            <span class="doc-cell-label">Total:</span>
                            <strong id="novoTotalAnimais" style="font-size: 1rem; color: #111827; padding-left: 0.5rem;">0</strong>
                        </div>
                    </div>

                    <!-- CHECKLIST DE EMBARQUE -->
                    <div class="doc-section-header" style="font-size: 0.76rem; background: #f3f4f6;">INFORMAÇÕES EMBARQUE</div>

                    <div class="doc-checks-grid">
                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Animal sangrando
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Animal mancando
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Gaiola muito cheia
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox" checked> Nenhum
                            </label>
                        </div>

                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Excesso de magreza ou debilitado
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Animal sujo e/ou com sinal de pisoteio
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Chute/ paulada/ uso de ferrão
                            </label>
                            <div style="display: flex; align-items: center; gap: 0.3rem;">
                                <label class="doc-check-item">
                                    <input type="checkbox"> Outro:
                                </label>
                                <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                            </div>
                        </div>

                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Cutucões fortes
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Tentativa de quebrar a cauda
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Arraste
                            </label>
                        </div>
                    </div>

                    <!-- ASSINATURA RESPONSÁVEL DA FAZENDA -->
                    <div class="doc-signatures-section" style="padding-bottom: 0.5rem;">
                        <div style="max-width: 420px; margin: 1.2rem auto 0.2rem auto;">
                            <input type="text" class="doc-sign-input" placeholder="Assinatura da fazenda">
                            <div class="doc-sign-line">Assinatura do responsável (fazenda)</div>
                        </div>
                    </div>

                    <!-- SEÇÃO 3: INFORMAÇÕES VIAGEM -->
                    <div class="doc-section-header">INFORMAÇÕES VIAGEM</div>

                    <div class="doc-row" style="flex-wrap: wrap;">
                        <div class="doc-cell" style="gap: 0.8rem; flex: 1.5; border-right: 1px solid #000;">
                            <span class="doc-cell-label">Parada imprevista?</span>
                            <label class="doc-check-item"><input type="radio" name="novo_parada_imp" value="nao" checked> Não</label>
                            <label class="doc-check-item"><input type="radio" name="novo_parada_imp" value="sim"> Sim</label>
                            
                            <span style="font-size: 0.72rem; color: #4b5563; margin-left: 0.5rem;">Motivo:</span>
                            <label class="doc-check-item"><input type="checkbox"> Transbordo</label>
                            <label class="doc-check-item"><input type="checkbox"> Acidente na pista</label>
                            <label class="doc-check-item"><input type="checkbox"> Atoleiro</label>
                            <label class="doc-check-item"><input type="checkbox"> Problema mecânico</label>
                            <label class="doc-check-item"><input type="checkbox"> Nenhum</label>
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Data e Hora de chegada na unidade:</span>
                            <input type="datetime-local" name="dataHoraFim" class="doc-cell-input">
                        </div>
                    </div>

                    <!-- SEÇÃO 4: INFORMAÇÕES DESEMBARQUE -->
                    <div class="doc-section-header">INFORMAÇÕES DESEMBARQUE</div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Horário do desembarque:</span>
                            <input type="datetime-local" name="horarioDesembarque" class="doc-cell-input">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Nº curral:</span>
                            <input type="text" name="numeroCurral" class="doc-cell-input" placeholder="Ex: Curral C-04">
                        </div>
                    </div>

                    <div class="doc-checks-grid" style="grid-template-columns: 1.4fr 1fr;">
                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Gaiola com buraco, estrado solto e/ou com ponta levantada
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Uso abusivo do choque (na cara, no umbigo, genitálias e/ou ânus)
                            </label>
                            <div style="display: flex; align-items: center; gap: 0.3rem;">
                                <label class="doc-check-item">
                                    <input type="checkbox"> Outros atos de abuso:
                                </label>
                                <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                            </div>
                        </div>

                        <div class="doc-checks-col">
                            <label class="doc-check-item">
                                <input type="checkbox"> Porteiras não abrem totalmente
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox"> Problema mecânico no veículo
                            </label>
                            <label class="doc-check-item">
                                <input type="checkbox" checked> Nenhum
                            </label>
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1; gap: 1rem;">
                            <span class="doc-cell-label">A sirene de ré do veículo funcionou ao encostar no desembarcadouro?</span>
                            <label class="doc-check-item"><input type="radio" name="novo_sirene_re" value="sim" checked> Sim</label>
                            <label class="doc-check-item"><input type="radio" name="novo_sirene_re" value="nao"> Não</label>
                        </div>
                    </div>

                    <!-- SEÇÃO 5: INFORMAÇÕES ANIMAIS -->
                    <div class="doc-section-header">INFORMAÇÕES ANIMAIS</div>

                    <div class="doc-row" style="background: #fafafa;">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Em pé:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Deitado:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Morto:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Emergência:</span>
                            <input type="number" class="doc-cell-input" min="0" value="0">
                        </div>
                        <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                            <span class="doc-cell-label">Total:</span>
                            <strong id="novoTotalDesembarque">0</strong>
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Em caso de emergência, descrever o motivo:</span>
                            <input type="text" class="doc-cell-input" placeholder="Descreva aqui se houver emergência...">
                        </div>
                    </div>

                    <div class="doc-row">
                        <div class="doc-cell" style="flex: 1;">
                            <span class="doc-cell-label">Comentários (opcional):</span>
                            <input type="text" class="doc-cell-input" placeholder="Observações gerais sobre a viagem...">
                        </div>
                    </div>

                    <!-- SEÇÃO 6: ASSINATURAS -->
                    <div class="doc-signatures-section">
                        <!-- Linha 1: Assinatura do Motorista -->
                        <div style="max-width: 440px; margin: 1.5rem auto 0.5rem auto;">
                            <input type="text" name="assinaturaMotorista" class="doc-sign-input" placeholder="Assinatura do motorista">
                            <div class="doc-sign-line">Assinatura do entregador responsável (motorista)</div>
                        </div>

                        <!-- Linha 2: Manobrista e Curraleiro -->
                        <div class="doc-sign-duo">
                            <div>
                                <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                    <span style="font-size: 0.72rem; font-weight: 700;">Nome Manobrista:</span>
                                    <input type="text" name="nomeManobrista" class="doc-cell-input" placeholder="Nome do manobrista">
                                </div>
                                <input type="text" name="assinaturaManobrista" class="doc-sign-input" placeholder="Assinatura">
                                <div class="doc-sign-line">Assinatura do entregador responsável (manobrista)</div>
                            </div>

                            <div>
                                <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                    <span style="font-size: 0.72rem; font-weight: 700;">Nome Curraleiro:</span>
                                    <input type="text" name="nomeCurraleiro" class="doc-cell-input" placeholder="Nome do curraleiro">
                                </div>
                                <input type="text" name="assinaturaCurraleiro" class="doc-sign-input" placeholder="Assinatura">
                                <div class="doc-sign-line">Assinatura do recebedor (curraleiro) responsável</div>
                            </div>
                        </div>
                    </div>

                    <!-- OBSERVAÇÃO DO RODAPÉ -->
                    <div class="doc-footer-obs">
                        Obs: O proprietário do veículo é responsável pela documentação dos animais. Após a viagem, entregue imediatamente este comprovante.
                    </div>

                </div>
            </div>

            <!-- BARRA DE AÇÕES -->
            <div class="doc-actions-bar">
                <button type="reset" class="btn btn-secondary">
                    Limpar Formulário
                </button>
                <div style="display: flex; gap: 0.8rem;">
                    <button type="button" class="btn btn-secondary" onclick="window.print()">
                        🖨️ Imprimir
                    </button>
                    <button type="submit" class="btn btn-primary">
                        📝 Emitir / Cadastrar Relatório
                    </button>
                </div>
            </div>
        </form>
    </div>

    <!-- ==================================================================== -->
    <!-- ABA 2: LISTAGEM DE RELATÓRIOS CADASTRADOS -->
    <!-- ==================================================================== -->
    <div id="abaListarDoc" style="display: none; margin-top: 1.5rem;">
        <div class="list-section-header">
            <h2>Relatórios de Viagem Cadastrados</h2>
            <span class="badge-count">${not empty trajetoModels ? trajetoModels.size() : 0} registros</span>
        </div>

        <div class="table-responsive">
            <c:choose>
                <c:when test="${empty trajetoModels}">
                    <div class="empty-state">
                        <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                        </svg>
                        <p>Nenhum relatório cadastrado até o momento.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Relatório Nº</th>
                                <th>Status</th>
                                <th>Motorista</th>
                                <th>Caminhão</th>
                                <th>Pecuarista</th>
                                <th>Carga (M/F/Mar)</th>
                                <th>Doc (GTA/NF)</th>
                                <th style="text-align: center;">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${trajetoModels}">
                                <tr>
                                    <td><strong>#${t.id}</strong></td>
                                    <td>
                                        <span class="badge-status ${t.status == 'CONCLUIDA' ? 'badge-status-concluida' : 'badge-status-andamento'}">
                                            <c:out value="${t.status}" />
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.motoristaModel != null}">
                                                <strong><c:out value="${t.motoristaModel.nome}" /></strong>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.caminhaoModel != null}">
                                                <span class="text-code"><c:out value="${t.caminhaoModel.placaCavalo}" /></span>
                                            </c:when>
                                            <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.pecuarista != null}">
                                                <c:out value="${t.pecuarista.nome}" />
                                            </c:when>
                                            <c:otherwise><span class="text-muted">N/A</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="carga-badge-box">
                                            <span>M: <strong>${t.qtdMacho}</strong></span>
                                            <span>F: <strong>${t.qtdFemea}</strong></span>
                                            <span>Mar: <strong>${t.qtdMarruco}</strong></span>
                                            <span class="badge-total-animais">Total: ${t.qtdMacho + t.qtdFemea + t.qtdMarruco}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="font-size: 0.8rem; line-height: 1.3;">
                                            <div>GTA: <span class="text-code">${not empty t.numeroGTA ? t.numeroGTA : '-'}</span></div>
                                            <div>NF: <span class="text-code">${not empty t.numeroNotaFiscal ? t.numeroNotaFiscal : '-'}</span></div>
                                        </div>
                                    </td>
                                    <td style="text-align: center;">
                                        <div style="display: flex; align-items: center; justify-content: center; gap: 0.4rem;">
                                            <a href="${pageContext.request.contextPath}/trajeto?acao=editar&id=${t.id}" class="btn-sm-edit" style="text-decoration: none;">
                                                <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                                                </svg>
                                                Abrir / Editar
                                            </a>
                                            <form action="${pageContext.request.contextPath}/trajeto" method="post" style="display: inline;" onsubmit="return confirm('Tem certeza que deseja excluir o trajeto #${t.id}?');">
                                                <input type="hidden" name="acao" value="excluir">
                                                <input type="hidden" name="id" value="${t.id}">
                                                <button type="submit" class="btn-sm-danger">
                                                    <svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                    </svg>
                                                    Excluir
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
    function alternarAbaTrajeto(aba) {
        const btnCriar = document.getElementById('tabBtnCriarDoc');
        const btnListar = document.getElementById('tabBtnListarDoc');
        const divCriar = document.getElementById('abaCriarDoc');
        const divListar = document.getElementById('abaListarDoc');

        if (aba === 'criar') {
            btnCriar.classList.add('active');
            btnListar.classList.remove('active');
            divCriar.style.display = 'block';
            divListar.style.display = 'none';
        } else {
            btnCriar.classList.remove('active');
            btnListar.classList.add('active');
            divCriar.style.display = 'none';
            divListar.style.display = 'block';
        }
    }

    function calcularTotalAnimaisNovo() {
        const m = parseInt(document.getElementById('novoQtdMacho').value) || 0;
        const f = parseInt(document.getElementById('novoQtdFemea').value) || 0;
        const mar = parseInt(document.getElementById('novoQtdMarruco').value) || 0;
        const tot = m + f + mar;
        document.getElementById('novoTotalAnimais').textContent = tot;
        document.getElementById('novoTotalDesembarque').textContent = tot;
    }

    function atualizarPlacasNovo() {
        const select = document.getElementById('novo-select-caminhao');
        const opt = select.options[select.selectedIndex];
        const carretaInput = document.getElementById('novo-placa-carreta');
        if (opt && opt.getAttribute('data-carreta')) {
            carretaInput.value = opt.getAttribute('data-carreta');
        } else {
            carretaInput.value = '';
        }
    }
</script>

</body>
</html>
