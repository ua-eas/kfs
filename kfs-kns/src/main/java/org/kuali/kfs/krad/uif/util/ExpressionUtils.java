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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.field.DataField;
import org.kuali.kfs.krad.uif.layout.LayoutManager;
import org.kuali.kfs.krad.uif.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for UIF expressions
 */
public class ExpressionUtils {

    /**
     * Adjusts the property expressions for a given object. Any nested properties are moved to the parent
     * object. Binding adjust prefixes are replaced with the correct values.
     * <p>
     * <p>
     * The UifConstants#NO_BIND_ADJUST_PREFIX prefix will be removed
     * as this is a placeholder indicating that the property is directly on the form.
     * The UifConstants#FIELD_PATH_BIND_ADJUST_PREFIX prefix will be replaced by
     * the object's field path - this is only applicable to DataFields. The
     * UifConstants#DEFAULT_PATH_BIND_ADJUST_PREFIX prefix will be replaced
     * by the view's default path if it is set.
     * </p>
     *
     * @param view   - the parent view of the object
     * @param object - Object to adjust property expressions on
     */
    public static void adjustPropertyExpressions(View view, Object object) {
        if (object == null) {
            return;
        }

        // get the map of property expressions to adjust
        Map<String, String> propertyExpressions = new HashMap<String, String>();
        if (Component.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((Component) object).getPropertyExpressions();
        } else if (LayoutManager.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((LayoutManager) object).getPropertyExpressions();
        } else if (BindingInfo.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((BindingInfo) object).getPropertyExpressions();
        }

        Map<String, String> adjustedPropertyExpressions = new HashMap<String, String>();
        for (Map.Entry<String, String> propertyExpression : propertyExpressions.entrySet()) {
            String propertyName = propertyExpression.getKey();
            String expression = propertyExpression.getValue();

            // if property name is nested, need to move the expression to the parent object
            if (StringUtils.contains(propertyName, ".")) {
                boolean expressionMoved = moveNestedPropertyExpression(object, propertyName, expression);

                // if expression moved, skip rest of control statement so it is not added to the adjusted map
                if (expressionMoved) {
                    continue;
                }
            }

            // replace the binding prefixes
            String adjustedExpression = replaceBindingPrefixes(view, object, expression);

            adjustedPropertyExpressions.put(propertyName, adjustedExpression);
        }

        // update property expressions map on object
        ObjectPropertyUtils.setPropertyValue(object, UifPropertyPaths.PROPERTY_EXPRESSIONS,
            adjustedPropertyExpressions);
    }

