<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.efficientia.efficientia.model.TrajetoModel" %>

<%
    DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dtfDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    request.setAttribute("dtfDate", dtfDate);
    request.setAttribute("dtfTime", dtfTime);
    request.setAttribute("dtfDateTime", dtfDateTime);
%>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar Relatório de Embarque e Desembarque #${trajetoModel.id} | Efficientia</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=IBM+Plex+Sans:wght@300;400;500;600;700&family=Manrope:wght@500;600;700;800;900&family=Caveat:wght@600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/teste.css">
</head>
<body>

<div class="container" style="max-width: 1020px;">
    <!-- Barra Superior -->
    <header class="header">
        <div class="header-title-box">
            <h1>Efficientia <span>| Controle de Transporte</span></h1>
            <p>Edição de Relatório de Embarque e Desembarque</p>
        </div>
        <div style="display: flex; gap: 0.8rem;">
            <button type="button" class="btn-voltar" onclick="window.print()">
                🖨️ Imprimir / PDF
            </button>
            <a href="${pageContext.request.contextPath}/teste.jsp?servlet=trajeto" class="btn-voltar">
                ⚡ Painel de Testes
            </a>
            <a href="${pageContext.request.contextPath}/trajeto" class="btn-voltar">
                &larr; Voltar para Lista
            </a>
        </div>
    </header>

    <!-- FORMULÁRIO ESTILO DOCUMENTO OFICIAL -->
    <form action="${pageContext.request.contextPath}/trajeto" method="post" id="formEditarDocumento">
        <input type="hidden" name="acao" value="atualizar">
        <input type="hidden" name="id" value="${trajetoModel.id}">

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
                        <c:choose>
                            <c:when test="${not empty trajetoModel.id && trajetoModel.id > 0}">
                                ${trajetoModel.id}
                            </c:when>
                            <c:otherwise>33235</c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- LINHA DE STATUS OPERACIONAL -->
                <div class="doc-row" style="background: rgba(30, 41, 59, 0.04); border-bottom: 2px solid #000; padding: 0.5rem 0.75rem; align-items: center; justify-content: space-between;">
                    <div style="display: flex; align-items: center; gap: 0.75rem;">
                        <span class="doc-cell-label" style="margin: 0; font-weight: 700; color: #1e293b;">STATUS DO TRANSPORTE:</span>
                        <select name="status" class="doc-cell-select" style="font-weight: 700; font-size: 0.9rem; padding: 0.35rem 0.75rem; border-radius: 4px; border: 2px solid #000; background: #fff; cursor: pointer;" required>
                            <option value="EM_ANDAMENTO" ${trajetoModel.status == 'EM_ANDAMENTO' || trajetoModel.status == null ? 'selected' : ''}>EM ANDAMENTO</option>
                            <option value="CONCLUIDA" ${trajetoModel.status == 'CONCLUIDA' ? 'selected' : ''}>CONCLUÍDA</option>
                        </select>
                    </div>
                    <span style="font-size: 0.8rem; color: #475569; font-style: italic;">
                        ${trajetoModel.status == 'CONCLUIDA' ? '✓ Viagem finalizada no frigorífico' : '⚡ Caminhão em rota de transporte'}
                    </span>
                </div>

                <!-- LINHA 1: PECUARISTA -->
                <div class="doc-row">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Nome pecuarista:</span>
                        <select name="idPecuarista" class="doc-cell-select" required>
                            <option value="">Selecione o pecuarista...</option>
                            <c:forEach var="pec" items="${pecuaristaModels}">
                                <option value="${pec.id}" ${trajetoModel.pecuarista != null && trajetoModel.pecuarista.id == pec.id ? 'selected' : ''}>
                                    ${pec.nome} (CPF: ${pec.cpf})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <!-- LINHA 2: PLACAS CAVALO E CARRETA -->
                <div class="doc-row">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Caminhão:</span>
                        <select id="doc-select-caminhao" name="idCaminhao" class="doc-cell-select" required onchange="atualizarPlacas()">
                            <option value="">Selecione o caminhão...</option>
                            <c:forEach var="cam" items="${caminhaoModels}">
                                <option value="${cam.id}" 
                                        data-cavalo="${cam.placaCavalo}" 
                                        data-carreta="${cam.placaCarreta}"
                                        ${trajetoModel.caminhaoModel != null && trajetoModel.caminhaoModel.id == cam.id ? 'selected' : ''}>
                                    Cavalo: ${cam.placaCavalo} | Carreta: ${cam.placaCarreta}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Placa carreta:</span>
                        <input type="text" id="doc-placa-carreta" class="doc-cell-input" readonly value="${trajetoModel.caminhaoModel != null ? trajetoModel.caminhaoModel.placaCarreta : ''}">
                    </div>
                </div>

                <!-- LINHA 3: DATAS E HORÁRIOS DE EMBARQUE -->
                <div class="doc-row">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Data e Hora embarque:</span>
                        <input type="datetime-local" name="horarioEmbarque" class="doc-cell-input" 
                               value="${trajetoModel.horarioEmbarque != null ? dtfDateTime.format(trajetoModel.horarioEmbarque) : ''}" required>
                    </div>
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Horário de saída da propriedade:</span>
                        <input type="datetime-local" name="dataHoraInicio" class="doc-cell-input"
                               value="${trajetoModel.dataHoraInicio != null ? dtfDateTime.format(trajetoModel.dataHoraInicio) : ''}">
                    </div>
                </div>

                <!-- LINHA 4: MOTORISTA E DOCUMENTAÇÃO -->
                <div class="doc-row">
                    <div class="doc-cell" style="flex: 1.2;">
                        <span class="doc-cell-label">Código/ Motorista:</span>
                        <select name="idMotorista" class="doc-cell-select" required>
                            <option value="">Selecione o motorista...</option>
                            <c:forEach var="mot" items="${motoristaModels}">
                                <option value="${mot.id}" ${trajetoModel.motoristaModel != null && trajetoModel.motoristaModel.id == mot.id ? 'selected' : ''}>
                                    Cód. #${mot.id} - ${mot.nome}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="doc-cell" style="flex: 0.9;">
                        <span class="doc-cell-label">Nº GTA:</span>
                        <input type="text" name="numeroGTA" class="doc-cell-input" value="<c:out value='${trajetoModel.numeroGTA}' />" required>
                    </div>
                    <div class="doc-cell" style="flex: 0.9;">
                        <span class="doc-cell-label">Nº nota fiscal:</span>
                        <input type="text" name="numeroNotaFiscal" class="doc-cell-input" value="<c:out value='${trajetoModel.numeroNotaFiscal}' />" required>
                    </div>
                </div>

                <!-- LINHA 5: QUILOMETRAGEM -->
                <div class="doc-row">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Km saída do embarcadouro:</span>
                        <input type="number" name="kmSaida" class="doc-cell-input" value="${trajetoModel.kmSaida}">
                    </div>
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Km chegada desembarcadouro:</span>
                        <input type="number" name="kmChegada" class="doc-cell-input" value="${trajetoModel.kmChegada}">
                    </div>
                </div>

                <!-- SEÇÃO 2: ANIMAIS / EMBARQUE -->
                <div class="doc-section-header">ANIMAIS / EMBARQUE</div>
                
                <div class="doc-row" style="background: #fafafa;">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Machos:</span>
                        <input type="number" id="qtdMacho" name="qtdMacho" class="doc-cell-input calc-total" min="0" value="${trajetoModel.qtdMacho}" oninput="calcularTotalAnimais()">
                    </div>
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Fêmeas:</span>
                        <input type="number" id="qtdFemea" name="qtdFemea" class="doc-cell-input calc-total" min="0" value="${trajetoModel.qtdFemea}" oninput="calcularTotalAnimais()">
                    </div>
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Marrucos:</span>
                        <input type="number" id="qtdMarruco" name="qtdMarruco" class="doc-cell-input calc-total" min="0" value="${trajetoModel.qtdMarruco}" oninput="calcularTotalAnimais()">
                    </div>
                    <div class="doc-cell" style="flex: 1; background: #f3f4f6;">
                        <span class="doc-cell-label">Total:</span>
                        <strong id="totalAnimais" style="font-size: 1rem; color: #111827; padding-left: 0.5rem;">
                            ${trajetoModel.qtdMacho + trajetoModel.qtdFemea + trajetoModel.qtdMarruco}
                        </strong>
                    </div>
                </div>

                <!-- CHECKLIST DE EMBARQUE -->
                <div class="doc-section-header" style="font-size: 0.76rem; background: #f3f4f6;">INFORMAÇÕES EMBARQUE</div>

                <div class="doc-checks-grid">
                    <div class="doc-checks-col">
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_sangrando"> Animal sangrando
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_mancando"> Animal mancando
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_gaiola_cheia"> Gaiola muito cheia
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_nenhum_emb" checked> Nenhum
                        </label>
                    </div>

                    <div class="doc-checks-col">
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_magreza"> Excesso de magreza ou debilitado
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_sujo"> Animal sujo e/ou com sinal de pisoteio
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_chute"> Chute/ paulada/ uso de ferrão
                        </label>
                        <div style="display: flex; align-items: center; gap: 0.3rem;">
                            <label class="doc-check-item">
                                <input type="checkbox" name="chk_outro_emb"> Outro:
                            </label>
                            <input type="text" class="doc-cell-input" style="font-size: 0.75rem;">
                        </div>
                    </div>

                    <div class="doc-checks-col">
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_cutucoes"> Cutucões fortes
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_cauda"> Tentativa de quebrar a cauda
                        </label>
                        <label class="doc-check-item">
                            <input type="checkbox" name="chk_arraste"> Arraste
                        </label>
                    </div>
                </div>

                <!-- ASSINATURA RESPONSÁVEL DA FAZENDA -->
                <div class="doc-signatures-section" style="padding-bottom: 0.5rem;">
                    <div style="max-width: 420px; margin: 1rem auto 0.2rem auto;">
                        <input type="text" class="doc-sign-input" value="${trajetoModel.pecuarista != null ? trajetoModel.pecuarista.assinatura : ''}" readonly>
                        <div class="doc-sign-line">Assinatura do responsável (fazenda)</div>
                    </div>
                </div>

                <!-- SEÇÃO 3: INFORMAÇÕES VIAGEM -->
                <div class="doc-section-header">INFORMAÇÕES VIAGEM</div>

                <div class="doc-row" style="flex-wrap: wrap;">
                    <div class="doc-cell" style="gap: 0.8rem; flex: 1.5; border-right: 1px solid #000;">
                        <span class="doc-cell-label">Parada imprevista?</span>
                        <label class="doc-check-item"><input type="radio" name="parada_imp" value="nao" checked> Não</label>
                        <label class="doc-check-item"><input type="radio" name="parada_imp" value="sim"> Sim</label>
                        
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
                        <input type="datetime-local" name="dataHoraFim" class="doc-cell-input"
                               value="${trajetoModel.dataHoraFim != null ? dtfDateTime.format(trajetoModel.dataHoraFim) : ''}">
                    </div>
                </div>

                <!-- SEÇÃO 4: INFORMAÇÕES DESEMBARQUE -->
                <div class="doc-section-header">INFORMAÇÕES DESEMBARQUE</div>

                <div class="doc-row">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Horário do desembarque:</span>
                        <input type="datetime-local" name="horarioDesembarque" class="doc-cell-input"
                               value="${trajetoModel.horarioDesembarque != null ? dtfDateTime.format(trajetoModel.horarioDesembarque) : ''}">
                    </div>
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Nº curral:</span>
                        <input type="text" name="numeroCurral" class="doc-cell-input" value="<c:out value='${trajetoModel.numeroCurral}' />" placeholder="Ex: Curral C-04">
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
                        <label class="doc-check-item"><input type="radio" name="sirene_re" value="sim" checked> Sim</label>
                        <label class="doc-check-item"><input type="radio" name="sirene_re" value="nao"> Não</label>
                    </div>
                </div>

                <!-- SEÇÃO 5: INFORMAÇÕES ANIMAIS -->
                <div class="doc-section-header">INFORMAÇÕES ANIMAIS</div>

                <div class="doc-row" style="background: #fafafa;">
                    <div class="doc-cell" style="flex: 1;">
                        <span class="doc-cell-label">Em pé:</span>
                        <input type="number" class="doc-cell-input" min="0" value="${trajetoModel.qtdMacho + trajetoModel.qtdFemea + trajetoModel.qtdMarruco}">
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
                        <strong>${trajetoModel.qtdMacho + trajetoModel.qtdFemea + trajetoModel.qtdMarruco}</strong>
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
                        <input type="text" name="assinaturaMotorista" class="doc-sign-input" 
                               value="<c:out value='${trajetoModel.assinaturaMotorista}' />" placeholder="Assinatura do motorista">
                        <div class="doc-sign-line">Assinatura do entregador responsável (motorista)</div>
                    </div>

                    <!-- Linha 2: Manobrista e Curraleiro -->
                    <div class="doc-sign-duo">
                        <div>
                            <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                <span style="font-size: 0.72rem; font-weight: 700;">Nome Manobrista:</span>
                                <input type="text" name="nomeManobrista" class="doc-cell-input" value="<c:out value='${trajetoModel.nomeManobrista}' />" placeholder="Nome do manobrista">
                            </div>
                            <input type="text" name="assinaturaManobrista" class="doc-sign-input" value="<c:out value='${trajetoModel.assinaturaManobrista}' />" placeholder="Assinatura">
                            <div class="doc-sign-line">Assinatura do entregador responsável (manobrista)</div>
                        </div>

                        <div>
                            <div style="display: flex; gap: 0.4rem; margin-bottom: 0.2rem; align-items: center;">
                                <span style="font-size: 0.72rem; font-weight: 700;">Nome Curraleiro:</span>
                                <input type="text" name="nomeCurraleiro" class="doc-cell-input" value="<c:out value='${trajetoModel.nomeCurraleiro}' />" placeholder="Nome do curraleiro">
                            </div>
                            <input type="text" name="assinaturaCurraleiro" class="doc-sign-input" value="<c:out value='${trajetoModel.assinaturaCurraleiro}' />" placeholder="Assinatura">
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
            <a href="${pageContext.request.contextPath}/trajeto" class="btn btn-secondary">
                Cancelar Edição
            </a>
            <div style="display: flex; gap: 0.8rem;">
                <button type="button" class="btn btn-secondary" onclick="window.print()">
                    🖨️ Imprimir Documento
                </button>
                <button type="submit" class="btn btn-primary">
                    💾 Salvar Alterações do Relatório
                </button>
            </div>
        </div>
    </form>
</div>

<script>
    function calcularTotalAnimais() {
        const m = parseInt(document.getElementById('qtdMacho').value) || 0;
        const f = parseInt(document.getElementById('qtdFemea').value) || 0;
        const mar = parseInt(document.getElementById('qtdMarruco').value) || 0;
        document.getElementById('totalAnimais').textContent = m + f + mar;
    }

    function atualizarPlacas() {
        const select = document.getElementById('doc-select-caminhao');
        const opt = select.options[select.selectedIndex];
        const carretaInput = document.getElementById('doc-placa-carreta');
        if (opt && opt.getAttribute('data-carreta')) {
            carretaInput.value = opt.getAttribute('data-carreta');
        } else {
            carretaInput.value = '';
        }
    }
</script>

</body>
</html>
