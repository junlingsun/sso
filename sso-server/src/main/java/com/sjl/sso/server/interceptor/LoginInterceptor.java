package com.sjl.sso.server.interceptor;


import com.alibaba.fastjson.JSON;
import com.sjl.sso.core.constant.SsoConstant;
import com.sjl.sso.core.to.UserTo;
import com.sjl.sso.core.util.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;





    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean match = matcher.match("/*/login*",requestURI);

        String sessionId = LoginUtil.checkSession(request, SsoConstant.COOKIE_KEY);
        UserTo userTo = checkUser(sessionId);
        if (userTo == null) {
            if (match) {
                return true;
            }
            response.sendRedirect(contextPath +"/login");
            return false;
        }
        request.setAttribute("user", userTo);
        request.setAttribute("sessionId", sessionId);
        return true;
    }

    private UserTo checkUser(String sessionId) {

        if (!StringUtils.hasLength(sessionId)) {
            return null;
        }

        String username = LoginUtil.getUsername(sessionId);
        String redisKey = SsoConstant.REDIS_USER_PREFIX + username;

        String value = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.hasLength(value)) {
            UserTo userTo = JSON.parseObject(value, UserTo.class);
            return userTo;
        }

        return null;
    }
}
