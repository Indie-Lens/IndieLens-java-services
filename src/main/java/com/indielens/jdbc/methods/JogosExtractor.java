package com.indielens.jdbc.methods;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.indielens.jdbc.Conexao;
import com.indielens.jdbc.tables.Jogo;

public class JogosExtractor {

    public static boolean InserirBanco(List<Jogo> lista) {
        if (lista == null || lista.isEmpty() || lista.size() > 100) {
            return false;
        }

        Conexao conexao = new Conexao();
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
                conexaoLote.rollback();
                System.out.println("Erro ao inserir o lote de jogos: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Erro na conexao do lote de jogos: " + e.getMessage());
        }

        return sucesso;
    }
}
