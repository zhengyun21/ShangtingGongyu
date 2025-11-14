package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.function.FailableIntBinaryOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private LeaseTermMapper leaseTermMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private AttrValueMapper attrValueMapper;

    @Resource
    private RoomInfoMapper mapper;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private RoomAttrValueService roomAttrValueService;

    @Resource
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Resource
    private RoomLeaseTermService roomLeaseTermService;

    @Resource
    private ApartmentInfoService apartmentInfoService;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;

    @Resource
    private PaymentTypeMapper paymentTypeMapper;

    @Override
    public List<RoomInfo> getRoomListByApartmentId(Long id) {
        return mapper.getRoomListByApartmentId(id);
    }

    @Override
    public void removeRoomById(Long id) {
        //判断是否存在房间
        boolean exists = super.getById(id) != null;

        if (exists) {
            //删除roominfo信息
            super.removeById(id);

            //删除图片列表
            LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
            graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoWrapper.eq(GraphInfo::getItemId, id);
            graphInfoService.remove(graphInfoWrapper);

            //删除属性信息
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueWrapper = new LambdaQueryWrapper<>();
            roomAttrValueWrapper.eq(RoomAttrValue::getRoomId, id);
            roomAttrValueService.remove(roomAttrValueWrapper);

            //删除配套信息
            LambdaQueryWrapper<RoomFacility> facilityInfoWrapper = new LambdaQueryWrapper<>();
            facilityInfoWrapper.eq(RoomFacility::getRoomId, id);
            roomFacilityService.remove(facilityInfoWrapper);

            //删除标签信息
            LambdaQueryWrapper<RoomLabel> roomLabelWrapper = new LambdaQueryWrapper<>();
            roomLabelWrapper.eq(RoomLabel::getRoomId, id);
            roomLabelService.remove(roomLabelWrapper);

            //删除支付方式列表
            LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeWrapper = new LambdaQueryWrapper<>();
            roomPaymentTypeWrapper.eq(RoomPaymentType::getRoomId, id);
            roomPaymentTypeService.remove(roomPaymentTypeWrapper);

            //删除可选租期列表
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermWrapper.eq(RoomLeaseTerm::getRoomId, id);
            roomLeaseTermService.remove(roomLeaseTermWrapper);
        } else {
            throw new LeaseException(ResultCodeEnum.ADMIN_ROOM_DELETE_ERROR);
        }
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        RoomDetailVo roomDetailVo = new RoomDetailVo();

        RoomInfo roomInfo = roomInfoMapper.selectById(id);

        BeanUtils.copyProperties(roomInfo, roomDetailVo);

        //查询所属公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoService.getById(roomInfo.getApartmentId());

        //查询图片列表
        LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
        graphInfoWrapper.eq(GraphInfo::getItemId, id);
        List<GraphInfo> graphInfoList = graphInfoService.list(graphInfoWrapper);
        ArrayList<GraphVo> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(graphInfoList)) {
            for (GraphInfo graphInfo : graphInfoList) {
                GraphVo graphVo = new GraphVo();
                graphVo.setName(graphInfo.getName());
                graphVo.setUrl(graphInfo.getUrl());
                list.add(graphVo);
            }
        }

        //查询属性信息列表
        List<AttrValueVo> attrValueVoList = attrValueMapper.selectAttrValueVo(id);

        //查询配套信息列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectFacilityInfoById(id);

        //查询标签信息列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectLabelByRoomId(id);

        //查询支付方式列表
        List<PaymentType> paymentTypeList = paymentTypeMapper.selectTypeByRoomId(id);

        //查询可选租期列表
        List<LeaseTerm> leaseTermList = leaseTermMapper.selectLeaseTermByRoomId(id);

        roomDetailVo.setApartmentInfo(apartmentInfo);
        roomDetailVo.setGraphVoList(list);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setLeaseTermList(leaseTermList);

        return roomDetailVo;
    }

    @Override
    public IPage<RoomItemVo> selectByRoomItemVo(IPage<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.selectByRoomItemVo(page, queryVo);
    }

    @Override
    public void saveOrUpdateRoomSubmit(RoomSubmitVo roomSubmitVo) {
        /*
            首先，需要判断数据库里是否存在该房间信息
            如果存在，则需要先删除房间包含的信息，然后重新更新信息。
            如果不存在，则创建房间信息，添加包含的信息。
            最后service save or update 信息。
         */
        //判断是否存在房间
        Long id = roomSubmitVo.getId();
        if (id != null) {
            //存在，删除已有的信息
            //图片列表
            LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
            graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoWrapper.eq(GraphInfo::getItemId, id);
            graphInfoService.remove(graphInfoWrapper);

            //属性信息列表
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueWrapper = new LambdaQueryWrapper<>();
            roomAttrValueWrapper.eq(RoomAttrValue::getRoomId, id);
            roomAttrValueService.remove(roomAttrValueWrapper);

            //配套信息列表
            LambdaQueryWrapper<RoomFacility> roomFacilityWrapper = new LambdaQueryWrapper<>();
            roomFacilityWrapper.eq(RoomFacility::getRoomId, id);
            roomFacilityService.remove(roomFacilityWrapper);

            //标签信息列表
            LambdaQueryWrapper<RoomLabel> roomLabelWrapper = new LambdaQueryWrapper<>();
            roomLabelWrapper.eq(RoomLabel::getRoomId, id);
            roomLabelService.remove(roomLabelWrapper);

            //支付方式列表
            LambdaQueryWrapper<RoomPaymentType> roomPaymentWrapper = new LambdaQueryWrapper<>();
            roomPaymentWrapper.eq(RoomPaymentType::getRoomId, id);
            roomPaymentTypeService.remove(roomPaymentWrapper);

            //可选租期列表
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermWrapper.eq(RoomLeaseTerm::getRoomId, id);
            roomLeaseTermService.remove(roomLeaseTermWrapper);
        }

        //不存在，则添加信息
        super.saveOrUpdate(roomSubmitVo);
        //添加图片列表
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        List<GraphInfo> graphInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(graphVoList)){
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemId(2L);
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
           graphInfoService.saveBatch(graphInfoList);
        }

        //添加属性信息列表
        List<Long> attrValueList = roomSubmitVo.getAttrValueIds();
        List<RoomAttrValue> roomAttrValueList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attrValueList)){
            for (Long attrValueId : attrValueList) {
                RoomAttrValue roomAttrValue = new RoomAttrValue();
                roomAttrValue.setRoomId(id);
                roomAttrValue.setAttrValueId(attrValueId);
                roomAttrValueList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueList);
        }

        //添加配套信息列表
        List<Long> facilityInfoList = roomSubmitVo.getFacilityInfoIds();
        List<RoomFacility> roomFacilityList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(facilityInfoList)){
            for (Long facilityInfoId : facilityInfoList) {
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(id);
                roomFacility.setFacilityId(facilityInfoId);
                roomFacilityList.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilityList);
        }

        //添加标签信息列表
        List<Long> labelInfoList = roomSubmitVo.getLabelInfoIds();
        List<RoomLabel> roomLabelList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(labelInfoList)){
            for (Long labelInfoId : labelInfoList) {
                RoomLabel roomLabel = new RoomLabel();
                roomLabel.setLabelId(labelInfoId);
                roomLabel.setRoomId(id);
                roomLabelList.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabelList);
        }

        //添加支付方式列表
        List<Long> paymentTypeList = roomSubmitVo.getPaymentTypeIds();
        List<RoomPaymentType> roomPaymentTypeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(paymentTypeList)){
            for (Long Id : paymentTypeList) {
                RoomPaymentType roomPaymentType = new RoomPaymentType();
                roomPaymentType.setPaymentTypeId(Id);
                roomPaymentType.setRoomId(id);
                roomPaymentTypeList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypeList);
        }

        //添加可选租期列表
        List<Long> roomLeaseTermList = roomSubmitVo.getLeaseTermIds();
        List<RoomLeaseTerm> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roomLeaseTermList)){
            for (Long termId : roomLeaseTermList) {
                RoomLeaseTerm rlt = new RoomLeaseTerm();
                rlt.setRoomId(id);
                rlt.setLeaseTermId(termId);
                list.add(rlt);
            }
            roomLeaseTermService.saveBatch(list);
        }
    }
}




