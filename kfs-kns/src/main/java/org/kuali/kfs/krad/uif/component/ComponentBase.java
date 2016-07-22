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
package org.kuali.kfs.krad.uif.component;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.modifier.ComponentModifier;
import org.kuali.kfs.krad.uif.CssConstants;
import org.kuali.kfs.krad.uif.UifConstants.ViewStatus;
import org.kuali.kfs.krad.uif.control.ControlBase;
import org.kuali.kfs.krad.uif.util.ExpressionUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base implementation of <code>Component</code> which other component
 * implementations should extend
 *
 * <p>
 * Provides base component properties such as id and template. Also provides
 * default implementation for the <code>ScriptEventSupport</code> and
 * <code>Ordered</code> interfaces. By default no script events except the
 * onDocumentReady are supported.
 * </p>
 *
 * 
 */
public abstract class ComponentBase extends ConfigurableBase implements Component {
    private static final long serialVersionUID = -4449335748129894350L;

    private String id;
    private String factoryId;
    private String template;
    private String title;

    private boolean render;
    private boolean refresh;

    @KeepExpression
    private String progressiveRender;
    private boolean progressiveRenderViaAJAX;
    private boolean progressiveRenderAndRefresh;
    private List<String> progressiveDisclosureControlNames;
    private String progressiveDisclosureConditionJs;

    @KeepExpression
    private String conditionalRefresh;
    private String conditionalRefreshConditionJs;
    private List<String> conditionalRefreshControlNames;

    private String refreshWhenChanged;
    private List<String> refreshWhenChangedControlNames;
    private boolean refreshedByAction;

    private boolean resetDataOnRefresh;
    private String refreshDiscloseMethodToCall;

    private boolean hidden;
    private boolean readOnly;
    private Boolean required;

    private String align;
    private String valign;
    private String width;

    private int colSpan;
    private int rowSpan;

    private String style;
    private List<String> styleClasses;

    private int order;

    private boolean skipInTabOrder;

    private String finalizeMethodToCall;
    private List<Object> finalizeMethodAdditionalArguments;
    private MethodInvokerConfig finalizeMethodInvoker;
    private boolean selfRendered;
    private String renderOutput;

    private boolean persistInSession;

    private ComponentSecurity componentSecurity;

    private String onLoadScript;
    private String onUnloadScript;
    private String onCloseScript;
    private String onBlurScript;
    private String onChangeScript;
    private String onClickScript;
    private String onDblClickScript;
    private String onFocusScript;
    private String onSubmitScript;
    private String onKeyPressScript;
    private String onKeyUpScript;
    private String onKeyDownScript;
    private String onMouseOverScript;
    private String onMouseOutScript;
    private String onMouseUpScript;
    private String onMouseDownScript;
    private String onMouseMoveScript;
    private String onDocumentReadyScript;

    private List<ComponentModifier> componentModifiers;

    private Map<String, String> componentOptions;
    private String componentOptionsJSString;

    @ReferenceCopy(newCollectionInstance = true)
    private transient Map<String, Object> context;

    private List<PropertyReplacer> propertyReplacers;

