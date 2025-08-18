package com.project.veriphi.utils;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

@Component
@Slf4j
@ConfigurationProperties(prefix = "redis")
public class RedisClient {

    @Getter @Setter
    private String host;
    @Getter @Setter
    private int port;
    @Getter @Setter
    private String password;

    private UnifiedJedis jedis;

    @PostConstruct
    public void init() {
        log.info("initializing redis connection to URL: "+host + ":" + port);
        HostAndPort node = new HostAndPort(host, port);
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .password(password).build();
        jedis = new UnifiedJedis(node, config);
    }

    public String setString(String key, String value){
        try{
            return jedis.set(key, value);
        } catch (Exception e){
            log.error("Error occurred while putting string to redis: {}", e.getMessage());
            return null;
        }
    }

    public String getString(String key){
        try{
            return jedis.get(key);
        } catch (Exception e){
            log.error("Error occurred while getting string from redis: {}", e.getMessage());
            return  null;
        }
    }
}
