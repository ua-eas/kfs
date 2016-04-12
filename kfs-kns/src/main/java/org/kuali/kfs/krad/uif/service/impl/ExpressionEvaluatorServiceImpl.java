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
package org.kuali.kfs.krad.uif.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.Configurable;
import org.kuali.kfs.krad.uif.component.KeepExpression;
import org.kuali.kfs.krad.uif.component.PropertyReplacer;
import org.kuali.kfs.krad.uif.layout.LayoutManager;
import org.kuali.kfs.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.kfs.krad.uif.util.CloneUtils;
import org.kuali.kfs.krad.uif.util.ExpressionFunctions;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Evaluates expression language statements using the Spring EL engine TODO:
 * Look into using Rice KRMS for evaluation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExpressionEvaluatorServiceImpl implements ExpressionEvaluatorService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ExpressionEvaluatorServiceImpl.class);

    /**
     * @see ExpressionEvaluatorService#evaluateObjectExpressions(java.lang.Object,
     *      java.lang.Object, java.util.Map)
     */
    public void evaluateObjectExpressions(Object object, Object contextObject,
            Map<String, Object> evaluationParameters) {
        if ((object instanceof Component) || (object instanceof LayoutManager)) {
            evaluatePropertyReplacers(object, contextObject, evaluationParameters);
        }
        evaluatePropertyExpressions(object, contextObject, evaluationParameters);
    }

    /**
     * @see ExpressionEvaluatorService#evaluateExpressionTemplate(java.lang.Object,
     *      java.util.Map, java.lang.String)
     */
    public String evaluateExpressionTemplate(Object contextObject, Map<String, Object> evaluationParameters,
            String expressionTemplate) {
        StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
        context.setVariables(evaluationParameters);
        addCustomFunctions(context);

        ExpressionParser parser = new SpelExpressionParser();

        String result = null;
        try {
            Expression expression = null;
            if (StringUtils.contains(expressionTemplate, UifConstants.EL_PLACEHOLDER_PREFIX)) {
                expression = parser.parseExpression(expressionTemplate, new TemplateParserContext(
                        UifConstants.EL_PLACEHOLDER_PREFIX, UifConstants.EL_PLACEHOLDER_SUFFIX));
            } else {
                expression = parser.parseExpression(expressionTemplate);
            }

            result = expression.getValue(context, String.class);
        } catch (Exception e) {
            LOG.error("Exception evaluating expression: " + expressionTemplate);
            throw new RuntimeException("Exception evaluating expression: " + expressionTemplate, e);
        }

        return result;
    }

    /**
     * @see ExpressionEvaluatorService#evaluateExpression(java.lang.Object,
     *      java.util.Map, java.lang.String)
     */
    public Object evaluateExpression(Object contextObject, Map<String, Object> evaluationParameters,
            String expressionStr) {
        StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
        context.setVariables(evaluationParameters);
        addCustomFunctions(context);

        // if expression contains placeholders remove before evaluating
        if (StringUtils.startsWith(expressionStr, UifConstants.EL_PLACEHOLDER_PREFIX) && StringUtils.endsWith(
                expressionStr, UifConstants.EL_PLACEHOLDER_SUFFIX)) {
            expressionStr = StringUtils.removeStart(expressionStr, UifConstants.EL_PLACEHOLDER_PREFIX);
            expressionStr = StringUtils.removeEnd(expressionStr, UifConstants.EL_PLACEHOLDER_SUFFIX);
        }

        ExpressionParser parser = new SpelExpressionParser();
        Object result = null;
        try {
            Expression expression = parser.parseExpression(expressionStr);

            result = expression.getValue(context);
        } catch (Exception e) {
            LOG.error("Exception evaluating expression: " + expressionStr);
            throw new RuntimeException("Exception evaluating expression: " + expressionStr, e);
        }

        return result;
    }

    /**
     * Registers custom functions for el expressions with the given context
     *
     * @param context - context instance to register functions to
     */
    protected void addCustomFunctions(StandardEvaluationContext context) {
        try {
            // TODO: possibly reflect ExpressionFunctions and add automatically
            context.registerFunction("isAssignableFrom", ExpressionFunctions.class.getDeclaredMethod("isAssignableFrom",
                    new Class[]{Class.class, Class.class}));
            context.registerFunction("empty", ExpressionFunctions.class.getDeclaredMethod("empty",
                    new Class[]{Object.class}));
            context.registerFunction("getName", ExpressionFunctions.class.getDeclaredMethod("getName",
                    new Class[]{Class.class}));
            context.registerFunction("getParm", ExpressionFunctions.class.getDeclaredMethod("getParm",
                    new Class[]{String.class, String.class, String.class}));
            context.registerFunction("getParmInd", ExpressionFunctions.class.getDeclaredMethod("getParmInd",
                    new Class[]{String.class, String.class, String.class}));
            context.registerFunction("hasPerm", ExpressionFunctions.class.getDeclaredMethod("hasPerm",
                    new Class[]{String.class, String.class}));
            context.registerFunction("hasPermDtls", ExpressionFunctions.class.getDeclaredMethod("hasPermDtls",
                    new Class[]{String.class, String.class, Map.class, Map.class}));
            context.registerFunction("hasPermTmpl", ExpressionFunctions.class.getDeclaredMethod("hasPermTmpl",
                    new Class[]{String.class, String.class, Map.class, Map.class}));
        } catch (NoSuchMethodException e) {
            LOG.error("Custom function for el expressions not found: " + e.getMessage());
            throw new RuntimeException("Custom function for el expressions not found: " + e.getMessage(), e);
        }
    }

    /**
     * Iterates through any configured <code>PropertyReplacer</code> instances for the component and
     * evaluates the given condition. If the condition is met, the replacement value is set on the
     * corresponding property
     *
     * @param object - object instance with property replacers list, should be either a component or layout manager
     * @param contextObject - context for el evaluation
     * @param evaluationParameters - parameters for el evaluation
     */
    protected void evaluatePropertyReplacers(Object object, Object contextObject,
            Map<String, Object> evaluationParameters) {
        List<PropertyReplacer> replacers = null;
        if (Component.class.isAssignableFrom(object.getClass())) {
            replacers = ((Component) object).getPropertyReplacers();
        } else if (LayoutManager.class.isAssignableFrom(object.getClass())) {
            replacers = ((LayoutManager) object).getPropertyReplacers();
        }

        for (PropertyReplacer propertyReplacer : replacers) {
            String conditionEvaluation = evaluateExpressionTemplate(contextObject, evaluationParameters,
                    propertyReplacer.getCondition());
            boolean conditionSuccess = Boolean.parseBoolean(conditionEvaluation);
            if (conditionSuccess) {
                ObjectPropertyUtils.setPropertyValue(object, propertyReplacer.getPropertyName(),
                        propertyReplacer.getReplacement());
            }
        }
    }

    /**
     * Retrieves the Map from the given object that containing the property expressions that should
     * be evaluated. Each expression is then evaluated and the result is used to set the property value
     *
     * <p>
     * If the expression is an el template (part static text and part expression), only the expression
     * part will be replaced with the result. More than one expressions may be contained within the template
     * </p>
     *
     * @param object - object instance to evaluate expressions for
     * @param contextObject - object providing the default context for expressions
     * @param evaluationParameters - map of additional parameters that may be used within the expressions
     */
    protected void evaluatePropertyExpressions(Object object, Object contextObject,
            Map<String, Object> evaluationParameters) {
        Map<String, String> propertyExpressions = new HashMap<String, String>();
        if (Configurable.class.isAssignableFrom(object.getClass())) {
            propertyExpressions = ((Configurable) object).getPropertyExpressions();
        }

        for (Entry<String, String> propertyExpression : propertyExpressions.entrySet()) {
            String propertyName = propertyExpression.getKey();
            String expression = propertyExpression.getValue();

            // check whether expression should be evaluated or property should retain the expression
            if (CloneUtils.fieldHasAnnotation(object.getClass(), propertyName, KeepExpression.class)) {
                // set expression as property value to be handled by the component
                ObjectPropertyUtils.setPropertyValue(object, propertyName, expression);
                continue;
            }

            Object propertyValue = null;

            // determine whether the expression is a string template, or evaluates to another object type
            if (StringUtils.startsWith(expression, UifConstants.EL_PLACEHOLDER_PREFIX) && StringUtils.endsWith(
                    expression, UifConstants.EL_PLACEHOLDER_SUFFIX) && (StringUtils.countMatches(expression,
                    UifConstants.EL_PLACEHOLDER_PREFIX) == 1)) {
                propertyValue = evaluateExpression(contextObject, evaluationParameters, expression);
            } else {
                // treat as string template
                propertyValue = evaluateExpressionTemplate(contextObject, evaluationParameters, expression);
            }

            ObjectPropertyUtils.setPropertyValue(object, propertyName, propertyValue);
        }
    }

    /**
     * @see ExpressionEvaluatorService#containsElPlaceholder(java.lang.String)
     */
    public boolean containsElPlaceholder(String value) {
        boolean containsElPlaceholder = false;

        if (StringUtils.isNotBlank(value)) {
            String elPlaceholder = StringUtils.substringBetween(value, UifConstants.EL_PLACEHOLDER_PREFIX,
                    UifConstants.EL_PLACEHOLDER_SUFFIX);
            if (StringUtils.isNotBlank(elPlaceholder)) {
                containsElPlaceholder = true;
            }
        }

        return containsElPlaceholder;
    }

}
