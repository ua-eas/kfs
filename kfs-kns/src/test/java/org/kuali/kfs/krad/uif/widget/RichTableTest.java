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
package org.kuali.kfs.krad.uif.widget;

import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.validation.Employee;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.field.DataField;
import org.kuali.kfs.krad.uif.layout.TableLayoutManager;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * test the RichTable widget
 */

public class RichTableTest {

    private RichTable richTable;
    private CollectionGroup group;

    //private
    @Before
    public void setup() {

        richTable = new RichTable();

        group = new CollectionGroup();
        group.setCollectionObjectClass(Employee.class);
        TableLayoutManager layoutManager = new TableLayoutManager();
        layoutManager.setRenderSequenceField(true);
        group.setLayoutManager(layoutManager);
        group.setRenderSelectField(false);
        group.setRenderLineActions(false);

        List<Component> items = new ArrayList<Component>(1);
        DataField name = new DataField();
        name.setPropertyName("employeeId");
        items.add(name);
        DataField number = new DataField();
        number.setPropertyName("positionTitle");
        items.add(number);
        DataField contactEmail = new DataField();
        contactEmail.setPropertyName("contactEmail");
        items.add(contactEmail);

        group.setItems(items);
    }

    @Test
    /**
     * test that without aoColumns being set explicitly, the default behaviour continues
     */
    public void testComponentOptionsDefault() throws Exception {
        String expected = "[ null ,{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"} , "
            + "{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"} , "
            + "{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"} ]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }


    @Test
    /**
     * test that when aoColumns is explicitly set, it is integrated into the rich table rendering logic
     */
    public void testComponentOptionsAoColumnsJSOptions() throws Exception {
        String innerColValues = "{bVisible: false}, null, null";
        String options = "[" + innerColValues + "]";
        String expected = "[ null ," + innerColValues + " ]";
        assertRichTableComponentOptions(options, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }

    @Test
    /**
     * test whether a hidden column, when marked as sortable is still hidden
     */
    public void testComponentOptionsHideColumnOnRichTable() {
        Set<String> hiddenColumns = new HashSet<String>();
        hiddenColumns.add("employeeId");
        Set<String> sortableColumns = new HashSet<String>();
        sortableColumns.add("positionTitle");
        richTable.setSortableColumns(sortableColumns);
        richTable.setHiddenColumns(hiddenColumns);
        String expected = "[ null ,{bVisible: false}, {\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"}, {'bSortable': false}]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }

    @Test
    /**
     * test that sortableColumns and hiddenColumns, when set on layoutManager, override those properties on the richTable
     */
    public void testComponentOptionsHideColumnOnLayoutManager() {
        // set rich table properties
        Set<String> richTableHiddenColumns = new HashSet<String>();
        richTableHiddenColumns.add("employeeId");
        Set<String> sortableColumns = new HashSet<String>();
        sortableColumns.add("positionTitle");
        richTable.setSortableColumns(sortableColumns);
        richTable.setHiddenColumns(richTableHiddenColumns);
        // set layout manager properties
        Set<String> lmHiddenColumns = new HashSet<String>();
        lmHiddenColumns.add("contactEmail");
        Set<String> lmSortableColumns = new HashSet<String>();
        lmSortableColumns.add("employeeId");
        ((TableLayoutManager) group.getLayoutManager()).setSortableColumns(lmSortableColumns);
        ((TableLayoutManager) group.getLayoutManager()).setHiddenColumns(lmHiddenColumns);

        String expected = "[ null ,{\"sSortDataType\" : \"dom-text\" , \"sType\" : \"string\"}, {'bSortable': false}, {bVisible: false}]";
        assertRichTableComponentOptions(null, expected, UifConstants.TableToolsKeys.AO_COLUMNS);
    }

    /**
     * a common method to test rich table options
     *
     * @param optionsOnGroup     - a string in JSON format of the options set on the collection group
     * @param optionsOnRichTable - a string in JSON format of the options set on the rich table
     * @param optionKey          - a string with the rich table option key being tested
     */
    private void assertRichTableComponentOptions(String optionsOnGroup, String optionsOnRichTable, String optionKey) {
        richTable.getComponentOptions().put(optionKey, optionsOnGroup);
        richTable.performFinalize(new View(), new UifFormBase(), group);
        assertEquals(optionsOnRichTable, richTable.getComponentOptions().get(optionKey));
    }
}
