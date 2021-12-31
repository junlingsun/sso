package com.sjl.sso.core.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginUtil {
    public static String checkSession(HttpServletRequest request, String cookieKey) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(cookieKey)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * get username from sessionId: username_token
     * @param sessionId
     * @return username
     */
    public static String getUsername(String sessionId) {

        if (!StringUtils.hasLength(sessionId)) return null;
        String[] strs = sessionId.split("_");
        return strs[0];

    }

    public static void removeCokkie(HttpServletRequest request, HttpServletResponse response, String cookieKey) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(cookieKey)) {
                    cookie.setMaxAge(0);
                    cookie.setValue(null);
                    response.addCookie(cookie);
                }
            }
        }
    }
}
