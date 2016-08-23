/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.kns.web.struts.form.pojo;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.config.impl.ModuleConfigImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.krad.bo.DocumentAttachment;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

public class PojoPluginTest {

    /**
     * <p>Testing scenario that was not working in the linked issue off of
     * KULRICE-6877: KualiMaintainbleImpl#performCollectionForceUpperCase blowing up</p>
     *
     * @throws Exception
     */
    @Test
    public void testGetChildCollectionThrowsNestedNullException() throws Exception {

        // We need to initialize PropertyUtils to use our plugins
        new PojoPlugin().init(null, new ModuleConfigImpl());

        TestCollectionHolderHolder tchh = new TestCollectionHolderHolder();
        tchh.setTch(new TestCollectionHolder());

        // this simulates a situation in which the property (tch) is a proxied object
        // that can't be fetched, so getting it works (returns the proxy) but trying
        // to access the collection underneath it throws a NestedNullException
        Object result = ObjectUtils.getPropertyValue(tchh, "tch.collection");

        // before, the empty string was being returned, which doesn't make sense for a collection
        assertFalse("".equals(result));

        // now we return null
        assertTrue(null == result);
    }

    /**
     * <p>Testing scenario that isWriteable blows up with NestedNullException when property value is null
     * KULRICE-6877: KualiMaintainbleImpl#performCollectionForceUpperCase blowing up</p>
     *
     * @throws Exception
     */
    @Test
    public void testNestedNullIsWriteable() throws Exception {

        // We need to initialize PropertyUtils to use our plugins
        new PojoPlugin().init(null, new ModuleConfigImpl());

        TestCollectionHolderHolder tchh = new TestCollectionHolderHolder();
        assertTrue(PropertyUtils.isWriteable(tchh, "tch2.collection"));


    }

    /**
     * Tests that original IndexOutOfBoundsException is thrown when the bean is not a PersistableBusinessObject
     */
    @Test
    public void testGenerateIndexedPropertyNonPBO() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
        IndexOutOfBoundsException ioobe = new IndexOutOfBoundsException("test exception");
        try {
            new PojoPropertyUtilsBean().generateIndexedProperty(new TestCollectionHolder(), "collection", 0, ioobe);
            Assert.fail("Expected to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals(ioobe, e);
        }
    }

    /**
     * Tests that original IndexOutOfBoundsException is thrown when the property is not a List
     */
    @Test
    public void testGenerateIndexedPropertyNonList() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException  {
        IndexOutOfBoundsException ioobe = new IndexOutOfBoundsException("test exception");
        try {
            new PojoPropertyUtilsBean().generateIndexedProperty(new DocumentAttachment(), "attachmentContent", 0, ioobe);
            Assert.fail("Expected to throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            assertEquals(ioobe, e);
        }
    }

    @Test
    public void testUndefinedOJBClass() {
        final Object notAnOjbObject = new HashMap();
        // stub out the persistence service
        PojoPropertyUtilsBean.PersistenceStructureServiceProvider.persistenceStructureService =
                (PersistenceStructureService) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                        new Class[] { PersistenceStructureService.class },
                        new InvocationHandler() {
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                if ("listCollectionObjectTypes".equals(method.getName())) {
                                    return new HashMap();
                                }
                                return null;
                            }
                        });
        assertNull(new PojoPropertyUtilsBean.PersistenceStructureServiceProvider().getCollectionItemClass(notAnOjbObject, "abcd"));
    }

    /**
     * Ugly name, but it holds a TestCollectionHolder
     */
    public static class TestCollectionHolderHolder extends PersistableBusinessObjectBase {
        private TestCollectionHolder tch = null;
        private TestCollectionHolder2 tch2;

        public TestCollectionHolder getTch() {
            return tch;
        }

        public void setTch(TestCollectionHolder tch) {
            this.tch = tch;
        }

        public TestCollectionHolder2 getTch2() {
            return tch2;
        }

        public void setTch2(TestCollectionHolder2 tch2) {
            this.tch2 = tch2;
        }
    }

    /**
     * Test class that holds a collection, but trying to get it results in a
     * NestedNullException.
     * @throws NestedNullException
     */
    public static class TestCollectionHolder extends PersistableBusinessObjectBase {
        private Collection collection = Collections.emptyList();

        public Collection getCollection() {
            throw new NestedNullException();
        }

        public void setCollection(Collection collection) {
            this.collection = collection;
        }
    }

    /**
     * Test class that holds a collection, but trying to get it results in a
     * NestedNullException.
     * @throws NestedNullException
     */
    public static class TestCollectionHolder2 extends PersistableBusinessObjectBase {
        private Collection collection = Collections.emptyList();

        public Collection getCollection() {
            return collection;
        }

        public void setCollection(Collection collection) {
            this.collection = collection;
        }
    }


}
