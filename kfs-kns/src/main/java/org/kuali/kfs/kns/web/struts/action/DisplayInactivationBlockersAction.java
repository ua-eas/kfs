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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.kfs.kns.web.struts.form.DisplayInactivationBlockersForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.InactivationBlockingDisplayService;
import org.kuali.kfs.krad.service.KRADServiceLocatorInternal;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * 
 *
 */
public class DisplayInactivationBlockersAction extends org.kuali.kfs.kns.web.struts.action.KualiAction {
	
	public ActionForward displayAllInactivationBlockers(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DisplayInactivationBlockersForm displayInactivationBlockersForm = (DisplayInactivationBlockersForm) form;
		DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		InactivationBlockingDisplayService inactivationBlockingDisplayService = KRADServiceLocatorInternal.getInactivationBlockingDisplayService();
		
		Class blockedBoClass = Class.forName(displayInactivationBlockersForm.getBusinessObjectClassName());
		BusinessObject blockedBo = (BusinessObject) blockedBoClass.newInstance();
		for (String key : displayInactivationBlockersForm.getPrimaryKeyFieldValues().keySet()) {
			ObjectUtils.setObjectProperty(blockedBo, key, displayInactivationBlockersForm.getPrimaryKeyFieldValues().get(key));
		}
		
		Map<String, List<String>> allBlockers = new TreeMap<String, List<String>>();
		
		Set<InactivationBlockingMetadata> inactivationBlockers = dataDictionaryService.getAllInactivationBlockingDefinitions(blockedBoClass);
		for (InactivationBlockingMetadata inactivationBlockingMetadata : inactivationBlockers) {
			String blockingBoLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(inactivationBlockingMetadata.getBlockingReferenceBusinessObjectClass().getName()).getObjectLabel();
			String relationshipLabel = inactivationBlockingMetadata.getRelationshipLabel();
			String displayLabel;
			if (StringUtils.isEmpty(relationshipLabel)) {
				displayLabel = blockingBoLabel;
			}
			else {
				displayLabel = blockingBoLabel + " (" + relationshipLabel + ")";
			}
			List<String> blockerObjectList = inactivationBlockingDisplayService.listAllBlockerRecords(blockedBo, inactivationBlockingMetadata);
			
			if (!blockerObjectList.isEmpty()) {
				List<String> existingList = allBlockers.get(displayLabel);
				if (existingList != null) {
					existingList.addAll(blockerObjectList);
				}
				else {
					allBlockers.put(displayLabel, blockerObjectList);
				}
			}
		}
		
		displayInactivationBlockersForm.setBlockingValues(allBlockers);
		
		return mapping.findForward(RiceConstants.MAPPING_BASIC);
	}
}
