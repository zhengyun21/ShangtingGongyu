package com.atguigu.lease.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/10/26 16:16
 * Author: novice21
 * Version: 1.0
 */
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {


    private String endPoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;


}
