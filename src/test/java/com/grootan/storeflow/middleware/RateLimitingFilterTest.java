package com.grootan.storeflow.middleware;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    private final RateLimitingFilter filter = new RateLimitingFilter();

    @Test
    void doFilter_nonAuthRoute_bypassesRateLimiting() throws Exception {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/products");
            MockHttpServletResponse res = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);
            filter.doFilterInternal(req, res, chain);
            assertThat(res.getStatus()).isEqualTo(200);
            verify(chain).doFilter(req, res);
        }
    }

    @Test
    void doFilter_authRoute_returns429AfterLimit() throws Exception {
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/auth/login");
            req.setRemoteAddr("192.168.1.1");
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilterInternal(req, res, mock(FilterChain.class));
            assertThat(res.getStatus()).isNotEqualTo(429);
        }
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/auth/login");
        req.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilterInternal(req, res, mock(FilterChain.class));
        assertThat(res.getStatus()).isEqualTo(429);
    }

    @Test
    void doFilter_differentIps_haveSeparateBuckets() throws Exception {
        MockHttpServletRequest req1 = new MockHttpServletRequest("POST", "/api/auth/login");
        req1.setRemoteAddr("192.168.1.1");
        MockHttpServletRequest req2 = new MockHttpServletRequest("POST", "/api/auth/login");
        req2.setRemoteAddr("192.168.1.2");

        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(req1, new MockHttpServletResponse(), mock(FilterChain.class));
        }

        MockHttpServletResponse res2 = new MockHttpServletResponse();
        filter.doFilterInternal(req2, res2, mock(FilterChain.class));
        assertThat(res2.getStatus()).isNotEqualTo(429);
    }
}
