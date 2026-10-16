package com.indielens.jdbc.methods;

import org.springframework.jdbc.core.JdbcTemplate;

import com.indielens.jdbc.tables.Jogo_auxiliar;
import com.indielens.jdbc.tables.Jogo_detalhes;
import com.indielens.jdbc.tables.Jogo_main;
import com.indielens.jdbc.tables.Jogo_metricas;
import com.indielens.jdbc.tables.Jogo_midia;
import com.indielens.jdbc.tables.Jogo_playtime;
import com.indielens.jdbc.tables.Jogo_suporte;

public class JogosInserter {

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

    public void inserirMain(Jogo_main jogo) {
        // O banco gera o id_jogo_rastreavel automaticamente.
        String comandoSql = "INSERT INTO jogo_main (id_jogo, id_registro, nome, desenvolvedor, "
            + "idade_minima, proprietarios_estimados, preco, data_lancamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getNome(),
            jogo.getDesenvolvedor(), jogo.getIdadeMinima(), jogo.getProprietariosEstimados(),
            jogo.getPreco(), jogo.getDataLancamento());
    }

    public void inserirMidia(Jogo_midia jogo) {
        String comandoSql = "INSERT INTO jogo_midia (id_jogo, id_registro, imagem_cabecalho, "
            + "capturas_tela_URL, videos_URL) VALUES (?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getImagemCabecalho(),
            jogo.getCapturasTelaURL(), jogo.getVideosUrl());
    }

    public void inserirSuporte(Jogo_suporte jogo) {
        String comandoSql = "INSERT INTO jogo_suporte (id_jogo, id_registro, suporte_windows, "
            + "suporte_MAC, suporte_linux, idiomas_suportados, idiomas_suportados_audio) VALUES (?, ?, ?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getSuporteWindows(),
            jogo.getSuporteMAC(), jogo.getSuporteLinux(), jogo.getIdiomasSuportados(),
            jogo.getIdiomasSuportadosAudio());
    }

    public void inserirDetalhes(Jogo_detalhes jogo) {
        String comandoSql = "INSERT INTO jogo_detalhes (id_jogo, id_registro, jogo_anotacoes, "
            + "publicante, descricao, tags, generos, categorias) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getJogoAnotacoes(),
            jogo.getPublicante(), jogo.getDescricao(), jogo.getTags(), jogo.getGeneros(), jogo.getCategorias());
    }

    public void inserirMetricas(Jogo_metricas jogo) {
        String comandoSql = "INSERT INTO jogo_metricas (id_jogo, id_registro, peak_CCU, avaliacoes_positivas, "
            + "avaliacoes_negativas, qtd_dlc, qtd_recomendacoes, qtd_conquistas, nota_metacritic, "
            + "nota_usuarios, nota_score) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getPeakCCU(),
            jogo.getAvaliacoesPositivas(), jogo.getAvaliacoesNegativas(), jogo.getQtdDlc(),
            jogo.getQtdRecomendacoes(), jogo.getQtdConquistas(), jogo.getNotaMetacritic(),
            jogo.getNotaUsuarios(), jogo.getNotaScore());
    }

    public void inserirPlaytime(Jogo_playtime jogo) {
        String comandoSql = "INSERT INTO jogo_playtime (id_jogo, id_registro, media_horas_total, "
            + "media_horas_duas_semanas, mediana_horas_total, mediana_horas_duas_semanas) VALUES (?, ?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getMediaHorasTotal(),
            jogo.getMediaHorasDuasSemanas(), jogo.getMedianaHorasTotal(), jogo.getMedianaHorasDuasSemanas());
    }

    public void inserirAuxiliar(Jogo_auxiliar jogo) {
        String comandoSql = "INSERT INTO jogo_auxiliar (id_jogo, id_registro, desconto_pct, site_oficial, "
            + "email_suporte, url_metacritic, url_suporte, usuario_review) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        template.update(comandoSql, jogo.getIdJogo(), jogo.getIdRegistro(), jogo.getDescontoPct(),
            jogo.getSiteOficial(), jogo.getEmailSuporte(), jogo.getUrlMetacritic(), jogo.getUrlSuporte(),
            jogo.getUsuarioReview());
    }
}
