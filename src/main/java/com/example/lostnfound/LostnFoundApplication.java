package com.example.lostnfound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@RestController
public class LostnFoundApplication {

	public static void main(String[] args) {
		SpringApplication.run(LostnFoundApplication.class, args);
	}
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@GetMapping("/checkDbConnection")
	public String checkDbConnection() {
		try {
			jdbcTemplate.execute("SELECT 1");
			return "Database is connected";
		} catch (Exception e) {
			return "Database is not connected: " + e.getMessage();
		}
	}
}
