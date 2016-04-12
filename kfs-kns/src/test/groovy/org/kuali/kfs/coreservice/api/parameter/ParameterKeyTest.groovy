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
package org.kuali.kfs.coreservice.api.parameter

import org.junit.Test
import org.kuali.kfs.coreservice.test.JAXBAssert

class ParameterKeyTest {
        private static final String XML = """
        <parameterKey xmlns="http://rice.kuali.org/core/v2_0">
            <applicationId>AC</applicationId>
            <namespaceCode>NC</namespaceCode>
            <componentCode>CC</componentCode>
            <name>N</name>
        </parameterKey>
    """

    private static final String APPLICATION_ID = "AC"
    private static final String NAMESPACE_CODE = "NC"
    private static final String COMPONENT_CODE = "CC"
    private static final String NAME = "N"

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_all_null() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_first_null() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(null, NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_first_empty() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create("", NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_first_whitespace() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(" ", NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_second_null() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, null, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_second_empty() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, "", COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_second_whitespace() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, " ", COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_third_null() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, null, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_third_empty() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, "", NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_third_whitespace() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, " ", NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_fourth_null() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, COMPONENT_CODE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_fourth_empty() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, COMPONENT_CODE, "");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_fourth_whitespace() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, COMPONENT_CODE, " ");
    }

    @Test
    void happy_path() {
        org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, org.kuali.kfs.coreservice.api.parameter.ParameterKey.class)
	}

    private create() {
		return org.kuali.kfs.coreservice.api.parameter.ParameterKey.create(APPLICATION_ID, NAMESPACE_CODE, COMPONENT_CODE, NAME);
	}
}
