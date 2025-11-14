package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.ApartmentInfoService;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    @Resource
    private LeaseAgreementMapper mapper;

    @Resource
    private ApartmentInfoMapper apartMapper;

    @Resource
    private RoomInfoMapper roomMapper;

    @Resource
    private PaymentTypeMapper paymentMapper;

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Override
    public AgreementVo getDetailsById(Long id) {
        /*
            根据id判断是否存在租约信息
            不存在，则返回null
            存在，则查询信息。
         */
        LeaseAgreement agreement = super.getById(id);

        if (agreement != null){
            //查询签约公寓信息
            ApartmentInfo apartmentInfo = apartMapper.selectById(agreement.getApartmentId());

            //查询签约房间信息
            RoomInfo roomInfo = roomMapper.selectById(agreement.getRoomId());

            //查询支付方式
            PaymentType paymentType = paymentMapper.selectById(agreement.getPaymentTypeId());

            //查询租期
            LeaseTerm leaseTerm = leaseTermMapper.selectById(agreement.getLeaseTermId());

            AgreementVo agreementVo = new AgreementVo();
            BeanUtils.copyProperties(agreement,agreementVo);
            agreementVo.setApartmentInfo(apartmentInfo);
            agreementVo.setRoomInfo(roomInfo);
            agreementVo.setPaymentType(paymentType);
            agreementVo.setLeaseTerm(leaseTerm);

            return agreementVo;
        }else {
            return null;
        }

    }

    @Override
    public IPage<AgreementVo> selectByPage(IPage<AgreementVo> page, AgreementQueryVo queryVo) {
        return mapper.selectByPage(page,queryVo);
    }
}




