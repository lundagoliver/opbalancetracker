package com.systems88.opbalancetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = { "com.systems88.opbalancetracker"})
public class OpbalancetrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpbalancetrackerApplication.class, args);
	}

}
