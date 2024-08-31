package com.ankk.tro.testrestemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class KeycloakManagerCustom {

    // Attributes :
    private static BeanKeycloakAccessToken instance;


    // Methods :
    public static void initialiser(String serverUrl, String clientId, String clientSecret)
    {
        // Create a RestTemplate for making HTTP requests
        RestTemplate restTemplate = new RestTemplate();
        // Set up the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Set up the request body with the necessary parameters
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        requestBody.add("grant_type", "client_credentials");
        // Create the HTTP request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        // Make the request to Keycloak token endpoint
        ResponseEntity<BeanKeycloakAccessToken> responseEntity = restTemplate.postForEntity(serverUrl, requestEntity, BeanKeycloakAccessToken.class);
        // Read if needed :
        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            instance = responseEntity.getBody();
        }
    }

    public static BeanKeycloakAccessToken getInstance() {
        return instance;
    }
}
