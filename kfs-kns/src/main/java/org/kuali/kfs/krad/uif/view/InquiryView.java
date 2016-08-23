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
package org.kuali.kfs.krad.uif.view;

import org.kuali.kfs.krad.uif.container.ContainerBase;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;

/**
 * Type of <code>View</code> that provides a read-only display of a record of
 * data (object instance)
 *
 * <p>
 * The <code>InquiryView</code> provides the interface for the Inquiry
 * framework. It works with the <code>Inquirable</code> service and inquiry
 * controller. The view does render a form to support the configuration of
 * actions to perform operations on the data.
 * </p>
 *
 * <p>
 * Inquiry views are primarily configured by the object class they are
 * associated with. This provides the default dictionary information for the
 * fields. If more than one inquiry view is needed for the same object class,
 * the view name can be used to further identify an unique view
 * </p>
 *
 *
 */
public class InquiryView extends FormView {
    private static final long serialVersionUID = 716926008488403616L;

    private Class<?> dataObjectClassName;

    public InquiryView() {
        super();

        setViewTypeName(ViewType.INQUIRY);
        setValidateDirty(false);
        setTranslateCodes(true);
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the abstractTypeClasses map for the inquiry object path</li>
     * </ul>
     *
     * @see ContainerBase#performInitialization(View, java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        getAbstractTypeClasses().put(getDefaultBindingObjectPath(), getDataObjectClassName());
    }

    /**
     * Class name for the object the inquiry applies to
     *
     * <p>
     * The object class name is used to pick up a dictionary entry which will
     * feed the attribute field definitions and other configuration. In addition
     * it is used to configure the <code>Inquirable</code> which will carry out
     * the inquiry action
     * </p>
     *
     * @return Class<?> inquiry object class
     */
    public Class<?> getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for the object class name
     *
     * @param dataObjectClassName
     */
    public void setDataObjectClassName(Class<?> dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

}
