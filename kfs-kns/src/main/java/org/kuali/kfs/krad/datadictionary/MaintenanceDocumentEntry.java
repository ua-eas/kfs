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
package org.kuali.kfs.krad.datadictionary;

import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.exception.ClassValidationException;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentAuthorizer;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentPresentationControllerBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Data dictionary entry class for <code>MaintenanceDocument</code>
 */
public class MaintenanceDocumentEntry extends DocumentEntry {
    private static final long serialVersionUID = 4990040987835057251L;

    protected Class<?> dataObjectClass;
    protected Class<? extends Maintainable> maintainableClass;

    protected List<String> lockingKeys = new ArrayList<String>();

    protected boolean allowsNewOrCopy = true;
    protected boolean preserveLockingKeysOnCopy = false;
    protected boolean allowsRecordDeletion = false;

    public MaintenanceDocumentEntry() {
        super();

        setDocumentClass(getStandardDocumentBaseClass());
        documentAuthorizerClass = MaintenanceDocumentAuthorizerBase.class;
        documentPresentationControllerClass = MaintenanceDocumentPresentationControllerBase.class;
    }

    public Class<? extends Document> getStandardDocumentBaseClass() {
        return MaintenanceDocumentBase.class;
    }

    /*
            This attribute is used in many contexts, for example, in maintenance docs, it's used to specify the classname
            of the BO being maintained.
     */
    public void setDataObjectClass(Class<?> dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        this.dataObjectClass = dataObjectClass;
    }

    public Class<?> getDataObjectClass() {
        return dataObjectClass;
    }

    /**
     * @see DocumentEntry#getEntryClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class getEntryClass() {
        return dataObjectClass;
    }

    /*
            The maintainableClass element specifies the name of the
            java class which is responsible for implementing the
            maintenance logic.
            The normal one is KualiMaintainableImpl.java.
     */
    public void setMaintainableClass(Class<? extends Maintainable> maintainableClass) {
        if (maintainableClass == null) {
            throw new IllegalArgumentException("invalid (null) maintainableClass");
        }
        this.maintainableClass = maintainableClass;
    }

    public Class<? extends Maintainable> getMaintainableClass() {
        return maintainableClass;
    }

    /**
     * @return List of all lockingKey fieldNames associated with this LookupDefinition, in the order in which they were
     * added
     */
    public List<String> getLockingKeyFieldNames() {
        return lockingKeys;
    }

    /**
     * Gets the allowsNewOrCopy attribute.
     *
     * @return Returns the allowsNewOrCopy.
     */
    public boolean getAllowsNewOrCopy() {
        return allowsNewOrCopy;
    }

    /**
     * The allowsNewOrCopy element contains a value of true or false.
     * If true, this indicates the maintainable should allow the
     * new and/or copy maintenance actions.
     */
    public void setAllowsNewOrCopy(boolean allowsNewOrCopy) {
        this.allowsNewOrCopy = allowsNewOrCopy;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see DocumentEntry#completeValidation()
     */
    public void completeValidation() {
        super.completeValidation();

        for (String lockingKey : lockingKeys) {
            if (!DataDictionary.isPropertyOf(dataObjectClass, lockingKey)) {
                throw new AttributeValidationException(
                    "unable to find attribute '" + lockingKey + "' for lockingKey in dataObjectClass '" +
                        dataObjectClass.getName());
            }
        }

        for (ReferenceDefinition reference : defaultExistenceChecks) {
            reference.completeValidation(dataObjectClass, null);
        }


        if (documentAuthorizerClass != null &&
            !MaintenanceDocumentAuthorizer.class.isAssignableFrom(documentAuthorizerClass)) {
            throw new ClassValidationException(
                "This maintenance document for '" + getDataObjectClass().getName() + "' has an invalid " +
                    "documentAuthorizerClass ('" + documentAuthorizerClass.getName() + "').  " +
                    "Maintenance Documents must use an implementation of MaintenanceDocumentAuthorizer.");
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintenanceDocumentEntry for documentType " + getDocumentTypeName();
    }

    public List<String> getLockingKeys() {
        return lockingKeys;
    }

    /*
           The lockingKeys element specifies a list of fields
           that comprise a unique key.  This is used for record locking
           during the file maintenance process.
    */
    public void setLockingKeys(List<String> lockingKeys) {
        for (String lockingKey : lockingKeys) {
            if (lockingKey == null) {
                throw new IllegalArgumentException("invalid (null) lockingKey");
            }
        }
        this.lockingKeys = lockingKeys;
    }

    /**
     * @return the preserveLockingKeysOnCopy
     */
    public boolean getPreserveLockingKeysOnCopy() {
        return this.preserveLockingKeysOnCopy;
    }

    /**
     * @param preserveLockingKeysOnCopy the preserveLockingKeysOnCopy to set
     */
    public void setPreserveLockingKeysOnCopy(boolean preserveLockingKeysOnCopy) {
        this.preserveLockingKeysOnCopy = preserveLockingKeysOnCopy;
    }

    /**
     * @return the allowRecordDeletion
     */
    public boolean getAllowsRecordDeletion() {
        return this.allowsRecordDeletion;
    }

    /**
     * @param allowsRecordDeletion the allowRecordDeletion to set
     */
    public void setAllowsRecordDeletion(boolean allowsRecordDeletion) {
        this.allowsRecordDeletion = allowsRecordDeletion;
    }

}
