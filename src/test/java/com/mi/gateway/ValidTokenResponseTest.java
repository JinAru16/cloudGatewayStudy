package com.mi.gateway;


import com.mi.gateway.util.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ValidTokenResponseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestTokenProvider testTokenProvider;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("유효토큰을 제시하면 엔드포인트에서 200 response를 받는다.")
    void validTokenGet_200_Response(){
        //given 테스트용 토큰 발급.
        String testToken = testTokenProvider.generateTestAuthToken("test");
        System.out.println("testToken : " + testToken);

        System.out.println(jwtTokenProvider.getUsername(testToken));

        //then
        webTestClient.get()
                .uri("/board") // 단순 라우트된 URI
                .cookie("jwt", testToken)
                .exchange()
                .expectStatus().isOk(); // 게이트웨이에서 토큰 통과 시 정상 응답

    }
}
