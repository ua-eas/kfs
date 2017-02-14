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
package org.kuali.kfs.sys.rest.helper;

import com.google.common.collect.Lists;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.sys.rest.service.SerializationService;
import org.kuali.kfs.vnd.businessobject.VendorAddress;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(EasyMockRunner.class)
public class CollectionSerializationHelperTest {

    private final static String NAME = "defaultAddresses";
    @Mock(type = MockType.NICE)
    private SerializationService serializationServiceMock;
    @TestSubject
    private CollectionSerializationHelper cut = new CollectionSerializationHelper(NAME, VendorAddress.class,
        this.serializationServiceMock);

    @Test
    public void getTranslatedFields_givenCollectionsExist_storesCollectionsUnderProperKey() throws Exception {
        expect(this.serializationServiceMock.businessObjectFieldsToMap(Collections.emptyList()))
            .andReturn(new HashMap<>());
        replay(serializationServiceMock);

        CollectionSerializationHelper helperMock1 = mock(CollectionSerializationHelper.class);
        this.cut.addCollectionSerializationHelper(helperMock1);
        replay(helperMock1);

        CollectionSerializationHelper helperMock2 = mock(CollectionSerializationHelper.class);
        this.cut.addCollectionSerializationHelper(helperMock2);
        replay(helperMock2);

        Map<String, Object> result = this.cut.getTranslatedFields();
        assertTrue(
            result.get(SerializationService.COLLECTIONS_KEY).equals(Lists.newArrayList(helperMock1, helperMock2)));
    }

    @Test
    public void getTranslatedFields_givenNoCollectionsExist_collectionsKeyDoesNotExistInResult() throws Exception {
        expect(this.serializationServiceMock.businessObjectFieldsToMap(Collections.emptyList()))
            .andReturn(new HashMap<>());
        replay(serializationServiceMock);

        Map<String, Object> result = this.cut.getTranslatedFields();
        assertFalse(result.containsKey(SerializationService.COLLECTIONS_KEY));
    }
}