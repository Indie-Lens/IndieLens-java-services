package com.indielens.jdbc.tables;

public class Jogo_playtime {
	
	private Integer idJogo;
	private Integer idRegistro;
	private Integer mediaHorasTotal;
	private Integer mediaHorasDuasSemanas;
	private Integer medianaHorasTotal;
	private Integer medianaHorasDuasSemanas;

	public Jogo_playtime(Integer idJogo, Integer idRegistro, Integer mediaHorasTotal, Integer mediaHorasDuasSemanas, Integer medianaHorasTotal, Integer medianaHorasDuasSemanas) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.mediaHorasTotal = mediaHorasTotal;
		this.mediaHorasDuasSemanas = mediaHorasDuasSemanas;
		this.medianaHorasTotal = medianaHorasTotal;
		this.medianaHorasDuasSemanas = medianaHorasDuasSemanas;
	}
	
	public Integer getIdJogo() { return idJogo; }
	public Integer getIdRegistro() { return idRegistro; }
	public Integer getMediaHorasTotal() { return mediaHorasTotal; }
	public Integer getMedianaHorasTotal() { return medianaHorasTotal; }
	public Integer getMediaHorasDuasSemanas() { return mediaHorasDuasSemanas; }
	public Integer getMedianaHorasDuasSemanas() { return medianaHorasDuasSemanas; }

	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setMediaHorasTotal(Integer mediaHorasTotal) { this.mediaHorasTotal = mediaHorasTotal; }
	public void setMedianaHorasTotal(Integer medianaHorasTotal) { this.medianaHorasTotal = medianaHorasTotal; }
	public void setMediaHorasDuasSemanas(Integer mediaHorasDuasSemanas) { this.mediaHorasDuasSemanas = mediaHorasDuasSemanas; }
	public void setMedianaHorasDuasSemanas(Integer medianaHorasDuasSemanas) { this.medianaHorasDuasSemanas = medianaHorasDuasSemanas; }
}
