package com.web.hiphim.config;

import com.web.hiphim.security.exceptions.CustomAccessDeniedHandler;
import com.web.hiphim.security.filters.AuthorizationHeaderPerRequest;
import com.web.hiphim.security.services.CustomAuthenticationProvider;
import com.web.hiphim.security.services.CustomLogoutHandler;
import com.web.hiphim.security.services.CustomLogoutSuccessHandler;
import com.web.hiphim.security.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * HiPhim web security configuration
 * */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Value("${spring.origin.url}")
    private String originUrl;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private CustomLogoutHandler customLogoutHandler;
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Autowired
    private AuthorizationHeaderPerRequest authorizationHeaderPerRequest;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    // Specify Cors configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin(originUrl);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // This method used to authenticate user credentials
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(customAuthenticationProvider)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    // This method used to config Http Security attributes
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()

                // Configuration authorize requests
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/user/**").permitAll()
                .anyRequest().authenticated()

                // Configuration exception handling
                .and()
                .exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler)

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)

                // Configuration logout handling
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/home/logout"))
                .invalidateHttpSession(true)
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll();

        http
                .addFilterBefore(authorizationHeaderPerRequest, UsernamePasswordAuthenticationFilter.class);
    }
}
