package com.indielens.jdbc.tables;

import java.util.List;

public class Jogo {
	
	private List<Jogo_main> main;
	private List<Jogo_midia> midia;
	private List<Jogo_suporte> suporte;
	private List<Jogo_detalhes> detalhes;
	private List<Jogo_metricas> metricas;
	private List<Jogo_playtime> playtime;
	private List<Jogo_Auxiliar> auxiliar;
	
	public Jogo(List<Jogo_main> main, List<Jogo_midia> midia, List<Jogo_suporte> suporte, List<Jogo_detalhes> detalhes,
			List<Jogo_metricas> metricas, List<Jogo_playtime> playtime, List<Jogo_Auxiliar> auxiliar) {
		this.main = main;
		this.midia = midia;
		this.suporte = suporte;
		this.detalhes = detalhes;
		this.metricas = metricas;
		this.playtime = playtime;
		this.auxiliar = auxiliar;
	}
	
	public List<Jogo_main> getMain() { return main; }
	public List<Jogo_midia> getMidia() { return midia; }
	public List<Jogo_suporte> getSuporte() { return suporte; }
	public List<Jogo_detalhes> getDetalhes() { return detalhes; }
	public List<Jogo_metricas> getMetricas() { return metricas; }
	public List<Jogo_playtime> getPlaytime() { return playtime; }
	public List<Jogo_Auxiliar> getAuxiliar() { return auxiliar; }

	public void setMain(List<Jogo_main> main) { this.main = main; }
	public void setMidia(List<Jogo_midia> midia) { this.midia = midia; }
	public void setSuporte(List<Jogo_suporte> suporte) { this.suporte = suporte; }
	public void setDetalhes(List<Jogo_detalhes> detalhes) { this.detalhes = detalhes; }
	public void setMetricas(List<Jogo_metricas> metricas) { this.metricas = metricas; }
	public void setPlaytime(List<Jogo_playtime> playtime) { this.playtime = playtime; }
	public void setAuxiliar(List<Jogo_Auxiliar> auxiliar) { this.auxiliar = auxiliar; }

}
