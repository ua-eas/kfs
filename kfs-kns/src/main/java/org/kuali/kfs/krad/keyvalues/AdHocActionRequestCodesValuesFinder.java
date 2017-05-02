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
package org.kuali.kfs.krad.keyvalues;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.uif.control.UifKeyValuesFinder;
import org.kuali.kfs.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.kfs.krad.uif.view.ViewModel;
import org.kuali.kfs.krad.web.form.DocumentFormBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiConstants;

import java.util.ArrayList;
import java.util.List;

public class AdHocActionRequestCodesValuesFinder extends UifKeyValuesFinderBase {
    private static final long serialVersionUID = 8457876358655872234L;

    /**
     * @see UifKeyValuesFinder#getKeyValues(ViewModel)
     */
    @Override
    public List<KeyValue> getKeyValues(ViewModel model) {
        DocumentFormBase documentFormBase = (DocumentFormBase) model;
        Document document = documentFormBase.getDocument();
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KewApiConstants.ACTION_REQUEST_FYI_REQ, KewApiConstants.ACTION_REQUEST_FYI_REQ_LABEL));
        keyValues.add(new ConcreteKeyValue(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL));
        keyValues.add(new ConcreteKeyValue(KewApiConstants.ACTION_REQUEST_APPROVE_REQ, KewApiConstants.ACTION_REQUEST_APPROVE_REQ_LABEL));

        if ((document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved())) {
            keyValues.add(new ConcreteKeyValue(KewApiConstants.ACTION_REQUEST_COMPLETE_REQ, KewApiConstants.ACTION_REQUEST_COMPLETE_REQ_LABEL));
        }
        return keyValues;
    }
}
