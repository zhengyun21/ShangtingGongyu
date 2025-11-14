package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Resource
    private ApartmentLabelService apartmentLabelService;

    @Resource
    private ApartmentFacilityService apartmentFacilityService;

    @Resource
    private ApartmentFeeValueService apartmentFeeValueService;

    @Resource
    private GraphInfoService graphInfoService;

    @Resource
    private ApartmentInfoMapper mapper;

    @Resource
    private GraphInfoMapper graphInfoMapper;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private FacilityInfoMapper facilityInfoMapper;

    @Resource
    private FeeValueMapper feeValueMapper;

    @Resource
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private LabelInfoService labelInfoService;

    @Override
    public IPage<ApartmentItemVo> pageApartmentItemByQuery(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return mapper.pageApartmentItemByQuery(page, queryVo);
    }

    @Override
    public ApartmentDetailVo getDetailsByid(Long id) {
        //查询公寓是否存在
        ApartmentInfo apartmentInfo = this.getById(id);
        if (apartmentInfo == null) {
            return null;
        }

        //查询图片列表
        List<GraphVo> graphVoListist = graphInfoMapper.selectGraphByItemTypeAndId(ItemType.APARTMENT, id);
        //查询标签列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectLabelByApartmentId(id);
        //查询配套列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectFacilityByApartmentId(id);
        //查询杂费列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectFeeByApartmentId(id);

        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoListist);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return apartmentDetailVo;
    }

    @Override
    public boolean removeApartmentById(Long id) {

        LambdaQueryWrapper<RoomInfo> roomQueryWrapper = new LambdaQueryWrapper<>();
        roomQueryWrapper.eq(RoomInfo::getApartmentId, id);
        Long count = roomInfoMapper.selectCount(roomQueryWrapper);
        if (count > 0) {
            //直接为前端返回如下响应：先删除房间信息再删除公寓信息
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);
        }

        super.removeById(id);

        //删除公寓图片信息
        LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
        graphInfoWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphInfoWrapper);

        //2.删除ApartmentLabel
        LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(labelQueryWrapper);

        //3.删除ApartmentFacility
        LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(facilityQueryWrapper);

        //4.删除ApartmentFeeValue
        LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
        feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(feeQueryWrapper);
        return true;

    }

    @Override
    public void saveOrUpdateApartmentVo(ApartmentSubmitVo apartmentSubmitVo) {

        Long id = apartmentSubmitVo.getId();
        boolean isUpdate = id != null;
        super.saveOrUpdate(apartmentSubmitVo);

        if (isUpdate) {
            //删除配套信息
            LambdaQueryWrapper<ApartmentFacility> facilityWrapper = new LambdaQueryWrapper<>();
            facilityWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(facilityWrapper);

            //删除标签信息
            LambdaQueryWrapper<ApartmentLabel> labelWrapper = new LambdaQueryWrapper<>();
            labelWrapper.eq(ApartmentLabel::getApartmentId, apartmentSubmitVo.getId());
            apartmentLabelService.remove(labelWrapper);

            //删除杂费值
            LambdaQueryWrapper<ApartmentFeeValue> feeValueWrapper = new LambdaQueryWrapper<>();
            feeValueWrapper.eq(ApartmentFeeValue::getApartmentId, apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(feeValueWrapper);

            //删除图片信息
            LambdaQueryWrapper<GraphInfo> graphInfoWrapper = new LambdaQueryWrapper<>();
            graphInfoWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphInfoWrapper);
        }

        //新增图片信息
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            ArrayList<GraphInfo> list = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setName(graphVo.getName());
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setUrl(graphVo.getUrl());
                list.add(graphInfo);
            }
            graphInfoService.saveBatch(list);
        }

        //新增杂费值信息
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> arrayList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                arrayList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(arrayList);
        }

        //新增标签信息
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)){
            ArrayList<ApartmentLabel> list = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                list.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(list);
        }

        //新增配套信息
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIds)){
            ArrayList<ApartmentFacility> list = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityInfoId);
                list.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(list);
        }
    }

}




