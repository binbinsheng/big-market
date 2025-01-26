package com.binbinsheng.domain.activity.service;

import com.binbinsheng.domain.activity.respository.IActivityRepository;
import org.springframework.stereotype.Service;


@Service
public class RaffleActivity extends AbstractRaffleActivity{
    public RaffleActivity(IActivityRepository activityRepository) {
        super(activityRepository);
    }
}
