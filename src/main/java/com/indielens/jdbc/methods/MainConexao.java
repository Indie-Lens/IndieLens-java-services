package com.indielens.jdbc.methods;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.indielens.jdbc.Conexao;

public class MainConexao {
	
	public static void main(String[] args) {
		Conexao conexao = new Conexao();
		JdbcTemplate tamplate = new JdbcTemplate(conexao.getConexao());
		
		
		List<Map<String, Object>> perfis = tamplate.queryForList("SELECT * FROM perfil");

		// Imprime todas as linhas no terminal
		perfis.forEach(linha -> System.out.println(linha));
	}

}
