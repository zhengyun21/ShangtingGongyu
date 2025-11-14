package com.atguigu.lease.common.minio;

import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/10/26 16:19
 * Author: novice21
 * Version: 1.0
 */

@Configuration
@ConfigurationPropertiesScan("com.atguigu.lease.common.minio")
@ConditionalOnProperty(name = "minio.endpoint")
public class MinioConfiguration {

    @Resource
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioProperties.getEndPoint()).credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
    }

}
