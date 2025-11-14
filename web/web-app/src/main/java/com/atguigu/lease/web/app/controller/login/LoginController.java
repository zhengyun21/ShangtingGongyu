package com.atguigu.lease.web.app.controller.login;


import com.atguigu.lease.common.login.AppLoginUser;
import com.atguigu.lease.common.login.AppLoginUserHolder;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.web.app.mapper.UserInfoMapper;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录管理")
@RestController
@RequestMapping("/app/")
public class LoginController {

    @Resource
    private LoginService service;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserInfoMapper mapper;

    @GetMapping("login/getCode")
    @Operation(summary = "获取短信验证码")
    public Result getCode(@RequestParam String phone) {
        service.getCode(phone);
        return Result.ok();
    }

    @PostMapping("login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody LoginVo loginVo) {
        String result = service.loginCheck(loginVo);
        return Result.ok(result);
    }

    @GetMapping("info")
    @Operation(summary = "获取登录用户信息")
    public Result<UserInfoVo> info() {
        UserInfoVo userInfoVo = new UserInfoVo();
        AppLoginUser loginUser = AppLoginUserHolder.getAppLoginUser();
        System.out.println(loginUser);
        UserInfo userInfo = mapper.selectById(loginUser.getUserId());
        userInfoVo.setNickname(userInfo.getNickname());
        userInfoVo.setAvatarUrl(userInfo.getAvatarUrl());
        return Result.ok(userInfoVo);
    }
}

