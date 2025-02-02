package com.binbinsheng.domain.task.service;

import com.binbinsheng.domain.task.model.entity.TaskEntity;
import com.binbinsheng.domain.task.repository.ITaskRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class TaskService implements ITaskService{

    @Resource
    private ITaskRepository repository;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {

        return repository.queryNoSendMessageTaskList();

    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        repository.sendMessage(taskEntity);
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        repository.updateTaskSendMessageCompleted(userId, messageId);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        repository.updateTaskSendMessageFail(userId, messageId);
    }
}
