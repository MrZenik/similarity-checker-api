package com.yevhen.berladyniuk.codesimilaritychecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CodeSimilarityCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeSimilarityCheckerApplication.class, args);
	}

}
