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
package org.kuali.kfs.module.external.kc.service.impl;

import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class AwardFundManagerServiceImpl implements ExternalizableBusinessObjectService {

    private ExternalizableBusinessObjectService awardService;

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Award award = (Award) awardService.findMatching(primaryKeys);
        if (award == null) {
            return null;
        } else {
            return award.getAwardPrimaryFundManager();
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        fieldValues.put(KcConstants.FUND_MANAGER_ID, fieldValues.get(KimConstants.AttributeConstants.PRINCIPAL_ID));
        fieldValues.remove(KimConstants.AttributeConstants.PRINCIPAL_ID);
        Collection<Award> awards = awardService.findMatching(fieldValues);
        List<ContractsAndGrantsFundManager> managers = new ArrayList<>();
        for (Award award : awards) {
            managers.add(award.getAwardPrimaryFundManager());
        }
        return managers;
    }

    protected ExternalizableBusinessObjectService getAwardService() {
        return awardService;
    }

    public void setAwardService(ExternalizableBusinessObjectService awardService) {
        this.awardService = awardService;
    }
}

