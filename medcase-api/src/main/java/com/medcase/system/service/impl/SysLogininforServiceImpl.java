package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.system.entity.SysLogininforEntity;
import com.medcase.system.mapper.SysLogininforMapper;
import com.medcase.system.service.ISysLogininforService;

/**
 * 系统访问日志情况信息 服务层处理
 * 
 */
@Service
public class SysLogininforServiceImpl implements ISysLogininforService {


    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininforEntity logininfor) {

        logininforMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininforEntity> selectLogininforList(
            String ipaddr, String status, String userName,
            String beginTime, String endTime) {

        return logininforMapper.selectLogininforList(
                ipaddr, status, userName, beginTime, endTime);
    }

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds) {

        return logininforMapper.deleteByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {

        logininforMapper.cleanLogininfor();
    }
}
