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
import redis.clients.jedis.params.GetExParams;

import java.util.Map;

@Component
@Slf4j
@ConfigurationProperties(prefix = "redis")
@Getter
@Setter
public class RedisClient {

    private String host;
    private int port;
    private String password;

    private UnifiedJedis jedis;

    @PostConstruct
    public void init() {
        log.info("initializing redis connection to URL: "+host + ":" + port);
        HostAndPort node = new HostAndPort(host, port);
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .password(password).build();
        jedis = new UnifiedJedis(node, config);
        jedis.setex("key4", 50, "value4");
        for(int i = 0; i<10000; i++){}
        System.out.println("data found: "+jedis.getEx("key4", new GetExParams()));
    }

    public String setString(String key, String value){
        try{
            return jedis.set(key, value);
        } catch (Exception e){
            log.error("Error occurred while putting string to redis: {}", e.getMessage());
            return null;
        }
    }

    public String setString(String key, String value, long expireSeconds){
        try{
            return jedis.setex(key, expireSeconds,value);
        } catch (Exception e){
            log.error("Error occurred while putting string to redis: {}", e.getMessage());
            return null;
        }
    }

    public String getString(String key){
        try{
            String output = jedis.get(key);
            return output==null ? "null" : output;
        } catch (Exception e){
            log.error("Error occurred while getting string from redis: {}", e.getMessage());
            return  null;
        }
    }

    public String getString(String key, boolean expirationSet){
        try{
            String output = jedis.getEx(key, new GetExParams());
            return output==null ? "null" : output;
        } catch (Exception e){
            log.error("Error occurred while getting string from redis: {}", e.getMessage());
            return  null;
        }
    }

    public String createHash(String key, Map<String, String> values) {
        try{
            long output = jedis.hset(key, values);
            return output>0 && output!=values.size() ? "partial" : "success";
        } catch (Exception e){
            log.error("Error occurred while creating hash: {}", e.getMessage());
            return  null;
        }
    }

    public String updateHash(String key, String field, String value) {
        try{
            long output = jedis.hset(key, field, value);
            if(output != 1) return "failure";
            return "success";
        } catch (Exception e) {
            log.error("Error while updating hash: {}", e.getMessage());
            return null;
        }
    }
    public String getHashField(String key, String field) {
        try{
            String output = jedis.hget(key, field);
            return output == null ? "null" : output;
        } catch (Exception e) {
            log.error("Error while updating hash: {}", e.getMessage());
            return null;
        }
    }
    public Map<String, String> getHash(String key){
        try{
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("Error while updating hash: {}", e.getMessage());
            return null;
        }
    }
}
