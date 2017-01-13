/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 The Kuali Foundation
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

package org.kuali.kfs.gl.rest.resource;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("general-ledger-configuration")
public class GeneralLedgerConfigurationResource {
    private static volatile AccountService accountService;
    private static volatile ParameterService parameterService;

    @GET
    public Response getGeneralLedgerConfiguration() {
        Map<String, Object> configuration = new HashMap<>();

        configuration.put("accountsCanCrossCharts", getAccountService().accountsCanCrossCharts());
        configuration.put("continuationAccountBypassBalanceTypeCodes", Arrays.asList( "EX","IE","PE" )); // yep.  really.
        configuration.put("continuationAccountBypassOriginationCodes", getParameterService().getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES));
        configuration.put("continuationAccountBypassDocumentTypeCodes", getParameterService().getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES));
        configuration.put("contractsAndGrantsDenotingValues", getParameterService().getParameterValuesAsString(Account.class, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE));
        configuration.put("fundGroupDenotesContractsandGrants", getParameterService().getParameterValueAsBoolean(Account.class, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG));
        configuration.put("annualClosingDocumentType", getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE));
        configuration.put("scrubberValidationDaysOffset", getParameterService().getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET));
        configuration.put("objectTypeBypassOriginations", getParameterService().getParameterValuesAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OBJECT_TYPE_BYPASS_ORIGINATIONS));

        return Response.ok(configuration).build();
    }

    public static AccountService getAccountService() {
        if (accountService == null) {
            accountService = SpringContext.getBean(AccountService.class);
        }
        return accountService;
    }

    public static ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    public static void setAccountService(AccountService accountService) {
        GeneralLedgerConfigurationResource.accountService = accountService;
    }

    public static void setParameterService(ParameterService parameterService) {
        GeneralLedgerConfigurationResource.parameterService = parameterService;
    }
}
