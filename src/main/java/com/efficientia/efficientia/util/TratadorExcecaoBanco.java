package com.efficientia.efficientia.util;

import com.efficientia.efficientia.exception.CnpjDuplicadoException;
import com.efficientia.efficientia.exception.CodigoDuplicadoException;
import com.efficientia.efficientia.exception.CpfDuplicadoException;
import com.efficientia.efficientia.exception.DadoDuplicadoException;
import com.efficientia.efficientia.exception.DocumentoFiscalDuplicadoException;
import com.efficientia.efficientia.exception.EmailDuplicadoException;
import com.efficientia.efficientia.exception.PlacaDuplicadaException;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitário responsável pela interceptação, identificação e tradução de erros de banco de dados
 * (notadamente {@link SQLException}) em exceções semânticas de negócio da aplicação Efficientia.
 *
 * <p>Mapeia violações de restrições de unicidade (UNIQUE constraints - SQLState 23505 no PostgreSQL)
 * para a hierarquia de {@link DadoDuplicadoException}.</p>
 */
public final class TratadorExcecaoBanco {

    /**
     * Código de estado SQL padrão ANSI / PostgreSQL para violação de chave única (unique_violation).
     */
    public static final String SQL_STATE_UNIQUE_VIOLATION = "23505";

    /**
     * Código de erro do MySQL para duplicidade de chave (ER_DUP_ENTRY).
     */
    public static final int MYSQL_ERROR_DUPLICATE_ENTRY = 1062;

    private static final Pattern CONSTRAINT_PATTERN = Pattern.compile(
            "(?:unique constraint|restrição de unicidade)[\"\\s]+([a-zA-Z0-9_]+)[\"\\s]?",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern KEY_DETAIL_PATTERN = Pattern.compile(
            "Key \\(([^)]+)\\)=\\(([^)]*)\\)",
            Pattern.CASE_INSENSITIVE
    );

    private TratadorExcecaoBanco() {
        // Classe utilitária com métodos estáticos
    }

    /**
     * Verifica se a {@link SQLException} informada decorre de uma violação de chave única/duplicidade.
     *
     * @param e exceção SQL a ser inspecionada
     * @return true se for uma violação de unicidade reconhecida, false caso contrário
     */
    public static boolean isDuplicidade(SQLException e) {
        if (e == null) {
            return false;
        }

        String sqlState = e.getSQLState();
        if (SQL_STATE_UNIQUE_VIOLATION.equals(sqlState)) {
            return true;
        }

        if (e.getErrorCode() == MYSQL_ERROR_DUPLICATE_ENTRY) {
            return true;
        }

        String msg = e.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            return lower.contains("duplicate key")
                    || lower.contains("violates unique constraint")
                    || lower.contains("viola a restrição de unicidade")
                    || (sqlState != null && sqlState.startsWith("23") && lower.contains("unique"));
        }

        return false;
    }

    /**
     * Avalia a {@link SQLException} e, caso seja uma violação de chave única, lança a exceção
     * tipada correspondente (ex: {@link CpfDuplicadoException}, {@link EmailDuplicadoException}).
     * Se não for duplicidade, o método não dispara exceção e permite a continuidade do fluxo do chamador.
     *
     * @param e              exceção SQL capturada no bloco catch
     * @param entidadePadrao nome da entidade associada à operação (ex: "Pecuarista", "Motorista")
     * @throws DadoDuplicadoException caso a exceção seja de duplicidade
     */
    public static void tratarSeDuplicado(SQLException e, String entidadePadrao) throws DadoDuplicadoException {
        tratarSeDuplicado(e, entidadePadrao, null, null);
    }

    /**
     * Avalia a {@link SQLException} com valores alternativos para o campo e valor caso não sejam
     * passíveis de extração automática da mensagem do banco.
     *
     * @param e              exceção SQL capturada no bloco catch
     * @param entidadePadrao nome da entidade associada (ex: "Pecuarista", "Empresa")
     * @param campoFallback  nome do campo suspeito (ex: "CPF", "CNPJ") caso a mensagem do SGBD seja opaca
     * @param valorFallback  valor que estava sendo inserido/atualizado
     * @throws DadoDuplicadoException caso a exceção seja de duplicidade
     */
    public static void tratarSeDuplicado(
            SQLException e,
            String entidadePadrao,
            String campoFallback,
            String valorFallback
    ) throws DadoDuplicadoException {
        if (isDuplicidade(e)) {
            throw traduzir(e, entidadePadrao, campoFallback, valorFallback);
        }
    }

