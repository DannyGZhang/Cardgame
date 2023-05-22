package com.comicstore.tournamentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TournamentServiceApplication {

	@Bean
	RestTemplate restTemplate() {return new RestTemplate();}
	public static void main(String[] args) {
		SpringApplication.run(TournamentServiceApplication.class, args);
	}

}
