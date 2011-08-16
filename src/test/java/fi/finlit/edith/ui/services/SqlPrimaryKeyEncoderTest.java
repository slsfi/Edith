package fi.finlit.edith.ui.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.hibernate.AbstractHibernateTest;

public class SqlPrimaryKeyEncoderTest extends AbstractHibernateTest {

    @Inject
    private UserDao userDao;
    
    private SqlPrimaryKeyEncoder<User> encoder;

    @Before
    public void setUp() throws Exception {
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
        encoder = new SqlPrimaryKeyEncoder<User>(userDao);
    }
       
    @Test
    public void GetAllValues() {
        assertTrue(encoder.getAllValues().isEmpty());
    }

    @Test
    public void ToClient() {
        User user = userDao.getByUsername("timo");        
        assertEquals(user.getId().toString(), encoder.toClient(user));
    }

    @Test
    public void ToValue() {
        User user = userDao.getByUsername("timo");
        assertEquals(user, encoder.toValue(user.getId().toString()));
        
        assertEquals(1, encoder.getAllValues().size());
    }

}
