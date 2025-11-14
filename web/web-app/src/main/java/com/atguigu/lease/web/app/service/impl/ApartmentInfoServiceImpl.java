package com.atguigu.lease.web.app.service.impl;

import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.app.mapper.ApartmentInfoMapper;
import com.atguigu.lease.web.app.mapper.FacilityInfoMapper;
import com.atguigu.lease.web.app.mapper.LabelInfoMapper;
import com.atguigu.lease.web.app.mapper.RoomInfoMapper;
import com.atguigu.lease.web.app.service.*;
import com.atguigu.lease.web.app.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.app.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.app.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.graph.Graph;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Resource
    private RoomInfoMapper roomInfoMapper;

    @Resource
    private FacilityInfoMapper facilityMapper;

    @Resource
    private ApartmentFacilityService facilityService;

    @Resource
    private LabelInfoMapper labelInfoMapper;

    @Resource
    private ApartmentLabelService labelService;

    @Resource
    private ApartmentInfoMapper infoMapper;

    @Resource
    private GraphInfoService graphService;

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //根据id查询基本信息
        ApartmentInfo apartmentInfo = infoMapper.selectById(id);

        //查询图片列表
        List<GraphVo> graphVoList = new ArrayList<>();
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT)
                .eq(GraphInfo::getItemId,id);
        List<GraphInfo> graphInfoList = graphService.list(graphWrapper);
        if (!CollectionUtils.isEmpty(graphInfoList)){
            for (GraphInfo graphInfo : graphInfoList) {
                GraphVo graphVo = new GraphVo();
                graphVo.setName(graphInfo.getName());
                graphVo.setUrl(graphInfo.getUrl());
                graphVoList.add(graphVo);
            }
        }

        //查询标签列表
        List<LabelInfo> labelInfoList = new ArrayList<>();
        LambdaQueryWrapper<ApartmentLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(ApartmentLabel::getApartmentId,id);
        List<ApartmentLabel> apartmentLabelList = labelService.list(labelWrapper);
        if (!CollectionUtils.isEmpty(apartmentLabelList)){
            for (ApartmentLabel apartmentLabel : apartmentLabelList) {
                LabelInfo labelInfo = labelInfoMapper.selectById(apartmentLabel.getLabelId());
                labelInfoList.add(labelInfo);
            }
        }

        //查询配套列表
        List<FacilityInfo> facilityInfoList = new ArrayList<>();
        LambdaQueryWrapper<ApartmentFacility> facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(ApartmentFacility::getApartmentId,id);
        List<ApartmentFacility> facilityList = facilityService.list(facilityWrapper);
        if (!CollectionUtils.isEmpty(facilityList)){
            for (ApartmentFacility apartmentFacility : facilityList) {
                FacilityInfo facilityInfo = facilityMapper.selectById(apartmentFacility.getFacilityId());
                facilityInfoList.add(facilityInfo);
            }
        }

        //查询最小租金值
        BigDecimal minRent = roomInfoMapper.selectRentByApartmentId(apartmentInfo.getId());

        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setMinRent(minRent);
        return apartmentDetailVo;
    }

    @Override
    public ApartmentItemVo selectApartmentItemVoById(Long apartmentId) {
        //根据id查询基本信息
        ApartmentInfo apartmentInfo = infoMapper.selectById(apartmentId);

        //查询标签列表
        List<LabelInfo> labelInfoList = new ArrayList<>();
        LambdaQueryWrapper<ApartmentLabel> labelWrapper = new LambdaQueryWrapper<>();
        labelWrapper.eq(ApartmentLabel::getApartmentId,apartmentId);
        List<ApartmentLabel> apartmentLabelList = labelService.list(labelWrapper);
        if (!CollectionUtils.isEmpty(apartmentLabelList)){
            for (ApartmentLabel apartmentLabel : apartmentLabelList) {
                LabelInfo labelInfo = labelInfoMapper.selectById(apartmentLabel.getLabelId());
                labelInfoList.add(labelInfo);
            }
        }

        //查询图片列表
        List<GraphVo> graphVoList = new ArrayList<>();
        LambdaQueryWrapper<GraphInfo> graphWrapper = new LambdaQueryWrapper<>();
        graphWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT)
                .eq(GraphInfo::getItemId,apartmentId);
        List<GraphInfo> graphInfoList = graphService.list(graphWrapper);
        if (!CollectionUtils.isEmpty(graphInfoList)){
            for (GraphInfo graphInfo : graphInfoList) {
                GraphVo graphVo = new GraphVo();
                graphVo.setName(graphInfo.getName());
                graphVo.setUrl(graphInfo.getUrl());
                graphVoList.add(graphVo);
            }
        }

        //查询最小租金值
        BigDecimal minRent = roomInfoMapper.selectRentByApartmentId(apartmentId);

        ApartmentItemVo apartmentItemVo = new ApartmentItemVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentItemVo);
        apartmentItemVo.setGraphVoList(graphVoList);
        apartmentItemVo.setLabelInfoList(labelInfoList);
        apartmentItemVo.setMinRent(minRent);

        return apartmentItemVo;
    }
}