    public ComponentBase() {
        super();

        order = 0;
        colSpan = 1;
        rowSpan = 1;

        render = true;
        selfRendered = false;
        progressiveRenderViaAJAX = false;
        progressiveRenderAndRefresh = false;
        refreshedByAction = false;
        resetDataOnRefresh = false;
        persistInSession = false;

        componentSecurity = ObjectUtils.newInstance(getComponentSecurityClass());

        finalizeMethodAdditionalArguments = new ArrayList<Object>();
        styleClasses = new ArrayList<String>();
        componentModifiers = new ArrayList<ComponentModifier>();
        componentOptions = new HashMap<String, String>();
        context = new HashMap<String, Object>();
        propertyReplacers = new ArrayList<PropertyReplacer>();
    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li></li>
     * </ul>
     *
     * @see Component#performInitialization(View, java.lang.Object)
     */
    public void performInitialization(View view, Object model) {

    }

    /**
     * The following updates are done here:
     *
     * <ul>
     * <li>Evaluate the progressive render condition (if set) and combine with the current render status to set the
     * render status</li>
     * </ul>
     *
     * @see Component#performApplyModel(View, java.lang.Object, Component)
     */
    public void performApplyModel(View view, Object model, Component parent) {
        if (StringUtils.isNotEmpty(progressiveRender)) {
            // progressive anded with conditional render, will not render at least one of the two are false
            Boolean progRenderEval = (Boolean) KRADServiceLocatorWeb.getExpressionEvaluatorService().evaluateExpression(
                    model, context, progressiveRender);

            this.setRender(progRenderEval && this.render);
        }
    }

    /**
     * The following finalization is done here:
     *
     * <ul>
     * <li>progressiveRender and conditionalRefresh variables are processed if set</li>
     * <li>If any of the style properties were given, sets the style string on
     * the style property</li>
     * <li>Set the skipInTabOrder flag for nested components</li>
     * </ul>
     *
     * @see Component#performFinalize(View, java.lang.Object, Component)
     */
    public void performFinalize(View view, Object model, Component parent) {
        if (StringUtils.isNotEmpty(progressiveRender)) {
            progressiveRender = ExpressionUtils.replaceBindingPrefixes(view, this, progressiveRender);
            progressiveDisclosureControlNames = new ArrayList<String>();
            progressiveDisclosureConditionJs = ExpressionUtils.parseExpression(progressiveRender,
                    progressiveDisclosureControlNames);
        }

        if (StringUtils.isNotEmpty(conditionalRefresh)) {
            conditionalRefresh = ExpressionUtils.replaceBindingPrefixes(view, this, conditionalRefresh);
            conditionalRefreshControlNames = new ArrayList<String>();
            conditionalRefreshConditionJs = ExpressionUtils.parseExpression(conditionalRefresh,
                    conditionalRefreshControlNames);
        }

        if (StringUtils.isNotEmpty(refreshWhenChanged)) {
            refreshWhenChanged = ExpressionUtils.replaceBindingPrefixes(view, this, refreshWhenChanged);
            refreshWhenChangedControlNames = new ArrayList<String>();
            String[] names = StringUtils.split(refreshWhenChanged, ",");
            for (String name : names) {
                refreshWhenChangedControlNames.add(name.trim());
            }
        }

        // TODO: does this check on final status need to be here?
        if (!ViewStatus.FINAL.equals(view.getViewStatus())) {
            // add the align, valign, and width settings to style
            if (StringUtils.isNotBlank(getAlign()) && !StringUtils.contains(getStyle(), CssConstants.TEXT_ALIGN)) {
                appendToStyle(CssConstants.TEXT_ALIGN + getAlign() + ";");
            }

            if (StringUtils.isNotBlank(getValign()) && !StringUtils.contains(getStyle(), CssConstants.VERTICAL_ALIGN)) {
                appendToStyle(CssConstants.VERTICAL_ALIGN + getValign() + ";");
            }

            if (StringUtils.isNotBlank(getWidth()) && !StringUtils.contains(getStyle(), CssConstants.WIDTH)) {
                appendToStyle(CssConstants.WIDTH + getWidth() + ";");
            }
        }

        // Set the skipInTabOrder flag on all nested components
        // Set the tabIndex on controls to -1 in order to be skipped on tabbing
        for (Component component : getComponentsForLifecycle()) {
            if (component != null && component instanceof ComponentBase && skipInTabOrder) {
                ((ComponentBase) component).setSkipInTabOrder(skipInTabOrder);
                if (component instanceof ControlBase) {
                    ((ControlBase) component).setTabIndex(-1);
                }
            }
        }
    }

    /**
     * @see Component#getComponentsForLifecycle()
     */
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = new ArrayList<Component>();

        return components;
    }

    /**
     * @see Component#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        for (ComponentModifier modifier : componentModifiers) {
            components.addAll(modifier.getComponentPrototypes());
        }

        components.addAll(getPropertyReplacerComponents());

        return components;
    }

    /**
     * Returns list of components that are being held in property replacers configured for this component
     *
     * @return List<Component>
     */
    public List<Component> getPropertyReplacerComponents() {
        List<Component> components = new ArrayList<Component>();
        for (Object replacer : propertyReplacers) {
            components.addAll(((PropertyReplacer) replacer).getNestedComponents());
        }

        return components;
    }

    /**
     * @see Component#getId()
     */
    public String getId() {
        return this.id;
    }

