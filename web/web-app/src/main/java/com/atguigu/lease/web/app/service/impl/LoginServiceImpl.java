package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.app.mapper.UserInfoMapper;
import com.atguigu.lease.web.app.service.LoginService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.vo.user.LoginVo;
import com.atguigu.lease.web.app.vo.user.UserInfoVo;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static com.atguigu.lease.common.com.atguigu.lease.common.constant.RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC;
import static com.atguigu.lease.common.result.ResultCodeEnum.*;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserInfoService infoService;

    @Resource
    private UserInfoMapper mapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void getCode(String phone) {
        //判断手机号是否为空
        if (!StringUtils.hasText(phone)) {
            throw new LeaseException(APP_LOGIN_PHONE_EMPTY);
        }

        //限制访问频率
        String key = RedisConstant.APP_LOGIN_PREFIX + phone;
        Boolean hasKey = stringRedisTemplate.hasKey(key);
        if (hasKey) {
            Long expireTime = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (RedisConstant.APP_LOGIN_CODE_TTL_SEC - expireTime < APP_LOGIN_CODE_RESEND_TIME_SEC) {
                throw new LeaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);
            }
        }

        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        String code = captcha.text().toLowerCase();// 获取验证码的字符
        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);


    }

    @Override
    public String loginCheck(LoginVo loginVo) {

        //前端发送手机号码`phone`。
        String phone = loginVo.getPhone();

        //首先校验`phone`和`code`是否为空，若为空，直接响应`手机号码为空`或者`验证码为空`，若不为空则进入下步判断。
        if (!StringUtils.hasText(phone)) {
            throw new LeaseException(APP_LOGIN_PHONE_EMPTY);
        }

        if (!StringUtils.hasText(loginVo.getCode())) {
            throw new LeaseException(APP_LOGIN_CODE_EMPTY);
        }

        //根据`phone`从Redis中查询之前保存的验证码，若查询结果为空，则直接响应`验证码已过期` ，若不为空则进入下一步判断。
        String code = stringRedisTemplate.opsForValue().get(RedisConstant.APP_LOGIN_PREFIX + phone);
        if (!StringUtils.hasText(code)) {
            throw new LeaseException(APP_LOGIN_CODE_EXPIRED);
        }

        //比较前端发送的验证码和从Redis中查询出的验证码，若不同，则直接响应`验证码错误`，若相同则进入下一步判断。
        if (!loginVo.getCode().equals(code)) {
            throw new LeaseException(APP_LOGIN_CODE_ERROR);
        }

        //使用`phone`从数据库中查询用户信息，若查询结果为空，则创建新用户，并将用户保存至数据库，然后进入下一步判断。
        UserInfo user = mapper.selectByPhone(phone);
        if (user == null) {
            user = new UserInfo();
            user.setPhone(phone);
            user.setStatus(BaseStatus.ENABLE);
            user.setNickname("用户-" + loginVo.getPhone().substring(6));
            infoService.save(user);
        }
        //判断用户是否被禁用，若被禁，则直接响应`账号被禁用`，否则进入下一步。
        if (user.getStatus().equals(BaseStatus.DISABLE)) {
            throw new LeaseException(APP_ACCOUNT_DISABLED_ERROR);
        }

        //创建JWT并响应给前端。
        return JwtUtil.createToken(user.getId(), phone);

    }
}
