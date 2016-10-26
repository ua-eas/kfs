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

package org.kuali.kfs.fp.document.validation.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.coreservice.api.parameter.EvaluationOperator;
import org.kuali.kfs.coreservice.api.parameter.ParameterType;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.impl.parameter.ParameterEvaluatorServiceImpl;

public class DisbursementVoucherDocumentNonResidentAlienInformationRuleTest {
    protected final static String[] INCOME_CLASS_CODES = new String[] {
            DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_NON_REPORTABLE,
            DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_ROYALTIES,
            DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR,
            DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_FELLOWSHIP
    };
    protected DisbursementVoucherNonResidentAlienInformationValidation validation;
    protected ParameterService parameterService;

    private static Parameter fellowshipIncomeClassOnlyParameter;
    private static Parameter allIncomeClassParameter;
    private static Parameter emptyIncomeClassParameter;
    private static Parameter blankIncomeClassParameter;

    static {
        Parameter.Builder paramBuilder = newIncomeClassCodesRequiringStateParameter();
        paramBuilder.setValue(DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_FELLOWSHIP);
        paramBuilder.setEvaluationOperator(EvaluationOperator.ALLOW);
        fellowshipIncomeClassOnlyParameter = paramBuilder.build();

        // Note that NON-REPORTABLE income class means that state and federal taxes
        // are not reported so it is not included in the list of 'all' class codes
        paramBuilder = newIncomeClassCodesRequiringStateParameter();
        paramBuilder.setValue(
            String.format("%s;%s;%s",
                DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_ROYALTIES,
                DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_INDEPENDENT_CONTRACTOR,
                DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_FELLOWSHIP
            )
        );
        paramBuilder.setEvaluationOperator(EvaluationOperator.ALLOW);
        allIncomeClassParameter = paramBuilder.build();

        paramBuilder = newIncomeClassCodesRequiringStateParameter();
        paramBuilder.setValue("");
        paramBuilder.setEvaluationOperator(EvaluationOperator.ALLOW);
        emptyIncomeClassParameter = paramBuilder.build();

        paramBuilder = newIncomeClassCodesRequiringStateParameter();
        paramBuilder.setValue(" ");
        paramBuilder.setEvaluationOperator(EvaluationOperator.ALLOW);
        blankIncomeClassParameter = paramBuilder.build();
    }

    private static Parameter.Builder newIncomeClassCodesRequiringStateParameter() {
        return Parameter.Builder.create(
            "KFS",
            "KFS-FP",
            "DisbursementVoucher",
            DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM,
            ParameterType.Builder.create("CONFG")
        );
    }

    @Before
    public void setUp() {
        parameterService = EasyMock.createMock(ParameterService.class);

        ParameterEvaluatorService parameterEvaluatorService = new ParameterEvaluatorServiceImpl();
        ((ParameterEvaluatorServiceImpl)parameterEvaluatorService).setParameterService(parameterService);

        validation = new DisbursementVoucherNonResidentAlienInformationValidation();
        validation.setParameterService(parameterService);
        validation.setParameterEvaluatorService(parameterEvaluatorService);
    }