    /**
     * @see Component#setId(java.lang.String)
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @see Component#getFactoryId()
     */
    public String getFactoryId() {
        return this.factoryId;
    }

    /**
     * @see Component#setFactoryId(java.lang.String)
     */
    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * @see Component#getTemplate()
     */
    public String getTemplate() {
        return this.template;
    }

    /**
     * @see Component#setTemplate(java.lang.String)
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @see Component#getTitle()
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @see Component#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @see Component#isHidden()
     */
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * @see Component#setHidden(boolean)
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @see Component#isReadOnly()
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * @see Component#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @see Component#getRequired()
     */
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * @see Component#setRequired(java.lang.Boolean)
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @see Component#isRender()
     */
    public boolean isRender() {
        return this.render;
    }

    /**
     * @see Component#setRender(boolean)
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @see Component#getColSpan()
     */
    public int getColSpan() {
        return this.colSpan;
    }

    /**
     * @see Component#setColSpan(int)
     */
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    /**
     * @see Component#getRowSpan()
     */
    public int getRowSpan() {
        return this.rowSpan;
    }

    /**
     * @see Component#setRowSpan(int)
     */
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    /**
     * Horizontal alignment of the component within its container
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies how the component
     * should be aligned horizontally within the container. During the finalize
     * phase the CSS text-align style will be created for the align setting.
     * </p>
     *
     * @return String horizontal align
     * @see CssConstants.TextAligns
     */
    public String getAlign() {
        return this.align;
    }

    /**
     * Sets the components horizontal alignment
     *
     * @param align
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * Vertical alignment of the component within its container
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies how the component
     * should be aligned vertically within the container. During the finalize
     * phase the CSS vertical-align style will be created for the valign
     * setting.
     * </p>
     *
     * @return String vertical align
     * @see CssConstants.VerticalAligns
     */
    public String getValign() {
        return this.valign;
    }

    /**
     * Setter for the component's vertical align
     *
     * @param valign
     */
    public void setValign(String valign) {
        this.valign = valign;
    }

    /**
     * Width the component should take up in the container
     * <p>
     * All components belong to a <code>Container</code> and are placed using a
     * <code>LayoutManager</code>. This property specifies a width the component
     * should take up in the Container. This is not applicable for all layout
     * managers. During the finalize phase the CSS width style will be created
     * for the width setting.
     * </p>
     * <p>
     * e.g. '30%', '55px'
     * </p>
     *
     * @return String width string
     */
    public String getWidth() {
        return this.width;
    }

    /**
     * Setter for the components width
     *
     * @param width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @see Component#getStyle()
     */
    public String getStyle() {
        return this.style;
    }

    /**
     * @see Component#setStyle(java.lang.String)
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @see Component#getStyleClasses()
     */
    public List<String> getStyleClasses() {
        return this.styleClasses;
    }

    /**
     * @see Component#setStyleClasses(java.util.List)
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
     * @see Component#addStyleClass(java.lang.String)
     */
    public void addStyleClass(String styleClass) {
        if (!styleClasses.contains(styleClass)) {
            styleClasses.add(styleClass);
        }
    }

    /**
     * @see Component#appendToStyle(java.lang.String)
     */
    public void appendToStyle(String styleRules) {
        if (style == null) {
            style = "";
        }
        style = style + styleRules;
    }

    /**
     * @see Component#getFinalizeMethodToCall()
     */
    public String getFinalizeMethodToCall() {
        return this.finalizeMethodToCall;
    }

    /**
     * Setter for the finalize method
     *
     * @param finalizeMethodToCall
     */
    public void setFinalizeMethodToCall(String finalizeMethodToCall) {
        this.finalizeMethodToCall = finalizeMethodToCall;
    }

    /**
     * @see Component#getFinalizeMethodAdditionalArguments()
     */
    public List<Object> getFinalizeMethodAdditionalArguments() {
        return finalizeMethodAdditionalArguments;
    }

    /**
     * Setter for the finalize additional arguments list
     *
     * @param finalizeMethodAdditionalArguments
     */
    public void setFinalizeMethodAdditionalArguments(List<Object> finalizeMethodAdditionalArguments) {
        this.finalizeMethodAdditionalArguments = finalizeMethodAdditionalArguments;
    }

    /**
     * @see Component#getFinalizeMethodInvoker()
     */
    public MethodInvokerConfig getFinalizeMethodInvoker() {
        return this.finalizeMethodInvoker;
    }

