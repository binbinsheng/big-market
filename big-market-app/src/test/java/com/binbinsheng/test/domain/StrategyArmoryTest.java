package com.binbinsheng.test.domain;


import com.binbinsheng.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyArmoryTest {

    @Resource
    private IStrategyArmory strategyArmory;

    @Test
    public void test_strategyArmory(){
        strategyArmory.assembleLotteryStrategy(100001L);
    }

    @Test
    public void test_getAssembleRandom(){
        log.info("测试结果:{} -奖品ID",strategyArmory.getRandomAwardId(100001L));
        log.info("测试结果:{} -奖品ID",strategyArmory.getRandomAwardId(100001L));
        log.info("测试结果:{} -奖品ID",strategyArmory.getRandomAwardId(100001L));
    }
}
