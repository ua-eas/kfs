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
package org.kuali.kfs.krad.web.bind;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.FormatException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultBindingErrorProcessor;

/**
 * This is a description of what this class does - pctsh don't forget to fill this in.
 */
public class UifBindingErrorProcessor extends DefaultBindingErrorProcessor {

    public void processPropertyAccessException(PropertyAccessException ex, BindingResult bindingResult) {
        // Create field error with the exceptions's code, e.g. "typeMismatch".
        super.processPropertyAccessException(ex, bindingResult);
        Object rejectedValue = ex.getValue();
        if (!(rejectedValue == null || rejectedValue.equals(""))) {
            if (ex.getCause() instanceof FormatException) {
                GlobalVariables.getMessageMap().putError(ex.getPropertyName(), ((FormatException) ex.getCause()).getErrorKey(),
                    new String[]{rejectedValue.toString()});
            } else {
                GlobalVariables.getMessageMap().putError(ex.getPropertyName(), RiceKeyConstants.ERROR_CUSTOM,
                    new String[]{"Invalid format"});
            }
        }
    }

}