    /**
     * Adjusts the property expressions for a given object
     * <p>
     * <p>
     * The UifConstants#NO_BIND_ADJUST_PREFIX prefix will be removed
     * as this is a placeholder indicating that the property is directly on the form.
     * The UifConstants#FIELD_PATH_BIND_ADJUST_PREFIX prefix will be replaced by
     * the object's field path - this is only applicable to DataFields. The
     * UifConstants#DEFAULT_PATH_BIND_ADJUST_PREFIX prefix will be replaced
     * by the view's default path if it is set.
     * </p>
     *
     * @param view       - the parent view of the object
     * @param object     - Object to adjust property expressions on
     * @param expression - The expression to adjust
     * @return the adjusted expression String
     */
    public static String replaceBindingPrefixes(View view, Object object, String expression) {
        String adjustedExpression = StringUtils.replace(expression, UifConstants.NO_BIND_ADJUST_PREFIX, "");

        // replace the field path prefix for DataFields
        if (object instanceof DataField) {

            // Get the binding path from the object
            BindingInfo bindingInfo = ((DataField) object).getBindingInfo();
            String fieldPath = bindingInfo.getBindingPath();

            // Remove the property name from the binding path
            fieldPath = StringUtils.removeEnd(fieldPath, "." + bindingInfo.getBindingName());
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.FIELD_PATH_BIND_ADJUST_PREFIX,
                fieldPath + ".");
        } else {
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.FIELD_PATH_BIND_ADJUST_PREFIX,
                "");
        }

        // replace the default path prefix if there is one set on the view
        if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.DEFAULT_PATH_BIND_ADJUST_PREFIX,
                view.getDefaultBindingObjectPath() + ".");

        } else {
            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.DEFAULT_PATH_BIND_ADJUST_PREFIX,
                "");
        }

        // replace line path binding prefix with the actual line path
        if (adjustedExpression.contains(UifConstants.LINE_PATH_BIND_ADJUST_PREFIX) && (object instanceof Component)) {
            String linePath = getLinePathPrefixValue((Component) object);

            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.LINE_PATH_BIND_ADJUST_PREFIX,
                linePath + ".");
        }

        // replace node path binding prefix with the actual node path
        if (adjustedExpression.contains(UifConstants.NODE_PATH_BIND_ADJUST_PREFIX) && (object instanceof Component)) {
            String nodePath = "";

            Map<String, Object> context = ((Component) object).getContext();
            if (context.containsKey(UifConstants.ContextVariableNames.NODE_PATH)) {
                nodePath = (String) context.get(UifConstants.ContextVariableNames.NODE_PATH);
            }

            adjustedExpression = StringUtils.replace(adjustedExpression, UifConstants.NODE_PATH_BIND_ADJUST_PREFIX,
                nodePath + ".");
        }

        return adjustedExpression;
    }

    /**
     * Determines the value for the UifConstants#LINE_PATH_BIND_ADJUST_PREFIX binding prefix
     * based on collection group found in the component context
     *
     * @param component - component instance for which the prefix is configured on
     * @return String line binding path or empty string if path not found
     */
    protected static String getLinePathPrefixValue(Component component) {
        String linePath = "";

        CollectionGroup collectionGroup = (CollectionGroup) (component.getContext().get(
            UifConstants.ContextVariableNames.COLLECTION_GROUP));
        if (collectionGroup == null) {
            return linePath;
        }

        Object indexObj = component.getContext().get(UifConstants.ContextVariableNames.INDEX);
        if (indexObj != null) {
            int index = (Integer) indexObj;
            boolean addLine = false;
            Object addLineObj = component.getContext().get(UifConstants.ContextVariableNames.IS_ADD_LINE);

            if (addLineObj != null) {
                addLine = (Boolean) addLineObj;
            }

            if (addLine) {
                linePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
            } else {
                linePath = collectionGroup.getBindingInfo().getBindingPath() + "[" + index + "]";
            }
        }

        return linePath;
    }

    /**
     * Moves any nested property expressions to the parent object
     *
     * @param object       - the object containing the expression
     * @param propertyName - the property the expression is on
     * @param expression   - the expression to move
     * @return
     */
    protected static boolean moveNestedPropertyExpression(Object object, String propertyName, String expression) {
        boolean moved = false;

        // get the parent object for the property
        String parentPropertyName = StringUtils.substringBeforeLast(propertyName, ".");
        String propertyNameInParent = StringUtils.substringAfterLast(propertyName, ".");

        Object parentObject = ObjectPropertyUtils.getPropertyValue(object, parentPropertyName);
        if ((parentObject != null) && ObjectPropertyUtils.isReadableProperty(parentObject,
            UifPropertyPaths.PROPERTY_EXPRESSIONS) && ((parentObject instanceof Component)
            || (parentObject instanceof LayoutManager)
            || (parentObject instanceof BindingInfo))) {
            Map<String, String> propertyExpressions = ObjectPropertyUtils.getPropertyValue(parentObject,
                UifPropertyPaths.PROPERTY_EXPRESSIONS);
            if (propertyExpressions == null) {
                propertyExpressions = new HashMap<String, String>();
            }

            // add expression to map on parent object
            propertyExpressions.put(propertyNameInParent, expression);
            ObjectPropertyUtils.setPropertyValue(parentObject, UifPropertyPaths.PROPERTY_EXPRESSIONS,
                propertyExpressions);
            moved = true;
        }

        return moved;
    }

    /**
     * Takes in an expression and a list to be filled in with names(property names)
     * of controls found in the expression. This method returns a js expression which can
     * be executed on the client to determine if the original exp was satisfied before
     * interacting with the server - ie, this js expression is equivalent to the one passed in.
     * <p>
     * There are limitations on the Spring expression language that can be used as this method.
     * It is only used to parse expressions which are valid case statements for determining if
     * some action/processing should be performed.  ONLY Properties, comparison operators, booleans,
     * strings, matches expression, and boolean logic are supported.  Properties must
     * be a valid property on the form, and should have a visible control within the view.
     * <p>
     * Example valid exp: account.name == 'Account Name'
     *
     * @param exp
     * @param controlNames
     * @return
     */
    public static String parseExpression(String exp, List<String> controlNames) {
        // clean up expression to ease parsing
        exp = exp.trim();
        if (exp.startsWith("@{")) {
            exp = StringUtils.removeStart(exp, "@{");
            if (exp.endsWith("}")) {
                exp = StringUtils.removeEnd(exp, "}");
            }
        }

        exp = StringUtils.replace(exp, "!=", " != ");
        exp = StringUtils.replace(exp, "==", " == ");
        exp = StringUtils.replace(exp, ">", " > ");
        exp = StringUtils.replace(exp, "<", " < ");
        exp = StringUtils.replace(exp, "<=", " <= ");
        exp = StringUtils.replace(exp, ">=", " >= ");

        String conditionJs = exp;
        String stack = "";

        boolean expectingSingleQuote = false;
        boolean ignoreNext = false;
        for (int i = 0; i < exp.length(); i++) {
            char c = exp.charAt(i);
            if (!expectingSingleQuote && !ignoreNext && (c == '(' || c == ' ' || c == ')')) {
                evaluateCurrentStack(stack.trim(), controlNames);
                //reset stack
                stack = "";
                continue;
            } else if (!ignoreNext && c == '\'') {
                stack = stack + c;
                expectingSingleQuote = !expectingSingleQuote;
            } else if (c == '\\') {
                stack = stack + c;
                ignoreNext = !ignoreNext;
            } else {
                stack = stack + c;
                ignoreNext = false;
            }
        }

        if (StringUtils.isNotEmpty(stack)) {
            evaluateCurrentStack(stack.trim(), controlNames);
        }

        conditionJs = conditionJs.replaceAll("\\s(?i:ne)\\s", " != ").replaceAll("\\s(?i:eq)\\s", " == ").replaceAll(
            "\\s(?i:gt)\\s", " > ").replaceAll("\\s(?i:lt)\\s", " < ").replaceAll("\\s(?i:lte)\\s", " <= ")
            .replaceAll("\\s(?i:gte)\\s", " >= ").replaceAll("\\s(?i:and)\\s", " && ").replaceAll("\\s(?i:or)\\s",
                " || ").replaceAll("\\s(?i:not)\\s", " != ").replaceAll("\\s(?i:null)\\s?", " '' ").replaceAll(
                "\\s?(?i:#empty)\\((.*?)\\)", "isValueEmpty($1)");

        if (conditionJs.contains("matches")) {
            conditionJs = conditionJs.replaceAll("\\s+(?i:matches)\\s+'.*'", ".match(/" + "$0" + "/) != null ");
            conditionJs = conditionJs.replaceAll("\\(/\\s+(?i:matches)\\s+'", "(/");
            conditionJs = conditionJs.replaceAll("'\\s*/\\)", "/)");
        }

        for (String propertyName : controlNames) {
            conditionJs = conditionJs.replace(propertyName, "coerceValue(\"" + propertyName + "\")");
        }

        return conditionJs;
    }

    /**
     * Used internally by parseExpression to evalute if the current stack is a property
     * name (ie, will be a control on the form)
     *
     * @param stack
     * @param controlNames
     */
    public static void evaluateCurrentStack(String stack, List<String> controlNames) {
        if (StringUtils.isNotBlank(stack)) {
            if (!(stack.equals("==")
                || stack.equals("!=")
                || stack.equals(">")
                || stack.equals("<")
                || stack.equals(">=")
                || stack.equals("<=")
                || stack.equalsIgnoreCase("ne")
                || stack.equalsIgnoreCase("eq")
                || stack.equalsIgnoreCase("gt")
                || stack.equalsIgnoreCase("lt")
                || stack.equalsIgnoreCase("lte")
                || stack.equalsIgnoreCase("gte")
                || stack.equalsIgnoreCase("matches")
                || stack.equalsIgnoreCase("null")
                || stack.equalsIgnoreCase("false")
                || stack.equalsIgnoreCase("true")
                || stack.equalsIgnoreCase("and")
                || stack.equalsIgnoreCase("or")
                || stack.contains("#empty")
                || stack.startsWith("'")
                || stack.endsWith("'"))) {

                boolean isNumber = false;
                if ((StringUtils.isNumeric(stack.substring(0, 1)) || stack.substring(0, 1).equals("-"))) {
                    try {
                        Double.parseDouble(stack);
                        isNumber = true;
                    } catch (NumberFormatException e) {
                        isNumber = false;
                    }
                }

                if (!(isNumber)) {
                    if (!controlNames.contains(stack)) {
                        controlNames.add(stack);
                    }
                }
            }
        }
    }

}
