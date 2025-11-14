package com.atguigu.lease.web.admin.schedule;

import com.atguigu.lease.model.entity.LeaseAgreement;
import com.atguigu.lease.model.enums.LeaseStatus;
import com.atguigu.lease.web.admin.service.LeaseAgreementService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * ClassName:
 * Description: TODO
 * Datetime: 2025/11/2 17:30
 * Author: novice21
 * Version: 1.0
 */
@Component
public class ScheduledTasks {
    @Resource
    private LeaseAgreementService service;

    @Scheduled(cron = "0 0 0 * * *")
    public void checkLeaseStatus() {

        LambdaUpdateWrapper<LeaseAgreement> updateWrapper = new LambdaUpdateWrapper<>();
        Date now = new Date();
        updateWrapper.le(LeaseAgreement::getLeaseEndDate, now);
        updateWrapper.eq(LeaseAgreement::getStatus, LeaseStatus.SIGNED);
        updateWrapper.in(LeaseAgreement::getStatus, LeaseStatus.SIGNED, LeaseStatus.WITHDRAWING);

        service.update(updateWrapper);
    }
}
