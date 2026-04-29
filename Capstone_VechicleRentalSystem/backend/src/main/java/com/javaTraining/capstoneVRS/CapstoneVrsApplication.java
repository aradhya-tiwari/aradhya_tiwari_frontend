package com.javaTraining.capstoneVRS;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CapstoneVrsApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		ZoneId currentZone = ZoneId.systemDefault();
		SpringApplication.run(CapstoneVrsApplication.class, args);

	}

}
