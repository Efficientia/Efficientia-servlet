package com.efficientia.efficientia.util;

import java.util.regex.Pattern;

/**
 * Utilitario central de validacao e sanitizacao de dados de entrada baseado em Expressoes Regulares (REGEX).
 *
 * Esta classe foi desenvolvida para atender as demandas interdisciplinares do 1o Ano:
 * - Sistemas Operacionais (Extra): Validacao estrita por REGEX para entradas de CPF e Celular/Telefone (sem DDI +55).
 * - Logica de Programacao (Minimo e Extra): Validacao defensiva de dados de entrada, codigo modular,
 *   boas praticas de nomenclatura e tratamento de excecoes.
 * - Banco de Dados 1 e POO: Higienizacao de dados para conformidade com as restricoes
 *   CHECK das tabelas do PostgreSQL (ex.: CHECK length(cpf) = 11 e CHECK length(telefone) >= 10).
 *
 * Todos os padroes Pattern sao declarados como constantes estaticas pre-compiladas,
 * otimizando o consumo de memoria e processamento durante as requisicoes HTTP.
 */
public final class ValidadorRegex {

    // Construtor privado para evitar instanciacao de classe utilitaria
    private ValidadorRegex() {
        throw new UnsupportedOperationException("Classe utilitaria nao deve ser instanciada.");
    }

    // ==================== CONSTANTES DE EXPRESSOES REGULARES ====================

    /**
     * Expressao regular para validacao de CPF.
     * Aceita tanto formato com mascara (000.000.000-00) quanto somente os 11 digitos numericos.
     */
    public static final String REGEX_CPF = "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$";

    /**
     * Expressao regular para validacao de Celular e Telefone nacional sem o codigo de pais (+55).
     * Exige o DDD de 2 digitos seguido por:
     * - Celular com 9 digitos: (11) 98765-4321, 11 98765-4321 ou 11987654321
     * - Telefone fixo com 8 digitos: (11) 3456-7890, 11 3456-7890 ou 1134567890
     */
    public static final String REGEX_TELEFONE = "^(\\(\\d{2}\\)\\s?|\\d{2}\\s?)(9?\\d{4})[-.\\s]?(\\d{4})$";

    /**
     * Expressao regular para validacao de Placa de Veiculo.
     * Suporta o Padrao Mercosul (ex.: ABC1D23) e o Padrao Tradicional (ex.: ABC-1234).
     */
    public static final String REGEX_PLACA = "^[A-Za-z]{3}-?[0-9][0-9A-Za-z][0-9]{2}$";

    /**
     * Expressao regular para validacao sintatica de E-mail.
     * Verifica usuario, arroba, dominio e extensao minima de 2 letras.
     */
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Expressao regular para validacao de CNPJ.
     * Aceita formato com mascara (00.000.000/0001-00) ou 14 digitos continuos.
     */
    public static final String REGEX_CNPJ = "^(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}|\\d{14})$";

    /**
     * Expressao regular para numero de GTA (Guia de Transito Animal).
     * Aceita sequencias alfanumericas com pontuacoes permitidas (de 3 a 20 caracteres).
     */
    public static final String REGEX_GTA = "^[0-9A-Za-z.\\-/]{3,20}$";

    /**
     * Expressao regular para Numero de Nota Fiscal.
     * Aceita numeros com pontos, barras ou tracos (de 1 a 44 caracteres).
     */
    public static final String REGEX_NOTA_FISCAL = "^[0-9.\\-/]{1,44}$";

    // ==================== PADROES PRE-COMPILADOS (PATTERNS) ====================

