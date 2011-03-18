package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.User;
import fi.finlit.edith.ui.services.PrimaryKeyEncoder;
import fi.finlit.edith.ui.services.UserRepository;

public class PrimaryKeyEncoderTest extends AbstractServiceTest {

    @Inject
    private UserRepository userRepository;

    private PrimaryKeyEncoder<User> encoder;

    private User user;

    @Before
    public void setUp(){
        encoder = new PrimaryKeyEncoder<User>(userRepository);
        user = new User();
        user.setUsername("test");
        userRepository.save(user);
    }

    @Test
    public void ToClient() {
        assertEquals(user.getId(), encoder.toClient(user));
    }

    @Test
    public void ToValue() {
        assertNotNull(encoder.toValue(user.getId()));
    }

}
