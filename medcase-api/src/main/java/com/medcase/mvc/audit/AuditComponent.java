package com.medcase.mvc.audit;

import com.medcase.common.utils.json.JsonUtils;
import com.medcase.mvc.user.AbstractLoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            IAudit auditOperation,
            Object spelReturnValue,
            HttpServletRequest request,
            AbstractLoginUser loginUser,
            Object... reqArgs) {
        AuditLogEntity recordEntity = new AuditLogEntity();
        recordEntity.setUserId(loginUser.getId()).setUserNickname(loginUser.getNickname())
                .setOperation(auditOperation.getOperation())
                .setReqArgument(JsonUtils.toJSONString(reqArgs))
                .setResult(JsonUtils.toJSONString(spelReturnValue))
                .setReqPath(request.getServletPath()).setReqMethod(request.getMethod())
                .setCreated(new Date());

        Object traceId = request.getAttribute("trace-id");
        if (traceId instanceof Long) {
            recordEntity.setTraceId(Long.parseLong(traceId + ""));
        } else {
            log.warn("LOST TRACE ID");
        }

        executorService.submit(() -> {
            auditLogMapper.insert(recordEntity);
        });
    }

}
