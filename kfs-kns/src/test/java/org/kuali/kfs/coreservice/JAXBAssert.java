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
package org.kuali.kfs.coreservice;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * A class with some assertion utilities for JAXB-related operations.
 */
public final class JAXBAssert {

    private JAXBAssert() {
    }

    /**
     * Performs the following steps and assertions:
     * <p>
     * <ol>
     * <li>Creates a new JAXBContext with the given list of classesToBeBound.</li>
     * <li>Marshals the provided objectToMarshall to XML.</li>
     * <li>Unmarshals the marshaled XML to recreate the original object.<li>
     * <li>Asserts that the newly unmarhsaled object and the original provided object are equal by invoking {@link Object#equals(Object)}.</li>
     * <li>Unmarshals the given expectedXml to an object and asserts that it is equal with the original unmarshaled object by invoking {@link Object#equals(Object)}.</li>
     * </ol>
     *
     * @param objectToMarshal  the object to marshal to XML using JAXB
     * @param expectedXml      an XML string that will be unmarshaled to an Object and compared with objectToMarshal using {@link Object#equals(Object)}
     * @param classesToBeBound - list of java classes to be recognized by the created JAXBContext
     */
    public static void assertEqualXmlMarshalUnmarshal(Object objectToMarshal, String expectedXml, Class<?>... classesToBeBound) {
        String marshaledXml = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classesToBeBound);
            Marshaller marshaller = jaxbContext.createMarshaller();

            StringWriter stringWriter = new StringWriter();

            marshaller.marshal(objectToMarshal, stringWriter);

            marshaledXml = stringWriter.toString();

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            Object actual = unmarshaller.unmarshal(new StringReader(stringWriter.toString()));
            assertEquals("Unmarshalled object should be equal to original objectToMarshall.", objectToMarshal, actual);

            if (StringUtils.isBlank(expectedXml)) {
                throw new IllegalArgumentException("Expected XML must be specified.");
            }

            Object expected = unmarshaller.unmarshal(new StringReader(expectedXml.trim()));
            assertEquals("Unmarshalled objects should be equal.", expected, actual);
        } catch (Throwable e) {
            System.err.println("Outputting marshaled XML from failed assertion:\n" + marshaledXml);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else if (e instanceof Error) {
                throw (Error) e;
            }
            throw new RuntimeException("Failed to marshall/unmarshall with JAXB.  See the nested exception for details.", e);
        }
    }

    public static void assertEqualXmlMarshalUnmarshalWithResource(Object objectToMarshal, InputStream expectedXml, Class<?>... classesToBeBound) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(expectedXml));
        StringWriter writer = new StringWriter();
        int data = -1;
        while ((data = reader.read()) != -1) {
            writer.write(data);
        }
        assertEqualXmlMarshalUnmarshal(objectToMarshal, writer.toString(), classesToBeBound);
    }


}
