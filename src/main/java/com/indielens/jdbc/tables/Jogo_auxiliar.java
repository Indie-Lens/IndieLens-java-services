package com.indielens.jdbc.tables;

public class Jogo_auxiliar {

	private Integer idJogo;
	private Integer idRegistro;
	private Integer descontoPct;
	private String urlSuporte;
	private String siteOficial;
	private String emailSuporte;
	private String urlMetacritic;
	private String usuarioReview;
	
	public Jogo_auxiliar(Integer idJogo, Integer idRegistro, Integer descontoPct, String siteOficial,String emailSuporte, String urlMetacritic, String urlSuporte, String usuarioReview) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.descontoPct = descontoPct;
		this.siteOficial = siteOficial;
		this.emailSuporte = emailSuporte;
		this.urlMetacritic = urlMetacritic;
		this.usuarioReview = usuarioReview;
		this.urlSuporte = urlSuporte;
	}

	public Integer getIdJogo() { return idJogo; }
	public Integer getIdRegistro() { return idRegistro; }
	public Integer getDescontoPct() { return descontoPct; }
	public String getSiteOficial() { return siteOficial; }
	public String getUrlSuporte() { return urlSuporte; }
	public String getEmailSuporte() { return emailSuporte; }
	public String getUrlMetacritic() { return urlMetacritic; }
	public String getUsuarioReview() { return usuarioReview; }
	
	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setDescontoPct(Integer descontoPct) { this.descontoPct = descontoPct; }
	public void setIdJogo(String urlSuporte) { this.urlSuporte = urlSuporte; }
	public void setSiteOficial(String siteOficial) { this.siteOficial = siteOficial; }
	public void setEmailSuporte(String emailSuporte) { this.emailSuporte = emailSuporte; }
	public void setUrlMetacritic(String urlMetacritic) { this.urlMetacritic = urlMetacritic; }
	public void setUsuarioReview(String usuarioReview) { this.usuarioReview = usuarioReview; }

}
