package com.nucleusTeqJava2.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		System.out.println("Spring Boot Application Started");

		System.out.println("   GET /users - Get all users");
		System.out.println("   GET /users/{id} - Get user by ID");
		System.out.println("   POST /users - Create new user");
		System.out.println("   PUT /users/{id} - Update user");
		System.out.println("   DELETE /users/{id} - Delete user\n");

		System.out.println("   POST /notifications/trigger?notificationType=email&eventType=created&entityName=User");
		System.out.println("   POST /notifications/send?notificationType=sms&message=Hello\n");

		System.out.println(
				"   GET /message?content=\" Sample text Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"&type=short");
		System.out.println(
				"   GET /message?content=\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\"&type=long\n");
	}

}