    /**
     * Traduz uma {@link SQLException} de unicidade para a instância especializada de
     * {@link DadoDuplicadoException} com base no nome da constraint ou coluna violada.
     *
     * @param e              exceção SQL de duplicidade
     * @param entidadePadrao entidade padrão do contexto do DAO
     * @return exceção tipada pronta para lançamento
     */
    public static DadoDuplicadoException traduzir(SQLException e, String entidadePadrao) {
        return traduzir(e, entidadePadrao, null, null);
    }

    /**
     * Traduz uma {@link SQLException} de unicidade para a instância especializada de
     * {@link DadoDuplicadoException}.
     *
     * @param e              exceção SQL de duplicidade
     * @param entidadePadrao entidade padrão do contexto do DAO
     * @param campoFallback  campo a ser utilizado se a identificação automática falhar
     * @param valorFallback  valor a ser utilizado se a identificação automática falhar
     * @return exceção tipada correspondente
     */
    public static DadoDuplicadoException traduzir(
            SQLException e,
            String entidadePadrao,
            String campoFallback,
            String valorFallback
    ) {
        String constraint = extrairConstraint(e);
        String coluna = extrairColuna(e);
        String valor = extrairValor(e);

        if (valor == null || valor.isBlank()) {
            valor = valorFallback;
        }

        String identificador = "";
        if (constraint != null) {
            identificador += constraint.toLowerCase();
        }
        if (coluna != null) {
            identificador += " " + coluna.toLowerCase();
        }
        if (campoFallback != null) {
            identificador += " " + campoFallback.toLowerCase();
        }
        if (e != null && e.getMessage() != null) {
            identificador += " " + e.getMessage().toLowerCase();
        }

        // Mapeamento semântico baseado na constraint ou coluna
        if (identificador.contains("cpf")) {
            return new CpfDuplicadoException(entidadePadrao, valor, constraint, e);
        }
        if (identificador.contains("cnpj")) {
            return new CnpjDuplicadoException(entidadePadrao, valor, constraint, e);
        }
        if (identificador.contains("email")) {
            return new EmailDuplicadoException(entidadePadrao, valor, constraint, e);
        }
        if (identificador.contains("placa")) {
            String tipoPlaca = identificador.contains("cavalo") ? "Placa Cavalo" :
                    (identificador.contains("carreta") ? "Placa Carreta" : "Placa");
            return new PlacaDuplicadaException(entidadePadrao, tipoPlaca, valor, constraint, e);
        }
        if (identificador.contains("codigo")) {
            return new CodigoDuplicadoException(entidadePadrao, valor, constraint, e);
        }
        if (identificador.contains("gta") || identificador.contains("nota_fiscal") || identificador.contains("nfe")) {
            String tipoDoc = identificador.contains("gta") ? "Número GTA" : "Nota Fiscal Eletrônica (NF-e)";
            return new DocumentoFiscalDuplicadoException(tipoDoc, valor, constraint, e);
        }
        if (identificador.contains("nome") || identificador.contains("razao")) {
            return new DadoDuplicadoException(
                    entidadePadrao,
                    "Nome / Razão Social",
                    valor,
                    constraint,
                    "nome_duplicado",
                    e
            );
        }

        String campoFinal = campoFallback != null ? campoFallback : (coluna != null ? coluna : "Campo Único");
        return new DadoDuplicadoException(
                entidadePadrao,
                campoFinal,
                valor,
                constraint,
                "dado_duplicado",
                e
        );
    }

    /**
     * Extrai o nome da constraint violada da mensagem do erro ou detalhes da exceção.
     *
     * @param e exceção SQL
     * @return nome da constraint ou null se não identificada
     */
    public static String extrairConstraint(SQLException e) {
        if (e == null || e.getMessage() == null) {
            return null;
        }

        Matcher matcher = CONSTRAINT_PATTERN.matcher(e.getMessage());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extrai a coluna referenciada na cláusula Detail do PostgreSQL.
     *
     * @param e exceção SQL
     * @return nome da coluna ou null
     */
    public static String extrairColuna(SQLException e) {
        if (e == null || e.getMessage() == null) {
            return null;
        }

        Matcher matcher = KEY_DETAIL_PATTERN.matcher(e.getMessage());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extrai o valor do dado duplicado que gerou a violação da cláusula Detail do PostgreSQL.
     *
     * @param e exceção SQL
     * @return valor duplicado ou null
     */
    public static String extrairValor(SQLException e) {
        if (e == null || e.getMessage() == null) {
            return null;
        }

        Matcher matcher = KEY_DETAIL_PATTERN.matcher(e.getMessage());
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }
}
