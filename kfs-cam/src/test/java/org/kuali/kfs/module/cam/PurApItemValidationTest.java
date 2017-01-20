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
package org.kuali.kfs.module.cam;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.kns.util.MessageList;
import org.kuali.kfs.module.cam.businessobject.AssetTransactionType;
import org.kuali.kfs.module.purap.fixture.PurchasingCapitalAssetFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class PurApItemValidationTest extends MaintenanceRuleTestBase {

    private CapitalAssetManagementModuleService camModuleService;

    protected void setUp() throws Exception {
        super.setUp();
        KNSGlobalVariables.setMessageList(new MessageList());
        if (null == camModuleService) {
            camModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // Tests of validateAccountingLinesNotCapitalAndExpense

//    /**
//     * Tests that, if two object codes of Capital Asset level have been processed, the
//     * rule will be passed.
//     */
//    public void testValidateAccountingLinesNotCapitalAndExpense_TwoCapital() {
//       HashSet<String> set = PurchasingCapitalAssetFixture.TWO_CAPITAL.populateForCapitalAndExpenseCheck();
//       ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_CAPITAL.getObjectCode();
//       assertTrue(camModuleService.validateAccountingLinesNotCapitalAndExpense(set, "1", objectCode));
//    }
//
//    /**
//     * Tests that, if two object codes of a level that is not Capital Asset have been processed,
//     * the rule will be passed.
//     */
//    public void testValidateAccountingLinesNotCapitalAndExpense_TwoExpense() {
//        HashSet<String> set = PurchasingCapitalAssetFixture.TWO_EXPENSE.populateForCapitalAndExpenseCheck();
//        ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_EXPENSE.getObjectCode();
//        assertTrue(camModuleService.validateAccountingLinesNotCapitalAndExpense(set, "1", objectCode));
//    }
//
//    /**
//     * Tests that, if an object code with a level of Capital Asset has been processed together
//     * with an object code not of a level of Capital Asset, then the rule will be failed.
//     */
//    public void testValidateAccountingLinesNotCapitalAndExpense_CapitalExpense() {
//        HashSet<String> set = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.populateForCapitalAndExpenseCheck();
//        ObjectCode objectCode = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.getObjectCode();
//        assertFalse(camModuleService.validateAccountingLinesNotCapitalAndExpense(set, "1", objectCode));
//    }
//
    // Tests of validateLevelCapitalAssetIndication

    @SuppressWarnings("deprecation")
    private ObjectCode getObjectCodeWithLevel(PurchasingCapitalAssetFixture fixture, String levelCode) {
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectLevel objLevel = new ObjectLevel();
        objLevel.setFinancialObjectLevelCode(levelCode);
        objectCode.setFinancialObjectLevel(objLevel);
        objectCode.setFinancialObjectLevelCode(levelCode);
        return objectCode;
    }

//    /**
//     * Tests that the rule will be failed if given an extended price above the
//     * threshold for capital assets, and an object code whose level should be among those listed in
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter.
//     */
//    public void testValidateLevelCapitalAssetIndication_HappyPath() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");
//        assertFalse(camModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//
//    /**
//     * Tests that the rule will be passed if given an extended price above the
//     * threshold for capital assets, but an object code whose level should not be among those listed in
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, but should indicate a definite capital asset.
//     */
//    public void testValidateLevelCapitalAssetIndication_CapCodeLevel() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_CAP_OBJECT_CODE;
//        KualiDecimal itemQuantity = fixture.getQuantity();
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "CAP");
//        assertTrue(camModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//
//    /**
//     * Tests that the rule will be passed if given an extended price above the
//     * threshold for capital assets, but an object code whose level should not be among those listed in
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, but should indicate an expense.
//     */
//    public void testValidateLevelCapitalAssetIndication_ExpenseCodeLevel() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_EXPENSE_OBJECT_CODE;
//        KualiDecimal itemQuantity = fixture.getQuantity();
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "DEBT");
//        assertTrue(camModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//
//
//    /**
//     * Tests that the rule will be passed if given a an extended price below the
//     * threshold for capital assets, but an object code whose level should be among those listed in
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, due to the lower price.
//     */
//    public void testValidateLevelCapitalAssetIndication_NonCapitalPrice() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_POSITIVE_PRICE_POSSIBLE_OBJECT_CODE;
//        KualiDecimal itemQuantity = fixture.getQuantity();
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");
//        assertTrue(camModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//
    // Tests of validateObjectCodeVersusTransactionType

    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_Passing() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectSubType financialObjectSubType = new ObjectSubType();
        objectCode.setFinancialObjectSubTypeCode("AM");
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        AssetTransactionType tranType = new AssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW");
        //TODO: uncomment this when data in MDS jira fixed
//        assertTrue(camModuleService.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1", true));
    }

    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_NotIncludedCombination() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectSubType financialObjectSubType = new ObjectSubType();
        objectCode.setFinancialObjectSubTypeCode("AM");
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        AssetTransactionType tranType = new AssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("FABR"); // "Fabrication"
        //TODO: uncomment this when data in MDS jira fixed
//        assertFalse(camModuleService.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1", true));
    }

    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_NotIncludedSubtype() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectSubType financialObjectSubType = new ObjectSubType();
        objectCode.setFinancialObjectSubTypeCode("BI");
        financialObjectSubType.setFinancialObjectSubTypeName("Bond Issuance");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        AssetTransactionType tranType = new AssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW");
        //TODO: uncomment this when data in MDS jira fixed
//        assertFalse(camModuleService.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1", true));
    }

    // Tests of validateCapitalAssetTransactionTypeVersusRecurrence

//    /**
//     * Tests that, if the rule is given a recurring payment type and a non-recurring transaction type,
//     * the rule will fail.
//     */
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NonRecurringTranType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertFalse(camModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//
//    /**
//     * Tests that, if the rule is given no payment type, and a non-recurring transaction type, the rule will pass.
//     */
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NonRecurringTranTypeAndNoRecurringPaymentType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NO_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertTrue(camModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NoTranType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_NO_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertFalse(camModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_RecurringTranType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_RECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertTrue(camModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_RecurringTranTypeAndNoRecurringPaymentType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NO_PAYMENT_TYPE_RECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertFalse(camModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//
}

