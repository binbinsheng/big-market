package com.binbinsheng.domain.credit.repository;

import com.binbinsheng.domain.credit.model.aggregate.TradeAggregate;

public interface ICreditRepository {
    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);
}
