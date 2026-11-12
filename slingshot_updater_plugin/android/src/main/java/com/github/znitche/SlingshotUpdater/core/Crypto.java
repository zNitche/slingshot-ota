package com.github.znitche.SlingshotUpdater.core;

import com.github.znitche.SlingshotUpdater.helpers.FilesUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Crypto {
    public static PublicKey loadRsaPublicKey(InputStream inputStream)
            throws Exception {

        String pemData;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            pemData = content.toString();
        }

        String pemBase64Data = pemData
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(pemBase64Data));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(spec);
    }

    public static Boolean validateFile(File file, InputStream pubKeyInputStream, File signature) {
        try {
            PublicKey pubKey = loadRsaPublicKey(pubKeyInputStream);

            byte[] fileData = Files.readAllBytes(file.toPath());
            byte[] signatureFileData = Files.readAllBytes(signature.toPath());

            return validateFileSignature(pubKey, fileData, signatureFileData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean validateFileSignature(PublicKey pubKey,
                                                byte[] data,
                                                byte[] signatureBytes) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initVerify(pubKey);
        signature.update(data);

        return signature.verify(signatureBytes);
    }

    public static Boolean validateFileSHA256(File file, String sha256) {
        try {
            String fileSha256Hash = FilesUtils.calculateSha256(file);

            return fileSha256Hash.equalsIgnoreCase(sha256);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
