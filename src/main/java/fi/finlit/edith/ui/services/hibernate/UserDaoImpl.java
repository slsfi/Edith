/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.sql.domain.QUser.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.EagerLoad;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;

import com.mysema.query.types.ConstructorExpression;

import fi.finlit.edith.dto.UserDetailsImpl;
import fi.finlit.edith.dto.UserInfo;
import fi.finlit.edith.sql.domain.Profile;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.HibernateDataModule;
import fi.finlit.edith.ui.services.UserDao;

@EagerLoad
public class UserDaoImpl extends AbstractDao<User> implements UserDao {

    private final AuthService authService;
    private final SaltSource saltSource;
    private final PasswordEncoder passwordEncoder;

    public UserDaoImpl(@Inject AuthService authService, @Inject SaltSource saltSource,
            @Inject PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.saltSource = saltSource;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getById(Long id) {
        return query().from(user).where(user.id.eq(id)).uniqueResult(user);
    }

    @Override
    public Collection<User> getAll() {
        return query().from(user).list(user);
    }

    @Override
    public User getByUsername(String username) {
        return query().from(user).where(user.username.eq(username)).uniqueResult(user);
    }

    @Override
    public List<User> getOrderedByName() {
        return query().from(user).orderBy(user.username.asc()).list(user);
    }

    @Override
    public UserInfo getCurrentUser() {
        return getUserInfoByUsername(authService.getUsername());
    }

    @Override
    public UserInfo getUserInfoByUsername(String username) {
        return query().from(user).where(user.username.eq(username))
                .uniqueResult(ConstructorExpression.create(UserInfo.class, user.id, user.username));
    }

    @Override
    public Collection<UserInfo> getAllUserInfos() {
        return query().from(user).list(
                ConstructorExpression.create(UserInfo.class, user.id, user.username));
    }

    @Override
    public List<User> addUsersFromCsvFile(String filePath, String encoding) throws IOException {

        // "/users.csv"), "ISO-8859-1"
        @SuppressWarnings("unchecked")
        List<String> lines = IOUtils.readLines(
                HibernateDataModule.class.getResourceAsStream(filePath), encoding);
        List<User> users = new ArrayList<User>();
        for (String line : lines) {
            String[] values = line.split(";");
            User user = getByUsername(values[2]);
            if (user == null) {
                user = new User();
            }
            user.setFirstName(values[0]);
            user.setLastName(values[1]);
            user.setUsername(values[2]);
            user.setEmail(values[3]);
            if (values[3].endsWith("mysema.com")) {
                user.setProfile(Profile.Admin);
            } else {
                user.setProfile(Profile.User);
            }

            // encode password
            UserDetailsImpl userDetails = new UserDetailsImpl(user.getUsername(),
                    user.getPassword(), user.getProfile().getAuthorities());
            String password = passwordEncoder.encodePassword(user.getUsername(),
                    saltSource.getSalt(userDetails));
            user.setPassword(password);

            getSession().save(user);
            users.add(user);
        }
        return users;
    }
    
    @Override
    public void save(User user) {
        getSession().save(user);
    }

}
