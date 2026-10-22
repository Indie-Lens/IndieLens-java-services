package com.indielens.jdbc.tables;

public class Jogo_suporte {

	private Integer idJogo;
	private Integer idRegistro;
	private Boolean suporteWindows;
	private Boolean suporteMAC;
	private Boolean suporteLinux;
	private String idiomasSuportados;
	private String idiomasSuportadosAudio;
	
	public Jogo_suporte(Integer idJogo, Integer idRegistro, Boolean suporteWindows, Boolean suporteMAC, Boolean suporteLinux, String idiomasSuportados, String idiomasSuportadosAudio) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.suporteWindows = suporteWindows;
		this.suporteMAC = suporteMAC;
		this.suporteLinux = suporteLinux;
		this.idiomasSuportados = idiomasSuportados;
		this.idiomasSuportadosAudio = idiomasSuportadosAudio;
	}

	public Integer getIdJogo() { return idJogo; }
	public Integer getIdRegistro() { return idRegistro; }
	public Boolean getSuporteMAC() { return suporteMAC; }
	public Boolean getSuporteLinux() { return suporteLinux; }
	public Boolean getSuporteWindows() { return suporteWindows; }
	public String getIdiomasSuportados() { return idiomasSuportados; }
	public String getIdiomasSuportadosAudio() { return idiomasSuportadosAudio; }
	
	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setSuporteMAC(Boolean suporteMAC) { this.suporteMAC = suporteMAC; }
	public void setSuporteLinux(Boolean suporteLinux) { this.suporteLinux = suporteLinux; }
	public void setSuporteWindows(Boolean suporteWindows) { this.suporteWindows = suporteWindows; }
	public void setIdiomasSuportados(String idiomasSuportados) { this.idiomasSuportados = idiomasSuportados; }
	public void setIdiomasSuportadosAudio(String idiomasSuportadosAudio) { this.idiomasSuportadosAudio = idiomasSuportadosAudio; }
}
