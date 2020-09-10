package com.example.springforum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class SpringForumApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringForumApplication.class, args);
	}

}
