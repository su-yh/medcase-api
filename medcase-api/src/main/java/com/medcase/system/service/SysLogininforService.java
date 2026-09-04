package com.medcase.system.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysLogininforEntity;
import com.medcase.system.mapper.SysLogininforMapper;

/**
 * 系统访问日志情况信息 服务层处理
 * 
 */
@Service
public class SysLogininforService {


    @Autowired
    private SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininforEntity logininfor) {

        logininforMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public PageResult<SysLogininforEntity> selectPage(
            PageParam pageParam, String ipaddr, String status, String userName,
            String beginTime, String endTime) {

        return logininforMapper.selectPage(
                pageParam, ipaddr, status, userName, beginTime, endTime);
    }

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds) {

        return logininforMapper.deleteByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor() {

        logininforMapper.cleanLogininfor();
    }
}
