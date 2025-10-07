package no.hvl.dat250.Assignment1.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.UnifiedJedis;

@Configuration
public class RedisConfig {
    @Bean
    public UnifiedJedis jedis() {
        return new UnifiedJedis("redis://localhost:6379");
    }
}
