package com.web.hiphim.security.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Handle logic when user logout successfully
 * */
@Service
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    // Send status 200 OK to client
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                Authentication authentication) {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }
}
