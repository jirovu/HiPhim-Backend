package com.web.hiphim.security.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CookieProvider {
    @Value("${server.servlet.session.cookie.max-age}")
    private int cookieAge;
    @Value("${server.servlet.session.cookie.name}")
    private String cookieName;

    public void create(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(cookieAge);
        cookie.setPath("/");
        cookie.setValue(value);
        response.addCookie(cookie);
    }

    public String getValue(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }
}
