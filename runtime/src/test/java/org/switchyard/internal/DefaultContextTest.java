/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.internal;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  Tests for context-related operations.
 */
public class DefaultContextTest {
    
    private static final String PROP_NAME = "foo";
    private static final String PROP_VAL= "bar";
    private DefaultContext _context;
    
    @Before
    public void setUp() throws Exception {
        _context = new DefaultContext();
    }
    
    @Test
    public void testGetSet() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertEquals(PROP_VAL, _context.getProperty(PROP_NAME));
    }

    @Test
    public void testHasProperty() throws Exception {
        Assert.assertFalse(_context.hasProperty(PROP_NAME));
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
    }

    @Test
    public void testRemove() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
        _context.removeProperty(PROP_NAME);
        Assert.assertFalse(_context.hasProperty(PROP_NAME));
    }
    
    @Test
    public void testRemoveNonexistent() throws Exception {
        // Removing a nonexistent property should not throw an exception
        final String propName = "blahFooYech";
        Assert.assertFalse(_context.hasProperty(propName));
        _context.removeProperty(propName);
    }
    
    @Test
    public void testNullContextValue() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
        // setting the value to null should remove the property from context
        _context.setProperty(PROP_NAME, null);
        Assert.assertFalse(_context.hasProperty(PROP_NAME));
    }

    @Test
    public void testGetProperties() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Map<String, Object> props = _context.getProperties();
        Assert.assertEquals(PROP_VAL, props.get(PROP_NAME));
        
        // operations to the returned map should *not* be reflected in the context
        props.remove(PROP_NAME);
        Assert.assertFalse(props.containsKey(PROP_NAME));
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
    }
    
}
