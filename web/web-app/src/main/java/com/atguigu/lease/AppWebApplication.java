package com.atguigu.lease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/8 12:30
 * Author: novice21
 * Version: 1.0
 */

@SpringBootApplication
@Async
public class AppWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppWebApplication.class);
    }
}
