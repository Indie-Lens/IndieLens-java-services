package com.indielens.excelConverter;

import org.springframework.jdbc.core.JdbcTemplate;

import com.indielens.jdbc.Conexao;

public class RegisterReader {
	
	Conexao conexao = new Conexao();
	JdbcTemplate template = new JdbcTemplate(conexao.getConexao());
	
	public Integer getRegistro() {
		String comandoSql = "SELECT id_registro FROM registro ORDER BY id_registro DESC LIMIT 1";
		
		try {
			return template.queryForObject(comandoSql, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}

}
