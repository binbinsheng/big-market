package com.binbinsheng.domain.activity.service.quota;

import com.binbinsheng.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.binbinsheng.domain.activity.model.entity.*;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.IRaffleActivitySkuStockService;
import com.binbinsheng.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import com.binbinsheng.domain.activity.valobj.ActivitySkuStockKeyVO;
import com.binbinsheng.domain.activity.valobj.OrderStateVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class RaffleActivityAccountQuotaAccountQuotaService extends AbstractRaffleActivityAccountQuotaAccountQuota implements IRaffleActivitySkuStockService {

    public RaffleActivityAccountQuotaAccountQuotaService(IActivityRepository repository, DefaultActivityChainFactory defaultChainFactory) {
        super(repository, defaultChainFactory);
    }

    @Override
    protected void doSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        repository.doSaveOrder(createQuotaOrderAggregate);
    }

    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity,
                                                            ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {

        // 订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(skuRechargeEntity.getUserId());
        activityOrderEntity.setSku(skuRechargeEntity.getSku());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());
        // 公司里一般会有专门的雪花算法UUID服务，我们这里直接生成个12位就可以了。
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setState(OrderStateVO.completed);
        activityOrderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());

        // 构建聚合对象
        return CreateQuotaOrderAggregate.builder()
                .userId(skuRechargeEntity.getUserId())
                .activityId(activitySkuEntity.getActivityId())
                .totalCount(activityCountEntity.getTotalCount())
                .dayCount(activityCountEntity.getDayCount())
                .monthCount(activityCountEntity.getMonthCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }


    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        return repository.takeQueueValue();
    }

    @Override
    public void clearQueueValue() {
        repository.clearQueueValue();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        repository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        repository.clearActivitySkuStock(sku);
    }
}
