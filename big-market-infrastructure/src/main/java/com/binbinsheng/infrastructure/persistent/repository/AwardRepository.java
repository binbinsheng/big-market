package com.binbinsheng.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.binbinsheng.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.binbinsheng.domain.award.model.entity.TaskEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import com.binbinsheng.domain.award.model.entity.UserCreditAwardEntity;
import com.binbinsheng.domain.award.model.valobj.AccountStatusVO;
import com.binbinsheng.domain.award.repository.IAwardRepository;
import com.binbinsheng.infrastructure.event.EventPublisher;
import com.binbinsheng.infrastructure.persistent.dao.*;
import com.binbinsheng.infrastructure.persistent.po.Task;
import com.binbinsheng.infrastructure.persistent.po.UserAwardRecord;
import com.binbinsheng.infrastructure.persistent.po.UserCreditAccount;
import com.binbinsheng.infrastructure.persistent.po.UserRaffleOrder;
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
    @Resource
    IUserRaffleOrderDao userRaffleOrderDao;
    @Resource
    IAwardDao awardDao;
    @Resource
    IUserCreditAccountDao userCreditAccountDao;;

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

        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(userId);
        userRaffleOrderReq.setOrderId(userAwardRecordEntity.getOrderId());


        //路由写入数据库
        try{
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //写入中奖记录
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
                    //更新抽奖单
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(userRaffleOrderReq);
                    if (count != 1){
                        status.setRollbackOnly();
                        log.error("写入中奖记录，用户抽奖单已使用过，不可重复抽奖 userId: {} activityId: {} awardId: {}"
                                , userId, activityId, awardId);
                        throw new AppException(ResponseCode.ACTIVITY_ORDER_ERROR.getCode(),
                                ResponseCode.ACTIVITY_ORDER_ERROR.getInfo());
                    }

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

    @Override
    public String queryAwardConfigByAwardId(Integer awardId) {
        return awardDao.queryAwardConfigByAwardId(awardId);
    }

    @Override
    public String queryAwardKeyByAwardId(Integer awardId) {
        return awardDao.queryAwardKeyByAwardId(awardId);
    }

    @Override
    public void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate) {
        String userId = giveOutPrizesAggregate.getUserId();
        UserCreditAwardEntity userCreditAwardEntity = giveOutPrizesAggregate.getUserCreditAwardEntity();
        UserAwardRecordEntity userAwardRecordEntity = giveOutPrizesAggregate.getUserAwardRecordEntity();

        // 更新发奖记录
        UserAwardRecord userAwardRecordReq = new UserAwardRecord();
        userAwardRecordReq.setUserId(userId);
        userAwardRecordReq.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecordReq.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        // 更新用户积分 「首次则插入数据」
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userCreditAwardEntity.getUserId());
        userCreditAccountReq.setTotalAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAvailableAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.open.getCode());


        try {
            dbRouter.doRouter(giveOutPrizesAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    // 更新积分 || 创建积分账户
                    int updateAccountCount = userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    if (0 == updateAccountCount) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    }

                    // 更新奖品记录
                    int updateAwardCount = userAwardRecordDao.updateAwardRecordCompletedState(userAwardRecordReq);
                    if (0 == updateAwardCount) {
                        log.warn("更新中奖记录，重复更新拦截 userId:{} giveOutPrizesAggregate:{}", userId, JSON.toJSONString(giveOutPrizesAggregate));
                        status.setRollbackOnly();
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("更新中奖记录，唯一索引冲突 userId: {} ", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }

    }
}
