package com.binbinsheng.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.rebate.model.aggregate.BehaviorRebateOrderAggregate;
import com.binbinsheng.domain.rebate.model.entity.TaskEntity;
import com.binbinsheng.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.binbinsheng.domain.rebate.model.valobj.BehaviorTypeVO;
import com.binbinsheng.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.binbinsheng.domain.rebate.repository.IBehaviorRebateRepository;
import com.binbinsheng.infrastructure.event.EventPublisher;
import com.binbinsheng.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import com.binbinsheng.infrastructure.persistent.dao.ITaskDao;
import com.binbinsheng.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import com.binbinsheng.infrastructure.persistent.po.DailyBehaviorRebate;
import com.binbinsheng.infrastructure.persistent.po.Task;
import com.binbinsheng.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 行为返利服务仓储实现
 */

@Slf4j
@Repository
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    IDBRouterStrategy dbRouter;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    @Resource
    ITaskDao taskDao;
    @Resource
    IDailyBehaviorRebateDao dailyBehaviorRebateDao;
    @Resource
    EventPublisher eventPublisher;

    @Override
    public List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorType) {
        List<DailyBehaviorRebate> dailyBehaviorRebates
                = dailyBehaviorRebateDao.queryDailyBehaviorRebateByBehaviorType(behaviorType.getCode());
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = new ArrayList<>(dailyBehaviorRebates.size());
        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebates) {
            dailyBehaviorRebateVOS.add(DailyBehaviorRebateVO.builder()
                    .behaviorType(dailyBehaviorRebate.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                    .rebateType(dailyBehaviorRebate.getRebateType())
                    .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                    .build());
        }
        return dailyBehaviorRebateVOS;

    }

    @Override
    public List<UserBehaviorRebateOrderEntity> queryOrderByBusinessNo(String userId, String businessNo) {
        //1. 构建请求对象
        UserBehaviorRebateOrder userBehaviorRebateOrderReq = new UserBehaviorRebateOrder();
        userBehaviorRebateOrderReq.setUserId(userId);
        userBehaviorRebateOrderReq.setOutBusinessNo(businessNo);

        //2.查询结果
        List<UserBehaviorRebateOrder> userBehaviorRebateOrders
                = userBehaviorRebateOrderDao.queryOrderByBusinessNo(userBehaviorRebateOrderReq);

        List<UserBehaviorRebateOrderEntity> behaviorRebateOrderEntities = new ArrayList<>(userBehaviorRebateOrders.size());
        for (UserBehaviorRebateOrder userBehaviorRebateOrder : userBehaviorRebateOrders) {
            UserBehaviorRebateOrderEntity behaviorRebateOrderEntity = UserBehaviorRebateOrderEntity.builder()
                    .userId(userBehaviorRebateOrder.getUserId())
                    .orderId(userBehaviorRebateOrder.getOrderId())
                    .behaviorType(userBehaviorRebateOrder.getBehaviorType())
                    .rebateDesc(userBehaviorRebateOrder.getRebateDesc())
                    .rebateType(userBehaviorRebateOrder.getRebateType())
                    .rebateConfig(userBehaviorRebateOrder.getRebateConfig())
                    .outBusinessNo(userBehaviorRebateOrder.getOutBusinessNo())
                    .bizId(userBehaviorRebateOrder.getBizId())
                    .build();
            behaviorRebateOrderEntities.add(behaviorRebateOrderEntity);
        }
        return behaviorRebateOrderEntities;
    }

    @Override
    public void saveUserRebateRecord(String userId, ArrayList<BehaviorRebateOrderAggregate> behaviorRebateOrderAggregates) {
        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    for (BehaviorRebateOrderAggregate behaviorRebateOrderAggregate : behaviorRebateOrderAggregates) {
                        UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity = behaviorRebateOrderAggregate.getUserBehaviorRebateOrder();
                        //用户行为返利订单对象，数据库使用的对象
                        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                        userBehaviorRebateOrder.setUserId(userBehaviorRebateOrderEntity.getUserId());
                        userBehaviorRebateOrder.setOrderId(userBehaviorRebateOrderEntity.getOrderId());
                        userBehaviorRebateOrder.setBehaviorType(userBehaviorRebateOrderEntity.getBehaviorType());
                        userBehaviorRebateOrder.setRebateType(userBehaviorRebateOrderEntity.getRebateType());
                        userBehaviorRebateOrder.setRebateConfig(userBehaviorRebateOrderEntity.getRebateConfig());
                        userBehaviorRebateOrder.setRebateDesc(userBehaviorRebateOrderEntity.getRebateDesc());
                        userBehaviorRebateOrder.setOutBusinessNo(userBehaviorRebateOrderEntity.getOutBusinessNo());
                        userBehaviorRebateOrder.setBizId(userBehaviorRebateOrderEntity.getBizId());
                        userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);

                        //任务对象
                        TaskEntity taskEntity = behaviorRebateOrderAggregate.getTaskEntity();
                        Task task = new Task();
                        task.setUserId(taskEntity.getUserId());
                        task.setTopic(taskEntity.getTopic());
                        task.setMessageId(taskEntity.getMessageId());
                        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                        task.setState(taskEntity.getState().getCode());
                        taskDao.insert(task);
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入返利记录，唯一索引冲突 userId:{}", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        }finally {
            dbRouter.clear();
        }

        // 同步发送MQ消息
        for (BehaviorRebateOrderAggregate behaviorRebateOrderAggregate : behaviorRebateOrderAggregates) {
            TaskEntity taskEntity = behaviorRebateOrderAggregate.getTaskEntity();
            Task task = new Task();
            task.setUserId(taskEntity.getUserId());
            task.setMessageId(taskEntity.getMessageId());
            try {
                // 发送消息【在事务外执行，如果失败还有任务补偿】
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                // 更新数据库记录，task 任务表
                taskDao.updateTaskSendMessageCompleted(task);
            } catch (Exception e) {
                log.error("写入返利记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        }

    }


}
