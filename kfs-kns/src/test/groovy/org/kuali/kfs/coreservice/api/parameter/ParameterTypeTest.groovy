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
package org.kuali.kfs.coreservice.api.parameter

import org.junit.Test
import org.kuali.kfs.coreservice.test.JAXBAssert

class ParameterTypeTest {
    private static final Long VERSION_NUMBER = new Integer(1);
    private static final String OBJECT_ID = UUID.randomUUID();
    private static final String XML = """
        <parameterType xmlns="http://rice.kuali.org/core/v2_0">
            <code>PC</code>
            <name>Config</name>
            <active>true</active>
            <versionNumber>${VERSION_NUMBER}</versionNumber>
            <objectId>${OBJECT_ID}</objectId>
        </parameterType>
    """

    private static final String PARAMETER_TYPE_CODE = "PC"

    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create("");
    }

    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create("  ");
    }

    @Test
    void test_create_only_required() {
        org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create(org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create(PARAMETER_TYPE_CODE)).build();
    }

    @Test
    void happy_path() {
        org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create(PARAMETER_TYPE_CODE);
    }

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, org.kuali.kfs.coreservice.api.parameter.ParameterType.class)
    }

    private create() {
        return org.kuali.kfs.coreservice.api.parameter.ParameterType.Builder.create(new org.kuali.kfs.coreservice.api.parameter.ParameterTypeContract() {
            def String code = "PC"
            def String name = "Config"
            def boolean active = true
            def Long versionNumber = ParameterTypeTest.VERSION_NUMBER
            def String objectId = ParameterTypeTest.OBJECT_ID
        }).build()
    }
}
