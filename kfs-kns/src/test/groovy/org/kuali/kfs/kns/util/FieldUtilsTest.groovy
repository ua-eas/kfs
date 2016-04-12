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
package org.kuali.kfs.kns.util

import org.junit.Test
import org.kuali.kfs.kns.util.FieldUtils
import org.kuali.kfs.kns.web.ui.Row
import org.kuali.rice.core.api.uif.RemotableAttributeField
import org.kuali.rice.core.api.data.DataType
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup
import org.kuali.rice.core.api.uif.RemotableSelect
import org.kuali.rice.core.api.uif.RemotableTextInput
import static org.junit.Assert.assertEquals
import org.kuali.kfs.kns.web.ui.Field
import org.kuali.kfs.kns.web.ui.Column
import org.kuali.rice.core.web.format.CurrencyFormatter
import org.kuali.rice.core.web.format.Formatter
import org.kuali.rice.core.web.format.IntegerFormatter
import org.kuali.rice.krad.bo.BusinessObject

/**
 * Tests FieldUtils
 */
class FieldUtilsTest {
    @Test
    void testGenerateCollectionSubTabName() {
        def f = FieldUtils.constructContainerField("collection[1]", "Test Collection", [
            new Field(fieldLabel: "Field One", propertyName: "field1", propertyValue: "value one"),
            new Field(fieldLabel: "Field Two", propertyName: "field2", propertyValue: "value two"),
            new Field(fieldLabel: "Field Three", propertyName: "field3", propertyValue: "value three"),
            new Field(fieldLabel: "Multi-value Field", propertyName: "multiValueField", propertyValues: [ "value1", "value2", "value3" ])
        ], 50)
        f.setContainerElementName("containerName1-Element")
        f.setContainerName("containerName1")
        f.setContainerDisplayFields( [
            new Field(fieldLabel: "Contained Field One", propertyName: "containedField1", propertyValue: "contained value one"),
            new Field(fieldLabel: "Contained Field Two", propertyName: "containedField2", propertyValue: "contained value two"),
            new Field(fieldLabel: "Contained Field Three", propertyName: "containedField3", propertyValue: "contained value three"),
            new Field(fieldLabel: "Contained Multi-value Field", propertyName: "multiValueField", propertyValues: [ "value1", "value2", "value3" ])
        ])
        // multivalued field values not considered for purposes of collection sub tab name generation
        assertEquals("containerName-Elementcontained value onecontained value twocontained value three", FieldUtils.generateCollectionSubTabName(f))
    }

    @Test
    void testGenerationCollectionSubTabName_null() {
        def f = FieldUtils.constructContainerField("collection[1]", "Test Collection", [
                new Field(fieldLabel: "Field One", propertyName: "field1", propertyValue: "value one"),
                new Field(fieldLabel: "Field Two", propertyName: "field2", propertyValue: "value two"),
                new Field(fieldLabel: "Field Three", propertyName: "field3", propertyValue: "value three"),
                new Field(fieldLabel: "Multi-value Field", propertyName: "multiValueField", propertyValues: [ "value1", "value2", "value3" ])
        ], 50)
        f.setContainerElementName("containerName1-Element")
        f.setContainerName("containerName1")
        f.setContainerDisplayFields(null)

        assertEquals("containerName-Element", FieldUtils.generateCollectionSubTabName(f))
    }

    /**
     * Performs an as-of-yet very superficial check of remotableattributefield conversion
     */
    @Test
    void testRemotableAttributeFieldFormatter(){
        def field = RemotableAttributeField.Builder.create("string")
        field.shortLabel = "s"
        field.dataType = DataType.STRING
        field.maxLength = 10
        field.formatterName = "org.kuali.rice.core.api.util.type.KualiDecimal"
        Column col = FieldUtils.constructColumnFromAttributeField(field.build())
        assertEquals(CurrencyFormatter.class,col.getFormatter().getClass())
    }
    @Test
   void testRemotableAttributeFieldFormatterEmpty(){
       def field = RemotableAttributeField.Builder.create("string")
       field.shortLabel = "s"
       field.dataType = DataType.STRING
       field.maxLength = 10
       Column col = FieldUtils.constructColumnFromAttributeField(field.build())
       assertEquals(Formatter.class,col.getFormatter().getClass())
   }

