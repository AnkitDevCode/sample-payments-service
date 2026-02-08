package com.payment.starter.security.iam;

import com.nimbusds.jose.jwk.RSAKey;
import com.payment.starter.security.config.SecurityProperties;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Getter
public final class RsaKeyProvider {

    private final RSAKey rsaJwk;
    private final PrivateKey privateKey;

    public RsaKeyProvider(ResourceLoader loader, SecurityProperties.IamConfig config) {
        RSAPublicKey publicKey = loadPublicKey(loader, config.getSslCertificatePath());
        this.privateKey = loadPrivateKey(loader, config.getSslPrivateKeyPath());
        this.rsaJwk = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(config.getKeyId() != null ? config.getKeyId() : UUID.randomUUID().toString()).build();
    }

    private RSAPublicKey loadPublicKey(ResourceLoader loader, String path) {
        try {
            Resource r = loader.getResource(path);
            byte[] pem = read(r);
            byte[] decoded = decodePem(pem, "PUBLIC KEY");
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key", e);
        }
    }

    private PrivateKey loadPrivateKey(ResourceLoader loader, String path) {
        try {
            Resource r = loader.getResource(path);
            byte[] pem = read(r);
            byte[] decoded = decodePem(pem, "PRIVATE KEY");
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key", e);
        }
    }

    private byte[] read(Resource r) throws Exception {
        try (InputStream in = r.getInputStream()) {
            return in.readAllBytes();
        }
    }

    private byte[] decodePem(byte[] pem, String type) {
        return Base64.getDecoder().decode(new String(pem).replace("-----BEGIN " + type + "-----", "").replace("-----END " + type + "-----", "").replaceAll("\\s+", ""));
    }
}