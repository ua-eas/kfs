/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.util.spring;

import java.util.LinkedList;
import java.util.List;

/**
 * This class can be used to group items in spring for later retrieval by list name. Order is important, because it is used to
 * facilitate overrides.
 */
public class NamedOrderedListBean {
    private String name;
    private List<String> list;

    public NamedOrderedListBean() {
        list = new LinkedList();
    }

    public void setListItem(String listItem) {
        list.add(listItem);
    }

    public void setListItems(List<String> listItems) {
        list.addAll(listItems);
    }

    public List<String> getList() {
        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
