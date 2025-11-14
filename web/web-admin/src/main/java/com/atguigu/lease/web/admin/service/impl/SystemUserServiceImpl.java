package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.SystemPost;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.SystemPostService;
import com.atguigu.lease.web.admin.service.SystemUserService;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserItemVo;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author liubo
 * @description 针对表【system_user(员工信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser>
        implements SystemUserService {

    @Resource
    private SystemPostService systemPostService;

    @Resource
    private SystemUserMapper mapper;

    @Override
    public IPage<SystemUserItemVo> pageSystemUserByQuery(IPage<SystemUserItemVo> page, SystemUserQueryVo queryVo) {
        return mapper.pageSystemUserByQuery(page,queryVo);
    }

    @Override
    public SystemUserItemVo getSystemUserById(Long id) {
        SystemUserItemVo systemUserItemVo = new SystemUserItemVo();
        SystemUser systemUser = super.getById(id);
        BeanUtils.copyProperties(systemUser,systemUserItemVo);
        SystemPost systemPost = systemPostService.getById(systemUser.getPostId());
        systemUserItemVo.setPostName(systemPost.getName());

        return systemUserItemVo;
    }
}




