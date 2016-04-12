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
package org.kuali.kfs.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.view.View;

/**
 * Action field that performs an Ajax request and will result in updating of the page or a component
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AjaxActionField extends ActionField {
    private static final long serialVersionUID = -2831173647391138870L;

    private String refreshId;
    private String refreshPropertyName;

    public AjaxActionField() {
        super();
    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>Add methodToCall action parameter if set and setup event code for
     * setting action parameters</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     *      java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        Component refreshComponent = null;

        // if refresh property name is given, adjust the binding and then attempt to find the
        // component in the view index
        if (StringUtils.isNotBlank(refreshPropertyName)) {
            // TODO: does this support all binding prefixes?
            if (refreshPropertyName.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
                refreshPropertyName = StringUtils.removeStart(refreshPropertyName, UifConstants.NO_BIND_ADJUST_PREFIX);
            } else if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
                refreshPropertyName = view.getDefaultBindingObjectPath() + "." + refreshPropertyName;
            }

            DataField dataField = view.getViewIndex().getDataFieldByPath(refreshPropertyName);
            if (dataField != null) {
               refreshComponent = dataField;
            }
        }
        else if (StringUtils.isNotBlank(refreshId)) {
            Component component = view.getViewIndex().getComponentById(refreshId);
            if (component != null) {
                refreshComponent = component;
            }
        }

        String actionScript = "";
        if (refreshComponent != null) {
            refreshComponent.setRefreshedByAction(true);
            // update initial state
            Component initialComponent = view.getViewIndex().getInitialComponentStates().get(
                    refreshComponent.getFactoryId());
            if (initialComponent != null) {
                initialComponent.setRefreshedByAction(true);
                view.getViewIndex().getInitialComponentStates().put(refreshComponent.getFactoryId(), initialComponent);
            }

            // refresh component for action
            actionScript = "retrieveComponent('" + refreshComponent.getId() + "','" + refreshComponent.getFactoryId()
                    + "','" + getMethodToCall() + "');";
        }
        else {
            // refresh page
            actionScript = "submitForm();";
        }

        // add action script to client JS
        if (StringUtils.isNotBlank(getClientSideJs())) {
            actionScript = getClientSideJs() + actionScript;
        }
        setClientSideJs(actionScript);

        super.performFinalize(view, model, parent);
    }

    /**
     * Id for the component that should be refreshed after the action completes
     *
     * <p>
     * Either refresh id or refresh property name can be set to configure the component that should
     * be refreshed after the action completes. If both are blank, the page will be refreshed
     * </p>
     *
     * @return String valid component id
     */
    public String getRefreshId() {
        return refreshId;
    }

    /**
     * Setter for the component refresh id
     *
     * @param refreshId
     */
    public void setRefreshId(String refreshId) {
        this.refreshId = refreshId;
    }

    /**
     * Property name for the {@link DataField} that should be refreshed after the action completes
     *
     * <p>
     * Either refresh id or refresh property name can be set to configure the component that should
     * be refreshed after the action completes. If both are blank, the page will be refreshed
     * </p>
     *
     * <p>
     * Property name will be adjusted to use the default binding path unless it contains the form prefix
     *
     * @return String valid property name with an associated DataField
     * @see UifConstants#NO_BIND_ADJUST_PREFIX
     *      </p>
     */
    public String getRefreshPropertyName() {
        return refreshPropertyName;
    }

    /**
     * Setter for the property name of the DataField that should be refreshed
     *
     * @param refreshPropertyName
     */
    public void setRefreshPropertyName(String refreshPropertyName) {
        this.refreshPropertyName = refreshPropertyName;
    }
}
