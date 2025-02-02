package com.binbinsheng.domain.task.model.entity;

import com.binbinsheng.domain.award.event.SendAwardMessageEvent;
import com.binbinsheng.domain.award.model.valobj.TaskStateVO;
import com.binbinsheng.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskEntity {
    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private String message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private String state;
}
