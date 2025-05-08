package com.mi.gateway;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

@SpringBootTest
public class jasyptTest {
    @Value("${jasypt.encryptor.password}")
    private String password;

    @Qualifier("jasyptStringEncryptor")
    @Autowired
    private StringEncryptor stringEncryptor;

    @Test
    @Description("ENC 내부의 암호화문을 복호화하는데 성공한다.")
    void decrypt() {
        String encText = "ENC(+MizLpeZNubNTqbr6aGwHdezQlx//Nq1ScSFKouWxC/JH5Xs2nGlxSLUAv69XMf6+4QK35C1a+RQe70lwL150tZYfBfU4AhFAi1dQ9fU3umUBOSvI9LBNW5D9UMQV2eq)"; // 실제 yml에 있는 문자열
        String decrypted = stringEncryptor.decrypt(encText.replace("ENC(", "").replace(")", ""));
        System.out.println("복호화 결과: " + decrypted);
    }

    @Test
    @Description("DB 커넥션 정보를 복호화하는데 성공한다.")
    void decryptDbConnectionInformation() {
        String encText = "ENC()";
        String decrypted = stringEncryptor.decrypt(encText.replace("ENC(", "").replace(")", ""));
        System.out.println("복호화 결과: " + decrypted);
    }

    @Test
    @Description("가비아 커넥션 정보를 암호화하는데 성공한다.")
    void encryptGabiaDbConnectionInformation() {

        String username = "";
        String password = "";

        //String encrypted = stringEncryptor.encrypt(connection);
        String encUsername = stringEncryptor.encrypt(username);
        String encPassword = stringEncryptor.encrypt(password);

       // System.out.println("암호화 결과 : " + encrypted);
        System.out.println("아이디 : " + encUsername);
        System.out.println("비번 : " + encPassword);
    }
}