    @Test
   void testRemotableAttributeFieldDataTypeFormatter(){
       def field = RemotableAttributeField.Builder.create("integer")
       field.shortLabel = "s"
       field.dataType = DataType.INTEGER
       field.maxLength = 10
       Column col = FieldUtils.constructColumnFromAttributeField(field.build())
       assertEquals(IntegerFormatter.class,col.getFormatter().getClass())
   }

     @Test
   void testRemotableAttributeFieldFormatterDoesNotExist(){
       def field = RemotableAttributeField.Builder.create("integer")
       field.shortLabel = "s"
       field.dataType = DataType.INTEGER
       field.maxLength = 10
       field.formatterName = "xyz"
       Column col = FieldUtils.constructColumnFromAttributeField(field.build())
       assertEquals(IntegerFormatter.class,col.getFormatter().getClass())
   }
    @Test
    void testConvertRemotableAttributeFields() {
        def fields = [ ] as ArrayList<RemotableAttributeField>
        def field = RemotableAttributeField.Builder.create("string")
        field.shortLabel = "s"
        field.dataType = DataType.STRING
        field.maxLength = 10

        fields << field.build()

        field = RemotableAttributeField.Builder.create("boolean")
        field.shortLabel = "b"
        field.dataType = DataType.BOOLEAN
        field.defaultValues = [ "true" ] as Collection<String>
        field.control = RemotableCheckboxGroup.Builder.create(["2be": "To Be or Not To Be"])

        fields << field.build()

        field = RemotableAttributeField.Builder.create("long")
        field.shortLabel = "k"
        field.dataType = DataType.LONG
        field.defaultValues = [ "1" ] as Collection<String>
        field.control = RemotableSelect.Builder.create(["one" : "1", "two" : "2"]);

        fields << field.build()

        field = RemotableAttributeField.Builder.create("date")
        field.shortLabel = "d"
        field.dataType = DataType.DATE
        def input = RemotableTextInput.Builder.create()
        input.size = 20
        field.control = input

        fields << field.build()

        List<Row> rows = FieldUtils.convertRemotableAttributeFields(fields)

        //displayRows(rows)

        assertConvertedRows(rows, fields)
    }

    protected assertConvertedRows(List<Row> rows, List<RemotableAttributeField> rafs) {
        assertEquals(rows.size(), rafs.size())
        rows.eachWithIndex {
            it, i ->
            def f = it.fields[0]
            assertEquals(rafs[i].name, f.propertyName)
            assertEquals(rafs[i].shortLabel, f.fieldLabel)
            assertEquals(rafs[i].dataType.name().toLowerCase(), f.fieldDataType)
            switch (rafs[i].dataType) {
                case DataType.STRING:
                    assertEquals(Field.TEXT, f.fieldType)
                    break
                case DataType.BOOLEAN:
                    assertEquals(Field.CHECKBOX, f.fieldType)
                    break
                case DataType.LONG:
                    assertEquals(Field.DROPDOWN, f.fieldType)
                    break
                case DataType.DATE:
                    assertEquals(Field.TEXT, f.fieldType)
                    break
            }
        }
    }

    @Test
    void testPopulateFieldsFromBusinessObject() {
        OnionBo onionBo = new OnionBo();

        Field field = new Field("layer.layer.value", "value");
        field.fieldType = Field.CONTAINER;

        FieldUtils.populateFieldsFromBusinessObject(Collections.singletonList(field), onionBo);
    }

    public class OnionBo implements BusinessObject {

        def String value = "foo";
        def OnionBo layer = null;

        void refresh() {}
    }

//    protected displayRows(List<Row> rows) {
//        rows.each {
//            it.fields.each {
//                println it.propertyName
//                println it.fieldLabel
//                println it.fieldType
//            }
//        }
//    }
}
