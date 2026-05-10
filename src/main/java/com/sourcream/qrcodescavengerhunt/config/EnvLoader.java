package com.sourcream.qrcodescavengerhunt.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EnvLoader {
    public static  void loadEnv() {
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if(line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);

                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    System.setProperty(key, value);
                }
            }
        } catch (IOException exception) {
            System.out.println(".env file not found or could not be loaded: " + exception);
        }
    }
}
