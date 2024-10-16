package com.sourcream.qrcodescavengerhunt.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
public class FirebaseInitialzer {

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream("qr-code-scavengerhunt-firebase-adminsdk.json")))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
