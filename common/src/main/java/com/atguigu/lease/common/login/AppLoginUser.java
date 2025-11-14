package com.atguigu.lease.common.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/11 17:21
 * Author: novice21
 * Version: 1.0
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppLoginUser {

    private Long userId;

    private String username;
}
