package com.sourcream.qrcodescavengerhunt.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
       if (FirebaseApp.getApps().isEmpty()){
           FirebaseOptions options = FirebaseOptions.builder()
                   .setCredentials(GoogleCredentials.fromStream(new FileInputStream("./serviceAccountKey.json")))
                   .setStorageBucket("qr-code-scavengerhunt.appspot.com")
                   .build();

           FirebaseApp.initializeApp(options);
       }
    }

}
