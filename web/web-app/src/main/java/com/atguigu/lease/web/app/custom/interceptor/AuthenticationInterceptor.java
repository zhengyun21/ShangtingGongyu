package com.atguigu.lease.web.app.custom.interceptor;

import com.atguigu.lease.common.login.AppLoginUser;
import com.atguigu.lease.common.login.AppLoginUserHolder;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/8 14:25
 * Author: novice21
 * Version: 1.0
 */

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        String token = request.getHeader("access-token");
        String token = request.getHeader("access-token");

        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        AppLoginUserHolder.setAppLoginUser(new AppLoginUser(userId, username));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
