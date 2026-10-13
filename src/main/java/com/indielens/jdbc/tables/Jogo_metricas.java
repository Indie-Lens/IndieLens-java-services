package com.indielens.jdbc.objects;

public class Jogo_metricas {

	private Integer idJogo;
	private Integer idRegistro;
	private Integer peakCCU;
	private Integer avaliacoesPositivas;
	private Integer avaliacoesNegativas;
	private Integer qtdDlc;
	private Integer qtdRecomendacoes;
	private Integer qtdConquistas;
	private Double taxaAprovacao;
	private Integer notaMetacritic;
	private Double notaUsuarios;
	private Integer notaScore;
	
	public Jogo_metricas(Integer idJogo, Integer idRegistro, Integer peakCCU, Integer avaliacoesPositivas,Integer avaliacoesNegativas, Integer qtdDlc, Integer qtdRecomendacoes, Integer qtdConquistas,Double taxaAprovacao, Integer notaMetacritic, Double notaUsuarios, Integer notaScore) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.peakCCU = peakCCU;
		this.qtdDlc = qtdDlc;
		this.qtdConquistas = qtdConquistas;
		this.qtdRecomendacoes = qtdRecomendacoes;
		this.taxaAprovacao = taxaAprovacao;
		this.notaScore = notaScore;
		this.notaUsuarios = notaUsuarios;
		this.notaMetacritic = notaMetacritic;
		this.avaliacoesPositivas = avaliacoesPositivas;
		this.avaliacoesNegativas = avaliacoesNegativas;
	}
	
	public Integer getQtdDlc() { return qtdDlc; }
	public Integer getIdJogo() { return idJogo; }
	public Integer getPeakCCU() { return peakCCU; }
	public Integer getNotaScore() { return notaScore; }
	public Integer getIdRegistro() { return idRegistro; }
	public Integer getQtdConquistas() { return qtdConquistas; }
	public Integer getNotaMetacritic() { return notaMetacritic; }
	public Integer getQtdRecomendacoes() { return qtdRecomendacoes; }
	public Integer getAvaliacoesPositivas() { return avaliacoesPositivas; }
	public Integer getAvaliacoesNegativas() { return avaliacoesNegativas; }
	public Double getNotaUsuarios() { return notaUsuarios; }
	public Double getTaxaAprovacao() { return taxaAprovacao; }
	
	public void setQtdDlc(Integer qtdDlc) { this.qtdDlc = qtdDlc; }
	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setPeakCCU(Integer peakCCU) { this.peakCCU = peakCCU; }
	public void setNotaScore(Integer notaScore) { this.notaScore = notaScore; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setNotaUsuarios(Double notaUsuarios) { this.notaUsuarios = notaUsuarios; }
	public void setTaxaAprovacao(Double taxaAprovacao) { this.taxaAprovacao = taxaAprovacao; }
	public void setQtdConquistas(Integer qtdConquistas) { this.qtdConquistas = qtdConquistas; }
	public void setNotaMetacritic(Integer notaMetacritic) { this.notaMetacritic = notaMetacritic; }
	public void setQtdRecomendacoes(Integer qtdRecomendacoes) { this.qtdRecomendacoes = qtdRecomendacoes; }
	public void setAvaliacoesPositivas(Integer avaliacoesPositivas) { this.avaliacoesPositivas = avaliacoesPositivas; }
	public void setAvaliacoesNegativas(Integer avaliacoesNegativas) { this.avaliacoesNegativas = avaliacoesNegativas; }
}
