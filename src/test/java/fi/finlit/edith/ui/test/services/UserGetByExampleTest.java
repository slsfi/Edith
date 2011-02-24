package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.services.UserRepository;

public class UserGetByExampleTest extends AbstractServiceTest{

    @Inject
    private UserRepository userRepo;

    @Inject
    private SessionFactory sessionFactory;

    @Test
    public void GetByExample_With_User(){
        Collection<User> allUsers = userRepo.getAll();
        Session session = sessionFactory.openSession();
        try{
            for (User user : allUsers){
                assertNotNull(session.getByExample(user));
            }
        }finally{
            session.close();
        }
    }

}
