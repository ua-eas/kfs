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
package org.kuali.kfs.coreservice.api.namespace

import org.junit.Test
import org.kuali.kfs.coreservice.test.JAXBAssert

class NamespaceTest {
    private static final String CODE = "PC"
    private static final String APP_ID = "AC"
    private static final Long VERSION_NUMBER = new Long(1);
    private static final String OBJECT_ID = UUID.randomUUID();
    private static final String XML = """
        <namespace xmlns="http://rice.kuali.org/core/v2_0">
            <code>${CODE}</code>
            <applicationId>${APP_ID}</applicationId>
            <name>N</name>
            <active>true</active>
            <versionNumber>${VERSION_NUMBER}</versionNumber>
            <objectId>${OBJECT_ID}</objectId>
        </namespace>
    """


    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_all_null() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(null, APP_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create("", APP_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create("  ", APP_ID);
    }

    @Test
    void test_Builder_fail_second_null() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(CODE, null);
    }

    @Test
    void test_Builder_fail_second_empty() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(CODE, "");
    }

    @Test
    void test_Builder_fail_second_whitespace() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(CODE, "");
    }

    @Test
    void happy_path() {
        org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(CODE, APP_ID);
    }

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, org.kuali.kfs.coreservice.api.namespace.Namespace.class)
    }

    private create() {
        return org.kuali.kfs.coreservice.api.namespace.Namespace.Builder.create(new org.kuali.kfs.coreservice.api.namespace.NamespaceContract() {
            def String code = NamespaceTest.CODE
            def String applicationId = NamespaceTest.APP_ID
            def String name = "N"
            def boolean active = true
            def Long versionNumber = NamespaceTest.VERSION_NUMBER
            def String objectId = NamespaceTest.OBJECT_ID
        }).build()
    }
}
