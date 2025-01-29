package com.binbinsheng.domain.activity.service.quota.rule.impl;

import com.binbinsheng.domain.activity.model.entity.ActivityCountEntity;
import com.binbinsheng.domain.activity.model.entity.ActivityEntity;
import com.binbinsheng.domain.activity.model.entity.ActivitySkuEntity;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.armory.IActivityDispatch;
import com.binbinsheng.domain.activity.service.quota.rule.AbstractActionChain;
import com.binbinsheng.domain.activity.valobj.ActivitySkuStockKeyVO;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 商品库存规则结点
 */

@Slf4j
@Component("activity_sku_stock_action")
public class ActivitySkuActionChain extends AbstractActionChain {

    @Resource
    IActivityDispatch dispatch;

    @Resource
    IActivityRepository repository;

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】开始，sku:{} activityId:{}", activitySkuEntity.getSku(), activitySkuEntity.getActivityId());

        //扣减库存
        boolean status =  dispatch.subtractionActivitySkuStock(activitySkuEntity.getSku(), activityEntity.getEndDateTime());

        //写入延迟队列，延迟消费更新库存
        if (status) {
            repository.activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO.builder()
                    .sku(activitySkuEntity.getSku())
                    .activityId(activityEntity.getActivityId())
                    .build());
            return true;
        }

        //库存不足，直接抛异常
        throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());


    }
}
