package com.bookstore.utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecs {
    public static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .setContentType(ContentType.JSON)
                .build();
    }
    
    public static RequestSpecification getAuthenticatedSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(getBaseSpec())
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}