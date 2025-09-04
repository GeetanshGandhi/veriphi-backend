package com.project.veriphi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersistentJob {

    private static long no = 0;
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void powerUp(){
        log.info("persisting health check {}...", no);
    }
}
