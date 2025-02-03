package com.binbinsheng.domain.activity.service.armory;

import com.binbinsheng.domain.activity.model.entity.ActivitySkuEntity;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.types.common.Constants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ActivityArmory implements IActivityArmory, IActivityDispatch {

    @Resource
    IActivityRepository repository;

    @Override
    public boolean assembleActivitySku(Long sku) {

        //预热活动sku库存
        ActivitySkuEntity activitySkuEntity = repository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());

        //预热活动【查询时预热到缓存】
        repository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());

        //预热活动次数【查询时预热到缓存】
        repository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        return true;
    }

    @Override
    public boolean assembleActivitySkuByActivityId(Long activityId) {
        List<ActivitySkuEntity> activitySkuEntities = repository.queryActivitySkuListByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity : activitySkuEntities) {
            //预热活动sku库存 redis -> sku：sku_stock_count
            cacheActivitySkuStockCount(activitySkuEntity.getActivityId(), activitySkuEntity.getStockCount());
            //预热活动次数【查询时预热到缓存】 <- 这个相当于充值卡，靠诉你每次下单sku后能冲多少次数
            //Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId : ActivityCountEntity(总，日，月)
            repository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        }
        //预热活动【查询时预热到缓存】
        //Constants.RedisKey.ACTIVITY_KEY + activityId : ActivityEntity
        repository.queryRaffleActivityByActivityId(activityId);
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
