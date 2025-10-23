package org.infinitytwo.umbralore.core.security;

import java.io.*;
import java.net.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.json.*;

public class Authentication {
    private static final String GOOGLE_CERTS_URL = "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
    private static final String PROJECT_ID = "umbralore-mysical-engine";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static FirebaseTokenPayload verify(String idToken) throws Exception {
        SignedJWT jwt = SignedJWT.parse(idToken);
        String kid = jwt.getHeader().getKeyID();

        // Load Google's public certs
        Map<String, String> certs = mapper.readValue(new URL(GOOGLE_CERTS_URL), Map.class);

        if (!certs.containsKey(kid)) {
            throw new SecurityException("Unknown key ID: " + kid);
        }

        // Parse X.509 certificate and extract the public key
        String certPEM = certs.get(kid);
        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new java.io.ByteArrayInputStream(certPEM.getBytes()));
        RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();

        // Verify signature
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        if (!jwt.verify(verifier)) {
            throw new SecurityException("Invalid signature.");
        }

        // Validate Firebase-specific claims
        String aud = jwt.getJWTClaimsSet().getAudience().get(0);
        String iss = jwt.getJWTClaimsSet().getIssuer();
        String uid = (String) jwt.getJWTClaimsSet().getClaim("user_id");

        if (!aud.equals(PROJECT_ID)) {
            throw new SecurityException("Invalid audience: " + aud);
        }
        if (!iss.equals("https://securetoken.google.com/" + PROJECT_ID)) {
            throw new SecurityException("Invalid issuer: " + iss);
        }

        return new FirebaseTokenPayload(uid);
    }

    public record FirebaseTokenPayload(String uid) { }

    public static JSONObject loginWithEmail(String email, String password, String apiKey) throws IOException {
        URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject request = new JSONObject();
        request.put("email", email);
        request.put("password", password);
        request.put("returnSecureToken", true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(request.toString().getBytes());
        }

        try (InputStream is = conn.getInputStream()) {
            String result = new String(is.readAllBytes());
            return new JSONObject(result);
        } catch (IOException e) {
            InputStream err = conn.getErrorStream();
            String error = new String(err.readAllBytes());
            throw new IOException("Firebase error: " + error);
        }
    }
}
