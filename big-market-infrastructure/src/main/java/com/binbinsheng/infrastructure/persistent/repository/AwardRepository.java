package com.binbinsheng.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.binbinsheng.domain.award.model.entity.TaskEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import com.binbinsheng.domain.award.repository.IAwardRepository;
import com.binbinsheng.infrastructure.event.EventPublisher;
import com.binbinsheng.infrastructure.persistent.dao.ITaskDao;
import com.binbinsheng.infrastructure.persistent.dao.IUserAwardRecordDao;
import com.binbinsheng.infrastructure.persistent.po.Task;
import com.binbinsheng.infrastructure.persistent.po.UserAwardRecord;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * 奖品仓储服务
 */

@Repository
@Slf4j
public class AwardRepository implements IAwardRepository {

    @Resource
    ITaskDao taskDao;
    @Resource
    IUserAwardRecordDao userAwardRecordDao;
    @Resource
    EventPublisher eventPublisher;
    @Resource
    IDBRouterStrategy dbRouter;
    @Resource
    TransactionTemplate transactionTemplate;

    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();


        //转换成数据库认的pojo
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        //路由写入数据库
        try{
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //写入中奖记录
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入中奖记录, 唯一索引冲突 userId:{} activityId:{} awardId:{}"
                            , userId, activityId, awardId);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        }finally {
            dbRouter.clear();
        }

        //发送MQ消息
        try{
            //发送消息【在事务外执行，如果失败还有任务补充】
            eventPublisher.publish(task.getTopic(), task.getMessage());
            taskDao.updateTaskSendMessageCompleted(task);
        }catch (Exception e){
            log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            taskDao.updateTaskSendMessageFail(task);
        }



    }
}
