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
package org.kuali.kfs.krad.web.form;


import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.inquiry.Inquirable;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.uif.view.InquiryView;

import javax.servlet.http.HttpServletRequest;

/**
 * Form class for <code>InquiryView</code> screens
 */
public class InquiryForm extends UifFormBase {
    private static final long serialVersionUID = 4733144086378429410L;

    private String dataObjectClassName;
    private Object dataObject;

    private boolean redirectedInquiry;

    public InquiryForm() {
        setViewTypeName(ViewType.INQUIRY);

        redirectedInquiry = false;
    }

    /**
     * Picks out business object name from the request to get retrieve a
     * lookupable and set properties
     */
    @Override
    public void postBind(HttpServletRequest request) {
        super.postBind(request);

        if (StringUtils.isBlank(getDataObjectClassName())) {
            setDataObjectClassName(((InquiryView) getView()).getDataObjectClassName().getName());
        }
    }

    /**
     * Class name of the data object the inquiry will display
     * <p>
     * <p>
     * Used to set the data object class for the <code>Inquirable</code> which
     * is then used to perform the inquiry query
     * </p>
     *
     * @return String class name
     */
    public String getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for the inquiry data object class name
     *
     * @param dataObjectClassName
     */
    public void setDataObjectClassName(String dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * Result data object for inquiry that will be display with the view
     *
     * @return Object object instance containing the inquiry data
     */
    public Object getDataObject() {
        return this.dataObject;
    }

    /**
     * Setter for the inquiry data object
     *
     * @param dataObject
     */
    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * Indicates whether the requested was redirected from the inquiry framework due to an external object
     * request. This prevents the framework from performing another redirect check
     *
     * @return boolean true if request was a redirect, false if not
     */
    public boolean isRedirectedInquiry() {
        return redirectedInquiry;
    }

    /**
     * Setter for the redirected request indicator
     *
     * @param redirectedInquiry
     */
    public void setRedirectedInquiry(boolean redirectedInquiry) {
        this.redirectedInquiry = redirectedInquiry;
    }

    /**
     * <code>Inquirable</code>  instance that will be used to perform
     * the inquiry
     *
     * @return Inquirable instance
     */
    public Inquirable getInquirable() {
        return (Inquirable) getView().getViewHelperService();
    }

}
