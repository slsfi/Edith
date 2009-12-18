/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.editio.ui.services;

import com.mysema.tapestry.core.Callback;
import com.mysema.tapestry.core.CallbackService;

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
