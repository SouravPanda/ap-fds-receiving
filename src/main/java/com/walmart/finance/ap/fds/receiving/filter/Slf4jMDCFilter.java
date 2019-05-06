package com.walmart.finance.ap.fds.receiving.filter;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@Order(ReceivingConstants.MDC_FILTER_ORDER)
public class Slf4jMDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain)
            throws java.io.IOException, ServletException {
        final String correlationId;
        if (!StringUtils.isEmpty(ReceivingConstants.CORRELATION_ID_HEADER_KEY) &&
                !StringUtils.isEmpty(request.getHeader(ReceivingConstants.CORRELATION_ID_HEADER_KEY))) {
            correlationId = HtmlUtils.htmlEscape(request.getHeader(ReceivingConstants.CORRELATION_ID_HEADER_KEY));
        } else {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put(ReceivingConstants.CORRELATION_ID_HEADER_KEY, correlationId);
        response.addHeader(ReceivingConstants.CORRELATION_ID_HEADER_KEY, correlationId);
        chain.doFilter(request, response);
    }
}