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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.web.form.UifFormBase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages Uif form objects for a session
 */
public class UifFormManager implements Serializable {
    private static final long serialVersionUID = -6323378881342207080L;

    private UifFormBase currentForm;
    private Map<String, UifFormBase> uifForms;

    public UifFormManager() {
        this.uifForms = new HashMap<String, UifFormBase>();
    }

    public UifFormBase getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(UifFormBase currentForm) {
        this.currentForm = currentForm;
        addForm(currentForm);
    }

    public void addForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        uifForms.put(form.getFormKey(), form);
    }

    public UifFormBase getForm(String formKey) {
        if (uifForms.containsKey(formKey)) {
            return uifForms.get(formKey);
        }

        return null;
    }

    public void removeForm(UifFormBase form) {
        if (form == null) {
            return;
        }

        removeFormByKey(form.getFormKey());
    }

    public void removeFormByKey(String formKey) {
        if (uifForms.containsKey(formKey)) {
            uifForms.remove(formKey);
        }

        if ((currentForm != null) && StringUtils.equals(currentForm.getFormKey(), formKey)) {
            currentForm = null;
        }
    }
}
