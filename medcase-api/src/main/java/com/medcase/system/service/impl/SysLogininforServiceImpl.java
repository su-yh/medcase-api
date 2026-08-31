package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.system.domain.SysLogininfor;
import com.medcase.system.plus.SystemEntityConverter;
import com.medcase.system.plus.entity.SysLogininforEntity;
import com.medcase.system.plus.mapper.SysLogininforMapper;
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
    public void insertLogininfor(SysLogininfor logininfor) {

        SysLogininforEntity entity = SystemEntityConverter.toEntity(logininfor);
        logininforMapper.insert(entity);
        logininfor.setInfoId(entity.getInfoId());
    }

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {

        return SystemEntityConverter.copyList(logininforMapper.selectLogininforList(
                logininfor.getIpaddr(), logininfor.getStatus(), logininfor.getUserName(),
                logininfor.getParams().get("beginTime"), logininfor.getParams().get("endTime")),
                SysLogininfor.class);
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
