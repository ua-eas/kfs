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
package org.kuali.kfs.kns.bo.authorization;

import org.kuali.kfs.kns.authorization.BusinessObjectAuthorizer;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.DataObjectAuthorizerBase;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.GlobalVariables;

import java.util.HashMap;
import java.util.Map;

public class BusinessObjectAuthorizerBase extends DataObjectAuthorizerBase implements BusinessObjectAuthorizer {
    private static final long serialVersionUID = -6315759348728853851L;

	private static KualiModuleService kualiModuleService;
	private static DataDictionaryService dataDictionaryService;
	private static PersistenceStructureService persistenceStructureService;

    protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName) {
		return getPermissionService()
				.isPermissionDefinedByTemplate(namespaceCode, permissionTemplateName, new HashMap<String, String>(
                        getPermissionDetailValues(businessObject)));
	}

	protected final boolean permissionExistsByTemplate(
			BusinessObject businessObject, String namespaceCode,
			String permissionTemplateName, Map<String, String> permissionDetails) {
		Map<String, String> combinedPermissionDetails = new HashMap<String, String>(
				getPermissionDetailValues(businessObject));
		combinedPermissionDetails.putAll(permissionDetails);
		return getPermissionService()
				.isPermissionDefinedByTemplate(namespaceCode, permissionTemplateName, combinedPermissionDetails);
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId) {
		return getPermissionService().isAuthorized(principalId,
				namespaceCode, permissionName,
				new HashMap<String, String>(getRoleQualification(businessObject, principalId)));
	}

	public final boolean isAuthorizedByTemplate(BusinessObject dataObject,
			String namespaceCode, String permissionTemplateName,
			String principalId) {
		return getPermissionService().isAuthorizedByTemplate(principalId, namespaceCode, permissionTemplateName,
                new HashMap<String, String>(getPermissionDetailValues(dataObject)), new HashMap<String, String>(
                getRoleQualification(dataObject, principalId)));
	}

	public final boolean isAuthorized(BusinessObject businessObject,
			String namespaceCode, String permissionName, String principalId,
			Map<String, String> collectionOrFieldLevelPermissionDetails,
			Map<String, String> collectionOrFieldLevelRoleQualification) {
		Map<String, String> roleQualifiers = null;
		Map<String, String> permissionDetails = null;
		if (collectionOrFieldLevelRoleQualification != null) {
			roleQualifiers = new HashMap<String, String>(
					getRoleQualification(businessObject, principalId));
			roleQualifiers.putAll(collectionOrFieldLevelRoleQualification);
		} else {
			roleQualifiers = new HashMap<String, String>(
					getRoleQualification(businessObject, principalId));
		}
		/*if (collectionOrFieldLevelPermissionDetails != null) {
			permissionDetails = new HashMap<String, String>(
					getPermissionDetailValues(businessObject));
			permissionDetails.putAll(collectionOrFieldLevelPermissionDetails);
		} else {
			permissionDetails = new HashMap<String, String>(
					getPermissionDetailValues(businessObject));
		}*/

		return getPermissionService().isAuthorized(principalId,
				namespaceCode, permissionName,
				roleQualifiers);
	}


	/**
	 * Returns a role qualification map based off data from the primary business
	 * object or the document. DO NOT MODIFY THE MAP RETURNED BY THIS METHOD
	 *
	 * @param primaryBusinessObjectOrDocument
	 *            the primary business object (i.e. the main BO instance behind
	 *            the lookup result row or inquiry) or the document
	 * @return a Map containing role qualifications
	 */
	protected final Map<String, String> getRoleQualification(
			BusinessObject primaryBusinessObjectOrDocument) {
		return getRoleQualification(primaryBusinessObjectOrDocument, GlobalVariables
					.getUserSession().getPerson().getPrincipalId());
	}

	/**
	 * @see BusinessObjectAuthorizer#getCollectionItemPermissionDetails(org.kuali.rice.krad.bo.BusinessObject)
	 */
    @Override
	public Map<String, String> getCollectionItemPermissionDetails(
			BusinessObject collectionItemBusinessObject) {
		return new HashMap<String, String>();
	}

	/**
	 * @see BusinessObjectAuthorizer#getCollectionItemRoleQualifications(org.kuali.rice.krad.bo.BusinessObject)
	 */
    @Override
	public Map<String, String> getCollectionItemRoleQualifications(
			BusinessObject collectionItemBusinessObject) {
		return new HashMap<String, String>();
	}

	protected static KualiModuleService getKualiModuleService() {
		if (kualiModuleService == null) {
			kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
		}
		return kualiModuleService;
	}

	protected static DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KRADServiceLocatorWeb
					.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
}
