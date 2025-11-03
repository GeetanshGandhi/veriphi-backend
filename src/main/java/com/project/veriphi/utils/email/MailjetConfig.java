package com.project.veriphi.utils.email;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailjetConfig {

    @Bean
    public MailjetClient getMailjetClient(@Value("${mailjet.api.public}") String publicKey,
                                          @Value("${mailjet.api.private}") String privateKey){
        return new MailjetClient(
                ClientOptions.builder()
                        .apiKey(publicKey)
                        .apiSecretKey(privateKey)
                        .build()
        );
    }
}
