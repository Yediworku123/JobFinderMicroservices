package com.example.profileservice;

import com.example.profileservice.config.KeycloakProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableConfigurationProperties(KeycloakProperties.class)
public class ProfileServiceApplication implements CommandLineRunner {

	private final KeycloakProperties keycloakProps;

	// ✅ Constructor injection (cleaner than @Autowired field)
	public ProfileServiceApplication(KeycloakProperties keycloakProps) {
		this.keycloakProps = keycloakProps;
	}

	@Override
	public void run(String... args) {
		System.out.println("\n========== DEBUG ==========");
		System.out.println("Keycloak URL: " + keycloakProps.getUrl());
		System.out.println("Realm: " + keycloakProps.getRealm());
		System.out.println("===========================\n");
	}

	public static void main(String[] args) {
		SpringApplication.run(ProfileServiceApplication.class, args);
	}
}