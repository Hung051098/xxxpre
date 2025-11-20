package com.vn.hung.xxxpre.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {

    @Value("${google.service.account.json:classpath:xxxpre.json}")
    private Resource jsonKeyFile;

    @Value("${google.application.name:xxxpre-video}")
    private String applicationName;

    @Value("${google.truststore:#{null}}")
    private Resource trustStoreFile;

    @Value("${google.truststore.password:changeit}")
    private String trustStorePassword;

    @Value("${google.ssl.disable:false}")
    private boolean disableSSL;

    @Value("${http.proxy.host:}")
    private String proxyHost;

    @Value("${http.proxy.port:8080}")
    private int proxyPort;

    @Value("${google.oauth.client-id}")
    private String clientId;

    @Value("${google.oauth.client-secret}")
    private String clientSecret;

    @Value("${google.oauth.refresh-token}")
    private String refreshToken;

    @PostConstruct
    public void configureSSL() throws Exception {
        if (disableSSL) {
            System.out.println("⚠️ WARNING: SSL verification is DISABLED. Use only in development!");
            disableSSLVerification();
        } else if (trustStoreFile != null && trustStoreFile.exists()) {
            System.out.println("✓ Loading custom truststore: " + trustStoreFile.getFilename());
            setCustomTrustStore();
        }
    }

//    @Bean
//    public Drive driveService() throws GeneralSecurityException, IOException {
//        System.out.println("🔧 Initializing Google Drive Service...");
//        System.out.println("📄 JSON Key File: " + jsonKeyFile.getFilename());
//        System.out.println("📱 Application Name: " + applicationName);
//
//        NetHttpTransport.Builder transportBuilder = new NetHttpTransport.Builder();
//
//        if (proxyHost != null && !proxyHost.isEmpty()) {
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
//            transportBuilder.setProxy(proxy);
//        }
//
//        NetHttpTransport transport = transportBuilder.build();
//        GoogleCredential credential = createGoogleCredential(transport);
//
//        // Build Drive service with credential as HttpRequestInitializer
//        Drive drive = new Drive.Builder(transport, JacksonFactory.getDefaultInstance(), credential)
//                .setApplicationName(applicationName)
//                .build();
//
//        // Verify the credential is properly attached
//        var initializer = drive.getRequestFactory().getInitializer();
//        System.out.println("✓ Request initializer: " + (initializer != null ? initializer.getClass().getSimpleName() : "NULL"));
//
//        // Test the service with a simple API call
//        try {
//            var about = drive.about().get().setFields("user").execute();
//            System.out.println("✓ Drive API test successful!");
//            System.out.println("✓ Connected as: " + about.getUser().getEmailAddress());
//        } catch (IOException e) {
//            System.err.println("❌ Drive API test failed: " + e.getMessage());
//            System.err.println("❌ This means credentials are not working!");
//            throw e;
//        }
//
//        System.out.println("✓ Google Drive Service initialized successfully!");
//        return drive;
//    }
@Bean
public Drive googleDriveService() throws GeneralSecurityException, IOException {
    // 1. Configure Transport
    NetHttpTransport.Builder transportBuilder = new NetHttpTransport.Builder();
    if (proxyHost != null && !proxyHost.isEmpty()) {
        transportBuilder.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
    }
    NetHttpTransport transport = transportBuilder.build();
    GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    // 2. Create User Credentials (acting as YOU)
    UserCredentials credentials = UserCredentials.newBuilder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRefreshToken(refreshToken)
            .build();

    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

    // 3. Build Drive Service
    return new Drive.Builder(transport, jsonFactory, requestInitializer)
            .setApplicationName("xxxpre")
            .build();
}

    private GoogleCredential createGoogleCredential(NetHttpTransport httpTransport)
            throws IOException, GeneralSecurityException {

        GoogleCredential credential;

        // Read the JSON file
        try (InputStream is = jsonKeyFile.getInputStream()) {
            credential = GoogleCredential.fromStream(is, httpTransport, JacksonFactory.getDefaultInstance());
        }

        // CRITICAL: Must create scoped credential - this returns a NEW instance
        if (credential.createScopedRequired()) {
            credential = credential.createScoped(Collections.singleton(DriveScopes.DRIVE));
        }

        // Log credential info (without sensitive data)
        System.out.println("✓ Service Account: " + credential.getServiceAccountId());
        System.out.println("✓ Scopes: " + credential.getServiceAccountScopes());
        System.out.println("✓ Token URI: " + credential.getTokenServerEncodedUrl());

        // Test token refresh - this will fail if credential is not properly configured
        try {
            System.out.println("🔄 Refreshing access token...");
            credential.refreshToken();
            System.out.println("✓ Access token obtained successfully");
            System.out.println("✓ Token expires in: " + credential.getExpiresInSeconds() + " seconds");

            // Verify the token is actually set
            String token = credential.getAccessToken();
            if (token != null && !token.isEmpty()) {
                System.out.println("✓ Token length: " + token.length() + " chars");
                System.out.println("✓ Token preview: " + token.substring(0, Math.min(20, token.length())) + "...");
            } else {
                System.err.println("❌ WARNING: Access token is null or empty!");
                throw new IOException("Failed to obtain access token");
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to obtain access token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return credential;
    }

    private void setCustomTrustStore() throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        try (InputStream is = trustStoreFile.getInputStream()) {
            trustStore.load(is, trustStorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

        SSLContext.setDefault(sslContext);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    private void disableSSLVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());

        SSLContext.setDefault(sc);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}