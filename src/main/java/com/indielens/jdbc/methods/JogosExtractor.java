package com.indielens.jdbc.methods;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.indielens.jdbc.Conexao;
import com.indielens.jdbc.tables.Jogo;
import com.indielens.logMachine.FileWritter;

public class JogosExtractor {

    public static boolean InserirBanco(List<Jogo> lista) {
        return InserirBanco(lista, new Conexao());
    }

    public static boolean InserirBanco(List<Jogo> lista, Conexao conexao) {
        return InserirBanco(lista, conexao, null);
    }

    public static boolean InserirBanco(List<Jogo> lista, Conexao conexao, FileWritter log) {
        if (lista == null || lista.isEmpty() || lista.size() > 1000) {
            return false;
        }

        boolean sucesso = false;

        try (Connection conexaoLote = conexao.getConexao().getConnection()) {
            conexaoLote.setAutoCommit(false);

            // Todos os inserts usam a mesma conexao ate o commit do lote.
            JdbcTemplate template = new JdbcTemplate(new SingleConnectionDataSource(conexaoLote, true));
            JogosInserter inserter = new JogosInserter(template);

            try {
                inserter.inserirRegistro(lista.get(0).getMain());

                for (Jogo jogo : lista) {
                    inserter.inserirMain(jogo.getMain());
                    inserter.inserirMidia(jogo.getMidia());
                    inserter.inserirSuporte(jogo.getSuporte());
                    inserter.inserirDetalhes(jogo.getDetalhes());
                    inserter.inserirMetricas(jogo.getMetricas());
                    inserter.inserirPlaytime(jogo.getPlaytime());
                    inserter.inserirAuxiliar(jogo.getAuxiliar());
                }

                conexaoLote.commit();
                sucesso = true;
            } catch (SQLException | RuntimeException e) {
                try {
                    conexaoLote.rollback();
                    registrarErro(log, "Rollback do batch concluído. Causa: " + e.getMessage());
                } catch (SQLException falhaRollback) {
                    e.addSuppressed(falhaRollback);
                    registrarErro(log, "Falha no rollback: " + falhaRollback.getMessage()
                        + ". Causa original: " + e.getMessage());
                }
            }
        } catch (SQLException | RuntimeException e) {
            registrarErro(log, "Erro na conexão do batch: " + e.getMessage());
            return false;
        }

        return sucesso;
    }

    private static void registrarErro(FileWritter log, String texto) {
        if (log == null) {
            System.err.println(FileWritter.formatarRegistro(4, texto, false));
        } else {
            log.CriarRegistro(4, texto, false);
        }
    }
}
