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
package org.kuali.kfs.krad.web.bind;

import org.kuali.rice.core.api.CoreApiServiceLocator;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.security.GeneralSecurityException;

/**
 * Property editor which encrypts values for display and decrypts on binding, uses the
 * {@link org.kuali.rice.core.api.encryption.EncryptionService} to perform the encryption
 *
 * 
 */
public class UifEncryptionPropertyEditorWrapper extends PropertyEditorSupport{

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifEncryptionPropertyEditorWrapper.class);

    PropertyEditor propertyEditor;

    /**
     * @param propertyEditor
     */
    public UifEncryptionPropertyEditorWrapper(PropertyEditor propertyEditor) {
        super();
        this.propertyEditor = propertyEditor;
    }

    @Override
    public String getAsText() {
        try {
            if (propertyEditor != null) {
                if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                    return CoreApiServiceLocator.getEncryptionService().encrypt(propertyEditor.getAsText());
                }
            }
            if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                return CoreApiServiceLocator.getEncryptionService().encrypt(getValue());
            }
            return null;
        } catch (GeneralSecurityException e) {
            LOG.error("Unable to encrypt value");
            throw new RuntimeException("Unable to encrypt value.");
        }
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            String value = "";
            if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                value = CoreApiServiceLocator.getEncryptionService().decrypt(text);
            }
            if (propertyEditor != null) {
                propertyEditor.setAsText(value);
            } else {
                setValue(value);
            }
        } catch (GeneralSecurityException e) {
            LOG.error("Unable to decrypt value: " + text);
            throw new RuntimeException("Unable to decrypt value.");
        }
    }

}
