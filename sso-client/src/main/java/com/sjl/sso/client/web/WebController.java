package com.sjl.sso.client.web;

import com.alibaba.fastjson.JSON;
import com.sjl.sso.core.constant.SsoConstant;
import com.sjl.sso.core.to.UserTo;
import com.sjl.sso.core.util.LoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class WebController {

    @Value("${sso.server.url}")
    private String serverUrl;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/index")
    public String toIndexPage(HttpServletRequest request, HttpServletResponse response, Model model){
        String sessionId = LoginUtil.checkSession(request, SsoConstant.COOKIE_KEY);
        System.out.println("sessionId " + sessionId);
        if (!StringUtils.hasLength(sessionId)) {
            String token = request.getParameter(SsoConstant.COOKIE_KEY);
            if (!StringUtils.hasLength(token)) {
                String redirect = SsoConstant.REDIRECT_URL + "=" + request.getRequestURL();
                String url = serverUrl + "/login?" + redirect;
                return "redirect:" + url;
            }else {
                sessionId = token;
                Cookie cookie = new Cookie(SsoConstant.COOKIE_KEY, token);
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
            }

        }

        String username = LoginUtil.getUsername(sessionId);
        String redisKey = SsoConstant.REDIS_USER_PREFIX + username;
        String value = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.hasLength(value)) {
            UserTo userTo = JSON.parseObject(value, UserTo.class);
            model.addAttribute("user", userTo);
        }

        return "index";

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        //remove cookie
        LoginUtil.removeCokkie(request, response, SsoConstant.COOKIE_KEY);

        String redirect = serverUrl + "/logout";
        return "redirect:" + redirect;
    }
}
