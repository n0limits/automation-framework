package com.automation.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseAPI {
    protected RequestSpecification request;

    public BaseAPI() {
        this.request = APIClient.getRequestSpec();
    }

    protected Response get(String endpoint) {
        log.info("GET request to: {}", endpoint);
        return request.get(endpoint);
    }

    protected Response post(String endpoint, Object body) {
        log.info("POST request to: {}", endpoint);
        return request.body(body).post(endpoint);
    }

    protected Response put(String endpoint, Object body) {
        log.info("PUT request to: {}", endpoint);
        return request.body(body).put(endpoint);
    }

    protected Response delete(String endpoint) {
        log.info("DELETE request to: {}", endpoint);
        return request.delete(endpoint);
    }
}
