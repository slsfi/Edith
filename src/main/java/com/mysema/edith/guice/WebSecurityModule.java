/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
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
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login");
        bindConstant().annotatedWith(Names.named("shiro.unauthorizedUrl")).to("/denied");
        bindRealm().to(UserDaoRealm.class).asEagerSingleton();

        addFilterChain("/api/**", AUTHC);
        addFilterChain("/login", AUTHC);
        addFilterChain("/logout", LOGOUT);
        //addFilterChain("/**", ANON);
    }
}