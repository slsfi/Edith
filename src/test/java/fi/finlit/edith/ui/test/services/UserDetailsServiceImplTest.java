package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;

import fi.finlit.edith.ui.services.UserDao;
import fi.finlit.edith.ui.services.hibernate.AbstractHibernateTest;
import fi.finlit.edith.ui.services.hibernate.UserDetailsServiceImpl;

public class UserDetailsServiceImplTest extends AbstractHibernateTest {
    private UserDetailsServiceImpl service;

    @Inject
    private UserDao userDao;

    @Before
    public void setUp() throws Exception {
    	service = new UserDetailsServiceImpl(userDao);
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = UsernameNotFoundException.class)
    public void Load_By_Username_Not_Found() {
        service.loadUserByUsername("hacker");
    }

    @Test
    public void Load_By_Username() {
        UserDetails userDetails = service.loadUserByUsername("timo");
        assertEquals("timo", userDetails.getUsername());
        assertEquals("d8a0c6468369447dc6328adca0c17e8ad5e572f1", userDetails.getPassword());
        assertArrayEquals(new GrantedAuthority[] { new GrantedAuthorityImpl("ROLE_USER"), new GrantedAuthorityImpl("ROLE_ADMIN") }, userDetails.getAuthorities());
    }
}
