package com.atguigu.lease.common.login;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/11 17:20
 * Author: novice21
 * Version: 1.0
 */

public class AppLoginUserHolder {
    public static ThreadLocal<AppLoginUser> appLoginUserThreadLocal = new ThreadLocal<>();

    public static void setAppLoginUser(AppLoginUser loginUser){
        appLoginUserThreadLocal.set(loginUser);
    }

    public static AppLoginUser getAppLoginUser(){
        return appLoginUserThreadLocal.get();
    }

    public static void clear(){
        appLoginUserThreadLocal.remove();
    }
}
