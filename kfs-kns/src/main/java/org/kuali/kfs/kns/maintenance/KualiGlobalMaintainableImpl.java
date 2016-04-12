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
package org.kuali.kfs.kns.maintenance;

import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.krad.bo.GlobalBusinessObject;
import org.kuali.kfs.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.maintenance.*;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KualiGlobalMaintainableImpl extends KualiMaintainableImpl {
    private static final long serialVersionUID = 4814145799502207182L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiGlobalMaintainableImpl.class);

    /**
     * @see org.kuali.kfs.krad.maintenance.Maintainable#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        if (businessObject != null) {
            prepareGlobalsForSave();
        }
    }

    /**
     * @see Maintainable#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        if (businessObject != null) {
            processGlobalsAfterRetrieve();
        }
    }

    /**
     * This method does special-case handling for Globals, doing various tasks that need to be done to make a global doc valid after
     * its been loaded from the db/maintenance-system.
     */
    protected void processGlobalsAfterRetrieve() {

        // TODO: this needs refactoring ... its kind of lame that we have this set of
        // compound list statements, this should all be refactored. This could be moved
        // into a method on all GBOs, like GBO.prepareForSave(), or even better, subclass
        // KualiGlobalMaintainableImpl for each global, since this is all
        // maintainable-related stuff.

        GlobalBusinessObject gbo = (GlobalBusinessObject) businessObject;
        Class gboClass = businessObject.getClass();
        String finDocNumber = gbo.getDocumentNumber();

        // TODO: remove this whole pseudo-assertion code block once this gets moved into a doc-specific
        // maintainableImpl class.

        // This whole mess is to fail-fast if my assumptions about the nature of the parent bo of all
        // global-maintenance-documents is wrong
        boolean assumptionIsWrong = false;
        //TODO: Revisit this. Changing since getPrimaryKeys and listPrimaryKeyFieldNames are apparently same.
        //May be we might want to replace listPrimaryKeyFieldNames with getPrimaryKeys... Not sure.
        List primaryKeys = KNSServiceLocator.getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(gboClass);
        if (primaryKeys == null) {
            assumptionIsWrong = true;
        }
        else if (primaryKeys.isEmpty()) {
            assumptionIsWrong = true;
        }
        else if (primaryKeys.size() != 1) {
            assumptionIsWrong = true;
        }
        else if (!primaryKeys.get(0).getClass().equals(String.class)) {
            assumptionIsWrong = true;
        }
        else if (!KRADPropertyConstants.DOCUMENT_NUMBER.equalsIgnoreCase((String) primaryKeys.get(0))) {
            assumptionIsWrong = true;
        }
        if (assumptionIsWrong) {
            throw new RuntimeException("An assertion about the nature of the primary keys for this GBO has " + "failed, and processing cannot continue.");
        }

        // ASSUMPTION: This next section assumes that all GBOs have documentNumber as
        // their only primary key field, and that its named as follows. This will
        // either fail loudly or break silently if this assumption is not true. Once we
        // move this sort of thing into the global-doc-specific subclasses of
        // KualiGlobalMaintainableImpl, this will simplify tremendously.
        Map pkMap = new HashMap();
        pkMap.put(KRADPropertyConstants.DOCUMENT_NUMBER, finDocNumber);
        PersistableBusinessObject newBo = null;
        newBo = (PersistableBusinessObject) KRADServiceLocator.getBusinessObjectService().findByPrimaryKey(gboClass, pkMap);
        if (newBo == null) {
            throw new RuntimeException("The Global Business Object could not be retrieved from the DB.  " + "This should never happen under normal circumstances.  If this is a legitimate case " + "Then this exception should be removed.");
        }
        
        // property newCollectionRecord of PersistableObjectBase is not persisted, but is always true for globals
        try {
            ObjectUtils.setObjectPropertyDeep(newBo, KRADPropertyConstants.NEW_COLLECTION_RECORD, boolean.class, true, 2);
        }
        catch (Exception e) {
            LOG.error("unable to set newCollectionRecord property: " + e.getMessage());
            throw new RuntimeException("unable to set newCollectionRecord property: " + e.getMessage());
        }

        // replace the GBO loaded from XML with the GBO loaded from the DB
        setBusinessObject(newBo);
    }

    /**
     * This method does special-case handling for Globals, filling out various fields that need to be filled, etc.
     */
    protected void prepareGlobalsForSave() {
        GlobalBusinessObject gbo = (GlobalBusinessObject) businessObject;

        // set the documentNumber for all
        gbo.setDocumentNumber(getDocumentNumber());

        List<? extends GlobalBusinessObjectDetail> details = gbo.getAllDetailObjects();
        for ( GlobalBusinessObjectDetail detail : details ) {
            detail.setDocumentNumber(getDocumentNumber());
        }
    }

    /**
     * This overrides the standard version in KualiMaintainableImpl which works for non-global maintenance documents
     * Each global document must in turn override this with its own locking representation, since it varies from document to document (some have one detail class and others have two, and the way to combine the two detail classes is unique to document with two detail classes)
     * @see Maintainable#generateMaintenanceLocks()
     */
    @Override
    public abstract List<MaintenanceLock> generateMaintenanceLocks();

    /**
     * @see Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        BusinessObjectService boService = KRADServiceLocator.getBusinessObjectService();
        GlobalBusinessObject gbo = (GlobalBusinessObject) businessObject;

        // delete any indicated BOs
        List<PersistableBusinessObject> bosToDeactivate = gbo.generateDeactivationsToPersist();
        if (bosToDeactivate != null) {
            if (!bosToDeactivate.isEmpty()) {
                boService.save(bosToDeactivate);
            }
        }

        // persist any indicated BOs
        List<PersistableBusinessObject> bosToPersist = gbo.generateGlobalChangesToPersist();
        if (bosToPersist != null) {
            if (!bosToPersist.isEmpty()) {
                boService.save(bosToPersist);
            }
        }

    }
    
    public abstract Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass();
}
