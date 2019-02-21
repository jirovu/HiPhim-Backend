package com.web.hiphim.security.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/*
 * Handle logic when user logout
 * */
@Service
public class CustomLogoutHandler implements LogoutHandler {
    /*
     * Remove cookies from application
     * */
    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                       Authentication authentication) {
        HttpSession session = httpServletRequest.getSession(false);
        if (httpServletRequest.isRequestedSessionIdValid() && session != null) {
            session.invalidate();
        }

        Cookie[] cookies = httpServletRequest.getCookies();
        Arrays.stream(cookies).forEach(cookie -> {
            cookie.setMaxAge(0);
            cookie.setValue(null);
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        });
    }
}
