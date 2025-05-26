package com.binbinsheng.domain.award.service.distribute.impl;

import com.binbinsheng.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.binbinsheng.domain.award.model.entity.DistributeAwardEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import com.binbinsheng.domain.award.model.entity.UserCreditAwardEntity;
import com.binbinsheng.domain.award.model.valobj.AwardStateVO;
import com.binbinsheng.domain.award.repository.IAwardRepository;
import com.binbinsheng.domain.award.service.distribute.IDistributeAward;
import com.binbinsheng.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * 给用户发放积分奖励,支持award_config透传，满足黑名单积分奖励
 */
@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {

    @Resource
    IAwardRepository repository;

    @Override
    public void getOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        Integer  awardId = distributeAwardEntity.getAwardId();
        String awardConfig = distributeAwardEntity.getAwardConfig();

        if (StringUtils.isBlank(awardConfig)){
            //积分：0.01，1，10，100
            awardConfig = repository.queryAwardConfigByAwardId(awardId);
        }

        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if (creditRange.length != 2){
            throw new RuntimeException("award_config:" + awardConfig + "配置不是一个范围值，如1,100");
        }

        //根据积分范围生成随机积分
        BigDecimal creditAmount = generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        // 构建聚合对象
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizesAggregate.buildDistributeUserAwardRecordEntity(
                distributeAwardEntity.getUserId(),
                distributeAwardEntity.getOrderId(),
                distributeAwardEntity.getAwardId(),
                AwardStateVO.complete
        );

        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizesAggregate.buildUserCreditAwardEntity(distributeAwardEntity.getUserId(), creditAmount);

        GiveOutPrizesAggregate giveOutPrizesAggregate = new GiveOutPrizesAggregate();
        giveOutPrizesAggregate.setUserId(distributeAwardEntity.getUserId());
        giveOutPrizesAggregate.setUserAwardRecordEntity(userAwardRecordEntity);
        giveOutPrizesAggregate.setUserCreditAwardEntity(userCreditAwardEntity);

        // 存储发奖对象
        repository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);

    }

    private BigDecimal generateRandom(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) == 0){
            return min;
        }
        BigDecimal randomCredit = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));
        return randomCredit.round(new MathContext(3));
    }
}
