/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.testutil;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SystemPropertyCheckRule provides
 * 
 * @author tiwe
 * @version $Id$
 */
public class SystemPropertyCheckRule implements MethodRule {

    private static final Logger logger = LoggerFactory.getLogger(SystemPropertyCheckRule.class);

    private final String name;

    public SystemPropertyCheckRule(String name) {
        this.name = name;
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (System.getProperty(name) != null) {
                    base.evaluate();
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Skipping test " + method.getName());
                }
            }
        };
    }

}
