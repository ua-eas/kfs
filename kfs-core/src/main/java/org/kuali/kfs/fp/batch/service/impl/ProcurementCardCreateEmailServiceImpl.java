/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.fp.batch.service.impl;

import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.VelocityEmailService;
import org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Transactional
public class ProcurementCardCreateEmailServiceImpl extends VelocityEmailServiceBase implements VelocityEmailService {
    private String templateUrl;

    @Override
    public String getEmailSubject() {
        return "KFS Pcard Load Summary ";
    }

    @Override
    public Collection<String> getProdEmailReceivers() {
        return CoreFrameworkServiceLocator.getParameterService().getParameterValuesAsString(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.PCARD_BATCH_SUMMARY_TO_EMAIL_ADDRESSES);
    }

    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }
}
