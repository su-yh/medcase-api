package com.medcase.mvc.audit;

import com.medcase.mvc.user.AbstractLoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author suyh
 * @since 2024-10-18
 */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractAuditComponent {
    // 审计日志记录
    public boolean auditRecord(
            IAudit auditOperation,
            Object spelReturnValue,
            HttpServletRequest request,
            AbstractLoginUser loginUser,
            Object... reqArgs) {

        try {
            doAuditRecord(auditOperation, spelReturnValue, request, loginUser, reqArgs);
        } catch (Exception e) {
            log.warn("audit record exception.", e);
        }

        return true;
    }

    protected abstract void doAuditRecord(
            IAudit auditOperation,
            Object spelReturnValue,
            HttpServletRequest request,
            AbstractLoginUser loginUser,
            Object... reqArgs);

}
