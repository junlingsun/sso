package com.sjl.sso.server.web;


import com.alibaba.fastjson.JSON;
import com.sjl.sso.core.constant.SsoConstant;
import com.sjl.sso.core.to.UserTo;
import com.sjl.sso.core.util.LoginUtil;
import com.sjl.sso.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class WebController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String toIndexPage(){
        return "index";
    }

    @GetMapping("/login")
    public String toLoginPage(HttpServletRequest request, Model model){
        String redirectUrl = request.getParameter(SsoConstant.REDIRECT_URL);


        UserTo userTo = (UserTo) request.getAttribute("user");
        String sessionId = (String)request.getAttribute("sessionId");

        if (StringUtils.hasLength(redirectUrl)) {
                if (userTo != null) {
                    return "redirect:" + redirectUrl + "?"+ SsoConstant.COOKIE_KEY + "=" +sessionId;
                }
            model.addAttribute("redirectUrl", redirectUrl);
            return "login";
        }

        if (userTo != null) {
            return "redirect:/index";
        }
        return "login";
    }



    @PostMapping("/doLogin")
    public String login(HttpServletResponse response,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "redirect_url", required = false) String redirectUrl){
        if (StringUtils.hasLength(username) && StringUtils.hasLength(password)) {
            userService.saveUser(username, password);
            String token = username + "_" + UUID.randomUUID().toString().replace("-", "");
            Cookie cookie = new Cookie(SsoConstant.COOKIE_KEY, token);
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
            if (StringUtils.hasLength(redirectUrl)) {
                return "redirect:"+ redirectUrl + "?" + SsoConstant.COOKIE_KEY+"="+token;
            }

            return "redirect:/index";
        }

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        LoginUtil.removeCokkie(request, response, SsoConstant.COOKIE_KEY);
        return "redirect:/login";
    }

}
