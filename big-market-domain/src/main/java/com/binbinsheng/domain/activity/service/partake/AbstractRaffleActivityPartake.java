package com.binbinsheng.domain.activity.service.partake;

import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.binbinsheng.domain.activity.model.entity.ActivityEntity;
import com.binbinsheng.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.binbinsheng.domain.activity.model.entity.UserRaffleOrderEntity;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.quota.IRaffleActivityPartakeService;
import com.binbinsheng.domain.activity.valobj.ActivityStateVO;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.portable.ApplicationException;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {

    IActivityRepository repository;

    public AbstractRaffleActivityPartake(IActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //0.基础信息
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date currentDate = new Date();

        //1.活动查询
        ActivityEntity activityEntity = repository.queryRaffleActivityByActivityId(activityId);

        //校验活动状态（是否创建、过期）
        if (!ActivityStateVO.open.equals(activityEntity.getState())){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }

        //2.查询未被使用的活动参与订单记录
        UserRaffleOrderEntity userRaffleOrderEntity = repository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        if (userRaffleOrderEntity != null){
            log.info("创建参与活动订单 userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }

        //3.账户额度过滤&返回账户构建对象
        //return回来的都是有额度的
        CreatePartakeOrderAggregate createPartakeOrderAggregate = doFilterAccount(userId, activityId, currentDate);

        //4.构建订单
        UserRaffleOrderEntity userRaffleOrder = buildUserRaffleOrder(userId, activityId, currentDate);

        //5.填充抽奖实体对象
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);

        //6.保存聚合对象 - 一个领域里的一个聚合是一个事务操作
        repository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);

        return userRaffleOrder;
    }

    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);

    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);
}
