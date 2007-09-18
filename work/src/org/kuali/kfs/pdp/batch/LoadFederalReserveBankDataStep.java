/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp.batch;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.service.AchBankService;

public class LoadFederalReserveBankDataStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoadFederalReserveBankDataStep.class);

    private AchBankService achBankService;
    private KualiConfigurationService kualiConfigurationService;

    public boolean execute(String jobName) throws InterruptedException {
        LOG.debug("execute() started");

        String filename = kualiConfigurationService.getApplicationParameterValue(PdpConstants.PDP_APPLICATION, PdpConstants.ApplicationParameterKeys.ACH_BANK_INPUT_FILE);
        // TODO Fix this
        String stagingDir = "/Users/jsissom/";

        return achBankService.reloadTable(stagingDir + filename);
    }

    public void setAchBankService(AchBankService achBankService) {
        this.achBankService = achBankService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
