/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

/**
 * TimeServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SimpleTimeService implements TimeService{
    
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();        
    }

}
