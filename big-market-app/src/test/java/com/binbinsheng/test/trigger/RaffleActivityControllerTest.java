package com.binbinsheng.test.trigger;

import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.activity.service.armory.IActivityArmory;
import com.binbinsheng.trigger.api.IRaffleActivityService;
import com.binbinsheng.trigger.api.dto.ActivityDrawRequestDTO;
import com.binbinsheng.trigger.api.dto.ActivityDrawResponseDTO;
import com.binbinsheng.trigger.api.dto.UserActivityAccountRequestDTO;
import com.binbinsheng.trigger.api.dto.UserActivityAccountResponseDTO;
import com.binbinsheng.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动服务测试
 * @create 2024-04-20 11:02
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityControllerTest {

    @Resource
    private IRaffleActivityService raffleActivityService;
    @Resource
    private IActivityArmory activityArmory;


    @Before
    public void setUp() {
        log.info("装配活动：{}", activityArmory.assembleActivitySku(9011L));
    }

    @Test
    public void test_armory() {
        Response<Boolean> response = raffleActivityService.armory(100301L);
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_draw() {
        ActivityDrawRequestDTO request = new ActivityDrawRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("xiaofuge");
        Response<ActivityDrawResponseDTO> response = raffleActivityService.draw(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_calendarSignRebate(){
        Response<Boolean> response = raffleActivityService.calendarSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_isCalendarSignRebate() {
        Response<Boolean> response = raffleActivityService.isCalenderSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserActivityAccount() {
        UserActivityAccountRequestDTO request = new UserActivityAccountRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("xiaofuge123");

        // 查询数据
        Response<UserActivityAccountResponseDTO> response = raffleActivityService.queryUserActivityAccount(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }



}

