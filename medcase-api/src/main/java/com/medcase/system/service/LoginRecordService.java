package com.medcase.system.service;

import com.medcase.system.entity.LoginRecordEntity;
import com.medcase.system.mapper.LoginRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 登录记录服务。
 */
@Service
@RequiredArgsConstructor
public class LoginRecordService {

    private final LoginRecordMapper loginRecordMapper;

    public void insert(LoginRecordEntity record) {
        loginRecordMapper.insert(record);
    }
}
