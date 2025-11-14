package com.atguigu.lease.common.login;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/7 17:31
 * Author: novice21
 * Version: 1.0
 */

public class LoginUserHolder {
    public static ThreadLocal<LoginUser> loginUserThreadLocal = new ThreadLocal<>();



    public static void setLoginUser(LoginUser loginUser){
        loginUserThreadLocal.set(loginUser);
    }

    public static LoginUser getLoginUser(){
        return loginUserThreadLocal.get();
    }

    public static void clear(){
        loginUserThreadLocal.remove();
    }
}
