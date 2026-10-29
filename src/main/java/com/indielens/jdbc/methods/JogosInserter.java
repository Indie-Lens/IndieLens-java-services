package com.indielens.jdbc.methods;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import com.indielens.jdbc.tables.Jogo;

public class JogosInserter {
    private static final int MAXIMO_POR_INSERT = JogosExtractor.MAXIMO_LOTE;
    private final JdbcTemplate template;
    public JogosInserter(JdbcTemplate template) { this.template = template; }
    public void inserir(List<Jogo> jogos) {
        for (int inicio = 0; inicio < jogos.size(); inicio += MAXIMO_POR_INSERT) {
            int fim = Math.min(inicio + MAXIMO_POR_INSERT, jogos.size());
            StringBuilder sql = new StringBuilder("INSERT INTO IndieLensBak (id_jogo, id_registro, nome, data_lancamento, proprietarios_estimados, peak_CCU, idade_minima, preco, desconto_pct, qtd_dlc, descricao, idiomas_suportados, idiomas_suportados_audio, usuario_review, imagem_cabecalho, site_oficial, url_suporte, email_suporte, suporte_windows, suporte_MAC, suporte_linux, nota_metacritic, url_metacritic, nota_usuarios, avaliacoes_positivas, avaliacoes_negativas, nota_score, qtd_conquistas, qtd_recomendacoes, jogo_anotacoes, media_horas_total, media_horas_duas_semanas, mediana_horas_total, mediana_horas_duas_semanas, desenvolvedor, publicante, categorias, generos, tags, capturas_tela_URL, videos_URL) VALUES ");
            Object[] parametros = new Object[(fim - inicio) * 41];
            int posicao = 0;
            for (int i = inicio; i < fim; i++) {
                if (i > inicio) sql.append(", ");
                sql.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                Jogo jogo = jogos.get(i);
                Object[] valores = { jogo.id_jogo(), jogo.id_registro(), jogo.nome(), jogo.data_lancamento(), jogo.proprietarios_estimados(), jogo.peak_CCU(), jogo.idade_minima(), jogo.preco(), jogo.desconto_pct(), jogo.qtd_dlc(), jogo.descricao(), jogo.idiomas_suportados(), jogo.idiomas_suportados_audio(), jogo.usuario_review(), jogo.imagem_cabecalho(), jogo.site_oficial(), jogo.url_suporte(), jogo.email_suporte(), jogo.suporte_windows(), jogo.suporte_MAC(), jogo.suporte_linux(), jogo.nota_metacritic(), jogo.url_metacritic(), jogo.nota_usuarios(), jogo.avaliacoes_positivas(), jogo.avaliacoes_negativas(), jogo.nota_score(), jogo.qtd_conquistas(), jogo.qtd_recomendacoes(), jogo.jogo_anotacoes(), jogo.media_horas_total(), jogo.media_horas_duas_semanas(), jogo.mediana_horas_total(), jogo.mediana_horas_duas_semanas(), jogo.desenvolvedor(), jogo.publicante(), jogo.categorias(), jogo.generos(), jogo.tags(), jogo.capturas_tela_URL(), jogo.videos_URL() };
                System.arraycopy(valores, 0, parametros, posicao, valores.length);
                posicao += valores.length;
            }
            template.update(sql.toString(), parametros);
        }
    }
}
