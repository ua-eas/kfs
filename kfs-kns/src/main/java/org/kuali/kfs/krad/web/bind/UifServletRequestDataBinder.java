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
package org.kuali.kfs.krad.web.bind;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.uif.service.ViewService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Override of ServletRequestDataBinder in order to hook in the UifBeanPropertyBindingResult
 * which instantiates a custom BeanWrapperImpl.
 *
 *
 */
public class UifServletRequestDataBinder extends ServletRequestDataBinder {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            UifServletRequestDataBinder.class);

    private UifBeanPropertyBindingResult bindingResult;
    private ConversionService conversionService;

    public UifServletRequestDataBinder(Object target) {
        super(target);
        setBindingErrorProcessor(new UifBindingErrorProcessor());
    }

    public UifServletRequestDataBinder(Object target, String name) {
        super(target, name);
        setBindingErrorProcessor(new UifBindingErrorProcessor());
    }

    /**
     * Allows for a custom binding result class.
     *
     * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
     */
    @Override
    public void initBeanPropertyAccess() {
        Assert.state(this.bindingResult == null,
                "DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
        this.bindingResult = new UifBeanPropertyBindingResult(getTarget(), getObjectName(), isAutoGrowNestedPaths(),
                getAutoGrowCollectionLimit());
        if (this.conversionService != null) {
            this.bindingResult.initConversion(this.conversionService);
        }
    }

    /**
     * Allows for the setting attributes to use to find the data dictionary data from Kuali
     *
     * @see org.springframework.validation.DataBinder#getInternalBindingResult()
     */
    @Override
    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.bindingResult == null) {
            initBeanPropertyAccess();
        }
        return this.bindingResult;
    }

    /**
     * Disallows direct field access for Kuali
     *
     * @see org.springframework.validation.DataBinder#initDirectFieldAccess()
     */
    @Override
    public void initDirectFieldAccess() {
        LOG.error("Direct Field access is not allowed in UifServletRequestDataBinder.");
        throw new RuntimeException("Direct Field access is not allowed in Kuali");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bind(ServletRequest request) {
        super.bind(request);

        UifFormBase form = (UifFormBase) this.getTarget();

        // check for request param that indicates to skip view initialize
        Boolean skipViewInit = KRADUtils.getRequestParameterAsBoolean(request, UifParameters.SKIP_VIEW_INIT);
        if ((skipViewInit == null) || !skipViewInit.booleanValue()) {
            // initialize new view for request
            View view = null;

            String viewId = request.getParameter(UifParameters.VIEW_ID);
            if (viewId != null) {
                view = getViewService().getViewById(viewId);
            } else {
                // attempt to get view instance by type parameters
                ViewType viewType = null;

                String viewTypeName = request.getParameter(UifParameters.VIEW_TYPE_NAME);
                viewType = StringUtils.isBlank(viewTypeName) ? form.getViewTypeName() : ViewType.valueOf(viewTypeName);

                if (viewType != null) {
                    Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(
                            request.getParameterMap());
                    view = getViewService().getViewByType(viewType, parameterMap);
                }

                // if view not found attempt to find one based on the cached form
                if (view == null) {
                    view = getViewFromPreviousModel(form);

                    if (view != null) {
                        LOG.warn("Obtained viewId from cached form, this may not be safe!");
                    }
                }
            }

            if (view != null) {
                form.setViewId(view.getId());
                form.setView(view);
            } else {
                form.setViewId(null);
                form.setView(null);
            }
        }

        form.postBind((HttpServletRequest) request);

        // add form to manager
        GlobalVariables.getUifFormManager().addForm(form);
    }

    protected View getViewFromPreviousModel(UifFormBase form) {
        // maybe we have a view id from the session form
        if (form.getViewId() != null) {
            return getViewService().getViewById(form.getViewId());
        }

        return null;
    }

    public ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

}
