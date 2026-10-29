package com.indielens.jdbc.methods;

import org.springframework.jdbc.core.JdbcTemplate;

import com.indielens.jdbc.tables.Jogo;
import com.indielens.jdbc.tables.Jogo_auxiliar;
import com.indielens.jdbc.tables.Jogo_detalhes;
import com.indielens.jdbc.tables.Jogo_main;
import com.indielens.jdbc.tables.Jogo_metricas;
import com.indielens.jdbc.tables.Jogo_midia;
import com.indielens.jdbc.tables.Jogo_playtime;
import com.indielens.jdbc.tables.Jogo_suporte;

public class JogosInserter {

    public static final int MAXIMO_JOGOS_POR_INSERT = 100;

    private JdbcTemplate template;

    public JogosInserter(JdbcTemplate template) {
        this.template = template;
    }

    public void inserirRegistro(Jogo_main jogo) {
        // Os lotes da mesma leitura compartilham o id calculado pelo RegisterReader.
        String consultaSql = "SELECT COUNT(*) FROM registro WHERE id_registro = ?";
        Integer quantidade = template.queryForObject(consultaSql, Integer.class, jogo.getIdRegistro());

        if (quantidade == 0) {
            template.update("INSERT INTO registro (id_registro) VALUES (?)", jogo.getIdRegistro());
        }
    }

