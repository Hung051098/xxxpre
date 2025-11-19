package com.vn.hung.xxxpre.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.GeneralSecurityException;

@Configuration
public class AppConfig {

    @Value("${http.proxy.host:}")
    private String proxyHost;

    @Value("${http.proxy.port:8080}")
    private int proxyPort;

    @Value("${google.api.key}")
    private String apiKey;

    @Bean
    public Drive googleDriveService() throws GeneralSecurityException, IOException {
        // 1. Configure Transport (with Proxy if needed)
        NetHttpTransport.Builder transportBuilder = new NetHttpTransport.Builder();

        if (proxyHost != null && !proxyHost.isEmpty()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            transportBuilder.setProxy(proxy);
            System.out.println("✅ Google Drive Client configured with Proxy: " + proxyHost + ":" + proxyPort);
        }

        // Trust all certificates strategy is harder to inject into NetHttpTransport
        // without custom SSLContext factory, but usually standard transport is sufficient.
        // If you strictly need the "Trust All" SSL context from before,
        // you would need to build the SSLContext and pass it to transportBuilder.doNotValidateCertificate()
        // or similar, but doNotValidateCertificate() is deprecated/removed in newer versions.
        // For standard Google APIs, the default trust store is usually best.

        NetHttpTransport transport = transportBuilder.build();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // 2. Build the Drive Service
        // We pass an empty HttpRequestInitializer because we use API Key, not OAuth2 credentials here.
        return new Drive.Builder(transport, jsonFactory, request -> {
            // You can set global timeouts here if needed
            request.setConnectTimeout(20000);
            request.setReadTimeout(20000);
        })
                .setApplicationName("xxxpre")
                // The API Key will be set on individual requests or here if supported,
                // but usually usually set on the request object for this library pattern.
                .build();
    }
}