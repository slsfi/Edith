/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;

import com.mysema.rdfbean.tapestry.services.RDFBeanModule;

import fi.finlit.edith.testutil.Modules;
import fi.finlit.edith.testutil.TapestryTestRunner;
import fi.finlit.edith.ui.services.DataModule;
import fi.finlit.edith.ui.services.ServiceModule;

/**
 * AbstractServiceTest provides
 *
 * @author tiwe
 * @version $Id$
 */
@RunWith(TapestryTestRunner.class)
@Modules({
    ServiceTestModule.class,
    ServiceModule.class,
    DataModule.class,
    RDFBeanModule.class})
public abstract class AbstractServiceTest {

    @BeforeClass
    public static void beforeClass() throws SVNException{
        FSRepositoryFactory.setup();
    }

    protected abstract Class<?> getServiceClass();

    private List<InputStream> openStreams = new ArrayList<InputStream>();

    protected InputStream register(InputStream is){
        openStreams.add(is);
        return is;
    }    

    protected void closeStreams(){
        for (InputStream is : openStreams){
            try{
                is.close();    
            }catch(IOException io){
                io.printStackTrace();
            }            
        }           
    }

    
    @Test
    public void allCovered(){
        Class<?> serviceClass = getServiceClass();
        List<String> missing = CollectionFactory.newList();
        if (serviceClass != null){
            for (Method m : serviceClass.getMethods()){
                if (!m.getDeclaringClass().equals(serviceClass)){
                    continue;
                }
                try {
                    getClass().getMethod(m.getName());
                } catch (SecurityException e) {
                    String error = "Caught " + e.getClass().getName();
                    throw new RuntimeException(error, e);
                } catch (NoSuchMethodException e) {
                    missing.add(m.getName());
                }
            }
        }
        if (!missing.isEmpty()){
            fail("Missing tests : " + missing);
        }
    }

    protected static final String start(String localId){
        return "<anchor xml:id=\"start" + localId+"\"/>";
    }

    protected static final String end(String localId){
        return "<anchor xml:id=\"end" + localId+"\"/>";
    }

}
