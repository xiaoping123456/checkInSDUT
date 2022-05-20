package com.sdut.covid19;

import com.sdut.covid19.config.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class Covid19Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Covid19Application.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

}
