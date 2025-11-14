package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/7 16:14
 * Author: novice21
 * Version: 1.0
 */

public class JwtUtil {
    private static long tokenExpiration = 60 * 60 * 1000L * 24 * 365;
    private static SecretKey tokenSignKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());

    public static String createToken(Long userId, String username) {
        String token = Jwts.builder().
                setSubject("USER_INFO").
                setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)).
                claim("userId", userId).
                claim("username", username).
                signWith(tokenSignKey).
                compact();

        return token;
    }

    public static Claims parseToken(String token) {

        if (token == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }

        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenSignKey).build();
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(JwtUtil.createToken(2L,"13888888888"));
    }
//  web-admin  eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSX0lORk8iLCJleHAiOjE3OTQxMjU1MTgsInVzZXJJZCI6MiwidXNlcm5hbWUiOiJ1c2VyIn0.Hwop3y0tQHCYihLI4PCXM_XjAr3vjdfW6kWpPv9MJkE

//eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSX0lORk8iLCJleHAiOjE3NjI5MzY1MTgsInVzZXJJZCI6MiwidXNlcm5hbWUiOiIxMzg4ODg4ODg4OCJ9.k6MsMgQ0pMNNTHmsVd5xpCtXrPG2ZSqjp6gKN-c6ai0

    //eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVU0VSX0lORk8iLCJleHAiOjE3OTQ0NzI3NTAsInVzZXJJZCI6MiwidXNlcm5hbWUiOiIxMzg4ODg4ODg4OCJ9.ua7rqslB_2rhoykkskr07dlO7smxVRBV3pU0w_gSnqc
}
