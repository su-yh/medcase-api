package com.medcase.system.service;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysOperLogEntity;
import com.medcase.system.mapper.SysOperLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 操作日志 服务层处理
 * 
 */
@Service
public class SysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    public void insertOperlog(SysOperLogEntity operLog) {

        operLogMapper.insert(operLog);
    }

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public PageResult<SysOperLogEntity> selectPage(
            PageParam pageParam, String operIp, String title, Integer businessType,
            Integer status, String operName, String beginTime, String endTime) {

        return operLogMapper.selectPage(
                pageParam, operIp, title, businessType, status, operName, beginTime, endTime);
    }

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds) {

        return operLogMapper.deleteByIds(Arrays.asList(operIds));
    }

    /**
     * 清空操作日志
     */
    public void cleanOperLog() {

        operLogMapper.cleanOperLog();
    }
}
