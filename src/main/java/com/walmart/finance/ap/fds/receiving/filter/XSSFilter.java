package com.walmart.finance.ap.fds.receiving.filter;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(ReceivingConstants.XSS_FILTER_ORDER)
public class XSSFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                 final FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XSSRequestWrapper(request), response);

    }
}

