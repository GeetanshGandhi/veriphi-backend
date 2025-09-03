package com.project.veriphi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersistentJob {

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void powerUp(){
    }
}
