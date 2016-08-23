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
package org.kuali.kfs.sys.service.impl;

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.PayeeACHService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayeeACHServiceImpl implements PayeeACHService {

    private BusinessObjectService businessObjectService;

    @Override
    public boolean isPayeeSignedUpForACH(String payeeTypeCode, String payeeIdNumber) {

        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, payeeTypeCode);
        keys.put(PdpPropertyConstants.PAYEE_ID_NUMBER, payeeIdNumber);
        keys.put(KFSPropertyConstants.ACCOUNT_ACTIVE_INDICATOR, Boolean.TRUE);
        List<PayeeACHAccount> payeeACHAccountList = (List<PayeeACHAccount>) businessObjectService.findMatching(PayeeACHAccount.class, keys);

        if (ObjectUtils.isNull(payeeACHAccountList) || payeeACHAccountList.isEmpty())
            return false;

        return true;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
