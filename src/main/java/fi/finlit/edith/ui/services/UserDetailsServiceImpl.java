/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.domain.UserRepository;

/**
 * UserDetailsServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class UserDetailsServiceImpl implements UserDetailsService{
    
    @Inject
    private UserRepository userRepository;
//    
//    @Inject
//    private PasswordEncoder passwordEncoder;
//
//    @Inject
//    private SaltSource saltSource;
//    
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException, DataAccessException {
        User user = userRepository.getByUsername(username);
        if (user != null){
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    user.getUsername(), user.getPassword(), 
                    user.getProfile().getAuthorities());
//            userDetails.setPassword(passwordEncoder.encodePassword("pass",saltSource.getSalt(userDetails)));
            return userDetails;    
        }else{
            return null;
        }
        
    }

}
