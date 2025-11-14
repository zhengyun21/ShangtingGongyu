package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private SystemUserMapper mapper;

    @Resource
    private SystemUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public CaptchaVo getCaptcha() {

        //生成图形验证码和UUID
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String code = captcha.text().toLowerCase();
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();

        //保存到redis
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC, TimeUnit.SECONDS);

        //生成图片base64
        String image = captcha.toBase64();

        return new CaptchaVo(image, key);
    }

    @Override
    public String checkLogin(LoginVo loginVo) {
        /*
         * -    前端发送`username`、`password`、`captchaKey`、`captchaCode`请求登录。
         * -    判断`captchaCode`是否为空，若为空，则直接响应`验证码为空`；若不为空进行下一步判断。
         * -    根据`captchaKey`从Redis中查询之前保存的`code`，若查询出来的`code`为空，则直接响应`验证码已过期`；若不为空进行下一步判断。
         * -    比较`captchaCode`和`code`，若不相同，则直接响应`验证码不正确`；若相同则进行下一步判断。
         * -    根据`username`查询数据库，若查询结果为空，则直接响应`账号不存在`；若不为空则进行下一步判断。
         * -    查看用户状态，判断是否被禁用，若禁用，则直接响应`账号被禁`；若未被禁用，则进行下一步判断。
         * -    比对`password`和数据库中查询的密码，若不一致，则直接响应`账号或密码错误`，若一致则进行入最后一步。
         * -    创建JWT，并响应给浏览器。
         */
        //验证码是否为空
        if (!StringUtils.hasText(loginVo.getCaptchaCode())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY);
        }

        //查询redis验证码是否过期
        String code = stringRedisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if (code == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
        }

        //判断验证码是否相等
        if (!code.equals(loginVo.getCaptchaCode())) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);
        }

        //判断是否存在该用户
        LambdaQueryWrapper<SystemUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemUser::getUsername, loginVo.getUsername());
        long count = userService.count(lambdaQueryWrapper);
        if (count < 1) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);
        }

        //判断用户是否活跃
        lambdaQueryWrapper.eq(SystemUser::getStatus, BaseStatus.ENABLE);
        count = userService.count(lambdaQueryWrapper);
        if (count < 1) {
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR);
        }

        //判断用户密码是否正确
        lambdaQueryWrapper.eq(SystemUser::getPassword, DigestUtils.md5Hex(loginVo.getPassword()));
        count = userService.count(lambdaQueryWrapper);
        if (count < 1) {
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);
        }

        SystemUser systemUser = userService.getOne(lambdaQueryWrapper);
        return JwtUtil.createToken(systemUser.getId(), loginVo.getUsername());
    }

    @Override
    public SystemUserInfoVo getLoginUserInfo(Long userId) {
        SystemUser systemUser = mapper.selectById(LoginUserHolder.getLoginUser().getUserId());
        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo(systemUser.getUsername(),systemUser.getAvatarUrl());
        return systemUserInfoVo;
    }


}
