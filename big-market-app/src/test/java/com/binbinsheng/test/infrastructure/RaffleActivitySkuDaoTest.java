package com.binbinsheng.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.binbinsheng.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import com.binbinsheng.infrastructure.persistent.po.RaffleActivitySku;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RaffleActivitySkuDaoTest {

    @Resource
    IRaffleActivitySkuDao raffleActivitySkuDao;

    @Test
    public void test_queryActivitySku(){
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(9011L);
        log.info(JSON.toJSONString(raffleActivitySku));


    }

}
