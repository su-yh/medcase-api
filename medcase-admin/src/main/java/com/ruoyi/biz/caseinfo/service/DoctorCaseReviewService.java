package com.ruoyi.biz.caseinfo.service;

import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.biz.caseinfo.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.caseinfo.response.DoctorCaseReviewVO;
import com.ruoyi.biz.caseinfo.domain.DoctorCaseEntity;
import com.ruoyi.biz.caseinfo.mapper.DoctorCaseAdminMapper;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 病例审核业务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class DoctorCaseReviewService {
    private final DoctorCaseAdminMapper doctorCaseAdminMapper;

    public PageResult<DoctorCaseReviewVO> page(
            Integer pageNum, Integer pageSize, DoctorCaseReviewQuery query) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNum == null || pageNum < 1 ? PageParam.PAGE_NO : pageNum);
        pageParam.setPageSize(pageSize == null || pageSize < 1 ? PageParam.PAGE_SIZE : pageSize);

        PageResult<DoctorCaseEntity> pageResult =
                doctorCaseAdminMapper.selectAdminCasePage(pageParam, query);
        PageResult<DoctorCaseReviewVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(DoctorCaseReviewVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public DoctorCaseReviewVO detail(Long id) {
        DoctorCaseEntity entity = doctorCaseAdminMapper.selectAdminCaseById(id);
        return entity == null ? null : DoctorCaseReviewVO.fromEntity(entity);
    }
}
