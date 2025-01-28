package com.binbinsheng.domain.activity.service.armory;

import com.binbinsheng.domain.activity.model.entity.ActivitySkuEntity;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.types.common.Constants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class ActivityArmory implements IActivityArmory, IActivityDispatch {

    @Resource
    IActivityRepository repository;

    @Override
    public boolean assembleActivitySku(Long sku) {

        ActivitySkuEntity activitySkuEntity = repository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());

        //预热活动【查询时预热到缓存】
        repository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());

        //预热活动次数【查询时预热到缓存】
        repository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        return true;
    }

    private void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        repository.cacheActivitySkuStockCount(cacheKey, stockCount);
    }


    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return repository.subtractionActivitySkuStock(sku, cacheKey,endDateTime);
    }
}
