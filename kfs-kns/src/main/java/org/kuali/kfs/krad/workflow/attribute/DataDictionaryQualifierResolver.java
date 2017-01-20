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
package org.kuali.kfs.krad.workflow.attribute;

import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.RoutingTypeDefinition;
import org.kuali.kfs.krad.datadictionary.WorkflowAttributes;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.KRADServiceLocatorInternal;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.role.QualifierResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QualifierResolver which uses Data Dictionary defined workflow attributes to gather a collection
 * of qualifiers to use to determine the responsibility for a document at a given workflow route node.
 * <p>
 * WorkflowAttributes can be defined in the data dictionary like so (this has been abbreviated):
 * <p>
 * <!-- Exported Workflow Attributes -->
 * <bean id="DisbursementVoucherDocument-workflowAttributes" parent="DisbursementVoucherDocument-workflowAttributes-parentBean"/>
 * <p>
 * <bean id="DisbursementVoucherDocument-workflowAttributes-parentBean" abstract="true" parent="WorkflowAttributes">
 * <property name="routingTypeDefinitions">
 * <map>
 * <!-- no qualifiers for purchasing node -->
 * <entry key="Account" value-ref="RoutingType-AccountingDocument-Account-sourceOnly"/>
 * <entry key="AccountingOrganizationHierarchy" value-ref="RoutingType-AccountingDocument-OrganizationHierarchy-sourceOnly"/>
 * <entry key="Campus" value-ref="DisbursementVoucherDocument-RoutingType-Campus"/>
 * <!-- no qualifiers for tax review -->
 * <!-- no qualifiers for travel review -->
 * <entry key="PaymentMethod" value-ref="DisbursementVoucherDocument-RoutingType-PaymentMethod"/>
 * <entry key="Award" value-ref="RoutingType-AccountingDocument-Award"/>
 * </map>
 * </property>
 * </bean>
 * <p>
 * <bean id="DisbursementVoucherDocument-RoutingType-PaymentMethod" class="RoutingTypeDefinition">
 * <property name="routingAttributes">
 * <list>
 * <bean class="RoutingAttribute">
 * <property name="qualificationAttributeName" value="disbVchrPaymentMethodCode"/>
 * </bean>
 * </list>
 * </property>
 * <property name="documentValuePathGroups">
 * <list>
 * <bean class="DocumentValuePathGroup">
 * <property name="documentValues">
 * <list>
 * <value>disbVchrPaymentMethodCode</value>
 * </list>
 * </property>
 * </bean>
 * </list>
 * </property>
 * </bean>
 * <p>
 * At the PaymentMethod node of the document, the DisbursementVoucherDocument-RoutingType-PaymentMethod RoutingTypeDefinition will be
 * consulted; it will pull values from the document (in this case, document.disbVchrPaymentMethodCode) and populate those
 * into the role qualifier Map<String, String>, with the key being the qualificationAttributeName and the value being the value of the property
 * listed in the documentValuePathGroups in the document.
 */
public class DataDictionaryQualifierResolver extends QualifierResolverBase {
//    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataDictionaryQualifierResolver.class);


    /**
     * Given the RouteContext, determines the document type of the document being routed and the current
     * route nodes; generates a List of qualifier Map<String, String>s based on the the contents of the document.
     *
     * @see QualifierResolver#resolve(RouteContext)
     */
    public List<Map<String, String>> resolve(RouteContext context) {
        final String routeLevel = context.getNodeInstance().getName();
        final DocumentEntry documentEntry = getDocumentEntry(context);
        final RoutingTypeDefinition routingTypeDefinition = getWorkflowAttributeDefintion(documentEntry, routeLevel);
        final Document document = getDocument(context);
        List<Map<String, String>> qualifiers = null;

        if (document != null && routingTypeDefinition != null) {
            qualifiers = KRADServiceLocatorInternal.getWorkflowAttributePropertyResolutionService().resolveRoutingTypeQualifiers(document, routingTypeDefinition);
        } else {
            qualifiers = new ArrayList<Map<String, String>>();
            Map<String, String> basicQualifier = new HashMap<String, String>();
            qualifiers.add(basicQualifier);
        }
        decorateWithCommonQualifiers(qualifiers, document, documentEntry, routeLevel);
        return qualifiers;
    }

    /**
     * Retrieves the data dictionary entry for the document being operated on by the given route context
     *
     * @param context the current route context
     * @return the data dictionary document entry
     */
    protected DocumentEntry getDocumentEntry(RouteContext context) {
        return KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDocumentEntry(context.getDocument().getDocumentType().getName());
    }

    /**
     * Retrieves the proper List of WorkflowAttributes for the given route level from the data dictionary
     * document entry
     *
     * @param documentEntry  the data dictionary document entry for the currently routed document
     * @param routeLevelName the name of the route level
     * @return a WorkflowAttributeDefinition if one could be found for the route level; otherwise, nothing
     */
    protected RoutingTypeDefinition getWorkflowAttributeDefintion(DocumentEntry documentEntry, String routeLevelName) {
        final WorkflowAttributes workflowAttributes = documentEntry.getWorkflowAttributes();
        if (workflowAttributes == null) {
            return null;
        }
        final Map<String, RoutingTypeDefinition> routingTypeMap = workflowAttributes.getRoutingTypeDefinitions();
        if (routingTypeMap.containsKey(routeLevelName)) return routingTypeMap.get(routeLevelName);
        return null;
    }

    /**
     * Add common qualifiers to every Map<String, String> in the given List of Map<String, String>
     *
     * @param qualifiers    a List of Map<String, String>s to add common qualifiers to
     * @param document      the document currently being routed
     * @param documentEntry the data dictionary entry of the type of document currently being routed
     * @param routeLevel    the document's current route level
     */
    protected void decorateWithCommonQualifiers(List<Map<String, String>> qualifiers, Document document, DocumentEntry documentEntry, String routeLevel) {
        for (Map<String, String> qualifier : qualifiers) {
            addCommonQualifiersToMap(qualifier, document, documentEntry, routeLevel);
        }
    }

    /**
     * Adds common qualifiers to a given Map<String, String>
     *
     * @param qualifier     an Map<String, String> to add common qualifiers to
     * @param document      the document currently being routed
     * @param documentEntry the data dictionary entry of the type of document currently being routed
     * @param routeLevel    the document's current route level
     */
    protected void addCommonQualifiersToMap(Map<String, String> qualifier, Document document, DocumentEntry documentEntry, String routeLevel) {
        if (document != null) {
            qualifier.put(KIM_ATTRIBUTE_DOCUMENT_NUMBER, document.getDocumentNumber());
        }
        if (documentEntry != null) {
            qualifier.put(KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME, documentEntry.getDocumentTypeName());
        }
        qualifier.put(KIM_ATTRIBUTE_ROUTE_LEVEL_NAME, routeLevel);
    }
}
