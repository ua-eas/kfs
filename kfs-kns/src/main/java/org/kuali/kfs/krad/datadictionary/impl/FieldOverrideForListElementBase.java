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
package org.kuali.kfs.krad.datadictionary.impl;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.List;

/**
 * The super class which implementations of the FieldOverride interface will extend.
 */
public class FieldOverrideForListElementBase {

    private String propertyName;
    private Object element;
    protected String propertyNameForElementCompare;


    public String getPropertyNameForElementCompare() {
        return propertyNameForElementCompare;
    }

    public void setPropertyNameForElementCompare(String propertyNameForElementCompare) {
        this.propertyNameForElementCompare = propertyNameForElementCompare;
    }

    protected int getElementPositionInList(Object object, List theList) {
        Comparator comparator = this.getComparator();
        int pos = -1;

        if (object != null && theList != null) {
            for (int i = 0; i < theList.size(); ++i) {
                Object item = theList.get(i);
                boolean equalFlag = false;
                if (comparator != null) {
                    equalFlag = comparator.compare(object, item) == 0;
                } else {
                    equalFlag = item.equals(object);
                }
                if (equalFlag) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getElement() {
        return element;
    }

    public void setElement(Object value) {
        this.element = value;
    }


    public FieldOverrideForListElementBase() {
        super();
    }

    protected Comparator getComparator() {
        Comparator comparator = null;
        if (StringUtils.isNotBlank(propertyNameForElementCompare)) {
            comparator = new BeanComparator(propertyNameForElementCompare);
        } else {
            throw new RuntimeException("Missing required comparator definitions.");
        }
        return comparator;
    }

}
