package com.indielens.jdbc.tables;

import java.time.LocalDate;

public class Jogo_main {
	private String idJogoRastreavel;
	private Integer idJogo;
	private Integer idRegistro;
	private String nome;
	private LocalDate dataLancamento;
	private Double preco;
	private Integer proprietariosEstimados;
	private Integer idadeMinima;
	private String desenvolvedor;
	
	public Jogo_main(String idJogoRastreavel, Integer idJogo, Integer idRegistro, String nome, String desenvolvedor, Integer idadeMinima, Integer proprietariosEstimados, Double preco, LocalDate dataLancamento) {
		this.idJogoRastreavel = idJogoRastreavel;
		this.idJogo = idJogo;
		this.idRegistro = idRegistro;
		this.nome = nome;
		this.dataLancamento = dataLancamento;
		this.preco = preco;
		this.proprietariosEstimados = proprietariosEstimados;
		this.idadeMinima = idadeMinima;
		this.desenvolvedor = desenvolvedor;
	}

	public Integer getIdJogo() { return idJogo; }
	public Integer getIdRegistro() { return idRegistro; }
	public Integer getIdadeMinima() { return idadeMinima; }
	public String getIdJogoRastreavel() { return idJogoRastreavel; }
	public Integer getProprietariosEstimados() { return proprietariosEstimados; }
	
	public Double getPreco() { return preco; }
	public String getNome() { return nome; }
	public String getDesenvolvedor() { return desenvolvedor; }
	public LocalDate getDataLancamento() { return dataLancamento; }

	public void setNome(String nome) { this.nome = nome; }
	public void setPreco(Double preco) { this.preco = preco; }
	public void setIdJogo(Integer idJogo) { this.idJogo = idJogo; }
	public void setIdRegistro(Integer idRegistro) { this.idRegistro = idRegistro; }
	public void setIdadeMinima(Integer idadeMinima) { this.idadeMinima = idadeMinima; }
	public void setDesenvolvedor(String desenvolvedor) { this.desenvolvedor = desenvolvedor; }
	public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }
	public void setIdJogoRastreavel(String idJogoRastreavel) { this.idJogoRastreavel = idJogoRastreavel; }
	public void setProprietariosEstimados(Integer proprietariosEstimados) { this.proprietariosEstimados = proprietariosEstimados; }

}