    /**
     * Setter for the method invoker instance
     *
     * @param finalizeMethodInvoker
     */
    public void setFinalizeMethodInvoker(MethodInvokerConfig finalizeMethodInvoker) {
        this.finalizeMethodInvoker = finalizeMethodInvoker;
    }

    /**
     * @see Component#isSelfRendered()
     */
    public boolean isSelfRendered() {
        return this.selfRendered;
    }

    /**
     * @see Component#setSelfRendered(boolean)
     */
    public void setSelfRendered(boolean selfRendered) {
        this.selfRendered = selfRendered;
    }

    /**
     * @see Component#getRenderOutput()
     */
    public String getRenderOutput() {
        return this.renderOutput;
    }

    /**
     * @see Component#setRenderOutput(java.lang.String)
     */
    public void setRenderOutput(String renderOutput) {
        this.renderOutput = renderOutput;
    }

    /**
     * @see Component#isPersistInSession()
     */
    public boolean isPersistInSession() {
        return persistInSession;
    }

    /**
     * @see Component#setPersistInSession(boolean)
     */
    public void setPersistInSession(boolean persistInSession) {
        this.persistInSession = persistInSession;
    }

    /**
     * @see Component#getComponentSecurity()
     */
    public ComponentSecurity getComponentSecurity() {
        return componentSecurity;
    }

