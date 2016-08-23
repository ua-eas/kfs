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
package org.kuali.kfs.krad.app.persistence.jpa;

import java.util.HashSet;
import java.util.Set;

/**
 * The class which exposes the names of all KNS business object entities - which are managed
 * by all JPA persistence units
 */
public class KRADPersistableBusinessObjectClassExposer implements
    PersistableBusinessObjectClassExposer {

    /**
     * Exposes all KNS objects - a hard coded list for now - to persistence units
     *
     * @see PersistableBusinessObjectClassExposer#exposePersistableBusinessObjectClassNames()
     */
    @Override
    public Set<String> exposePersistableBusinessObjectClassNames() {
        Set<String> knsBOs = new HashSet<String>();
        /*knsBOs.add(org.kuali.rice.kns.document.DocumentBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.MaintenanceDocumentBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.MaintenanceLock.class.getName());
		knsBOs.add(org.kuali.rice.kns.document.TransactionalDocumentBase.class.getName());
		knsBOs.add(AdHocRoutePerson.class.getName());
		knsBOs.add(AdHocRouteRecipient.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.AdHocRouteWorkgroup.class.getName());
		knsBOs.add(Attachment.class.getName());
		//knsBOs.add(org.kuali.rice.kns.bo.CampusImpl.class.getName());
		//knsBOs.add(org.kuali.rice.kns.bo.CampusTypeImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.DocumentAttachment.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.DocumentHeader.class.getName());
		knsBOs.add(GlobalBusinessObjectDetailBase.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.LookupResults.class.getName());
		knsBOs.add(KualiCodeBase.class.getName());
		knsBOs.add(MultipleValueLookupMetadata.class.getName());
		knsBOs.add(NamespaceBo.class.getName());
		knsBOs.add(Note.class.getName());
		knsBOs.add(NoteType.class.getName());
		knsBOs.add(ParameterBo.class.getName());
		knsBOs.add(ComponentBo.class.getName());
		knsBOs.add(ParameterTypeBo.class.getName());
		knsBOs.add(CampusBo.class.getName());
		knsBOs.add(CampusTypeBo.class.getName());
		knsBOs.add(PersistableAttachmentBase.class.getName());
		knsBOs.add(PersistableBusinessObjectBase.class.getName());
		knsBOs.add(PersistableBusinessObjectExtensionBase.class.getName());
		knsBOs.add(SelectedObjectIds.class.getName());
		knsBOs.add(org.kuali.rice.location.impl.country.CountryBo.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.CountyImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.StateImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.PostalCodeImpl.class.getName());
		knsBOs.add(org.kuali.rice.kns.bo.SessionDocument.class.getName()); */
        return knsBOs;
    }

}
