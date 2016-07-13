package org.kuali.kfs.krad.dao.impl;

import org.apache.ojb.broker.query.Criteria;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

public class LookupDaoOjbTest {
    private LookupDaoOjb lookupDaoOjb;

    @Before
    public void setUp() {
        lookupDaoOjb = new LookupDaoOjb() {
            @Override
            protected Integer getSearchResultsLimit(Class businessObjectClass) {
                return 2;
            }

            @Override
            protected int getCount(Class businessObjectClass,Criteria criteria) {
                return 10;
            }

            @Override
            protected void applySearchResultsLimit(Class businessObjectClass, Criteria criteria, DatabasePlatform databasePlatform) {
                criteria.addSql("limit 2");
            }

            protected Collection getCollectionByQuery(Class businessObjectClass, Criteria criteria) {
                Collection results = new ArrayList();
                results.add(new TestBusinessObject());
                results.add(new TestBusinessObject());
                return results;
            }
        };
    }

    @Test
    public void testExecuteSearchWithLimit() {
        Criteria criteria = new Criteria();
        Collection results = lookupDaoOjb.executeSearch(BusinessObject.class,criteria,false);

        Assert.assertTrue(results instanceof CollectionIncomplete);
        CollectionIncomplete ci = (CollectionIncomplete)results;

        Assert.assertEquals(1,getCriteriaCount(criteria));
        Assert.assertEquals(2L,ci.size());
        Assert.assertEquals(10L,ci.getActualSizeIfTruncated().longValue());
    }

    @Test
    public void testExecuteSearchWithNoLimit() {
        Criteria criteria = new Criteria();
        Collection results = lookupDaoOjb.executeSearch(BusinessObject.class,criteria,true);

        Assert.assertTrue(results instanceof CollectionIncomplete);
        CollectionIncomplete ci = (CollectionIncomplete)results;

        Assert.assertEquals(0,getCriteriaCount(criteria));
        Assert.assertEquals(2L,ci.size());
        Assert.assertEquals(0L,ci.getActualSizeIfTruncated().longValue());
    }

    public int getCriteriaCount(Criteria criteria) {
        int criteriaCount = 0;
        Enumeration e = criteria.getElements();
        while ( e.hasMoreElements() ) {
            e.nextElement();
            criteriaCount++;
        }
        return criteriaCount;
    }

    class TestBusinessObject implements BusinessObject {
        @Override
        public void refresh() {

        }

        @Override
        public String toString() {
            return "";
        }
    }
}