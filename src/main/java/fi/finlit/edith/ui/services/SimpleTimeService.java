/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

@Deprecated //Local id is not used anymore
public class SimpleTimeService implements TimeService{

    private volatile long last = System.currentTimeMillis();

    @Override
    //FIXME This is not thread safe if multiple threads need to get unique long
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
