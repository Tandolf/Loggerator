package se.andolf.loggerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class LoggeratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoggeratorApplication.class, args);
	}

}

