package com.indielens.jdbc;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

public class MainConexao {
	
	public static void main(String[] args) {
		Conexao conexao = new Conexao();
		JdbcTemplate tamplate = new JdbcTemplate(conexao.getConexao());
		
		
		List<Map<String, Object>> perfis = tamplate.queryForList("SELECT * FROM perfil");

		// Imprime todas as linhas no terminal
		perfis.forEach(linha -> System.out.println(linha));
	}

}
