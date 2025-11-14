package com.atguigu.lease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/10/25 12:40
 * Author: novice21
 * Version: 1.0
 */
@SpringBootApplication
@EnableScheduling
public class AdminWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
    }
}