    /**
     * @see Component#setComponentSecurity(ComponentSecurity)
     */
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        this.componentSecurity = componentSecurity;
    }

    /**
     * Returns the security class that is associated with the component (used for initialization and validation)
     *
     * @return Class<? extends ComponentSecurity>
     */
    protected Class<? extends ComponentSecurity> getComponentSecurityClass() {
        return ComponentSecurity.class;
    }

    /**
     * @see Component#getComponentModifiers()
     */
    public List<ComponentModifier> getComponentModifiers() {
        return this.componentModifiers;
    }

    /**
     * @see Component#setComponentModifiers(java.util.List)
     */
    public void setComponentModifiers(List<ComponentModifier> componentModifiers) {
        this.componentModifiers = componentModifiers;
    }

    /**
     * @see Component#getContext()
     */
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * @see Component#setContext(java.util.Map)
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * @see Component#pushObjectToContext(java.lang.String,
     *      java.lang.Object)
     */
    public void pushObjectToContext(String objectName, Object object) {
        if (this.context == null) {
            this.context = new HashMap<String, Object>();
        }
        pushToPropertyReplacerContext(objectName, object);
        this.context.put(objectName, object);
    }

    /*
    * Adds the object to the context of the components in the
    * PropertyReplacer object. Only checks for a list, map or component.
    */
    protected void pushToPropertyReplacerContext(String objectName, Object object) {
        for (Component replacerComponent : getPropertyReplacerComponents()) {
            replacerComponent.pushObjectToContext(objectName, object);
        }
    }

    /**
     * @see ComponentBase#pushAllToContext
     */
    public void pushAllToContext(Map<String, Object> objects) {
        if (objects != null) {
            for (Map.Entry<String, Object> objectEntry : objects.entrySet()) {
                pushObjectToContext(objectEntry.getKey(), objectEntry.getValue());
            }
        }
    }

    /**
     * @see Component#getPropertyReplacers()
     */
    public List<PropertyReplacer> getPropertyReplacers() {
        return this.propertyReplacers;
    }

    /**
     * @see Component#setPropertyReplacers(java.util.List)
     */
    public void setPropertyReplacers(List<PropertyReplacer> propertyReplacers) {
        this.propertyReplacers = propertyReplacers;
    }

    /**
     * @see org.springframework.core.Ordered#getOrder()
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * Setter for the component's order
     *
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnLoad()
     */
    public boolean getSupportsOnLoad() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnLoadScript()
     */
    public String getOnLoadScript() {
        return onLoadScript;
    }

    /**
     * Setter for the components onLoad script
     *
     * @param onLoadScript
     */
    public void setOnLoadScript(String onLoadScript) {
        this.onLoadScript = onLoadScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnDocumentReady()
     */
    public boolean getSupportsOnDocumentReady() {
        return true;
    }

    /**
     * @see ScriptEventSupport#getOnDocumentReadyScript()
     */
    public String getOnDocumentReadyScript() {
        return onDocumentReadyScript;
    }

    /**
     * Setter for the components onDocumentReady script
     *
     * @param onDocumentReadyScript
     */
    public void setOnDocumentReadyScript(String onDocumentReadyScript) {
        this.onDocumentReadyScript = onDocumentReadyScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnUnload()
     */
    public boolean getSupportsOnUnload() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnUnloadScript()
     */
    public String getOnUnloadScript() {
        return onUnloadScript;
    }

    /**
     * Setter for the components onUnload script
     *
     * @param onUnloadScript
     */
    public void setOnUnloadScript(String onUnloadScript) {
        this.onUnloadScript = onUnloadScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnClose()
     */
    public boolean getSupportsOnClose() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnCloseScript()
     */
    public String getOnCloseScript() {
        return onCloseScript;
    }

    /**
     * Setter for the components onClose script
     *
     * @param onCloseScript
     */
    public void setOnCloseScript(String onCloseScript) {
        this.onCloseScript = onCloseScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnBlur()
     */
    public boolean getSupportsOnBlur() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnBlurScript()
     */
    public String getOnBlurScript() {
        return onBlurScript;
    }

    /**
     * Setter for the components onBlur script
     *
     * @param onBlurScript
     */
    public void setOnBlurScript(String onBlurScript) {
        this.onBlurScript = onBlurScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnChange()
     */
    public boolean getSupportsOnChange() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnChangeScript()
     */
    public String getOnChangeScript() {
        return onChangeScript;
    }

    /**
     * Setter for the components onChange script
     *
     * @param onChangeScript
     */
    public void setOnChangeScript(String onChangeScript) {
        this.onChangeScript = onChangeScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnClick()
     */
    public boolean getSupportsOnClick() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnClickScript()
     */
    public String getOnClickScript() {
        return onClickScript;
    }

    /**
     * Setter for the components onClick script
     *
     * @param onClickScript
     */
    public void setOnClickScript(String onClickScript) {
        this.onClickScript = onClickScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnDblClick()
     */
    public boolean getSupportsOnDblClick() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnDblClickScript()
     */
    public String getOnDblClickScript() {
        return onDblClickScript;
    }

    /**
     * Setter for the components onDblClick script
     *
     * @param onDblClickScript
     */
    public void setOnDblClickScript(String onDblClickScript) {
        this.onDblClickScript = onDblClickScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnFocus()
     */
    public boolean getSupportsOnFocus() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnFocusScript()
     */
    public String getOnFocusScript() {
        return onFocusScript;
    }

    /**
     * Setter for the components onFocus script
     *
     * @param onFocusScript
     */
    public void setOnFocusScript(String onFocusScript) {
        this.onFocusScript = onFocusScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnSubmit()
     */
    public boolean getSupportsOnSubmit() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnSubmitScript()
     */
    public String getOnSubmitScript() {
        return onSubmitScript;
    }

    /**
     * Setter for the components onSubmit script
     *
     * @param onSubmitScript
     */
    public void setOnSubmitScript(String onSubmitScript) {
        this.onSubmitScript = onSubmitScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnKeyPress()
     */
    public boolean getSupportsOnKeyPress() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnKeyPressScript()
     */
    public String getOnKeyPressScript() {
        return onKeyPressScript;
    }

    /**
     * Setter for the components onKeyPress script
     *
     * @param onKeyPressScript
     */
    public void setOnKeyPressScript(String onKeyPressScript) {
        this.onKeyPressScript = onKeyPressScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnKeyUp()
     */
    public boolean getSupportsOnKeyUp() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnKeyUpScript()
     */
    public String getOnKeyUpScript() {
        return onKeyUpScript;
    }

    /**
     * Setter for the components onKeyUp script
     *
     * @param onKeyUpScript
     */
    public void setOnKeyUpScript(String onKeyUpScript) {
        this.onKeyUpScript = onKeyUpScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnKeyDown()
     */
    public boolean getSupportsOnKeyDown() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnKeyDownScript()
     */
    public String getOnKeyDownScript() {
        return onKeyDownScript;
    }

    /**
     * Setter for the components onKeyDown script
     *
     * @param onKeyDownScript
     */
    public void setOnKeyDownScript(String onKeyDownScript) {
        this.onKeyDownScript = onKeyDownScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnMouseOver()
     */
    public boolean getSupportsOnMouseOver() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnMouseOverScript()
     */
    public String getOnMouseOverScript() {
        return onMouseOverScript;
    }

    /**
     * Setter for the components onMouseOver script
     *
     * @param onMouseOverScript
     */
    public void setOnMouseOverScript(String onMouseOverScript) {
        this.onMouseOverScript = onMouseOverScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnMouseOut()
     */
    public boolean getSupportsOnMouseOut() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnMouseOutScript()
     */
    public String getOnMouseOutScript() {
        return onMouseOutScript;
    }

    /**
     * Setter for the components onMouseOut script
     *
     * @param onMouseOutScript
     */
    public void setOnMouseOutScript(String onMouseOutScript) {
        this.onMouseOutScript = onMouseOutScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnMouseUp()
     */
    public boolean getSupportsOnMouseUp() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnMouseUpScript()
     */
    public String getOnMouseUpScript() {
        return onMouseUpScript;
    }

    /**
     * Setter for the components onMouseUp script
     *
     * @param onMouseUpScript
     */
    public void setOnMouseUpScript(String onMouseUpScript) {
        this.onMouseUpScript = onMouseUpScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnMouseDown()
     */
    public boolean getSupportsOnMouseDown() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnMouseDownScript()
     */
    public String getOnMouseDownScript() {
        return onMouseDownScript;
    }

    /**
     * Setter for the components onMouseDown script
     *
     * @param onMouseDownScript
     */
    public void setOnMouseDownScript(String onMouseDownScript) {
        this.onMouseDownScript = onMouseDownScript;
    }

    /**
     * @see ScriptEventSupport#getSupportsOnMouseMove()
     */
    public boolean getSupportsOnMouseMove() {
        return false;
    }

    /**
     * @see ScriptEventSupport#getOnMouseMoveScript()
     */
    public String getOnMouseMoveScript() {
        return onMouseMoveScript;
    }

    /**
     * Setter for the components onMouseMove script
     *
     * @param onMouseMoveScript
     */
    public void setOnMouseMoveScript(String onMouseMoveScript) {
        this.onMouseMoveScript = onMouseMoveScript;
    }

    public Map<String, String> getComponentOptions() {
        if (componentOptions == null) {
            componentOptions = new HashMap<String, String>();
        }
        return this.componentOptions;
    }

    public void setComponentOptions(Map<String, String> componentOptions) {
        this.componentOptions = componentOptions;
    }

    /**
     * Builds a string from the underlying <code>Map</code> of component options
     * that will export that options as a JavaScript Map for use in js and
     * jQuery plugins
     *
     * @return String of widget options formatted as JS Map
     */
    @Override
    public String getComponentOptionsJSString() {
        if (componentOptionsJSString != null) {
            return componentOptionsJSString;
        }

        if (componentOptions == null) {
            componentOptions = new HashMap<String, String>();
        }
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (String optionKey : componentOptions.keySet()) {
            String optionValue = componentOptions.get(optionKey);

            if (sb.length() > 1) {
                sb.append(",");
            }

            sb.append(optionKey);
            sb.append(":");

            boolean isNumber = false;
            if (StringUtils.isNotBlank(optionValue) && (StringUtils.isNumeric(optionValue.trim().substring(0, 1))
                    || optionValue.trim().substring(0, 1).equals("-"))) {
                try {
                    Double.parseDouble(optionValue.trim());
                    isNumber = true;
                } catch (NumberFormatException e) {
                    isNumber = false;
                }
            }
            // If an option value starts with { or [, it would be a nested value
            // and it should not use quotes around it
            if (StringUtils.startsWith(optionValue, "{") || StringUtils.startsWith(optionValue, "[")) {
                sb.append(optionValue);
            }
            // need to be the base boolean value "false" is true in js - a non
            // empty string
            else if (optionValue.equalsIgnoreCase("false") || optionValue.equalsIgnoreCase("true")) {
                sb.append(optionValue);
            }
            // if it is a call back function, do not add the quotes
            else if (StringUtils.startsWith(optionValue, "function") && StringUtils.endsWith(optionValue, "}")) {
                sb.append(optionValue);
            }
            // for numerics
            else if (isNumber) {
                sb.append(optionValue);
            } else {
                sb.append("\"" + optionValue + "\"");
            }
        }

        sb.append("}");

        return sb.toString();
    }

    @Override
    public void setComponentOptionsJSString(String componentOptionsJSString) {
        this.componentOptionsJSString = componentOptionsJSString;
    }

    public String getEventCode() {
        String eventCode = "";

        return eventCode;
    }

    /**
     * When set if the condition is satisfied, the component will be displayed. The component MUST BE a
     * container or field type. progressiveRender is defined in a limited Spring EL syntax. Only valid
     * form property names, and, or, logical comparison operators (non-arithmetic), and the matches
     * clause are allowed. String and regex values must use single quotes ('), booleans must be either true or false,
     * numbers must be a valid double, either negative or positive.
     *
     * <p>
     * DO NOT use progressiveRender and a conditional refresh statement on the same component
     * unless it is known that the component will always be visible in all cases when a conditional refresh happens
     * (ie conditional refresh has progressiveRender's condition anded with its own condition).
     * </p>
     *
     * <p>
     * <b>If a component should be refreshed every time it is shown, use the progressiveRenderAndRefresh option
     * with this property instead.</b>
     * </p>
     *
     * @return String progressiveRender expression
     */
    public String getProgressiveRender() {
        return this.progressiveRender;
    }

    /**
     * @param progressiveRender the progressiveRender to set
     */
    public void setProgressiveRender(String progressiveRender) {
        this.progressiveRender = progressiveRender;
    }

    /**
     * When set if the condition is satisfied, the component will be refreshed.
     * The component MUST BE a container or field type. conditionalRefresh is
     * defined in a limited Spring EL syntax. Only valid form property names,
     * and, or, logical comparison operators (non-arithmetic), and the matches
     * clause are allowed. String and regex values must use single quotes ('),
     * booleans must be either true or false, numbers must be a valid double
     * either negative or positive. <br>
     * DO NOT use progressiveRender and conditionalRefresh on the same component
     * unless it is known that the component will always be visible in all cases
     * when a conditionalRefresh happens (ie conditionalRefresh has
     * progressiveRender's condition anded with its own condition). <b>If a
     * component should be refreshed every time it is shown, use the
     * progressiveRenderAndRefresh option with this property instead.</b>
     *
     * @return the conditionalRefresh
     */
    public String getConditionalRefresh() {
        return this.conditionalRefresh;
    }

    /**
     * @param conditionalRefresh the conditionalRefresh to set
     */
    public void setConditionalRefresh(String conditionalRefresh) {
        this.conditionalRefresh = conditionalRefresh;
    }

    /**
     * Control names used to control progressive disclosure, set internally
     * cannot be set.
     *
     * @return the progressiveDisclosureControlNames
     */
    public List<String> getProgressiveDisclosureControlNames() {
        return this.progressiveDisclosureControlNames;
    }

    /**
     * The condition to show this component progressively converted to a js
     * expression, set internally cannot be set.
     *
     * @return the progressiveDisclosureConditionJs
     */
    public String getProgressiveDisclosureConditionJs() {
        return this.progressiveDisclosureConditionJs;
    }

    /**
     * The condition to refresh this component converted to a js expression, set
     * internally cannot be set.
     *
     * @return the conditionalRefreshConditionJs
     */
    public String getConditionalRefreshConditionJs() {
        return this.conditionalRefreshConditionJs;
    }

    /**
     * Control names used to control conditional refresh, set internally cannot
     * be set.
     *
     * @return the conditionalRefreshControlNames
     */
    public List<String> getConditionalRefreshControlNames() {
        return this.conditionalRefreshControlNames;
    }

    /**
     * When progressiveRenderViaAJAX is true, this component will be retrieved
     * from the server when it first satisfies its progressive render condition.
     * After the first retrieval, it is hidden/shown in the html by the js when
     * its progressive condition result changes. <b>By default, this is false,
     * so components with progressive render capabilities will always be already
     * within the client html and toggled to be hidden or visible.</b>
     *
     * @return the progressiveRenderViaAJAX
     */
    public boolean isProgressiveRenderViaAJAX() {
        return this.progressiveRenderViaAJAX;
    }

    /**
     * @param progressiveRenderViaAJAX the progressiveRenderViaAJAX to set
     */
    public void setProgressiveRenderViaAJAX(boolean progressiveRenderViaAJAX) {
        this.progressiveRenderViaAJAX = progressiveRenderViaAJAX;
    }

    /**
     * If true, when the progressiveRender condition is satisfied, the component
     * will always be retrieved from the server and shown(as opposed to being
     * stored on the client, but hidden, after the first retrieval as is the
     * case with the progressiveRenderViaAJAX option). <b>By default, this is
     * false, so components with progressive render capabilities will always be
     * already within the client html and toggled to be hidden or visible.</b>
     *
     * @return the progressiveRenderAndRefresh
     */
    public boolean isProgressiveRenderAndRefresh() {
        return this.progressiveRenderAndRefresh;
    }

    /**
     * @param progressiveRenderAndRefresh the progressiveRenderAndRefresh to set
     */
    public void setProgressiveRenderAndRefresh(boolean progressiveRenderAndRefresh) {
        this.progressiveRenderAndRefresh = progressiveRenderAndRefresh;
    }

    /**
     * Specifies a property by name that when it value changes will
     * automatically perform a refresh on this component. This can be a comma
     * separated list of multiple properties that require this component to be
     * refreshed when any of them change. <Br>DO NOT use with progressiveRender
     * unless it is know that progressiveRender condition will always be
     * satisfied before one of these fields can be changed.
     *
     * @return the refreshWhenChanged
     */
    public String getRefreshWhenChanged() {
        return this.refreshWhenChanged;
    }

    /**
     * @param refreshWhenChanged the refreshWhenChanged to set
     */
    public void setRefreshWhenChanged(String refreshWhenChanged) {
        this.refreshWhenChanged = refreshWhenChanged;
    }

    /**
     * Control names which will refresh this component when they are changed, added
     * internally
     *
     * @return the refreshWhenChangedControlNames
     */
    public List<String> getRefreshWhenChangedControlNames() {
        return this.refreshWhenChangedControlNames;
    }

    /**
     * @see Component#isRefreshedByAction()
     */
    public boolean isRefreshedByAction() {
        return refreshedByAction;
    }

    /**
     * @see Component#setRefreshedByAction(boolean)
     */
    public void setRefreshedByAction(boolean refreshedByAction) {
        this.refreshedByAction = refreshedByAction;
    }

    /**
     * @see Component#isResetDataOnRefresh()
     */
    public boolean isResetDataOnRefresh() {
        return resetDataOnRefresh;
    }

    /**
     * @see Component#setResetDataOnRefresh(boolean)
     */
    public void setResetDataOnRefresh(boolean resetDataOnRefresh) {
        this.resetDataOnRefresh = resetDataOnRefresh;
    }

    /**
     * @return the refresh
     */
    public boolean isRefresh() {
        return this.refresh;
    }

    /**
     * @param refresh the refresh to set
     */
    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * Name of a method on the controller that should be invoked as part of the component refresh and disclosure process
     *
     * <p>
     * During the component refresh or disclosure process it might be necessary to perform other operations, such as
     * preparing data or executing a business process. This allows the configuration of a method on the underlying
     * controller that should be called for the component refresh action. In this method, the necessary logic can be
     * performed and then the base component update method invoked to carry out the component refresh.
     * </p>
     *
     * <p>
     * Controller method to invoke must accept the form, binding result, request, and response arguments
     * </p>
     *
     * @return String valid controller method name
     */
    public String getRefreshDiscloseMethodToCall() {
        return refreshDiscloseMethodToCall;
    }

    /**
     * Setter for the controller method to call for a refresh or disclosure action on this component
     *
     * @param refreshDiscloseMethodToCall
     */
    public void setRefreshDiscloseMethodToCall(String refreshDiscloseMethodToCall) {
        this.refreshDiscloseMethodToCall = refreshDiscloseMethodToCall;
    }

    /**
     * @param skipInTabOrder flag
     */
    public void setSkipInTabOrder(boolean skipInTabOrder) {
        this.skipInTabOrder = skipInTabOrder;
    }

    /**
     * Flag indicating that this component and its nested components must be
     * skipped when keyboard tabbing.
     *
     * @return the skipInTabOrder flag
     */
    public boolean isSkipInTabOrder() {
        return skipInTabOrder;
    }

}
