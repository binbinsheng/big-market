package com.binbinsheng.domain.rebate.model.entity;


import com.binbinsheng.domain.rebate.event.SendRebateMessageEvent;
import com.binbinsheng.domain.rebate.model.valobj.TaskStateVO;
import com.binbinsheng.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送MQ的task实体对象
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private TaskStateVO state;


}
