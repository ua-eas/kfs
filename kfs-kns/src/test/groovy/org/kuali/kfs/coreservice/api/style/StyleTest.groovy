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
package org.kuali.kfs.coreservice.api.style

import org.junit.Assert
import org.junit.Test
import org.kuali.kfs.coreservice.test.JAXBAssert

class StyleTest {
	private static final String STYLE_ID = "1"
	private static final String NAME = "MyFirstStyle"
	private static final boolean ACTIVE = true
	private static final String XML_CONTENT = "<my><awesome><xml-stylesheet/></awesome></my>"
	private static final Long VERSION_NUMBER = 1
	private static final String OBJECT_ID = UUID.randomUUID();
	private static final String XML = """
        <style xmlns="http://rice.kuali.org/core/v2_0">
            <id>${STYLE_ID}</id>
            <name>${NAME}</name>
            <active>${ACTIVE}</active>
            <xmlContent><![CDATA[${XML_CONTENT}]]></xmlContent>
            <versionNumber>${VERSION_NUMBER}</versionNumber>
            <objectId>${OBJECT_ID}</objectId>
        </style>
    """

    @Test(expected=IllegalArgumentException.class)
    void testBuilderCreate_fail_null() {
        Style.Builder.create((String)null);
    }
	
	@Test(expected=IllegalArgumentException.class)
	void testBuilderCreate_fail_empty() {
		Style.Builder.create("");
	}
	
	@Test(expected=IllegalArgumentException.class)
	void testBuilderCreate_fail_blank() {
		Style.Builder.create(" ");
	}
	
	@Test(expected=IllegalArgumentException.class)
	void testBuilderCreate_contract_fail_null() {
		Style.Builder.create((StyleContract)null);
	}
	
	@Test
	void testBuilderCreate_minimal() {
		def builder = Style.Builder.create("myStyleName");
		def style = builder.build();
		assert style.name == "myStyleName"
		assert style.active
		Assert.assertNull style.objectId
		Assert.assertNull style.versionNumber
		Assert.assertNull style.id
		Assert.assertNull style.xmlContent
	}
	
	@Test
	void testBuilder_setName() {
		def builder = Style.Builder.create("myStyleName");
		try {
			builder.setName(null);
			Assert.fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {}
		try {
			builder.setName("");
			Assert.fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {}

		try {
			builder.setName(" ");
			Assert.fail("IllegalArgumentException should have been thrown.");
		} catch (IllegalArgumentException e) {}

	}
	
	@Test
	void testBuilder_fullCreate() {
		def Style style = create();
		assert style.id == STYLE_ID
		assert style.name == NAME
		assert style.active
		assert style.xmlContent == XML_CONTENT
		assert style.versionNumber == VERSION_NUMBER
		assert style.objectId == OBJECT_ID
	}

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, Style.class)
	}
	
	/**
	 * Ensures that toString executes cleanly.
	 */
	@Test
	public void testToString() {
		def Style style = create();
		def toString = style.toString()
		Assert.assertNotNull toString
		System.out.println(toString);
	}

    private create() {
		return Style.Builder.create(new StyleContract() {
				def String id = StyleTest.STYLE_ID
                def String name = StyleTest.NAME
                def boolean active = StyleTest.ACTIVE
				def String xmlContent = StyleTest.XML_CONTENT
                def Long versionNumber = StyleTest.VERSION_NUMBER
				def String objectId = StyleTest.OBJECT_ID
			}).build()
	}
}
