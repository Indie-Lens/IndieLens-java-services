package com.indielens.jdbc;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import io.github.cdimascio.dotenv.Dotenv;

public class Conexao {

	private DataSource conexao;
	
	public Conexao() {
		DriverManagerDataSource driver = new DriverManagerDataSource();
		Dotenv env = Dotenv.load();
		
		driver.setUsername(env.get("DB_USER"));
		driver.setPassword(env.get("DB_SENHA"));
		driver.setUrl("jdbc:mysql://" + env.get("DB_HOST") + ":" + env.get("DB_PORT") + "/" + env.get("DB_NAME") + "?useSSL=false&serverTimezone=UTC");
		driver.setDriverClassName("com.mysql.cj.jdbc.Driver");
		
		this.conexao = driver;
	}
	
	public DataSource getConexao() {
		return conexao;
	}
}