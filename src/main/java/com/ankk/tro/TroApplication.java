package com.ankk.tro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class TroApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TroApplication.class, args);
	}

}
