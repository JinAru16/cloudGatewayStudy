package com.mi.gateway.exception;

import com.mi.gateway.common.exception.CustomTokenException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class SecurityExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 여기서 필터 단계에서 발생한 예외를 캐치
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "예상치 못한 에러가 발생했습니다.";

        if (ex instanceof CustomTokenException) {
            status = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage(); // "토큰이 유효하지 않습니다."
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\": \"" + message + "\"}";

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
