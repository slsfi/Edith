/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Profile;
import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.services.repository.UserDetailsImpl;
import fi.finlit.edith.ui.services.svn.SubversionService;

public final class DataModule {
    private DataModule() {}

    private static final Logger logger = LoggerFactory.getLogger(DataModule.class);

    public static void contributeSeedEntity(
            OrderedConfiguration<Object> configuration,
            SaltSource saltSource,
            PasswordEncoder passwordEncoder,
            @Inject DocumentRepository documentRepository,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject SubversionService subversionService,
            @Inject UserRepository userRepository) throws IOException {

        logger.info("Initializing DataModule");

        subversionService.initialize();

        //documentRepository.getDocumentsOfFolder(documentRoot);

        addUsers(userRepository, saltSource, passwordEncoder);
    }

    @SuppressWarnings("unchecked")
    private static void addUsers(UserRepository userRepository,
            SaltSource saltSource,
            PasswordEncoder passwordEncoder) throws IOException {
        List<String> lines = IOUtils.readLines(DataModule.class.getResourceAsStream("/users.csv"), "ISO-8859-1");
        for (String line : lines){
            String[] values = line.split(";");
            User user = userRepository.getByUsername(values[2]);
            if (user == null){
                user = new User();
            }
            user.setFirstName(values[0]);
            user.setLastName(values[1]);
            user.setUsername(values[2]);
            user.setEmail(values[3]);
            if (values[3].endsWith("mysema.com")){
                user.setProfile(Profile.Admin);
            }else{
                user.setProfile(Profile.User);
            }

            // encode password
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    user.getUsername(), user.getPassword(),
                    user.getProfile().getAuthorities());
            String password = passwordEncoder.encodePassword(user.getUsername(), saltSource.getSalt(userDetails));
            user.setPassword(password);

            userRepository.save(user);
        }
    }

}
