package com.indielens.jdbc.tables;

public class Jogo_midia {
	
	private Integer idJogo;
	private Integer idRegistro;
	private String imagemCabecalho;
	private String capturasTelaURL;
	private String videosUrl;
	
	public Jogo_midia(Integer idJogo, Integer idRegistro, String imagemCabecalho, String capturasTelaURL,String videosUrl) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.imagemCabecalho = imagemCabecalho;
		this.capturasTelaURL = capturasTelaURL;
		this.videosUrl = videosUrl;
	}

	public Integer getIdJogo() { return idJogo; }
	public Integer getIdRegistro() { return idRegistro; }
	
	public String getVideosUrl() { return videosUrl; }
	public String getImagemCabecalho() { return imagemCabecalho; }
	public String getCapturasTelaURL() { return capturasTelaURL; }

	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setVideosUrl(String videosUrl) { this.videosUrl = videosUrl; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setImagemCabecalho(String imagemCabecalho) { this.imagemCabecalho = imagemCabecalho; }
	public void setCapturasTelaURL(String capturasTelaURL) { this.capturasTelaURL = capturasTelaURL; }
}
