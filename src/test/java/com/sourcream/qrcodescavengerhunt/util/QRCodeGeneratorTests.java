package com.sourcream.qrcodescavengerhunt.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class QRCodeGeneratorTests {

    private static final String BUCKET_NAME = "qr-code-scavengerhunt.appspot.com";
    private static final String FILE_NAME = "test-qr-code.png";
    private static final String TEST_TEXT = "test-location";
    private static final String EXPECTED_URL = "https://firebasestorage.googleapis.com/v0/b/" + BUCKET_NAME + "/o/" + FILE_NAME + "?alt=media";

    @Mock
    private Storage mockStorage;

    @Mock
    private Blob mockBlob;

    @InjectMocks
    private QRCodeGenerator qrCodeGenerator;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testGenerateQRCodeImageByteArray() throws Exception {
        byte[] qrCodeBytes = qrCodeGenerator.generateQRCodeImageByteArray(TEST_TEXT, 300, 300);

        assertNotNull(qrCodeBytes, "QR Code bytes should not be null");
        assertTrue(qrCodeBytes.length > 0, "QR Code byte array should not be empty");
    }

}
