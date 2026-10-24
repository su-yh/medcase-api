package com.medcase.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class TraceFilter implements Filter {
    public static final String TRACE_ID = "trace-id";
    public static final AtomicLong AL_TRACE_ID = new AtomicLong(0);
    // 以北京时间20261001 0 点为基线时间戳
    public static final long BASE_TIME_MILLS = 1790784000000L;
    private static final Random RANDOM = new Random();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        long initTraceId = System.currentTimeMillis() - BASE_TIME_MILLS;
        // 每一毫秒的增量空间是 2^8
        final int offset = 8;
        final long firstTraceId = (initTraceId << offset) + RANDOM.nextInt((1 << offset) - 1);

        log.info("initTraceId: {}, firstTraceId: {}", initTraceId, firstTraceId);

        AL_TRACE_ID.set(firstTraceId);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String traceId = request.getHeader(TRACE_ID);
        if (!StringUtils.hasText(traceId)) {

            traceId = nextTraceId() + "";
            request.setAttribute(TRACE_ID, traceId);
        }
        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_ID, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }

    public static long nextTraceId() {
        int delta = RANDOM.nextInt(10, 20);
        return AL_TRACE_ID.addAndGet(delta);
    }
}
