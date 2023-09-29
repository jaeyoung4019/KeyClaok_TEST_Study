package com.example.keycloak_test.vo.response_util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class RestExceptionAdvisor {

    @ExceptionHandler(RestException.class) @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public Response handleRESTException(RestException e) {
        return e.getResponse();
    }

    @ExceptionHandler(BindException.class) @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public Response validationBindException(BindException e) {
        return new Response.ResponseBuilder<>("알맞은 형식의 값을 입력해주세요." , 403).build();
    }
    @ExceptionHandler(UsernameNotFoundException.class) @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public Response userNameNotFoundException(UsernameNotFoundException e){
        return new Response.ResponseBuilder<>(e.getMessage() , 404).build();
    }

    @ExceptionHandler(IllegalStateException.class) @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public Response illegalStateException(IllegalStateException e){
        return new Response.ResponseBuilder<>(e.getMessage() , 402).build();
    }

    @ExceptionHandler(IllegalArgumentException.class) @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public Response illegalArgumentException(IllegalArgumentException e){
        return new Response.ResponseBuilder<>(e.getMessage() , 404).build();
    }
}