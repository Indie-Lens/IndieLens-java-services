package com.indielens.jdbc.objects;

public class Jogo_playtime {
	
	private Integer idJogo;
	private Integer idRegistro;
	private Integer mediaHorasTotal;
	private Integer medianaHorasTotal;
	private Integer mediaHorasDuasSemanas;
	private Integer medianaHorasDuasSemanas;

	public Jogo_playtime(Integer idJogo, Integer idRegistro, Integer mediaHorasTotal, Integer medianaHorasTotal, Integer mediaHorasDuasSemanas, Integer medianaHorasDuasSemanas) {
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.mediaHorasTotal = mediaHorasTotal;
		this.medianaHorasTotal = medianaHorasTotal;
		this.mediaHorasDuasSemanas = mediaHorasDuasSemanas;
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
