package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.system.domain.SysOperLog;
import com.medcase.system.plus.SystemEntityConverter;
import com.medcase.system.plus.entity.SysOperLogEntity;
import com.medcase.system.plus.mapper.SysOperLogMapper;
import com.medcase.system.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 * 
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog) {

        SysOperLogEntity entity = SystemEntityConverter.toEntity(operLog);
        operLogMapper.insert(entity);
        operLog.setOperId(entity.getOperId());
    }

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {

        return SystemEntityConverter.copyList(operLogMapper.selectOperLogList(
                operLog.getOperIp(), operLog.getTitle(), operLog.getBusinessType(),
                operLog.getBusinessTypes(), operLog.getStatus(), operLog.getOperName(),
                operLog.getParams().get("beginTime"), operLog.getParams().get("endTime")),
                SysOperLog.class);
    }

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {

        return operLogMapper.deleteByIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     * 
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId) {

        return SystemEntityConverter.toDomain(operLogMapper.selectById(operId));
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {

        operLogMapper.cleanOperLog();
    }
}
