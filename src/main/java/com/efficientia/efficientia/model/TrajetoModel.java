package com.efficientia.efficientia.model;

import java.time.LocalDateTime;

/**
 * Modelo representativo de um Trajeto / Viagem no sistema Efficientia.
 *
 * É a entidade central da logística de transporte pecuário. Consolida todos
 * os dados operacionais, fiscais e sanitários da viagem, incluindo:
 * - Agentes e Veículos: {@link MotoristaModel}, {@link CaminhaoModel} e {@link PecuaristaModel}.
 * - Documentação Obrigatória: Número da GTA (Guia de Trânsito Animal) e Nota Fiscal.
 * - Carga Viva: Quantitativos segregados por categoria (machos, fêmeas e marrucos).
 * - Controle Cronológico e Odômetro: Horários de embarque/desembarque, início/fim e Km saída/chegada.
 * - Recepção no Destino: Curral de desembarque, curraleiro e manobrista responsáveis.
 * - Validação Jurídica/Auditoria: Assinaturas digitais das partes envolvidas.
 */
public class TrajetoModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do trajeto (chave primária no banco de dados). */
    private int id;

    /** Motorista responsável pela condução do veículo durante o trajeto. */
    private MotoristaModel motoristaModel;

    /** Caminhão utilizado no transporte da carga viva. */
    private CaminhaoModel caminhaoModel;

    /** Status operacional do trajeto ({@link StatusTrajeto#EM_ANDAMENTO} ou {@link StatusTrajeto#CONCLUIDA}). */
    private StatusTrajeto status;

    /** Data e hora de início efetivo da viagem. */
    private LocalDateTime dataHoraInicio;

    /** Data e hora de conclusão da viagem (chegada ao destino). */
    private LocalDateTime dataHoraFim;

    /** Quilometragem do hodômetro no momento da saída da origem. */
    private int kmSaida;

    /** Quilometragem do hodômetro no momento da chegada ao destino. */
    private int kmChegada;

    /** Pecuarista/produtor rural remetente da carga viva. */
    private PecuaristaModel pecuaristaModel;

    /** Número da Guia de Trânsito Animal (documento sanitário oficial emitido pelo órgão competente). */
    private String numeroGTA;

    /** Número da Nota Fiscal referente aos animais transportados. */
    private String numeroNotaFiscal;

    /** Horário em que os animais foram embarcados no caminhão na propriedade rural. */
    private LocalDateTime horarioEmbarque;

    /** Quantidade de bovinos machos (bois/garrotes castrados) transportados. */
    private int qtdMacho;

    /** Quantidade de bovinos fêmeas (vacas/novilhas) transportadas. */
    private int qtdFemea;

    /** Quantidade de marrucos (touros reprodutores / machos inteiros não castrados) transportados. */
    private int qtdMarruco;

    /** Horário em que foi iniciado/concluído o desembarque dos animais no frigorífico/destino. */
    private LocalDateTime horarioDesembarque;

    /** Identificação ou número do curral de recepção no destino. */
    private String numeroCurral;

    /** Nome do funcionário responsável pelo manejo no curral (curraleiro). */
    private String nomeCurraleiro;

    /** Nome do manobrista responsável pelo posicionamento do caminhão na doca/curral. */
    private String nomeManobrista;

    /** Assinatura digital ou rubrica do curraleiro atestando a recepção dos animais. */
    private String assinaturaCurraleiro;

    /** Assinatura digital ou rubrica do manobrista. */
    private String assinaturaManobrista;

    /** Assinatura digital ou rubrica do motorista confirmando as etapas do transporte. */
    private String assinaturaMotorista;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na recuperação e hidratação do trajeto a partir do banco de dados.
     *
     * @param id                 identificador único do trajeto
     * @param motoristaModel     motorista condutor
     * @param caminhaoModel      caminhão utilizado
     * @param status             status da viagem (EM_ANDAMENTO / CONCLUIDA)
     * @param dataHoraInicio     data e hora de início
     * @param dataHoraFim        data e hora de encerramento
     * @param kmSaida            quilometragem na saída
     * @param kmChegada          quilometragem na chegada
     * @param pecuarista         pecuarista remetente
     * @param numeroGTA          número da Guia de Trânsito Animal (GTA)
     * @param numeroNotaFiscal   número da Nota Fiscal
     * @param horarioEmbarque    horário de embarque dos animais
     * @param qtdMacho           quantidade de machos
     * @param qtdFemea           quantidade de fêmeas
     * @param qtdMarruco         quantidade de touros/marrucos
     * @param horarioDesembarque horário de desembarque no curral
     * @param numeroCurral       identificação do curral de desembarque
     * @param nomeCurraleiro     nome do curraleiro responsável
     * @param nomeManobrista     nome do manobrista
     */
    public TrajetoModel(int id,
                        MotoristaModel motoristaModel,
                        CaminhaoModel caminhaoModel,
                        StatusTrajeto status,
                        LocalDateTime dataHoraInicio,
                        LocalDateTime dataHoraFim,
                        Integer kmSaida,
                        Integer kmChegada,
                        PecuaristaModel pecuarista,
                        String numeroGTA,
                        String numeroNotaFiscal,
                        LocalDateTime horarioEmbarque,
                        int qtdMacho,
                        int qtdFemea,
                        int qtdMarruco,
                        LocalDateTime horarioDesembarque,
                        String numeroCurral,
                        String nomeCurraleiro,
                        String nomeManobrista) {
        this.id = id;
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
        this.status = status;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.kmSaida = kmSaida != null ? kmSaida : 0;
        this.kmChegada = kmChegada != null ? kmChegada : 0;
        this.pecuaristaModel = pecuarista;
        this.numeroGTA = numeroGTA;
        this.numeroNotaFiscal = numeroNotaFiscal;
        this.horarioEmbarque = horarioEmbarque;
        this.qtdMacho = qtdMacho;
        this.qtdFemea = qtdFemea;
        this.qtdMarruco = qtdMarruco;
        this.horarioDesembarque = horarioDesembarque;
        this.numeroCurral = numeroCurral;
        this.nomeCurraleiro = nomeCurraleiro;
        this.nomeManobrista = nomeManobrista;
    }

    /**
     * Construtor sem ID.
     * Utilizado na abertura e agendamento de um novo trajeto antes de gravar no banco de dados.
     *
     * @param motoristaModel     motorista condutor
     * @param caminhaoModel      caminhão utilizado
     * @param status             status da viagem (EM_ANDAMENTO / CONCLUIDA)
     * @param dataHoraInicio     data e hora de início
     * @param dataHoraFim        data e hora de encerramento
     * @param kmSaida            quilometragem na saída
     * @param kmChegada          quilometragem na chegada
     * @param pecuarista         pecuarista remetente
     * @param numeroGTA          número da Guia de Trânsito Animal (GTA)
     * @param numeroNotaFiscal   número da Nota Fiscal
     * @param horarioEmbarque    horário de embarque dos animais
     * @param qtdMacho           quantidade de machos
     * @param qtdFemea           quantidade de fêmeas
     * @param qtdMarruco         quantidade de touros/marrucos
     * @param horarioDesembarque horário de desembarque no curral
     * @param numeroCurral       identificação do curral de desembarque
     * @param nomeCurraleiro     nome do curraleiro responsável
     * @param nomeManobrista     nome do manobrista
     */
    public TrajetoModel(MotoristaModel motoristaModel,
                        CaminhaoModel caminhaoModel,
                        StatusTrajeto status,
                        LocalDateTime dataHoraInicio,
                        LocalDateTime dataHoraFim,
                        int kmSaida,
                        int kmChegada,
                        PecuaristaModel pecuarista,
                        String numeroGTA,
                        String numeroNotaFiscal,
                        LocalDateTime horarioEmbarque,
                        int qtdMacho,
                        int qtdFemea,
                        int qtdMarruco,
                        LocalDateTime horarioDesembarque,
                        String numeroCurral,
                        String nomeCurraleiro,
                        String nomeManobrista) {
        this.motoristaModel = motoristaModel;
        this.caminhaoModel = caminhaoModel;
        this.status = status;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.kmSaida = kmSaida;
        this.kmChegada = kmChegada;
        this.pecuaristaModel = pecuarista;
        this.numeroGTA = numeroGTA;
        this.numeroNotaFiscal = numeroNotaFiscal;
        this.horarioEmbarque = horarioEmbarque;
        this.qtdMacho = qtdMacho;
        this.qtdFemea = qtdFemea;
        this.qtdMarruco = qtdMarruco;
        this.horarioDesembarque = horarioDesembarque;
        this.numeroCurral = numeroCurral;
        this.nomeCurraleiro = nomeCurraleiro;
        this.nomeManobrista = nomeManobrista;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único do trajeto.
     * @return ID numérico do trajeto
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único do trajeto.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o motorista designado para a viagem.
     * @return objeto {@link MotoristaModel}
     */
    public MotoristaModel getMotoristaModel() {
        return motoristaModel;
    }

    /**
     * Define o motorista do trajeto.
     * @param motoristaModel motorista condutor
     */
    public void setMotoristaModel(MotoristaModel motoristaModel) {
        this.motoristaModel = motoristaModel;
    }

    /**
     * Método utilitário alternativo para obter o motorista do trajeto.
     * @return objeto {@link MotoristaModel}
     */
    public MotoristaModel getMotorista() {
        return motoristaModel;
    }

    /**
     * Obtém o caminhão utilizado no transporte.
     * @return objeto {@link CaminhaoModel}
     */
    public CaminhaoModel getCaminhaoModel() {
        return caminhaoModel;
    }

    /**
     * Define o caminhão utilizado no transporte.
     * @param caminhaoModel veículo boiadeiro
     */
    public void setCaminhaoModel(CaminhaoModel caminhaoModel) {
        this.caminhaoModel = caminhaoModel;
    }

    /**
     * Método utilitário alternativo para obter o caminhão do trajeto.
     * @return objeto {@link CaminhaoModel}
     */
    public CaminhaoModel getCaminhao() {
        return caminhaoModel;
    }

    /**
     * Obtém o status operacional atual da viagem.
     * @return enum {@link StatusTrajeto}
     */
    public StatusTrajeto getStatus() {
        return status;
    }

    /**
     * Define o status operacional da viagem.
     * @param status novo status
     */
    public void setStatus(StatusTrajeto status) {
        this.status = status;
    }

    /**
     * Obtém a data e hora do início do trajeto.
     * @return data e hora de início (LocalDateTime)
     */
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    /**
     * Define a data e hora do início do trajeto.
     * @param dataHoraInicio nova data e hora de início
     */
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /**
     * Obtém a data e hora de encerramento do trajeto.
     * @return data e hora de fim (LocalDateTime)
     */
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    /**
     * Define a data e hora de encerramento do trajeto.
     * @param dataHoraFim nova data e hora de fim
     */
    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    /**
     * Obtém a quilometragem registrada na partida.
     * @return Km de saída
     */
    public int getKmSaida() {
        return kmSaida;
    }

    /**
     * Define a quilometragem registrada na partida.
     * @param kmSaida Km de saída
     */
    public void setKmSaida(Integer kmSaida) {
        this.kmSaida = kmSaida != null ? kmSaida : 0;
    }

    /**
     * Obtém a quilometragem registrada na chegada.
     * @return Km de chegada
     */
    public int getKmChegada() {
        return kmChegada;
    }

    /**
     * Define a quilometragem registrada na chegada.
     * @param kmChegada Km de chegada
     */
    public void setKmChegada(Integer kmChegada) {
        this.kmChegada = kmChegada != null ? kmChegada : 0;
    }

    /**
     * Obtém o pecuarista/produtor responsável pelo lote transportado.
     * @return objeto {@link PecuaristaModel}
     */
    public PecuaristaModel getPecuaristaModel() {
        return pecuaristaModel;
    }

    /**
     * Define o pecuarista/produtor da carga.
     * @param pecuaristaModel produtor rural
     */
    public void setPecuaristaModel(PecuaristaModel pecuaristaModel) {
        this.pecuaristaModel = pecuaristaModel;
    }

    /**
     * Método utilitário alternativo para obter o pecuarista do trajeto.
     * @return objeto {@link PecuaristaModel}
     */
    public PecuaristaModel getPecuarista() {
        return pecuaristaModel;
    }

    /**
     * Define o pecuarista/produtor da carga (método de compatibilidade).
     * @param pecuarista produtor rural
     */
    public void setPecuarista(PecuaristaModel pecuarista) {
        this.pecuaristaModel = pecuarista;
    }

    /**
     * Método compatível para definir o pecuarista da carga.
     * @param nomePecuarista produtor rural
     */
    public void setNomePecuarista(PecuaristaModel nomePecuarista) {
        this.pecuaristaModel = nomePecuarista;
    }

    /**
     * Obtém o número oficial da Guia de Trânsito Animal (GTA).
     * @return número da GTA
     */
    public String getNumeroGTA() {
        return numeroGTA;
    }

    /**
     * Define o número oficial da Guia de Trânsito Animal (GTA).
     * @param numeroGTA número da GTA
     */
    public void setNumeroGTA(String numeroGTA) {
        this.numeroGTA = numeroGTA;
    }

    /**
     * Obtém o número da Nota Fiscal do transporte/gado.
     * @return número da Nota Fiscal
     */
    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    /**
     * Define o número da Nota Fiscal do transporte/gado.
     * @param numeroNotaFiscal número da Nota Fiscal
     */
    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    /**
     * Obtém o momento do embarque dos animais no caminhão.
     * @return horário de embarque (LocalDateTime)
     */
    public LocalDateTime getHorarioEmbarque() {
        return horarioEmbarque;
    }

    /**
     * Define o momento do embarque dos animais no caminhão.
     * @param horarioEmbarque horário de embarque
     */
    public void setHorarioEmbarque(LocalDateTime horarioEmbarque) {
        this.horarioEmbarque = horarioEmbarque;
    }

    /**
     * Obtém a contagem de animais machos castrados.
     * @return quantidade de machos
     */
    public int getQtdMacho() {
        return qtdMacho;
    }

    /**
     * Define a contagem de animais machos castrados.
     * @param qtdMacho quantidade de machos
     */
    public void setQtdMacho(int qtdMacho) {
        this.qtdMacho = qtdMacho;
    }

    /**
     * Obtém a contagem de animais fêmeas.
     * @return quantidade de fêmeas
     */
    public int getQtdFemea() {
        return qtdFemea;
    }

    /**
     * Define a contagem de animais fêmeas.
     * @param qtdFemea quantidade de fêmeas
     */
    public void setQtdFemea(int qtdFemea) {
        this.qtdFemea = qtdFemea;
    }

    /**
     * Obtém a contagem de animais machos inteiros / reprodutores (marrucos).
     * @return quantidade de marrucos
     */
    public int getQtdMarruco() {
        return qtdMarruco;
    }

    /**
     * Define a contagem de animais machos inteiros / reprodutores (marrucos).
     * @param qtdMarruco quantidade de marrucos
     */
    public void setQtdMarruco(int qtdMarruco) {
        this.qtdMarruco = qtdMarruco;
    }

    /**
     * Obtém o horário em que foi realizado o desembarque dos animais no destino.
     * @return horário de desembarque (LocalDateTime)
     */
    public LocalDateTime getHorarioDesembarque() {
        return horarioDesembarque;
    }

    /**
     * Define o horário em que foi realizado o desembarque dos animais no destino.
     * @param horarioDesembarque horário de desembarque
     */
    public void setHorarioDesembarque(LocalDateTime horarioDesembarque) {
        this.horarioDesembarque = horarioDesembarque;
    }

    /**
     * Obtém o número ou identificação do curral de destino.
     * @return número do curral
     */
    public String getNumeroCurral() {
        return numeroCurral;
    }

    /**
     * Define o número ou identificação do curral de destino.
     * @param numeroCurral novo número do curral
     */
    public void setNumeroCurral(String numeroCurral) {
        this.numeroCurral = numeroCurral;
    }

    /**
     * Obtém o nome do curraleiro responsável pelo desembarque.
     * @return nome do curraleiro
     */
    public String getNomeCurraleiro() {
        return nomeCurraleiro;
    }

    /**
     * Define o nome do curraleiro responsável pelo desembarque.
     * @param nomeCurraleiro nome do curraleiro
     */
    public void setNomeCurraleiro(String nomeCurraleiro) {
        this.nomeCurraleiro = nomeCurraleiro;
    }

    /**
     * Obtém o nome do manobrista responsável no pátio.
     * @return nome do manobrista
     */
    public String getNomeManobrista() {
        return nomeManobrista;
    }

    /**
     * Define o nome do manobrista responsável no pátio.
     * @param nomeManobrista nome do manobrista
     */
    public void setNomeManobrista(String nomeManobrista) {
        this.nomeManobrista = nomeManobrista;
    }

    /**
     * Obtém a assinatura digital do curraleiro.
     * @return assinatura ou rubrica
     */
    public String getAssinaturaCurraleiro() {
        return assinaturaCurraleiro;
    }

    /**
     * Define a assinatura digital do curraleiro.
     * @param assinaturaCurraleiro assinatura ou rubrica
     */
    public void setAssinaturaCurraleiro(String assinaturaCurraleiro) {
        this.assinaturaCurraleiro = assinaturaCurraleiro;
    }

    /**
     * Obtém a assinatura digital do manobrista.
     * @return assinatura ou rubrica
     */
    public String getAssinaturaManobrista() {
        return assinaturaManobrista;
    }

    /**
     * Define a assinatura digital do manobrista.
     * @param assinaturaManobrista assinatura ou rubrica
     */
    public void setAssinaturaManobrista(String assinaturaManobrista) {
        this.assinaturaManobrista = assinaturaManobrista;
    }

    /**
     * Obtém a assinatura digital do motorista registrada para este trajeto.
     * @return assinatura ou rubrica
     */
    public String getAssinaturaMotorista() {
        return assinaturaMotorista;
    }

    /**
     * Define a assinatura digital do motorista para este trajeto.
     * @param assinaturaMotorista assinatura ou rubrica
     */
    public void setAssinaturaMotorista(String assinaturaMotorista) {
        this.assinaturaMotorista = assinaturaMotorista;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do trajeto.
     * @return string formatada com todos os atributos do trajeto
     */
    @Override
    public String toString() {
        return "TrajetoModel{" +
                "id=" + id +
                ", motoristaModel=" + motoristaModel +
                ", caminhaoModel=" + caminhaoModel +
                ", status=" + status +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                ", kmSaida=" + kmSaida +
                ", kmChegada=" + kmChegada +
                ", pecuaristaModel=" + pecuaristaModel +
                ", numeroGTA='" + numeroGTA + '\'' +
                ", numeroNotaFiscal='" + numeroNotaFiscal + '\'' +
                ", horarioEmbarque=" + horarioEmbarque +
                ", qtdMacho=" + qtdMacho +
                ", qtdFemea=" + qtdFemea +
                ", qtdMarruco=" + qtdMarruco +
                ", horarioDesembarque=" + horarioDesembarque +
                ", numeroCurral='" + numeroCurral + '\'' +
                ", nomeCurraleiro='" + nomeCurraleiro + '\'' +
                ", nomeManobrista='" + nomeManobrista + '\'' +
                ", assinaturaCurraleiro='" + assinaturaCurraleiro + '\'' +
                ", assinaturaManobrista='" + assinaturaManobrista + '\'' +
                ", assinaturaMotorista='" + assinaturaMotorista + '\'' +
                '}';
    }
}
