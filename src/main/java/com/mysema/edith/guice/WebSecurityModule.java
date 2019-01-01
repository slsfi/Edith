/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.guice.web.ShiroWebModule;

import com.google.inject.name.Names;

class WebSecurityModule extends ShiroWebModule {

    public WebSecurityModule(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    protected void configureShiroWeb() {
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login.html");
        bindConstant().annotatedWith(Names.named("shiro.unauthorizedUrl")).to("/denied");
        bindRealm().to(UserDaoRealm.class).asEagerSingleton();

        addFilterChain("/index.html", ANON);
        addFilterChain("/*.html", AUTHC);
        addFilterChain("/api/**", AUTHC);
        addFilterChain("/login.html", AUTHC);
        addFilterChain("/logout", LOGOUT);
    }
}