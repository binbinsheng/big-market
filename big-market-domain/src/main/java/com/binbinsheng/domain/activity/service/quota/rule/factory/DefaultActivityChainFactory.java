package com.binbinsheng.domain.activity.service.quota.rule.factory;


import com.binbinsheng.domain.activity.service.quota.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DefaultActivityChainFactory {

    private final Map<String, IActionChain> actionChainGroup;



    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainGroup) {
        this.actionChainGroup = actionChainGroup;
    }

    public IActionChain openActionChain(){
        IActionChain actionChain = actionChainGroup.get(ActionModel.activity_base_action.code);
        IActionChain current = actionChain;
        current.appendNext(actionChainGroup.get(ActionModel.activity_sku_stock_action.code));
        return actionChain;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionModel {

        activity_base_action("activity_base_action", "活动的库存、时间校验"),
        activity_sku_stock_action("activity_sku_stock_action", "活动sku库存"),
        ;

        private final String code;
        private final String info;

    }

}
