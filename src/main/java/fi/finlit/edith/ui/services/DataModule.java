package fi.finlit.edith.ui.services;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;

import fi.finlit.edith.domain.Profile;
import fi.finlit.edith.domain.User;

/**
 * DataModule provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DataModule {
    
    public static void contributeSeedEntity(OrderedConfiguration<Object> configuration, SaltSource saltSource, PasswordEncoder passwordEncoder) throws Exception {
//        PasswordEncoder passwordEncoder = new ShaPasswordEncoder();
//        
//        SaltSourceImpl saltSource = new SaltSourceImpl();
//        saltSource.setSystemWideSalt("DEADBEEF");
//        saltSource.afterPropertiesSet();
     
        // users
        for (String email : Arrays.asList(
                "timo.westkamper@mysema.com",
                "lassi.immonen@mysema.com",
                "heli.kautonen@finlit.fi",
                "matti.anttila@finlit.fi",
                "sakari.katajamaki@finlit.fi",
                "ossi.kokko@finlit.fi")){            
            String firstName = StringUtils.capitalize(email.substring(0, email.indexOf('.')));
            String lastName = StringUtils.capitalize(email.substring(firstName.length() + 1, email.indexOf('@')));
            
            User user = new User();
            user.setUsername(firstName.toLowerCase());
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            if (email.endsWith("mysema.com")){
                user.setProfile(Profile.Admin);
            }else{
                user.setProfile(Profile.User);
            }            
            
            // encode password
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    user.getUsername(), user.getPassword(), 
                    user.getProfile().getAuthorities());
            String password = passwordEncoder.encodePassword(user.getUsername(),saltSource.getSalt(userDetails));
            user.setPassword(password);
            
            configuration.add("user-" + user.getUsername(), user);
        }     
        
        // 
        
    }  

}
