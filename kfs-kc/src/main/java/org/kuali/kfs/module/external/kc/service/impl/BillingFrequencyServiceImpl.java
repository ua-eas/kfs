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
package org.kuali.kfs.module.external.kc.service.impl;

import org.kuali.kfs.integration.ar.AccountsReceivableBillingFrequency;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.module.external.kc.dto.BillingFrequencyDTO;
import org.kuali.kfs.module.external.kc.service.BillingFrequencyService;
import org.kuali.kfs.sys.KFSPropertyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingFrequencyServiceImpl implements BillingFrequencyService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningCampaignServiceImpl.class);
    private ModuleService responsibleModuleService;

    @Override
    public List<BillingFrequencyDTO> getAll() {
        return find(new HashMap<String, Object>());
    }

    @Override
    public List<BillingFrequencyDTO> getActive() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, "Y");
        return find(criteria);
    }

    @Override
    public BillingFrequencyDTO getBillingFrequency(String frequency) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.FREQUENCY, frequency);
        AccountsReceivableBillingFrequency billingFrequency = getResponsibleModuleService().getExternalizableBusinessObject(AccountsReceivableBillingFrequency.class, criteria);
        return getBillingFrequencyDTO(billingFrequency);
    }

    private List<BillingFrequencyDTO> find(Map<String, Object> criteria) {
        if (getResponsibleModuleService() != null) {
            return getBillingFrequencyDTO(getResponsibleModuleService().getExternalizableBusinessObjectsListForLookup(AccountsReceivableBillingFrequency.class, criteria, true));
        } else {
            return new ArrayList<BillingFrequencyDTO>();
        }
    }

    protected BillingFrequencyDTO getBillingFrequencyDTO(AccountsReceivableBillingFrequency billingFrequency) {
        if (billingFrequency != null) {
            BillingFrequencyDTO dto = new BillingFrequencyDTO();
            dto.setFrequency(billingFrequency.getFrequency());
            dto.setFrequencyDescription(billingFrequency.getFrequencyDescription());
            dto.setGracePeriodDays(billingFrequency.getGracePeriodDays());
            dto.setActive(billingFrequency.isActive());
            return dto;
        } else {
            return null;
        }
    }

    protected List<BillingFrequencyDTO> getBillingFrequencyDTO(List<AccountsReceivableBillingFrequency> billingFrequencies) {
        if (billingFrequencies != null) {
            List<BillingFrequencyDTO> results = new ArrayList<BillingFrequencyDTO>();
            for (AccountsReceivableBillingFrequency billingFrequency : billingFrequencies) {
                results.add(getBillingFrequencyDTO(billingFrequency));
            }
            return results;
        } else {
            return new ArrayList<BillingFrequencyDTO>();
        }
    }

    public synchronized ModuleService getResponsibleModuleService() {
        if (responsibleModuleService == null) {
            responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(AccountsReceivableBillingFrequency.class);
        }
        return responsibleModuleService;
    }

    public void setResponsibleModuleService(ModuleService responsibleModuleService) {
        this.responsibleModuleService = responsibleModuleService;
    }

}
