/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Profile;
import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.services.UserRepository;



/**
 * RegisterPage provides
 *
 * @author tiwe
 * @version $Id$
 *
 */
public class RegisterPage extends BasePage{

    @Property
    private User user;
    
    @Inject
    private UserRepository userRepository;
    
    @InjectPage
    private LoginPage loginPage;
    
    Object onSuccess(){
        user.setProfile(Profile.User);
        userRepository.save(user);
        // TODO : check that username is not taken
        return loginPage;
    }
}
