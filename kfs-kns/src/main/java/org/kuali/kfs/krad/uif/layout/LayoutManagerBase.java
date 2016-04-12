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
package org.kuali.kfs.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.uif.container.Container;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ConfigurableBase;
import org.kuali.kfs.krad.uif.component.PropertyReplacer;
import org.kuali.kfs.krad.uif.component.ReferenceCopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base class for all layout managers
 * 
 * <p>
 * Provides general properties of all layout managers, such as the unique id,
 * rendering template, and style settings
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LayoutManagerBase extends ConfigurableBase implements LayoutManager {
	private static final long serialVersionUID = -2657663560459456814L;

	private String id;
	private String template;
	private String style;

	private List<String> styleClasses;

	@ReferenceCopy(newCollectionInstance=true)
	private Map<String, Object> context;

	private List<PropertyReplacer> propertyReplacers;

	public LayoutManagerBase() {
        super();

		styleClasses = new ArrayList<String>();
		context = new HashMap<String, Object>();
		propertyReplacers = new ArrayList<PropertyReplacer>();
	}

	/**
	 * @see LayoutManager#performInitialization(View,
	 *      java.lang.Object, Container)
	 */
	public void performInitialization(View view, Object model, Container container) {
		// set id of layout manager from container
		if (StringUtils.isBlank(id)) {
			id = container.getId() + "_layout";
		}
	}

	/**
	 * @see LayoutManager#performApplyModel(View,
	 *      java.lang.Object, Container)
	 */
	public void performApplyModel(View view, Object model, Container container) {

	}

	/**
	 * @see LayoutManager#performFinalize(View,
	 *      java.lang.Object, Container)
	 */
	public void performFinalize(View view, Object model, Container container) {

	}

	/**
	 * Set of property names for the layout manager base for which on the
	 * property value reference should be copied. Subclasses can override this
	 * but should include a call to super
	 * 
	 * @see LayoutManager.
	 *      getPropertiesForReferenceCopy()
	 */
	public Set<String> getPropertiesForReferenceCopy() {
		Set<String> refCopyProperties = new HashSet<String>();

		refCopyProperties.add(UifPropertyPaths.CONTEXT);

		return refCopyProperties;
	}

	/**
	 * Default Impl
	 * 
	 * @see LayoutManager#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return Container.class;
	}

	/**
	 * @see LayoutManager#getComponentsForLifecycle()
	 */
	public List<Component> getComponentsForLifecycle() {
		return new ArrayList<Component>();
	}

    /**
     * @see LayoutManager#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        return components;
    }

	/**
	 * @see LayoutManager#getId()
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @see LayoutManager#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see LayoutManager#getTemplate()
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * @see LayoutManager#setTemplate(java.lang.String)
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @see LayoutManager#getStyle()
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * @see LayoutManager#setStyle(java.lang.String)
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @see LayoutManager#getStyleClasses()
	 */
	public List<String> getStyleClasses() {
		return this.styleClasses;
	}

	/**
	 * @see LayoutManager#setStyleClasses(java.util.List)
	 */
	public void setStyleClasses(List<String> styleClasses) {
		this.styleClasses = styleClasses;
	}

	/**
	 * Builds the HTML class attribute string by combining the styleClasses list
	 * with a space delimiter
	 * 
	 * @return String class attribute string
	 */
	public String getStyleClassesAsString() {
		if (styleClasses != null) {
			return StringUtils.join(styleClasses, " ");
		}

		return "";
	}

	/**
	 * Sets the styleClasses list from the given string that has the classes
	 * delimited by space. This is a convenience for configuration. If a child
	 * bean needs to inherit the classes from the parent, it should configure as
	 * a list and use merge="true"
	 * 
	 * @param styleClasses
	 */
	public void setStyleClasses(String styleClasses) {
		String[] classes = StringUtils.split(styleClasses);
		this.styleClasses = Arrays.asList(classes);
	}

	/**
	 * This method adds a single style to the list of css style classes on this layoutManager
	 * 
	 * @param style
	 */
	@Override
	public void addStyleClass(String styleClass){
		if(!styleClasses.contains(styleClass)){
			styleClasses.add(styleClass);
		}
	}

	/**
	 * @see LayoutManager#getContext()
	 */
	public Map<String, Object> getContext() {
		return this.context;
	}

	/**
	 * @see LayoutManager#setContext(java.util.Map)
	 */
	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	/**
	 * @see LayoutManager#pushObjectToContext(java.lang.String,
	 *      java.lang.Object)
	 */
	public void pushObjectToContext(String objectName, Object object) {
		if (this.context == null) {
			this.context = new HashMap<String, Object>();
		}

		this.context.put(objectName, object);
	}

	/**
	 * @see LayoutManager#getPropertyReplacers()
	 */
	public List<PropertyReplacer> getPropertyReplacers() {
		return this.propertyReplacers;
	}

	/**
	 * @see LayoutManager#setPropertyReplacers(java.util.List)
	 */
	public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
		this.propertyReplacers = propertyReplacers;
	}

}
