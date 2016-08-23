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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class LetterOfCreditFund implements ContractsAndGrantsLetterOfCreditFund, MutableInactivatable {

    private String letterOfCreditFundCode;
    private String letterOfCreditFundDescription;
    private java.sql.Date letterOfCreditFundStartDate;
    private java.sql.Date letterOfCreditFundExpirationDate;
    private boolean active;
    private KualiDecimal letterOfCreditFundAmount;
    private String letterOfCreditFundGroupCode;
    private LetterOfCreditFundGroup letterOfCreditFundGroup;



    public LetterOfCreditFund() { }
    public LetterOfCreditFund(String letterOfCreditFundCode, String letterOfCreditFundDescription) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
        this.letterOfCreditFundDescription = letterOfCreditFundDescription;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void refresh() { }

    @Override
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    @Override
    public String getLetterOfCreditFundDescription() {
        return letterOfCreditFundDescription;
    }

    public void setLetterOfCreditFundDescription(String letterOfCreditFundDescription) {
        this.letterOfCreditFundDescription = letterOfCreditFundDescription;
    }

    public Date getLetterOfCreditFundStartDate() {
        return letterOfCreditFundStartDate;
    }

    public void setLetterOfCreditFundStartDate(java.util.Date letterOfCreditFundStartDate) {
        if (letterOfCreditFundStartDate != null) {
            this.letterOfCreditFundStartDate = new Date(letterOfCreditFundStartDate.getTime());
        }
    }

    public void setLetterOfCreditFundStartDate(Date letterOfCreditStartDate) {
        this.letterOfCreditFundStartDate = letterOfCreditStartDate;
    }

    public Date getLetterOfCreditFundExpirationDate() {
        return letterOfCreditFundExpirationDate;
    }

    public void setLetterOfCreditFundExpirationDate(java.util.Date letterOfCreditFundExpirationDate) {
        if (letterOfCreditFundExpirationDate != null) {
            this.letterOfCreditFundExpirationDate = new Date(letterOfCreditFundExpirationDate.getTime());
        }
    }

    public void setLetterOfCreditFundExpirationDate(Date letterOfCreditFundExpirationDate) {
        this.letterOfCreditFundExpirationDate = letterOfCreditFundExpirationDate;
    }

    public KualiDecimal getLetterOfCreditFundAmount() {
        return letterOfCreditFundAmount;
    }

    public void setLetterOfCreditFundAmount(KualiDecimal letterOfCreditFundAmount) {
        this.letterOfCreditFundAmount = letterOfCreditFundAmount;
    }

    public void setLetterOfCreditFundAmount(BigDecimal letterOfCreditAmount) {
        if (letterOfCreditAmount != null) {
            this.letterOfCreditFundAmount = new KualiDecimal(letterOfCreditAmount);
        }
    }

    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    @Override
    public LetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        if (this.letterOfCreditFundGroup == null) {
            Map<String, Object> keyValues = new HashMap<>();
            keyValues.put("groupCode", getLetterOfCreditFundGroupCode());

            ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(LetterOfCreditFundGroup.class);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(LetterOfCreditFundGroup.class)){
                this.letterOfCreditFundGroup = responsibleModuleService.getExternalizableBusinessObject(LetterOfCreditFundGroup.class, keyValues);
            }
        }
        return letterOfCreditFundGroup;
    }
}
