package com.medcase.mvc.audit;

import com.medcase.common.utils.json.JsonUtils;
import com.medcase.filter.TraceFilter;
import com.medcase.mvc.user.AbstractLoginUser;
import com.medcase.system.mapper.AuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author suyh
 * @since 2024-10-18
 */
@Component("audit")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class AuditComponent extends AbstractAuditComponent {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AuditLogMapper auditLogMapper;

    @Override
    protected void doAuditRecord(
            AuditEnums auditOperation,
            Object spelReturnValue,
            HttpServletRequest request,
            AbstractLoginUser loginUser,
            Object... reqArgs) {
        String traceId = MDC.get(TraceFilter.TRACE_ID);
        AuditLogEntity recordEntity = new AuditLogEntity();
        recordEntity.setUserId(loginUser.getId()).setUserNickname(loginUser.getNickname())
                .setOperation(auditOperation)
                .setReqArgument(JsonUtils.toJSONString(reqArgs))
                .setResultDetail(JsonUtils.toJSONString(spelReturnValue))
                .setReqPath(request.getServletPath()).setReqMethod(request.getMethod())
                .setCreated(new Date());
        recordEntity.setTraceId(traceId);

        executorService.submit(() -> {
            auditLogMapper.insert(recordEntity);
        });
    }

}
