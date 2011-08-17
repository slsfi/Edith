package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.apache.tapestry5.beaneditor.PropertyModel;
import org.apache.tapestry5.grid.ColumnSort;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.sql.domain.QUser;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.UserDao;
import fi.finlit.edith.util.JPQLGridDataSource;

public class JPQLGridDataSourceTest extends AbstractHibernateTest {

    @Inject
    private HibernateSessionManager sessionManager;
    
    @Inject
    private UserDao userDao;
    
    private JPQLGridDataSource<User> dataSource;
    
    @Before
    public void setUp() throws IOException {
        QUser user = QUser.user;
        dataSource = new JPQLGridDataSource<User>(sessionManager, user, user.email.asc(), false);        
        userDao.addUsersFromCsvFile("/users.csv", "ISO-8859-1");
    }
    
    @Test
    public void GetAvailableRows() {
        assertTrue(dataSource.getAvailableRows() > 0);
    }

    @Test
    public void Prepare() {
        dataSource.prepare(0, 10, Collections.<SortConstraint>emptyList());
    }
    
    @Test
    public void Prepare_with_sort() {
        PropertyModel firstName = new SimplePropertyModel("firstName",String.class);
        SortConstraint constraint = new SortConstraint(firstName,ColumnSort.ASCENDING);
        dataSource.prepare(0, 10, Collections.singletonList(constraint));
    }

    @Test
    public void GetRowValue() {
        dataSource.prepare(0, 5, Collections.<SortConstraint>emptyList());
        for (int i = 0; i < 5; i++){
            assertNotNull(dataSource.getRowValue(i));
        }
    }
    
    @Test
    public void GetRowValue_Invalid_Index() {
        dataSource.prepare(0, 5, Collections.<SortConstraint>emptyList());
        assertNull(dataSource.getRowValue(100));
    }

    @Test
    public void GetRowType() {
        assertEquals(User.class, dataSource.getRowType());
    }

}
