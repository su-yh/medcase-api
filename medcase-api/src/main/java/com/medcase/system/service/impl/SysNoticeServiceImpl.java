package com.medcase.system.service.impl;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.system.domain.SysNotice;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysNoticeEntity;
import com.medcase.system.mapper.SysNoticeMapper;
import com.medcase.system.service.ISysNoticeService;

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
    public SysNotice selectNoticeById(Long noticeId) {

        return SystemEntityConverter.toDomain(noticeMapper.selectById(noticeId));
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {

        return SystemEntityConverter.copyList(noticeMapper.selectNoticeList(
                notice.getNoticeTitle(), notice.getNoticeType(), notice.getCreateBy()),
                SysNotice.class);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {

        SysNoticeEntity entity = SystemEntityConverter.toEntity(notice);
        int result = noticeMapper.insert(entity);
        notice.setNoticeId(entity.getNoticeId());
        return result;
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {

        return noticeMapper.updateById(SystemEntityConverter.toEntity(notice));
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
