package com.vijay.shb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication

@OpenAPIDefinition(info = @Info(title = "SHB Demo API", version = "1.0", description = "SHB code test"))
public class ShbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShbApplication.class, args);
	}

}
