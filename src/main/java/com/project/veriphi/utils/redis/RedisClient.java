package com.project.veriphi.utils.redis;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
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

    @Autowired
    private UnifiedJedis jedis;

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
            log.error("Error occurred while putting string-expiry to redis: {}", e.getMessage());
            return null;
        }
    }

    public String getString(String key, boolean expirationSet){
        try{
            if(!expirationSet){
                String output = jedis.get(key);
                return output==null ? "null" : output;
            }
            String output = jedis.getEx(key, new GetExParams());
            return output==null ? "null" : output;
        } catch (Exception e){
            log.error("Error occurred while getting string from redis: {}", e.getMessage());
            return  null;
        }
    }

    public String deleteKey(String key) {
        try {
            long out = jedis.del(key);
            return out == 1 ? "success" : "failure";
        } catch (Exception e) {
            log.error("Error while deleting cache key: {}", e.getMessage());
            return null;
        }
    }

    public String createHash(String key, Map<String, String> values) {
        System.out.println("creating hash...");
        try{
            long totalAdditions = 0;
            for(Map.Entry<String, String> e: values.entrySet()) {
                System.out.println("entry 1");
                long output = jedis.hset(key, e.getKey(), e.getValue());
                totalAdditions += output;
            }
            return totalAdditions == values.size() ? "success" : "failure";
        } catch (Exception e){
            log.error("Error occurred while creating hash: {}", e.getMessage());
            return  null;
        }
    }

    public String incrementHash(String key, String field, long value) {
        try{
            long output = jedis.hincrBy(key, field, value);
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
            log.error("Error while getting hash field: {}", e.getMessage());
            return null;
        }
    }
    public Map<String, String> getHash(String key){
        try{
            return jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("Error while getting hash: {}", e.getMessage());
            return null;
        }
    }
}
