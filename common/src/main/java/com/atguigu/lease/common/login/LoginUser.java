package com.atguigu.lease.common.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/7 17:32
 * Author: novice21
 * Version: 1.0
 */

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser {
    private Long userId;

    private String username;
}
