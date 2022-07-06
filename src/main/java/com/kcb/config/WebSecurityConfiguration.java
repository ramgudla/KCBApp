package com.kcb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
// https://www.marcobehler.com/guides/spring-security
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
    /*@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("apps").password("{noop}apps").roles("USER")
                .and()
                .withUser("admin").password("{noop}apps").roles("USER", "ADMIN");
    }*/

    // Secure the endpoins with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                //HTTP Basic authentication
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/query/**").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/notify/**").hasRole("USER")
                .and()
                .csrf().disable()
                .formLogin().disable();
    }
}