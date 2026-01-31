package com.payments.util;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class JwtGenerator {

    public static void main(String[] args) throws Exception {

        // Read private key from resources/certs
        String keyPem = loadPrivateKeyFromResources("certs/payment-service.key.pem");

        byte[] keyBytes = Base64.getDecoder().decode(keyPem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        RSAPrivateKey privateKey = (RSAPrivateKey)
                KeyFactory.getInstance("RSA").generatePrivate(spec);

        // ---- JWT claims ----
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("auth-service")         // Must match your config
                .audience("payment-service")                   // Must match your config
                .subject("user-123")
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1 hour
                .issueTime(new Date())
                .claim("email", "user@example.com")
                .claim("role", "ADMIN")
                .build();

        // ---- Header ----
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID("payment-service-key")  // Must match your config key-id
                .type(JOSEObjectType.JWT)
                .build();

        // ---- Sign ----
        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new RSASSASigner(privateKey));

        String token = jwt.serialize();

        System.out.println("=== Generated JWT Token ===");
        System.out.println(token);
        System.out.println("\n=== Token Details ===");
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Audience: " + claims.getAudience());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Expires: " + claims.getExpirationTime());
        System.out.println("Key ID: " + header.getKeyID());
        System.out.println("\n=== Test with: ===");
        System.out.println("curl -H \"Authorization: Bearer " + token + "\" http://localhost:8080/api/test");
    }

    private static String loadPrivateKeyFromResources(String resourcePath) throws Exception {
        // Load from classpath (resources folder)
        try (InputStream inputStream = JwtGenerator.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IllegalArgumentException("Private key file not found in resources: " + resourcePath);
            }

            String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // Remove PEM headers and whitespace
            return pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                    .replace("-----END RSA PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
        }
    }
}