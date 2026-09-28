package com.efficientia.efficientia.model;

/**
 * Modelo representativo de um Endereço no sistema Efficientia.
 *
 * Armazena informações geográficas e postais de propriedades rurais,
 * sedes corporativas, frigoríficos e pontos de parada/desembarque.
 */
public class EnderecoModel implements Model {

    // ==================== ATRIBUTOS ====================

    /** Identificador único do endereço (chave primária no banco de dados). */
    private int id;

    /** Código de Endereçamento Postal (CEP). */
    private String cep;

    /** Tipo de logradouro ou classificação do local (ex: Rua, Avenida, Rodovia, Estrada Rural). */
    private String tipo;

    /** Número predial, marco quilométrico ou "S/N". */
    private String numero;

    /** Nome do logradouro, rodovia, estrada ou via de acesso. */
    private String rua;

    /** Nome do município/cidade. */
    private String cidade;

    /** Sigla ou nome do Estado / Unidade Federativa (UF). */
    private String estado;

    /** Nome do país de localização (ex: Brasil). */
    private String pais;

    /** Complemento, referências de acesso ou detalhes da localidade. */
    private String complemento;

    // ==================== CONSTRUTORES ====================

    /**
     * Construtor completo com ID.
     * Utilizado na reconstituição do endereço a partir de dados persistidos no banco.
     *
     * @param id          identificador único do endereço
     * @param cep         Código de Endereçamento Postal (CEP)
     * @param tipo        tipo de logradouro (Rua, Rodovia, etc.)
     * @param numero      número predial ou km
     * @param rua         nome do logradouro/estrada
     * @param cidade      município
     * @param estado      Estado (UF)
     * @param pais        país
     * @param complemento informações complementares ou ponto de referência
     */
    public EnderecoModel(int id,
                         String cep,
                         String tipo,
                         String numero,
                         String rua,
                         String cidade,
                         String estado,
                         String pais,
                         String complemento) {
        this.id = id;
        this.cep = cep;
        this.tipo = tipo;
        this.numero = numero;
        this.rua = rua;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.complemento = complemento;
    }

    /**
     * Construtor sem ID.
     * Utilizado na criação de novos endereços antes da gravação no banco de dados.
     *
     * @param cep         Código de Endereçamento Postal (CEP)
     * @param tipo        tipo de logradouro
     * @param numero      número predial ou km
     * @param rua         nome do logradouro/estrada
     * @param cidade      município
     * @param estado      Estado (UF)
     * @param pais        país
     * @param complemento informações complementares ou ponto de referência
     */
    public EnderecoModel(String cep,
                         String tipo,
                         String numero,
                         String rua,
                         String cidade,
                         String estado,
                         String pais,
                         String complemento) {
        this.cep = cep;
        this.tipo = tipo;
        this.numero = numero;
        this.rua = rua;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.complemento = complemento;
    }

    // ==================== GETTERS E SETTERS ====================

    /**
     * Obtém o identificador único do endereço.
     * @return ID numérico
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Define o identificador único do endereço.
     * @param id ID numérico
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtém o CEP do endereço.
     * @return CEP formatado ou numérico
     */
    public String getCep() {
        return cep;
    }

    /**
     * Define o CEP do endereço.
     * @param cep novo CEP
     */
    public void setCep(String cep) {
        this.cep = cep;
    }

    /**
     * Obtém o tipo de logradouro.
     * @return tipo do logradouro (ex: Rua, Avenida, Rodovia)
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define o tipo de logradouro.
     * @param tipo novo tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtém o número predial ou marco de localização.
     * @return número ou indicação de marco
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Define o número predial ou marco de localização.
     * @param numero novo número
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Obtém o nome da via ou logradouro.
     * @return nome da rua/estrada
     */
    public String getRua() {
        return rua;
    }

    /**
     * Define o nome da via ou logradouro.
     * @param rua novo nome da rua/estrada
     */
    public void setRua(String rua) {
        this.rua = rua;
    }

    /**
     * Obtém a cidade/município.
     * @return nome da cidade
     */
    public String getCidade() {
        return cidade;
    }

    /**
     * Define a cidade/município.
     * @param cidade novo nome da cidade
     */
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    /**
     * Obtém a sigla ou nome da Unidade Federativa (UF).
     * @return UF / Estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define a sigla ou nome da Unidade Federativa (UF).
     * @param estado novo Estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtém o país do endereço.
     * @return nome do país
     */
    public String getPais() {
        return pais;
    }

    /**
     * Define o país do endereço.
     * @param pais novo país
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * Obtém o complemento ou ponto de referência.
     * @return complemento do endereço
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * Define o complemento ou ponto de referência.
     * @param complemento novo complemento
     */
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    // ==================== TO STRING ====================

    /**
     * Retorna a representação textual dos dados do endereço.
     * @return string formatada contendo os atributos do endereço
     */
    @Override
    public String toString() {
        return "EnderecoModel{" +
                "id=" + id +
                ", cep='" + cep + '\'' +
                ", tipo='" + tipo + '\'' +
                ", numero='" + numero + '\'' +
                ", rua='" + rua + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", pais='" + pais + '\'' +
                ", complemento='" + complemento + '\'' +
                '}';
    }
}
