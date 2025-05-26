package com.binbinsheng.infrastructure.persistent.dao;

import com.binbinsheng.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserCreditAccountDao {

    int updateAddAmount(UserCreditAccount userCreditAccountReq);

    void insert(UserCreditAccount userCreditAccountReq);
}
