package com.mysema.edith.guice;

import javax.servlet.ServletContext;

import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;

import com.google.inject.Provides;

class MyShiroWebModule extends ShiroWebModule {
    
    MyShiroWebModule(ServletContext sc) {
        super(sc);
    }

    protected void configureShiroWeb() {
        try {
            bindRealm().toConstructor(IniRealm.class.getConstructor(Ini.class));
        } catch (NoSuchMethodException e) {
            addError(e);
        }

        addFilterChain("/public/**", ANON);
        addFilterChain("/stuff/allowed/**", AUTHC_BASIC, config(PERMS, "yes"));
        addFilterChain("/stuff/forbidden/**", AUTHC_BASIC, config(PERMS, "no"));
        addFilterChain("/**", AUTHC_BASIC);
    }

    @Provides
    Ini loadShiroIni() {
        return Ini.fromResourcePath("classpath:shiro.ini");
    }
}