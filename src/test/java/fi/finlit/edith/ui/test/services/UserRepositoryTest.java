package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.UserRepository;

/**
 * UserRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class UserRepositoryTest extends AbstractServiceTest{

    @Inject
    private UserRepository userRepo;
    
    @Test
    public void getByUsername(){
        for (String username : Arrays.asList("timo", "lassi", "heli", "sakari", "ossi")){
            assertNotNull(userRepo.getByUsername(username));
        }
    }
}
