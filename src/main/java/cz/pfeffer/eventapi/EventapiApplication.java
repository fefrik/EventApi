package cz.pfeffer.eventapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"cz.pfeffer.eventapi.repository"})
@EnableTransactionManagement
public class EventapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventapiApplication.class, args);
	}

}