    public void inserirMain(Jogo[] jogos) {
        validarGrupo(jogos);
        // O banco gera o id_jogo_rastreavel automaticamente.
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_main (id_jogo, id_registro, nome, desenvolvedor, "
            + "idade_minima, proprietarios_estimados, preco, data_lancamento) VALUES ");
        Object[] parametros = new Object[jogos.length * 8];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?, ?, ?, ?)");
            Jogo_main jogo = jogos[indice].getMain();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getNome();
            parametros[parametro++] = jogo.getDesenvolvedor();
            parametros[parametro++] = jogo.getIdadeMinima();
            parametros[parametro++] = jogo.getProprietariosEstimados();
            parametros[parametro++] = jogo.getPreco();
            parametros[parametro++] = jogo.getDataLancamento();
        }

        template.update(comandoSql.toString(), parametros);
    }

    public void inserirMidia(Jogo[] jogos) {
        validarGrupo(jogos);
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_midia (id_jogo, id_registro, imagem_cabecalho, "
            + "capturas_tela_URL, videos_URL) VALUES ");
        Object[] parametros = new Object[jogos.length * 5];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?)");
            Jogo_midia jogo = jogos[indice].getMidia();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getImagemCabecalho();
            parametros[parametro++] = jogo.getCapturasTelaURL();
            parametros[parametro++] = jogo.getVideosUrl();
        }

        template.update(comandoSql.toString(), parametros);
    }

    public void inserirSuporte(Jogo[] jogos) {
        validarGrupo(jogos);
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_suporte (id_jogo, id_registro, suporte_windows, "
            + "suporte_MAC, suporte_linux, idiomas_suportados, idiomas_suportados_audio) VALUES ");
        Object[] parametros = new Object[jogos.length * 7];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?, ?, ?)");
            Jogo_suporte jogo = jogos[indice].getSuporte();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getSuporteWindows();
            parametros[parametro++] = jogo.getSuporteMAC();
            parametros[parametro++] = jogo.getSuporteLinux();
            parametros[parametro++] = jogo.getIdiomasSuportados();
            parametros[parametro++] = jogo.getIdiomasSuportadosAudio();
        }

        template.update(comandoSql.toString(), parametros);
    }

    public void inserirDetalhes(Jogo[] jogos) {
        validarGrupo(jogos);
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_detalhes (id_jogo, id_registro, jogo_anotacoes, "
            + "publicante, descricao, tags, generos, categorias) VALUES ");
        Object[] parametros = new Object[jogos.length * 8];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?, ?, ?, ?)");
            Jogo_detalhes jogo = jogos[indice].getDetalhes();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getJogoAnotacoes();
            parametros[parametro++] = jogo.getPublicante();
            parametros[parametro++] = jogo.getDescricao();
            parametros[parametro++] = jogo.getTags();
            parametros[parametro++] = jogo.getGeneros();
            parametros[parametro++] = jogo.getCategorias();
        }

        template.update(comandoSql.toString(), parametros);
    }

    public void inserirMetricas(Jogo[] jogos) {
        validarGrupo(jogos);
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_metricas (id_jogo, id_registro, peak_CCU, avaliacoes_positivas, "
            + "avaliacoes_negativas, qtd_dlc, qtd_recomendacoes, qtd_conquistas, nota_metacritic, "
            + "nota_usuarios, nota_score) VALUES ");
        Object[] parametros = new Object[jogos.length * 11];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            Jogo_metricas jogo = jogos[indice].getMetricas();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getPeakCCU();
            parametros[parametro++] = jogo.getAvaliacoesPositivas();
            parametros[parametro++] = jogo.getAvaliacoesNegativas();
            parametros[parametro++] = jogo.getQtdDlc();
            parametros[parametro++] = jogo.getQtdRecomendacoes();
            parametros[parametro++] = jogo.getQtdConquistas();
            parametros[parametro++] = jogo.getNotaMetacritic();
            parametros[parametro++] = jogo.getNotaUsuarios();
            parametros[parametro++] = jogo.getNotaScore();
        }

        template.update(comandoSql.toString(), parametros);
    }

    public void inserirPlaytime(Jogo[] jogos) {
        validarGrupo(jogos);
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_playtime (id_jogo, id_registro, media_horas_total, "
            + "media_horas_duas_semanas, mediana_horas_total, mediana_horas_duas_semanas) VALUES ");
        Object[] parametros = new Object[jogos.length * 6];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?, ?)");
            Jogo_playtime jogo = jogos[indice].getPlaytime();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getMediaHorasTotal();
            parametros[parametro++] = jogo.getMediaHorasDuasSemanas();
            parametros[parametro++] = jogo.getMedianaHorasTotal();
            parametros[parametro++] = jogo.getMedianaHorasDuasSemanas();
        }

        template.update(comandoSql.toString(), parametros);
    }

    public void inserirAuxiliar(Jogo[] jogos) {
        validarGrupo(jogos);
        StringBuilder comandoSql = new StringBuilder("INSERT INTO jogo_auxiliar (id_jogo, id_registro, desconto_pct, site_oficial, "
            + "email_suporte, url_metacritic, url_suporte, usuario_review) VALUES ");
        Object[] parametros = new Object[jogos.length * 8];
        int parametro = 0;

        for (int indice = 0; indice < jogos.length; indice++) {
            if (indice > 0) {
                comandoSql.append(", ");
            }
            comandoSql.append("(?, ?, ?, ?, ?, ?, ?, ?)");
            Jogo_auxiliar jogo = jogos[indice].getAuxiliar();
            parametros[parametro++] = jogo.getIdJogo();
            parametros[parametro++] = jogo.getIdRegistro();
            parametros[parametro++] = jogo.getDescontoPct();
            parametros[parametro++] = jogo.getSiteOficial();
            parametros[parametro++] = jogo.getEmailSuporte();
            parametros[parametro++] = jogo.getUrlMetacritic();
            parametros[parametro++] = jogo.getUrlSuporte();
            parametros[parametro++] = jogo.getUsuarioReview();
        }

        template.update(comandoSql.toString(), parametros);
    }

    private static void validarGrupo(Jogo[] jogos) {
        if (jogos == null || jogos.length == 0 || jogos.length > MAXIMO_JOGOS_POR_INSERT) {
            throw new IllegalArgumentException("Cada INSERT deve receber entre 1 e "
                + MAXIMO_JOGOS_POR_INSERT + " jogos.");
        }
    }
}
