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
package org.kuali.kfs.module.cg.web.struts;

import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.core.web.format.DateFormatter;

import java.util.Date;

public class ProposalAwardCloseForm extends FinancialSystemTransactionalDocumentFormBase {

    public ProposalAwardCloseForm() {
        super();
        setFormatterType("document.userInitiatedCloseDate", DateFormatter.class);
        setFormatterType("document.closeOnOrBeforeDate", DateFormatter.class);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "CLOS";
    }

    public ProposalAwardCloseDocument getMostRecentClose() {
        return SpringContext.getBean(CloseService.class).getMostRecentClose();
    }

    public ProposalAwardCloseDocument getCloseDocument() {
        return (ProposalAwardCloseDocument) getDocument();
    }

    public void setClose(ProposalAwardCloseDocument document) {
        setDocument(document);
    }

    public Date getUserInitiatedCloseDate() {
        return getCloseDocument().getUserInitiatedCloseDate();
    }

    public void setUserInitiatedCloseDate(Date date) {
        getCloseDocument().setUserInitiatedCloseDate(new java.sql.Date(date.getTime()));
    }

    public Date getCloseOnOrBeforeDate() {
        return getCloseDocument().getCloseOnOrBeforeDate();
    }

    public void setCloseOnOrBeforeDate(Date date) {
        getCloseDocument().setCloseOnOrBeforeDate(new java.sql.Date(date.getTime()));
    }

}
