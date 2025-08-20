package com.project.veriphi.utils.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener {

    @Autowired
    RedisClient client;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if(expiredKey.startsWith("User:")) {
            String[] components = expiredKey.split(":");
            String esKey = "EventSchedule:"+
                    components[2]+
                    ":"+components[3]+
                    ":"+components[4]+
                    ":"+components[5];
            client.incrementHash(esKey, components[6], Long.parseLong(components[6]));
        }
    }
}
