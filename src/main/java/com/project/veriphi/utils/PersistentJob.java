package com.project.veriphi.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/health")
public class PersistentJob {

    private static long no = 0;

    @GetMapping("/check")
    public void powerUp(){
        log.info("persisting health check {}...", no);
        no++;
    }
}
