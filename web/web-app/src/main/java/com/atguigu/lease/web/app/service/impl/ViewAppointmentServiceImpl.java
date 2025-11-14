package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.web.app.mapper.ViewAppointmentMapper;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.GraphInfoService;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentDetailVo;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【view_appointment(预约看房信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ViewAppointmentServiceImpl extends ServiceImpl<ViewAppointmentMapper, ViewAppointment>
        implements ViewAppointmentService {

    @Resource
    private ViewAppointmentMapper viewAppointmentMapper;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private ApartmentInfoService apartmentInfoService;

//    @Resource
//    private ViewAppointmentService viewAppointmentService;

    @Override
    public List<AppointmentItemVo> getViewItemList(Long userId) {
//        LambdaQueryWrapper<ViewAppointment> viewAppointmentWrapper = new LambdaQueryWrapper<>();
//        viewAppointmentWrapper.eq(ViewAppointment::getUserId,userId)
//                .eq(ViewAppointment::getIsDeleted,0);
//        List<ViewAppointment> list = viewAppointmentService.list(viewAppointmentWrapper);
//
//        List<AppointmentItemVo> appointmentItemVos = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(list)){
//            for (ViewAppointment viewAppointment : list) {
//                LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
//                graphInfoWrapper.eq(GraphInfo::getItemId,apartmentInfoService.getById(viewAppointment.getApartmentId()));
//                List<GraphInfo> graphInfoList = graphInfoService.list(graphInfoWrapper);
//                List<GraphVo> graphVoList = new ArrayList<>();
//                for (GraphInfo graphInfo : graphInfoList) {
//                    GraphVo graphVo = new GraphVo();
//                    graphVo.setUrl(graphInfo.getName());
//                    graphVo.setName(graphInfo.getUrl());
//                    graphVoList.add(graphVo);
//                }
//
//                AppointmentItemVo appointmentItemVo = new AppointmentItemVo();
//                appointmentItemVo.setApartmentName(apartmentInfoService.getById(viewAppointment.getApartmentId()).getName());
//                appointmentItemVo.setGraphVoList(graphVoList);
//                appointmentItemVo.setId(viewAppointment.getId());
//                appointmentItemVo.setAppointmentStatus(viewAppointment.getAppointmentStatus());
//                appointmentItemVo.setAppointmentTime(viewAppointment.getAppointmentTime());
//            }
//        }

        return null;
    }

    @Override
    public AppointmentDetailVo getDetailById(Long id) {

        ViewAppointment viewAppointment = viewAppointmentMapper.selectById(id);

        ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(viewAppointment.getApartmentId());

        AppointmentDetailVo agreementDetailVo = new AppointmentDetailVo();
        BeanUtils.copyProperties(viewAppointment, agreementDetailVo);

        agreementDetailVo.setApartmentItemVo(apartmentItemVo);

        return agreementDetailVo;
    }
}




