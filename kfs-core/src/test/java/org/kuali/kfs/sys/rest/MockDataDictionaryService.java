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
package org.kuali.kfs.sys.rest;

import org.apache.commons.lang3.StringUtils;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatterLiteral;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatterSubString;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.BusinessObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class MockDataDictionaryService implements DataDictionaryService {
    @Override
    public void setBaselinePackages(List baselinePackages) throws IOException {
    }

    @Override
    public DataDictionary getDataDictionary() {
        return null;
    }

    @Override
    public void addDataDictionaryLocations(List<String> locations) throws IOException {
    }

    @Override
    public ControlDefinition getAttributeControlDefinition(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Integer getAttributeSize(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Integer getAttributeMaxLength(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Pattern getAttributeValidatingExpression(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeLabel(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeShortLabel(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeErrorLabel(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Class<? extends Formatter> getAttributeFormatter(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Boolean getAttributeForceUppercase(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeSummary(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeDescription(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Boolean isAttributeRequired(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Boolean isAttributeDefined(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(Class dataObjectClass, String attributeName) {
        return null;
    }

    @Override
    public String getCollectionLabel(Class dataObjectClass, String collectionName) {
        return null;
    }

    @Override
    public String getCollectionShortLabel(Class dataObjectClass, String collectionName) {
        return null;
    }

    @Override
    public String getCollectionSummary(Class dataObjectClass, String collectionName) {
        return null;
    }

    @Override
    public String getCollectionDescription(Class dataObjectClass, String collectionName) {
        return null;
    }

    @Override
    public ControlDefinition getAttributeControlDefinition(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Integer getAttributeSize(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Integer getAttributeMinLength(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Integer getAttributeMaxLength(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeExclusiveMin(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeInclusiveMax(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Pattern getAttributeValidatingExpression(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeLabel(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeShortLabel(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeErrorLabel(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Class<? extends Formatter> getAttributeFormatter(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Boolean getAttributeForceUppercase(String entryName, String attributeName) {
        return null;
    }

    @Override
    public AttributeSecurity getAttributeSecurity(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeSummary(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeDescription(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String getAttributeValidatingErrorMessageKey(String entryName, String attributeName) {
        return null;
    }

    @Override
    public String[] getAttributeValidatingErrorMessageParameters(String entryName, String attributeName) {
        return new String[0];
    }

    @Override
    public Boolean isAttributeRequired(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Boolean isAttributeDefined(String entryName, String attributeName) {
        return null;
    }

    @Override
    public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(String entryName, String attributeName) {
        return null;
    }

    @Override
    public AttributeDefinition getAttributeDefinition(String entryName, String attributeName) {
        if (StringUtils.isBlank(entryName) || StringUtils.isBlank(attributeName)) {
            return null;
        }
        AttributeDefinition attributeDefinition = new AttributeDefinition();
        attributeDefinition.setName(attributeName);
        if (StringUtils.equals(entryName, "Bank")) {
            MaskFormatterSubString partialMaskFormatter = new MaskFormatterSubString();
            partialMaskFormatter.setMaskLength(12);
            MaskFormatterLiteral fullMaskFormatter = new MaskFormatterLiteral();
            fullMaskFormatter.setLiteral("XXXXZZZZ");
            if (StringUtils.equals(attributeName, KFSPropertyConstants.BANK_ROUTING_NUMBER)) {
                AttributeSecurity attrSec = new AttributeSecurity();
                attrSec.setPartialMask(true);
                attrSec.setPartialMaskFormatter(partialMaskFormatter);
                attrSec.setMaskFormatter(fullMaskFormatter);
                attributeDefinition.setAttributeSecurity(attrSec);
            } else if (StringUtils.equals(attributeName, KFSPropertyConstants.BANK_ACCOUNT_NUMBER)) {
                AttributeSecurity attrSec = new AttributeSecurity();
                attrSec.setMask(true);
                attrSec.setPartialMaskFormatter(partialMaskFormatter);
                attrSec.setMaskFormatter(fullMaskFormatter);
                attributeDefinition.setAttributeSecurity(attrSec);
            }
        }
        return attributeDefinition;
    }

    @Override
    public String getCollectionLabel(String entryName, String collectionName) {
        return null;
    }

    @Override
    public String getCollectionShortLabel(String entryName, String collectionName) {
        return null;
    }

    @Override
    public String getCollectionElementLabel(String entryName, String collectionName, Class dataObjectClass) {
        return null;
    }

    @Override
    public String getCollectionSummary(String entryName, String collectionName) {
        return null;
    }

    @Override
    public String getCollectionDescription(String entryName, String collectionName) {
        return null;
    }

    @Override
    public Class<? extends BusinessObject> getRelationshipSourceClass(String entryName, String relationshipName) {
        return null;
    }

    @Override
    public Class<? extends BusinessObject> getRelationshipTargetClass(String entryName, String relationshipName) {
        return null;
    }

    @Override
    public List<String> getRelationshipSourceAttributes(String entryName, String relationshipName) {
        return null;
    }

    @Override
    public List<String> getRelationshipTargetAttributes(String entryName, String relationshipName) {
        return null;
    }

    @Override
    public Map<String, String> getRelationshipAttributeMap(String entryName, String relationshipName) {
        return null;
    }

    @Override
    public List<String> getRelationshipEntriesForSourceAttribute(String entryName, String sourceAttributeName) {
        return null;
    }

    @Override
    public List<String> getRelationshipEntriesForTargetAttribute(String entryName, String targetAttributeName) {
        return null;
    }

    @Override
    public boolean hasRelationship(String entryName, String relationshipName) {
        return false;
    }

    @Override
    public List<String> getRelationshipNames(String entryName) {
        return null;
    }

    @Override
    public String getDocumentLabelByTypeName(String documentTypeName) {
        return null;
    }

    @Override
    public String getDocumentLabelByClass(Class documentOrBusinessObjectClass) {
        return null;
    }

    @Override
    public String getDocumentTypeNameByClass(Class documentClass) {
        return null;
    }

    @Override
    public String getValidDocumentTypeNameByClass(Class documentClass) {
        return null;
    }

    @Override
    public Class<? extends Document> getDocumentClassByTypeName(String documentTypeName) {
        return null;
    }

    @Override
    public Class<? extends Document> getValidDocumentClassByTypeName(String documentTypeName) {
        return null;
    }

    @Override
    public List<String> getGroupByAttributesForEffectiveDating(Class businessObjectClass) {
        return null;
    }

    @Override
    public Set<InactivationBlockingMetadata> getAllInactivationBlockingDefinitions(Class inactivationBlockedBusinessObjectClass) {
        return null;
    }

    @Override
    public View getViewById(String viewId) {
        return null;
    }

    @Override
    public Object getDictionaryObject(String id) {
        return null;
    }

    @Override
    public boolean containsDictionaryObject(String id) {
        return false;
    }

    @Override
    public View getViewByTypeIndex(UifConstants.ViewType viewTypeName, Map<String, String> indexKey) {
        return null;
    }
}