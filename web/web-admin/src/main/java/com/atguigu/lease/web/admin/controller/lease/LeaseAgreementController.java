package com.atguigu.lease.web.admin.controller.lease;


import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.atguigu.lease.web.admin.vo.agreement.AgreementQueryVo;
import com.atguigu.lease.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


@Tag(name = "租约管理")
@RestController
@RequestMapping("/admin/agreement")
public class LeaseAgreementController {

    @Resource
    private LeaseAgreementService service;

    @Operation(summary = "保存或修改租约信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody LeaseAgreement leaseAgreement) {
        boolean count = service.saveOrUpdate(leaseAgreement);
        return Result.ok(count);
    }

    @Operation(summary = "根据条件分页查询租约列表")
    @GetMapping("page")
    public Result<IPage<AgreementVo>> page(@RequestParam long current, @RequestParam long size, AgreementQueryVo queryVo) {
        IPage<AgreementVo> page = new Page<>(current,size);
        IPage<AgreementVo> agreementVoIPage =  service.selectByPage(page,queryVo);
        return Result.ok(agreementVoIPage);
    }

    @Operation(summary = "根据id查询租约信息")
    @GetMapping(name = "getById")
    public Result<AgreementVo> getById(@RequestParam Long id) {
        AgreementVo agreementVo =  service.getDetailsById(id);
        return Result.ok(agreementVo);
    }

    @Operation(summary = "根据id删除租约信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        boolean count = service.removeById(id);
        return Result.ok(count);
    }

    @Operation(summary = "根据id更新租约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam LeaseStatus status) {
        LambdaUpdateWrapper<LeaseAgreement> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(LeaseAgreement::getId,id)
                .set(LeaseAgreement::getStatus,status);
        boolean count = service.update(lambdaUpdateWrapper);
        return Result.ok(count);
    }

}

