package com.efficientia.efficientia.model;

/**
 * Modelo representativo de um Caminhão (veículo de transporte de carga viva) no sistema Efficientia.
 *
 * O caminhão é composto pelo cavalo mecânico (unidade tratora) e pela carreta (semirreboque boiadeiro),
 * possuindo uma capacidade máxima pré-definida para transporte de animais de corte.
 */
public class CaminhaoModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do caminhão (chave primária no banco de dados). */
    private int id;

    /** Empresa à qual o caminhão está vinculado. */
    private EmpresaModel empresaModel;

    /** Placa do cavalo mecânico (unidade tratora). */
    private String placaCavalo;

    /** Placa da carreta (semirreboque / baú boiadeiro). */
    private String placaCarreta;

    /** Capacidade máxima de transporte em número de cabeças de gado. */
    private int capacidadeMaxima;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID e vínculo com Empresa.
     * Utilizado na reconstituição do caminhão a partir de dados vindos do banco de dados.
     *
     * @param id               identificador único do caminhão
     * @param empresaModel     empresa proprietária/responsável pelo caminhão
     * @param placaCavalo      placa do cavalo mecânico
     * @param placaCarreta     placa da carreta boiadeira
     * @param capacidadeMaxima capacidade máxima de animais suportada
     */
    public CaminhaoModel(int id,
                         EmpresaModel empresaModel,
                         String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.id = id;
        this.empresaModel = empresaModel;
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    /**
     * Construtor sem ID com vínculo com Empresa.
     * Utilizado no cadastro de novos caminhões vinculados a uma empresa.
     *
     * @param empresaModel     empresa proprietária/responsável pelo caminhão
     * @param placaCavalo      placa do cavalo mecânico
     * @param placaCarreta     placa da carreta boiadeira
     * @param capacidadeMaxima capacidade máxima de animais suportada
     */
    public CaminhaoModel(EmpresaModel empresaModel,
                         String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.empresaModel = empresaModel;
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    /**
     * Construtor completo com ID (sem vínculo com Empresa).
     * Mantido para compatibilidade reversa.
     *
     * @param id               identificador único do caminhão
     * @param placaCavalo      placa do cavalo mecânico
     * @param placaCarreta     placa da carreta boiadeira
     * @param capacidadeMaxima capacidade máxima de animais suportada
     */
    public CaminhaoModel(int id,
                         String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.id = id;
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    /**
     * Construtor sem ID e sem empresa.
     * Utilizado no cadastro antes da persistência no banco.
     *
     * @param placaCavalo      placa do cavalo mecânico
     * @param placaCarreta     placa da carreta boiadeira
     * @param capacidadeMaxima capacidade máxima de animais suportada
     */
    public CaminhaoModel(String placaCavalo,
                         String placaCarreta,
                         int capacidadeMaxima) {
        this.placaCavalo = placaCavalo;
        this.placaCarreta = placaCarreta;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único do caminhão.
     * @return ID numérico do caminhão
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único do caminhão.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém a placa do cavalo mecânico (unidade tratora).
     * @return placa do cavalo
     */
    public String getPlacaCavalo() {
        return placaCavalo;
    }

    /**
     * Define a placa do cavalo mecânico.
     * @param placaCavalo nova placa do cavalo
     */
    public void setPlacaCavalo(String placaCavalo) {
        this.placaCavalo = placaCavalo;
    }

    /**
     * Obtém a placa da carreta / semirreboque boiadeiro.
     * @return placa da carreta
     */
    public String getPlacaCarreta() {
        return placaCarreta;
    }

    /**
     * Define a placa da carreta boiadeira.
     * @param placaCarreta nova placa da carreta
     */
    public void setPlacaCarreta(String placaCarreta) {
        this.placaCarreta = placaCarreta;
    }

    /**
     * Obtém a capacidade máxima de animais do caminhão.
     * @return capacidade máxima em número de cabeças de gado
     */
    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    /**
     * Define a capacidade máxima de animais do caminhão.
     * @param capacidadeMaxima nova capacidade máxima
     */
    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    /**
     * Obtém a empresa proprietária do caminhão.
     * @return objeto {@link EmpresaModel} associado ou null
     */
    public EmpresaModel getEmpresaModel() {
        return empresaModel;
    }

    /**
     * Define a empresa proprietária do caminhão.
     * @param empresaModel objeto {@link EmpresaModel}
     */
    public void setEmpresaModel(EmpresaModel empresaModel) {
        this.empresaModel = empresaModel;
    }

    /**
     * Método utilitário alternativo para obter a empresa associada.
     * @return objeto {@link EmpresaModel} associado
     */
    public EmpresaModel getEmpresa() {
        return empresaModel;
    }

    /**
     * Método utilitário alternativo para definir a empresa associada.
     * @param empresaModel objeto {@link EmpresaModel}
     */
    public void setEmpresa(EmpresaModel empresaModel) {
        this.empresaModel = empresaModel;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do caminhão.
     * @return string formatada contendo os atributos do caminhão
     */
    @Override
    public String toString() {
        return "CaminhaoModel{" +
                "id=" + id +
                ", empresaModel=" + (empresaModel != null ? empresaModel.getNome() : "null") +
                ", placaCavalo='" + placaCavalo + '\'' +
                ", placaCarreta='" + placaCarreta + '\'' +
                ", capacidadeMaxima=" + capacidadeMaxima +
                '}';
    }
}
