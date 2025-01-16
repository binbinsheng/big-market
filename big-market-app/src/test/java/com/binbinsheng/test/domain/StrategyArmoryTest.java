package com.binbinsheng.test.domain;


import com.binbinsheng.domain.strategy.service.armory.IStrategyArmory;
import com.binbinsheng.domain.strategy.service.armory.IStrategyDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {

    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Before /*测试装配*/
    public void test_strategyArmory(){
        strategyArmory.assembleLotteryStrategy(100001L);
    }

    @Test
    public void test_getAssembleRandom(){
        log.info("测试结果:{} -奖品ID",strategyDispatch.getRandomAwardId(100001L));
    }

    @Test
    public void test_getAssembleRandom_ruleWeightValue(){
        log.info("测试结果:{} - 4000策略配置",strategyDispatch.getRandomAwardId(100001L,
                "4000:102,103,104,105"));
        log.info("测试结果:{} - 5000策略配置",strategyDispatch.getRandomAwardId(100001L,
                "5000:102,103,104,105,106,107"));
        log.info("测试结果:{} - 6000策略配置",strategyDispatch.getRandomAwardId(100001L,
                "6000:102,103,104,105,106,107,108,109"));
    }
}
