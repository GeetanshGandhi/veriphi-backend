package com.project.veriphi.utils.redis;

import redis.clients.jedis.HostAndPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {

    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.password}")
    private String password;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password); // optional

        JedisClientConfiguration jedisClientConfiguration =
                JedisClientConfiguration.builder().usePooling().build();

        return new JedisConnectionFactory(config, jedisClientConfiguration);
    }

    @Bean
    public UnifiedJedis unifiedJedis(JedisConnectionFactory factory) {
        // Build UnifiedJedis from the same config Spring uses
        HostAndPort hostAndPort = new HostAndPort(factory.getHostName(), factory.getPort());

        DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .password(String.valueOf(factory.getPassword()))
                .build();

        return new UnifiedJedis(hostAndPort, clientConfig);
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisKeyExpirationListener listener) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Subscribe to expired events in DB 0
        container.addMessageListener(listener, new PatternTopic("__keyevent@0__:expired"));

        return container;
    }
}
