package com.binbinsheng.domain.task.repository;

import com.binbinsheng.domain.task.model.entity.TaskEntity;


import java.util.List;

/**
 * 补充发送MQ的任务服务仓储
 */

public interface ITaskRepository {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);

}
