package com.example.profileservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.keycloak")
public class KeycloakProperties {

    private String url;
    private String realm;
    private String clientId;
    private String clientSecret;

    // Getters & Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getRealm() { return realm; }
    public void setRealm(String realm) { this.realm = realm; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
}