package com.indielens.jdbc.objects;

public class Jogo_detalhes {
	
	private Integer idJogo;
	private Integer idRegistro;
	private String jogoAnotacoes;
	private String publicante;
	private String descricao;
	private String tags;
	private String generos;
	private String categorias;
	
	public Jogo_detalhes(Integer idJogo, Integer idRegistro, String jogoAnotacoes, String publicante, String descricao,String tags, String generos, String categorias) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.jogoAnotacoes = jogoAnotacoes;
		this.publicante = publicante;
		this.descricao = descricao;
		this.tags = tags;
		this.generos = generos;
		this.categorias = categorias;
	}
	
	public Integer getIdJogo() { return idJogo; }
	public Integer getIdRegistro() { return idRegistro; }
	
	public String getTags() { return tags; }
	public String getGeneros() { return generos; }
	public String getDescricao() { return descricao; }
	public String getPublicante() { return publicante; }
	public String getCategorias() { return categorias; }
	public String getJogoAnotacoes() { return jogoAnotacoes; }

	public void setTags(String tags) { this.tags = tags; }
	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setGeneros(String generos) { this.generos = generos; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public void setPublicante(String publicante) { this.publicante = publicante; }
	public void setCategorias(String categorias) { this.categorias = categorias; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setJogoAnotacoes(String jogoAnotacoes) { this.jogoAnotacoes = jogoAnotacoes; }

}
