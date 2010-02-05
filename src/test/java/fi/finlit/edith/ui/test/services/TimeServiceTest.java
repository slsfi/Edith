package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.ui.services.TimeService;

/**
 * TimeServiceTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TimeServiceTest extends AbstractServiceTest{

    @Inject
    private TimeService timeService;
    
    @Test
    public void currentTimeMillis(){
        long t1 = timeService.currentTimeMillis();
        long t2 = timeService.currentTimeMillis();
        assertTrue(t2 > t1);
    }
    
    @Override
    protected Class<?> getServiceClass() {
        return TimeService.class;
    }

}
