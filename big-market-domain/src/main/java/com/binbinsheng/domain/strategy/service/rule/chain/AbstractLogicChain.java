package com.binbinsheng.domain.strategy.service.rule.chain;

public abstract class AbstractLogicChain implements ILogicChain{

    private ILogicChain next;

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next() {
        return this.next;
    }

    public abstract String ruleModel();
}