    @Test
    public void testNonZeroStateTaxWithFellowshipOnlyParameter() {
        KualiDecimal stateIncomeTax = new KualiDecimal(15.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(fellowshipIncomeClassOnlyParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertFalse(
                String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code %s with fellowship only parameter", stateIncomeTax, incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
            );
        }
        Assert.assertFalse(
            String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code = null with fellowship only parameter", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testNonZeroStateTaxWithAllParameters() {
        KualiDecimal stateIncomeTax = new KualiDecimal(15.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(allIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertFalse(
                String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code %s with all parameters", stateIncomeTax, incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
            );
        }
        Assert.assertFalse(
            String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code = null with all parameters", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testNonZeroStateTaxWithEmptyParameter() {
        KualiDecimal stateIncomeTax = new KualiDecimal(15.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(emptyIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertFalse(
                String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code %s with empty parameter", stateIncomeTax, incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
            );
        }
        Assert.assertFalse(
            String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code = null with empty parameter", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testNonZeroStateTaxWithBlankParameter() {
        KualiDecimal stateIncomeTax = new KualiDecimal(15.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(blankIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertFalse(
                String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code %s with blank parameter", stateIncomeTax, incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
            );
        }
        Assert.assertFalse(
            String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = %s and income class code = null with blank parameter", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testNullStateTaxWithFellowshipOnlyParameter() {
        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(fellowshipIncomeClassOnlyParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            if (incomeClassCode.equals(DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_FELLOWSHIP)) {
                Assert.assertFalse(
                    String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = null and income class code %s with fellowship only parameter", incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(null, incomeClassCode)
                );
            } else {
                Assert.assertTrue(
                    String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code %s with fellowship only parameter", incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(null, incomeClassCode)
                );
            }
        }

        Assert.assertTrue(
            "Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code = null with fellowship only parameter",
            validation.checkAllowZeroStateIncomeTax(null, null)
        );
    }

    @Test
    public void testNullStateTaxWithAllParameters() {
        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(allIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            if (incomeClassCode.equals(DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_NON_REPORTABLE)) {
                Assert.assertTrue(
                    String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code %s with all parameters", incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(null, incomeClassCode)
                );
            } else {
                Assert.assertFalse(
                    String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = null and income class code %s with all parameters", incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(null, incomeClassCode)
                );
            }
        }

        Assert.assertTrue(
            "Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code = null with all parameters",
            validation.checkAllowZeroStateIncomeTax(null, null)
        );
    }

    @Test
    public void testNullStateTaxWithEmptyParameter() {
        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(emptyIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertTrue(
                String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code %s with empty parameter", incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(null, incomeClassCode)
            );
        }

        Assert.assertTrue(
            "Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code = null with empty parameter",
            validation.checkAllowZeroStateIncomeTax(null, null)
        );
    }

    @Test
    public void testNullStateTaxWithBlankParameter() {
        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(blankIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertTrue(
                String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code %s with blank parameter", incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(null, incomeClassCode)
            );
        }

        Assert.assertTrue(
            "Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code = null with blank parameter",
            validation.checkAllowZeroStateIncomeTax(null, null)
        );
    }

    @Test
    public void testZeroStateTaxWithFellowshipOnlyParameter() {
        KualiDecimal stateIncomeTax = new KualiDecimal(0.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(fellowshipIncomeClassOnlyParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            if (incomeClassCode.equals(DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_FELLOWSHIP)) {
                Assert.assertFalse(
                    String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = null and income class code %s with fellowship only parameter", stateIncomeTax, incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
                );
            } else {
                Assert.assertTrue(
                    String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code %s with fellowship only parameter", stateIncomeTax, incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
                );
            }
        }

        Assert.assertTrue(
            String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = %s and income class code = null with fellowship only parameter", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testZeroStateTaxWithAllParameters() {
        KualiDecimal stateIncomeTax = new KualiDecimal(0.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(allIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            if (incomeClassCode.equals(DisbursementVoucherConstants.NRA_TAX_INCOME_CLASS_NON_REPORTABLE)) {
                Assert.assertTrue(
                    String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = null and income class code %s with all parameters", stateIncomeTax, incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
                );
            } else {
                Assert.assertFalse(
                    String.format("Expected checkAllowStateZeroIncomeTax to return FALSE for stateIncomeTax = null and income class code %s with all parameters", stateIncomeTax, incomeClassCode),
                    validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
                );
            }
        }

        Assert.assertTrue(
            String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = %s and income class code = null with all parameters", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testZeroStateTaxWithEmptyParameter() {
        KualiDecimal stateIncomeTax = new KualiDecimal(0.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(emptyIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertTrue(
                String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = %s and income class code %s with empty parameter", stateIncomeTax, incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
            );
        }

        Assert.assertTrue(
            String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = %s and income class code = null with empty parameter", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }

    @Test
    public void testZeroStateTaxWithBlankParameter() {
        KualiDecimal stateIncomeTax = new KualiDecimal(0.0);

        EasyMock.expect(
            parameterService.getParameter(
                DisbursementVoucherDocument.class,
                DisbursementVoucherConstants.INCOME_CLASS_CODES_REQUIRING_STATE_TAX_PARM_NM
            )
        ).andReturn(blankIncomeClassParameter).anyTimes();
        EasyMock.replay(parameterService);

        for (String incomeClassCode : INCOME_CLASS_CODES) {
            Assert.assertTrue(
                String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = %s and income class code %s with blank parameter", stateIncomeTax, incomeClassCode),
                validation.checkAllowZeroStateIncomeTax(stateIncomeTax, incomeClassCode)
            );
        }

        Assert.assertTrue(
            String.format("Expected checkAllowStateZeroIncomeTax to return TRUE for stateIncomeTax = %s and income class code = null with blank parameter", stateIncomeTax),
            validation.checkAllowZeroStateIncomeTax(stateIncomeTax, null)
        );
    }
}
