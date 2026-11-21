package com.taskflow.taskflow.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitFilterTest {

    private final RateLimitFilter filter = new RateLimitFilter();

    private int call(String uri, String ip) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", uri);
        request.setRemoteAddr(ip);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response.getStatus();
    }

    @Test
    void shouldBlockAfterTenRequestsFromTheSameIp() throws Exception {
        for (int i = 0; i < 10; i++) {
            assertEquals(200, call("/api/auth/login", "10.0.0.1"));
        }
        assertEquals(429, call("/api/auth/login", "10.0.0.1"));
    }

    @Test
    void shouldCountEachIpSeparately() throws Exception {
        for (int i = 0; i < 11; i++) {
            call("/api/auth/login", "10.0.0.2");
        }
        assertEquals(200, call("/api/auth/login", "10.0.0.3"));
    }

    @Test
    void shouldNotLimitRoutesOutsideAuth() throws Exception {
        for (int i = 0; i < 15; i++) {
            assertEquals(200, call("/api/projects", "10.0.0.4"));
        }
    }
}
