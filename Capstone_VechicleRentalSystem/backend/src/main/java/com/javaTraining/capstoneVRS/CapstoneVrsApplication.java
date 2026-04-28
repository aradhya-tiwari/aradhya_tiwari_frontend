package com.javaTraining.capstoneVRS;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CapstoneVrsApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		ZoneId currentZone = ZoneId.systemDefault();
		System.out.println("Backend Started...");
		System.out.println(currentZone);
		SpringApplication.run(CapstoneVrsApplication.class, args);

	}

}
