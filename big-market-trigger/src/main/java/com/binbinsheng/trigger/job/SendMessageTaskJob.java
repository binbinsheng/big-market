package com.binbinsheng.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.binbinsheng.domain.task.model.entity.TaskEntity;
import com.binbinsheng.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 作为AwardService发送MQ消息的补偿，MQ消息发送可能失败，这个任务来扫描Task表中未发送或者发送失败的消息
 */

@Component
@Slf4j
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;
    @Resource
    private ThreadPoolExecutor executor;
    @Resource
    private IDBRouterStrategy dbRouter;


    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try{
            //获得分库数量
            int dbCount = dbRouter.dbCount();

            // 逐个库扫描表【每个库一个任务表】
            for (int dbIndex = 1; dbIndex <= dbCount; dbIndex++){
                int finalDbIdx = dbIndex;
                executor.execute(() -> {
                    dbRouter.setDBKey(finalDbIdx);
                    dbRouter.setTBKey(0);
                    List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                    for (TaskEntity taskEntity : taskEntities){
                        try{
                            taskService.sendMessage(taskEntity);
                            taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                        }catch (Exception e){
                            log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                            taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());

                        }
                    }

                });

            }
        }finally {
            dbRouter.clear();
        }
    }

}
