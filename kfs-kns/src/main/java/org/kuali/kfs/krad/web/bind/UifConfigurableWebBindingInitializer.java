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
package org.kuali.kfs.krad.web.bind;

import org.kuali.rice.core.api.util.type.AbstractKualiDecimal;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * Registers standard PropertyEditors used in binding for all http requests.
 *
 * 
 */
public class UifConfigurableWebBindingInitializer extends ConfigurableWebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder binder, WebRequest request) {
        super.initBinder(binder, request);

        binder.registerCustomEditor(KualiDecimal.class, new UifCurrencyEditor());
        binder.registerCustomEditor(KualiInteger.class, new UifKualiIntegerCurrencyEditor());

        binder.registerCustomEditor(KualiPercent.class, new UifPercentageEditor());

        binder.registerCustomEditor(java.sql.Date.class, new UifDateEditor());
        binder.registerCustomEditor(java.util.Date.class, new UifDateEditor());
        binder.registerCustomEditor(Timestamp.class, new UifTimestampEditor());

        // TODO do we need this since we are switching to spring tags
        binder.registerCustomEditor(boolean.class, new UifBooleanEditor());
        binder.registerCustomEditor(Boolean.class, new UifBooleanEditor());
        binder.registerCustomEditor(Boolean.TYPE, new UifBooleanEditor());

        // Use the spring custom number editor for Big decimals
        DecimalFormat bigIntFormatter = new DecimalFormat();
        bigIntFormatter.setMaximumFractionDigits(340);
        binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, bigIntFormatter, true));
        binder.registerCustomEditor(AbstractKualiDecimal.class, new CustomNumberEditor(AbstractKualiDecimal.class,
                bigIntFormatter, true));

        // Use the spring StringTrimmerEditor editor for Strings
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));

        // Use the StringArrayPropertyEditor for string arrays with "," as the
        // separator
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(",", false));
    }
}
