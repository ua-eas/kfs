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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.InactivatableFromTo;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingDefinition;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataObjectMetaDataService;
import org.kuali.kfs.krad.service.InactivationBlockingDetectionService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Performs checking of inactivation blocking
 *
 *
 */
@Transactional
public class InactivationBlockingDetectionServiceImpl implements InactivationBlockingDetectionService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InactivationBlockingDetectionServiceImpl.class);

    protected DataObjectMetaDataService dataObjectMetaDataService;
    protected BusinessObjectService businessObjectService;

    /**
     * Note we are checking the active getting after retrieving potential blocking records instead of setting criteria on the
	 * active field. This is because some implementations of {@link org.kuali.rice.core.api.mo.common.active.MutableInactivatable} might not have the active field, for example
	 * instances of {@link InactivatableFromTo}
	 *
     * @see InactivationBlockingDetectionService#listAllBlockerRecords(InactivationBlockingDefinition)
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable
     */
    @SuppressWarnings("unchecked")
	public Collection<BusinessObject> listAllBlockerRecords(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
		Collection<BusinessObject> blockingRecords = new ArrayList<BusinessObject>();

		Map<String, String> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking for blocker records for object: " + blockedBo);
			LOG.debug("    With Metadata: " + inactivationBlockingMetadata);
			LOG.debug("    Resulting Query Map: " + queryMap);
		}

		if (queryMap != null) {
			Collection potentialBlockingRecords = businessObjectService.findMatching(
					inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
			for (Iterator iterator = potentialBlockingRecords.iterator(); iterator.hasNext();) {
				MutableInactivatable businessObject = (MutableInactivatable) iterator.next();
				if (businessObject.isActive()) {
					blockingRecords.add((BusinessObject) businessObject);
				}
			}
		}

		return blockingRecords;
	}

	/**
	 * Note we are checking the active getting after retrieving potential blocking records instead of setting criteria on the
	 * active field. This is because some implementations of {@link org.kuali.rice.core.api.mo.common.active.MutableInactivatable} might not have the active field, for example
	 * instances of {@link InactivatableFromTo}
	 *
	 * @see InactivationBlockingDetectionService#hasABlockingRecord(org.kuali.rice.krad.bo.BusinessObject,
	 *      InactivationBlockingMetadata)
	 * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable
	 */
	public boolean hasABlockingRecord(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
		boolean hasBlockingRecord = false;

		Map<String, String> queryMap = buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
		if (queryMap != null) {
			Collection potentialBlockingRecords = businessObjectService.findMatching(
					inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass(), queryMap);
			for (Iterator iterator = potentialBlockingRecords.iterator(); iterator.hasNext();) {
				MutableInactivatable businessObject = (MutableInactivatable) iterator.next();
				if (businessObject.isActive()) {
					hasBlockingRecord = true;
					break;
				}
			}
		}

		// if queryMap were null, means that we couldn't perform a query, and hence, need to return false
		return hasBlockingRecord;
	}

	protected Map<String, String> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
		BusinessObject blockingBo = (BusinessObject) ObjectUtils.createNewObjectFromClass(inactivationBlockingMetadata
				.getBlockingReferenceBusinessObjectClass());

		DataObjectRelationship dataObjectRelationship = dataObjectMetaDataService
				.getDataObjectRelationship(blockingBo, blockedBo.getClass(),
                        inactivationBlockingMetadata.getBlockedReferencePropertyName(), "", true, false, false);

		// note, this method assumes that all PK fields of the blockedBo have a non-null and, for strings, non-blank values
		if (dataObjectRelationship != null) {
			Map<String, String> parentToChildReferences = dataObjectRelationship.getParentToChildReferences();
			Map<String, String> queryMap = new HashMap<String, String>();
			for (Map.Entry<String, String> parentToChildReference : parentToChildReferences.entrySet()) {
				String fieldName = parentToChildReference.getKey();
				Object fieldValue = ObjectUtils.getPropertyValue(blockedBo, parentToChildReference.getValue());
				if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
					queryMap.put(fieldName, fieldValue.toString());
				} else {
					LOG.error("Found null value for foreign key field " + fieldName
							+ " while building inactivation blocking query map.");
					throw new RuntimeException("Found null value for foreign key field '" + fieldName
							+ "' while building inactivation blocking query map.");
				}
			}

			return queryMap;
		}

		return null;
	}

    public void setDataObjectMetaDataService(DataObjectMetaDataService dataObjectMetaDataService) {
        this.dataObjectMetaDataService = dataObjectMetaDataService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
