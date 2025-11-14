package com.atguigu.lease.web.admin.controller.apartment;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.PaymentType;
import com.atguigu.lease.web.admin.service.PaymentTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "支付方式管理")
@RequestMapping("/admin/payment")
@RestController
public class PaymentTypeController {

    @Resource
    private PaymentTypeService paymentTypeService;

    @Operation(summary = "查询全部支付方式列表")
    @GetMapping("list")
    public Result<List<PaymentType>> listPaymentType() {
        //不使用TableLogic注解
//        QueryWrapper<PaymentType> paymentTypeQueryWrapper = new QueryWrapper<>();
//        paymentTypeQueryWrapper.eq("is_deleted",0);
//        List<PaymentType> paymentTypeList = paymentTypeService.list(paymentTypeQueryWrapper);

        //使用TableLogic注解
        List<PaymentType> paymentTypeList = paymentTypeService.list();
        return Result.ok(paymentTypeList);
    }

    @Operation(summary = "保存或更新支付方式")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdatePaymentType(@RequestBody PaymentType paymentType) {
        boolean count = paymentTypeService.saveOrUpdate(paymentType);
        return Result.ok(count);
    }

    @Operation(summary = "根据ID删除支付方式")
    @DeleteMapping("deleteById")
    public Result deletePaymentById(@RequestParam Long id) {
        //逻辑删除，只更改id_delete字段，不删除整条数据。
        boolean count = paymentTypeService.removeById(id);
        return Result.ok(count);
    }

}















