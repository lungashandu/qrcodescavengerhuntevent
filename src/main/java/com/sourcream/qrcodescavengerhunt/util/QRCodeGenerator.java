package com.sourcream.qrcodescavengerhunt.util;

import com.google.cloud.storage.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class QRCodeGenerator {

    private final String BUCKET_NAME = "qr-code-scavengerhunt.appspot.com";

    public String generateQRCodeAndUpload(String text) throws Exception {
        byte[] qrCodeBytes = generateQRCodeImageByteArray(text, 300, 300);
        String fileName = UUID.randomUUID().toString() + ".png";

        return uploadQRCodeToFirebase(fileName, qrCodeBytes);
    }

    private byte[] generateQRCodeImageByteArray(String text, int width, int height) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutStream);

        return pngOutStream.toByteArray();
    }

    private String uploadQRCodeToFirebase(String fileName, byte[] qrCodeBytes) throws Exception {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();

        Blob blob = storage.create(blobInfo, qrCodeBytes);

        return "https://firebasestorage.googleapis.com/v0/b/" + BUCKET_NAME + "/o/" + fileName + "?alt=media";
    }
}
