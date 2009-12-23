/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import com.mysema.query.paging.Callback;
import com.mysema.query.paging.CallbackService;

/**
 * CallbackServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class CallbackServiceImpl implements CallbackService{

    @Override
    public <RT> RT invoke(Callback<RT> cb) {
        return cb.invoke();
    }

}
