package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.system.entity.SysNoticeEntity;
import com.medcase.system.mapper.SysNoticeMapper;
import com.medcase.system.service.ISysNoticeService;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;

/**
 * 公告 服务层实现
 * 
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {

    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNoticeEntity selectNoticeById(Long noticeId) {

        return noticeMapper.selectById(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public PageResult<SysNoticeEntity> selectPage(
            PageParam pageParam, String noticeTitle, String noticeType, String createBy) {

        return noticeMapper.selectPage(pageParam, noticeTitle, noticeType, createBy);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNoticeEntity notice) {

        return noticeMapper.insert(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNoticeEntity notice) {

        return noticeMapper.updateById(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {

        return noticeMapper.deleteById(noticeId);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {

        return noticeMapper.deleteByIds(Arrays.asList(noticeIds));
    }
}
