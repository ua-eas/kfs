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
package org.kuali.kfs.krad.uif.field;

import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinderFactory;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.view.View;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * tests InputField object and methods
 **/
public class InputFieldTest {

    View view = null;
    TestModel model = null;
    KeyValuesFinder optionsFinder = null;
    BindingInfo bindingInfo = null;


    @Before
    public void setUp() {
        view = Mockito.mock(View.class);
        optionsFinder = Mockito.mock(KeyValuesFinder.class);
        bindingInfo = Mockito.mock(BindingInfo.class);
        model = new TestModel();
    }

    @Test
    public void testPerformFinalizeWithNonStringFieldOptions() throws Exception {
        // setup options finder
        Map<String, String> map = new HashMap<String, String>();
        map.put("testInteger", "1");
        optionsFinder = KeyValuesFinderFactory.fromMap(map);

        // setup preconditions (view status is final; bindinginfo return testInteger)
        when(view.getViewStatus()).thenReturn(UifConstants.ViewStatus.FINAL);
        when(bindingInfo.getBindingPath()).thenReturn("testInteger");

        // setup input field with binding info and readonly
        InputField testObj = new InputField();
        testObj.setBindingInfo(bindingInfo);
        testObj.setReadOnly(true);
        testObj.setOptionsFinder(optionsFinder);

        testObj.performFinalize(view, model, testObj);

    }

    // Simple model object to return testInteger integer
    private class TestModel {
        public int getTestInteger() {
            return 1;
        }
    }

}
