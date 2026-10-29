package com.indielens.excelConverter;

import org.springframework.jdbc.core.JdbcTemplate;

import com.indielens.jdbc.Conexao;

public class RegisterReader {
	
	private final JdbcTemplate template;

	public RegisterReader() {
		this(new Conexao());
	}

	public RegisterReader(Conexao conexao) {
		template = new JdbcTemplate(conexao.getConexao());
	}
	
	public Integer getRegistro() {
		String comandoSql = "SELECT COALESCE(MAX(id_registro), 0) FROM IndieLensBak";
		return template.queryForObject(comandoSql, Integer.class);
	}

}
