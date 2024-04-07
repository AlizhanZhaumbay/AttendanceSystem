package com.example.attendance_system.qr;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;

@Configuration
public class RedisConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration("localhost", 6379);

        JedisClientConfiguration jedisClientConfiguration
                = JedisClientConfiguration.builder().build();

        return new JedisConnectionFactory(redisStandaloneConfiguration,
                jedisClientConfiguration);
    }

    @Bean
    public <F,S> RedisTemplate<F, S> redisTemplate() {
        RedisTemplate<F, S> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    public <F, S> SetOperations<F, S> setOperations(RedisTemplate<F, S> redisTemplate){
        return redisTemplate.opsForSet();
    }

    @Bean
    public <F, S> ZSetOperations<F, S> zSetOperations(RedisTemplate<F, S> redisTemplate){
        return redisTemplate.opsForZSet();
    }

}
