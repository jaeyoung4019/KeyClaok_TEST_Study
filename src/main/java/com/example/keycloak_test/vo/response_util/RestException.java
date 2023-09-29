package com.example.keycloak_test.vo.response_util;

import lombok.Getter;


@Getter
public class RestException extends Exception{

    private final Response response;

    public RestException(int code, String message) {
        response = new Response.ResponseBuilder<>(message ,code).build();
    }
}
