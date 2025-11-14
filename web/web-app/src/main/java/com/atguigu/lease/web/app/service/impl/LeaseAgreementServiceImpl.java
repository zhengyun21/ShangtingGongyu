package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.AppLoginUser;
import com.atguigu.lease.common.login.AppLoginUserHolder;
import com.atguigu.lease.model.entity.GraphInfo;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.entity.LeaseTerm;
import com.atguigu.lease.model.entity.PaymentType;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.*;
import com.atguigu.lease.web.app.service.ApartmentInfoService;
import com.atguigu.lease.web.app.service.GraphInfoService;
import com.atguigu.lease.web.app.service.LeaseAgreementService;
import com.atguigu.lease.web.app.vo.agreement.AgreementDetailVo;
import com.atguigu.lease.web.app.vo.agreement.AgreementItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Resource
    private PaymentTypeMapper paymentTypeMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private ApartmentInfoMapper apartmentInfoMapper;

    @Resource
    private LeaseAgreementMapper mapper;

    @Override
    public List<AgreementItemVo> getListItem() {
        AppLoginUser appLoginUser = AppLoginUserHolder.getAppLoginUser();
        String phone = appLoginUser.getUsername();
        System.out.println(appLoginUser);
        return mapper.getListItem(phone);
    }

    @Override
    public AgreementDetailVo getDetailById(Long id) {
        LeaseAgreement leaseAgreement = mapper.selectById(id);
        if (leaseAgreement == null) {
            return null;
        }

        //获取公寓信息
        String apartmentName = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId()).getName();
        LambdaQueryWrapper<GraphInfo> apartmentGraphWrapper = new LambdaQueryWrapper<>();
        apartmentGraphWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT)
                .eq(GraphInfo::getItemId, leaseAgreement.getApartmentId());
        List<GraphInfo> apartmentGraphList = graphInfoService.list(apartmentGraphWrapper);
        List<GraphVo> apartmentGraphVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(apartmentGraphList)) {
            for (GraphInfo graphInfo : apartmentGraphList) {
                GraphVo graphVo = new GraphVo(graphInfo.getName(), graphInfo.getUrl());
                apartmentGraphVoList.add(graphVo);
            }
        }

        //获取房间信息
        String roomNumber = roomInfoMapper.selectById(leaseAgreement.getRoomId()).getRoomNumber();
        LambdaQueryWrapper<GraphInfo> roomGraphWrapper = new LambdaQueryWrapper<>();
        roomGraphWrapper.eq(GraphInfo::getItemType, ItemType.ROOM)
                .eq(GraphInfo::getItemId, leaseAgreement.getRoomId());
        List<GraphInfo> roomGraphInfoList = graphInfoService.list(roomGraphWrapper);
        List<GraphVo> roomGraphVoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomGraphInfoList)) {
            for (GraphInfo graphInfo : roomGraphInfoList) {
                GraphVo graphVo = new GraphVo(graphInfo.getName(), graphInfo.getUrl());
                roomGraphVoList.add(graphVo);
            }
        }

        //获取支付方式
        String paymentTypeName = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId()).getName();

        //获取租期月份及单位
        LeaseTerm leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());
        Integer monthCount = leaseTerm.getMonthCount();
        String unit = leaseTerm.getUnit();

        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(leaseAgreement,agreementDetailVo);
        agreementDetailVo.setApartmentName(apartmentName);
        agreementDetailVo.setLeaseTermUnit(unit);
        agreementDetailVo.setRoomNumber(roomNumber);
        agreementDetailVo.setPaymentTypeName(paymentTypeName);
        agreementDetailVo.setLeaseTermMonthCount(monthCount);
        agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);

        return agreementDetailVo;
    }
}




