package com.indielens.jdbc.tables;

public class Jogo {
	
	private Jogo_main main;
	private Jogo_midia midia;
	private Jogo_suporte suporte;
	private Jogo_detalhes detalhes;
	private Jogo_metricas metricas;
	private Jogo_playtime playtime;
	private Jogo_auxiliar auxiliar;
	
	public Jogo(Jogo_main main, Jogo_midia midia, Jogo_suporte suporte, Jogo_detalhes detalhes,
			Jogo_metricas metricas, Jogo_playtime playtime, Jogo_auxiliar auxiliar) {
		this.main = main;
		this.midia = midia;
		this.suporte = suporte;
		this.detalhes = detalhes;
		this.metricas = metricas;
		this.playtime = playtime;
		this.auxiliar = auxiliar;
	}
	
	public Jogo_main getMain() { return main; }
	public Jogo_midia getMidia() { return midia; }
	public Jogo_suporte getSuporte() { return suporte; }
	public Jogo_detalhes getDetalhes() { return detalhes; }
	public Jogo_metricas getMetricas() { return metricas; }
	public Jogo_playtime getPlaytime() { return playtime; }
	public Jogo_auxiliar getAuxiliar() { return auxiliar; }

	public void setMain(Jogo_main main) { this.main = main; }
	public void setMidia(Jogo_midia midia) { this.midia = midia; }
	public void setSuporte(Jogo_suporte suporte) { this.suporte = suporte; }
	public void setDetalhes(Jogo_detalhes detalhes) { this.detalhes = detalhes; }
	public void setMetricas(Jogo_metricas metricas) { this.metricas = metricas; }
	public void setPlaytime(Jogo_playtime playtime) { this.playtime = playtime; }
	public void setAuxiliar(Jogo_auxiliar auxiliar) { this.auxiliar = auxiliar; }

}
