package com.sdut.covid19.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @Author: badwei
 * @Date: 2022/4/30 23:54
 * @Description:
 */
@Component
@Slf4j
public class ThreadPoolExecutorConfig {

    @Bean("imageOcrThreadPoolExecutor")
    public ThreadPoolExecutor imageOcrThreadPoolExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
                3,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(512),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
