package com.mi.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CustomExceptionTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("만료된 토큰 제시할 때 response를 출력한다.")
    void printJsonResponse(){
        webTestClient.get()
                .uri("/protected-endpoint")
                .cookie("jwt", "invalid.token.value") // 잘못된 토큰 전달
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    String responseHeaders = new String(response.getResponseHeaders().toString());
                    System.out.println("헤더 : "+ responseHeaders);
                    System.out.println("응답 바디: " + responseBody);
                });
    }

    @Test
    @DisplayName("만료된 토큰을 제시하면 CustomTokenException이 터진다.")
    void throwCustomTokenException(){
        webTestClient.get()
                .uri("/protected-endpoint") // 인증이 필요한 URI
                .cookie("jwt", "invalid.token.value") // 잘못된 토큰 전달
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("토큰이 유효하지않습니다."); // CustomUserException 메시지 확인

    }
}
