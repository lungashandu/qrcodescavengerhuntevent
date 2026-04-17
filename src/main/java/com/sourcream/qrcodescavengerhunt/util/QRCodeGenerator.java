package com.sourcream.qrcodescavengerhunt.util;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class QRCodeGenerator {

    private final Storage storage;
    private final String BUCKET_NAME = "qr-code-scavengerhunt.appspot.com";
    private static final Logger logger = LoggerFactory.getLogger(QRCodeGenerator.class);

    @Autowired
    public QRCodeGenerator(Storage storage) {
        this.storage = storage;
    }

    public record QRCodeUploadResult(String fileName, String downloadUrl) {}

    public QRCodeUploadResult generateQRCodeAndUploadWithMetadata(String text) {
        byte[] qrCodeBytes = generateQRCodeImageByteArray(text, 300, 300);
        String fileName = UUID.randomUUID() + ".png";
        String uri = uploadQRCodeToFirebase(fileName, qrCodeBytes);
        return new QRCodeUploadResult(fileName, uri);
    }

    public String generateQRCodeAndUpload(String text) {
        return generateQRCodeAndUploadWithMetadata(text).downloadUrl();
    }

    public void deleteUploadedFile(String fileName) {
        try {
            BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                logger.info("Deleted orphaned QR code file: {}", fileName);
            } else {
                logger.warn("QR Code file not found during cleanup: {}", fileName);
            }
        } catch (Exception e) {
            logger.error("Failed to cleanup QR Code file {}", fileName, e);
        }
    }

    private byte[] generateQRCodeImageByteArray(String text, int width, int height) {
        try {
            if (text == null || text.isBlank()){
                logger.warn("Attempted to generate QR code with null or blank text");
                throw new IllegalArgumentException("QR code text must not be null or blank");
            }

            if (width <= 0 || height <= 0) {
                logger.warn("Attempted to generated QR code with the height or width set to 0");
                throw new IllegalArgumentException("QR code dimensions must be positive");
            }

            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);

            try (ByteArrayOutputStream pngOutStream = new ByteArrayOutputStream()) {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutStream);

                return pngOutStream.toByteArray();
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for QR Code generation: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Failed to generate QR Code", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR Code", e);
        }
    }

    private String uploadQRCodeToFirebase(String fileName, byte[] qrCodeBytes){
        try {
            if (fileName == null || fileName.isBlank()) {
                logger.warn("Attempted to upload QR Code with null or filename");
                throw new IllegalArgumentException("File name must not be null or blank");
            }

            if (qrCodeBytes == null || qrCodeBytes.length == 0) {
                logger.warn("Attempted to upload QR Code with null or empty QR Code byte array");
                throw new IllegalArgumentException("QR Code byte array must not be null or empty");
            }

            BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();

            storage.create(blobInfo, qrCodeBytes);

            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",BUCKET_NAME, URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input for Firebase QR code upload: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);

        } catch (StorageException e) {
            logger.error("Firebase Storage error while uploading QR code: {}", fileName, e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to upload QR code to firebase storage", e);

        } catch (Exception e) {
            logger.error("Unexpected error uploading QR code: {}", fileName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error uploading QR code", e);
        }
    }
}
