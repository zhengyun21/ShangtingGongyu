package com.atguigu.lease.web.admin.mapper;

import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.web.admin.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
* @author liubo
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.atguigu.lease.model.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    IPage<UserInfo> selectByPage(IPage<UserInfo> page, UserInfoQueryVo queryVo);
}




