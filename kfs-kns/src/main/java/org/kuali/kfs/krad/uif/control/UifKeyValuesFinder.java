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
package org.kuali.kfs.krad.uif.control;

import org.kuali.kfs.krad.uif.view.ViewModel;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;

import java.util.List;

/**
 * Values finder that can taken the {@link ViewModel} that provides data to the view
 * for conditionally setting the valid options
 *
 * <p>
 * Values finder also allows configuration for a blank option that will be added by the framework
 * </p>
 *
 *
 */
public interface UifKeyValuesFinder extends KeyValuesFinder {

    /**
     * Builds a list of key values representations for valid value selections using the given view model
     * to retrieve values from other fields and conditionally building the options
     *
     * @return List of KeyValue objects
     */
    public List<KeyValue> getKeyValues(ViewModel model);

    /**
     * Indicates whether a blank option should be included as a valid option
     *
     * @return boolean true if the blank option should be given, false if not
     */
    public boolean isAddBlankOption();
}
