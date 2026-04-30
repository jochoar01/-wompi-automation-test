package com.wompi.automation.abilities;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Ability: MakeHttpRequests
 * Otorga al actor la capacidad de conectarse a la API de WOMPI.
 */
public class MakeHttpRequests {

    private final String baseUrl;
    private final String merchantKey;

    private MakeHttpRequests(String baseUrl, String merchantKey) {
        this.baseUrl = baseUrl;
        this.merchantKey = merchantKey;
    }

    public static MakeHttpRequests aWompiCon(String baseUrl, String merchantKey) {
        return new MakeHttpRequests(baseUrl, merchantKey);
    }

    public RequestSpecification asRequest() {
        return given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + merchantKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
    }

    public String getMerchantKey() {
        return merchantKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
