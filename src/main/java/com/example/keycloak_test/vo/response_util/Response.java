package com.example.keycloak_test.vo.response_util;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@Getter
@ToString
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 362498820763181265L;
    private final String message;  // 결과 메시지
    private final HttpStatus status;       // 결과 코드
    private final int total;          // 총 응답 데이터 수
    private final T response; // 응답 데이터

    public static class ResponseBuilder<T> {

        private final String message;  // 결과 메시지
        private final int code;
        private int total;          // 총 응답 데이터 수
        private T response; // 응답 데이터

        private HttpStatus status;

        private void setStatus(int code){
            try {
                status = HttpStatus.valueOf(code);
            } catch (IllegalArgumentException ignored) {
                status = HttpStatus.MULTI_STATUS;
            };
        }

        public ResponseBuilder(String message, int code) {
            this.message = message;
            this.code = code;
            setStatus(code);
        }

        public ResponseBuilder<T> resultResponse(T value) {
            response = value;
            return this;
        }

        public ResponseBuilder<T> total(int value) {
            total = value;
            return this;
        }
        public Response<T> build() {
            return new Response<>(this);
        }
    }


    private Response(ResponseBuilder<T> responseBuilder){
        message = responseBuilder.message;
        status = responseBuilder.status;
        total = responseBuilder.total;
        response = responseBuilder.response;
    }

}

