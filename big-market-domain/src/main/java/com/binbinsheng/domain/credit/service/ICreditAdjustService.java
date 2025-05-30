package com.binbinsheng.domain.credit.service;

import com.binbinsheng.domain.credit.model.entity.TradeEntity;

public interface ICreditAdjustService {
    String createOrder(TradeEntity tradeEntity);
}
