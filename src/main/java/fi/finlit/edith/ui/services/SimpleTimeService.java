/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

/**
 * SimpleTimeService provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SimpleTimeService implements TimeService{

    private long last = System.currentTimeMillis();

    @Override
    public long currentTimeMillis() {
        while (last == System.currentTimeMillis()){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new ServiceException(e);
            }
        }
        last = System.currentTimeMillis();
        return last;
    }

}
