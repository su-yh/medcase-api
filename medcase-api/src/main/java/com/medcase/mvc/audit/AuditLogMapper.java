package com.medcase.mvc.audit;

import com.medcase.mp.mybatis.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author suyh
 * @since 2024-09-02
 */
@Mapper
public interface AuditLogMapper extends BaseMapperX<AuditLogEntity> {
}
