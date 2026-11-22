package com.huit.sportsbooking.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Cấu hình nạp sẵn 4 script Lua từ classpath khi ứng dụng khởi động (mục 3.7.3).
 */
@Configuration
public class RedisConfig {

    @Bean
    public DefaultRedisScript<Long> holdSlotsScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/lua/hold_slots.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> releaseHoldScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/lua/release_hold.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public DefaultRedisScript<Long> rateLimitScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/lua/rate_limit.lua"));
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    @SuppressWarnings("rawtypes")
    public DefaultRedisScript<List> popDueScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/lua/pop_due.lua"));
        script.setResultType(List.class);
        return script;
    }
}
