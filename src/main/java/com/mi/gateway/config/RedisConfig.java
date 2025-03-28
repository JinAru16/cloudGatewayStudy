package com.mi.gateway.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.auth.database}")
    private int authRedisIndex;

    @Value("${spring.data.redis.board.database}")
    private int boardRedisIndex;

    @Value("${spring.data.redis.auth.host}")
    private String authRedisHost;

    @Value("${spring.data.redis.auth.port}")
    private int authRedisPort;

    @Value("${spring.data.redis.board.host}")
    private String boardRedisHost;

    @Value("${spring.data.redis.board.port}")
    private int boardRedisPort;

    // ✅ [1] 블랙리스트 검증용 Redis ConnectionFactory
    @Bean(name = "authRedisConnectionFactory")
    public RedisConnectionFactory authRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(authRedisHost, authRedisPort);
        config.setDatabase(authRedisIndex);
        return new LettuceConnectionFactory(config);
    }

    // ✅ [3] 기본 `redisTemplate` 빈 추가 (authRedisConnectionFactory 사용)
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("authRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    // ✅ [4] 블랙리스트 검증용 RedisTemplate
    @Bean(name = "blacklistRedisTemplate")
    public RedisTemplate<String, Object> blacklistRedisTemplate(
            @Qualifier("authRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory);
    }

    // ✅ [7] RedisTemplate 생성 메서드 (공통)
    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // ✅ JSON 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(getObjectMapper()));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    // ✅ [8] ObjectMapper 설정 (LocalDateTime 지원)
    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }
}
