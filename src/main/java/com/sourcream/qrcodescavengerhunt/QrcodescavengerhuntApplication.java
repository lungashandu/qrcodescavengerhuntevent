package com.sourcream.qrcodescavengerhunt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class QrcodescavengerhuntApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrcodescavengerhuntApplication.class, args);
	}

}
