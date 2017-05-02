/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
            protected int getCount(Class businessObjectClass, Criteria criteria) {
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
        Collection results = lookupDaoOjb.executeSearch(BusinessObject.class, criteria, false);

        Assert.assertTrue(results instanceof CollectionIncomplete);
        CollectionIncomplete ci = (CollectionIncomplete) results;

        Assert.assertEquals(1, getCriteriaCount(criteria));
        Assert.assertEquals(2L, ci.size());
        Assert.assertEquals(10L, ci.getActualSizeIfTruncated().longValue());
    }

    @Test
    public void testExecuteSearchWithNoLimit() {
        Criteria criteria = new Criteria();
        Collection results = lookupDaoOjb.executeSearch(BusinessObject.class, criteria, true);

        Assert.assertTrue(results instanceof CollectionIncomplete);
        CollectionIncomplete ci = (CollectionIncomplete) results;

        Assert.assertEquals(0, getCriteriaCount(criteria));
        Assert.assertEquals(2L, ci.size());
        Assert.assertEquals(0L, ci.getActualSizeIfTruncated().longValue());
    }

    public int getCriteriaCount(Criteria criteria) {
        int criteriaCount = 0;
        Enumeration e = criteria.getElements();
        while (e.hasMoreElements()) {
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
