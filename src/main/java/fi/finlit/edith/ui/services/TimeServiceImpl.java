package fi.finlit.edith.ui.services;

/**
 * TimeServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TimeServiceImpl implements TimeService{

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}