    private static final Pattern PATTERN_CPF = Pattern.compile(REGEX_CPF);
    private static final Pattern PATTERN_TELEFONE = Pattern.compile(REGEX_TELEFONE);
    private static final Pattern PATTERN_PLACA = Pattern.compile(REGEX_PLACA, Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_CNPJ = Pattern.compile(REGEX_CNPJ);
    private static final Pattern PATTERN_GTA = Pattern.compile(REGEX_GTA);
    private static final Pattern PATTERN_NOTA_FISCAL = Pattern.compile(REGEX_NOTA_FISCAL);

    // ==================== METODOS DE VALIDACAO (BOOLEAN) ====================

    /**
     * Valida se a string fornecida atende ao formato de CPF (com ou sem mascara).
     *
     * @param cpf string contendo o CPF
     * @return true se o formato for valido; false caso contrario
     */
    public static boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }
        return PATTERN_CPF.matcher(cpf.trim()).matches();
    }

    /**
     * Valida se a string fornecida atende ao formato nacional de Telefone ou Celular,
     * considerando estritamente sem o codigo de pais (+55).
     * Exige DDD (2 digitos) + 8 ou 9 digitos, formatado ou em digitos puros.
     *
     * @param telefone string contendo o numero telefonico sem +55
     * @return true se o formato for valido; false caso contrario
     */
    public static boolean isCelularValido(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return false;
        }
        String texto = telefone.trim();
        // Rejeita explicitamente prefixos com indicativo internacional +55
        if (texto.startsWith("+") || texto.startsWith("00")) {
            return false;
        }
        return PATTERN_TELEFONE.matcher(texto).matches();
    }

    /**
     * Valida se a string fornecida atende ao padrao de Placa veicular (Mercosul ou Tradicional).
     *
     * @param placa identificador da placa
     * @return true se a placa for valida; false caso contrario
     */
    public static boolean isPlacaValida(String placa) {
        if (placa == null || placa.isBlank()) {
            return false;
        }
        return PATTERN_PLACA.matcher(placa.trim()).matches();
    }

    /**
     * Valida a integridade sintatica do endereco de e-mail.
     *
     * @param email endereco de correio eletronico
     * @return true se sintaticamente valido; false caso contrario
     */
    public static boolean isEmailValido(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return PATTERN_EMAIL.matcher(email.trim()).matches();
    }

    /**
     * Valida se o formato do CNPJ corresponde a mascara oficial ou aos 14 digitos continuos.
     *
     * @param cnpj identificador fiscal de pessoa juridica
     * @return true se for um formato valido de CNPJ; false caso contrario
     */
    public static boolean isCnpjValido(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return false;
        }
        return PATTERN_CNPJ.matcher(cnpj.trim()).matches();
    }

    /**
     * Valida o numero de GTA (Guia de Transito Animal).
     *
     * @param gta numero documental da guia animal
     * @return true se for valido; false caso contrario
     */
    public static boolean isGtaValido(String gta) {
        if (gta == null || gta.isBlank()) {
            return false;
        }
        return PATTERN_GTA.matcher(gta.trim()).matches();
    }

    /**
     * Valida o formato documental da Nota Fiscal de transporte ou venda.
     *
     * @param nf numero de documento fiscal
     * @return true se for valido; false caso contrario
     */
    public static boolean isNotaFiscalValida(String nf) {
        if (nf == null || nf.isBlank()) {
            return false;
        }
        return PATTERN_NOTA_FISCAL.matcher(nf.trim()).matches();
    }

    // ==================== METODOS DE SANITIZACAO E FORMATACAO ====================

    /**
     * Remove todos os caracteres nao numericos de uma string, restando apenas digitos (0 a 9).
     * Essencial para compatibilidade com as colunas do banco com restricao CHECK
     * (ex.: CPF armazenado como VARCHAR(11) sem pontuacao e telefone com no minimo 10 digitos).
     *
     * @param valor string com pontuacoes (ex.: "123.456.789-01" ou "(11) 98765-4321")
     * @return apenas os digitos (ex.: "12345678901" ou "11987654321") ou string vazia se nulo
     */
    public static String apenasDigitos(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("[^0-9]", "").trim();
    }

    /**
     * Higieniza e padroniza a placa do caminhao em caixa alta, sem hifens adicionais.
     *
     * @param placa placa com ou sem traco (ex.: "abc-1234")
     * @return placa em caixa alta padronizada (ex.: "ABC1234" ou "ABC1D23")
     */
    public static String normalizarPlaca(String placa) {
        if (placa == null) {
            return "";
        }
        return placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase().trim();
    }

    /**
     * Formata um CPF puramente numerico (11 digitos) para o padrao visual 000.000.000-00.
     *
     * @param cpf string contendo 11 digitos
     * @return CPF formatado com pontuacao ou o proprio valor caso nao possua 11 digitos
     */
    public static String formatarCpf(String cpf) {
        String digitos = apenasDigitos(cpf);
        if (digitos.length() != 11) {
            return cpf;
        }
        return digitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    /**
     * Formata um numero de telefone nacional com DDD para apresentacao visual amigavel.
     * Nao inclui o codigo internacional +55.
     *
     * @param telefone numero contendo 10 (fixo) ou 11 (celular) digitos
     * @return telefone formatado como (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX
     */
    public static String formatarTelefone(String telefone) {
        String digitos = apenasDigitos(telefone);
        if (digitos.length() == 11) {
            return digitos.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (digitos.length() == 10) {
            return digitos.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }
}
