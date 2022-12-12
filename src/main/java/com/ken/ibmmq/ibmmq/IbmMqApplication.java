package com.ken.ibmmq.ibmmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.db.operator.dboperator", "com.ken.ibmmq.ibmmq"})
public class IbmMqApplication {

	public static void main(String[] args) {
		SpringApplication.run(IbmMqApplication.class, args);
	}
}
