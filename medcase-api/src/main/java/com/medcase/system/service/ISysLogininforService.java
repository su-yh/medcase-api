package com.medcase.system.service;

import java.util.List;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysLogininforEntity;

/**
 * 系统访问日志情况信息 服务层
 * 
 */
public interface ISysLogininforService {

    /**
     * 新增系统登录日志
     * 
     * @param logininfor 访问日志对象
     */
    public void insertLogininfor(SysLogininforEntity logininfor);

    /**
     * 查询系统登录日志集合
     * 
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    public PageResult<SysLogininforEntity> selectPage(
            PageParam pageParam, String ipaddr, String status, String userName,
            String beginTime, String endTime);

    /**
     * 批量删除系统登录日志
     * 
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    public int deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    public void cleanLogininfor();
}
