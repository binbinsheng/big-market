package com.binbinsheng.domain.activity.service.rule;

import com.binbinsheng.domain.strategy.service.rule.chain.ILogicChain;

public abstract class AbstractActionChain implements IActionChain{

    private  IActionChain next;

    @Override
    public IActionChain appendNext(IActionChain next){
        this.next = next;
        return next;
    };

    @Override
    public IActionChain next(){
        return next;
    };

}
