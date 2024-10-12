package com.example.demo;

import com.example.demo.entites.users;
import com.example.demo.Repository.UserRepository;
import com.example.demo.entites.users;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
public class DemoApplication {
	private final UserRepository userRepository;

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		DemoApplication app = context.getBean(DemoApplication.class);
		app.run();
	}

	public void run() {
		// create new user
		System.out.println("Creating new user");
		users User = new users(5, "user5", "free");
		userRepository.save(User);
	}
}
