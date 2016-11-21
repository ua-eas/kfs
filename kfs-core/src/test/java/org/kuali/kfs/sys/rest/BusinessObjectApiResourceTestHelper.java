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
package org.kuali.kfs.sys.rest;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.OrganizationExtension;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSubSectionHeaderDefinition;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessObjectApiResourceTestHelper {

    public static List<MaintainableSectionDefinition> createUnitOfMeasureMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("itemUnitOfMeasureCode",
            "itemUnitOfMeasureDescription","active");
        MaintainableItemDefinition itemDef = new MaintainableSubSectionHeaderDefinition();
        itemDef.setName("I should not be here!");
        maintainableItemDefinitions.add(itemDef);
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        return maintainableSections;
    }

    public static List<MaintainableSectionDefinition> createBankMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("bankCode",
            "bankName","bankRoutingNumber","bankAccountNumber");
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        return maintainableSections;
    }

    public static List<MaintainableSectionDefinition> createOrganizationMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("chartOfAccountsCode",
            "organizationCode","responsibilityCenterCode","organizationName",
            "organizationExtension.chartOfAccountsCode","organizationExtension.organizationCode",
            "organizationExtension.hrmsCompany","organizationExtension.hrmsIuPositionAllowedFlag",
            "organizationExtension.hrmsIuTenureAllowedFlag");
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        return maintainableSections;
    }

    public static List<MaintainableSectionDefinition> createIndirectCostRecoveryTypeMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("code","name","active");

        MaintainableCollectionDefinition maintainableCollectionDefinition = new MaintainableCollectionDefinition();
        maintainableCollectionDefinition.setName("indirectCostRecoveryExclusionTypeDetails");
        maintainableCollectionDefinition.setBusinessObjectClass(TaxRegionRate.class);
        List<MaintainableFieldDefinition> collectionFieldDefinitions = BusinessObjectApiResourceTestHelper.createFieldDefinitions("chartOfAccountsCode","financialObjectCode","newCollectionRecord","active");

        maintainableCollectionDefinition.setMaintainableFields(collectionFieldDefinitions);
        maintainableItemDefinitions.add(maintainableCollectionDefinition);
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        return maintainableSections;
    }

    public static List<MaintainableSectionDefinition> createAccountMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("chartOfAccountsCode",
            "accountNumber","accountName","closed");
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        return maintainableSections;
    }

    public static void addItemDefinition(List<MaintainableItemDefinition> maintainableItemDefinitions, String fieldName) {
        MaintainableItemDefinition itemDef = new MaintainableFieldDefinition();
        itemDef.setName(fieldName);
        maintainableItemDefinitions.add(itemDef);
    }

    public static List<MaintainableItemDefinition> createItemDefinitions(String... fieldNames) {
        List<MaintainableItemDefinition> maintainableItemDefinitions = new ArrayList<>();
        for (String fieldName : fieldNames) {
            addItemDefinition(maintainableItemDefinitions, fieldName);
        }
        return maintainableItemDefinitions;
    }

    public static void addFieldDefinition(List<MaintainableFieldDefinition> maintainableItemDefinitions, String fieldName) {
        MaintainableFieldDefinition itemDef = new MaintainableFieldDefinition();
        itemDef.setName(fieldName);
        maintainableItemDefinitions.add(itemDef);
    }

    public static List<MaintainableFieldDefinition> createFieldDefinitions(String... fieldNames) {
        List<MaintainableFieldDefinition> maintainableItemDefinitions = new ArrayList<>();
        for (String fieldName : fieldNames) {
            addFieldDefinition(maintainableItemDefinitions, fieldName);
        }
        return maintainableItemDefinitions;
    }

    public static BusinessObjectEntry getDDEntry(Class clazz) {
        BusinessObjectEntry result = new BusinessObjectEntry();
        result.setBusinessObjectClass(clazz);
        return result;
    }

    public static UnitOfMeasure getUom() {
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setItemUnitOfMeasureCode("DEV");
        uom.setItemUnitOfMeasureDescription("Developer");
        uom.setActive(true);
        uom.setObjectId("12345");
        return uom;
    }

    public static Bank getBank() {
        Bank bank = new Bank();
        bank.setBankCode("FW");
        bank.setBankName("Fells Wargo");
        bank.setBankRoutingNumber("7777444477774444");
        bank.setBankAccountNumber("3333666644447777");
        bank.setObjectId("BK12345");

        Chart cashOffsetFinancialChartOfAccount = new Chart();
        cashOffsetFinancialChartOfAccount.setObjectId("BNKCHART5554455");
        bank.setCashOffsetFinancialChartOfAccount(cashOffsetFinancialChartOfAccount);
        return bank;
    }

    public static Map<String, Object> getSerializedBank() {
        Bank bank = getBank();

        Map<String, Object> bankMap = new HashMap<>();
        bankMap.put(KFSPropertyConstants.BANK_CODE, bank.getBankCode());
        bankMap.put(KFSPropertyConstants.BANK_NAME, bank.getBankName());
        bankMap.put(KFSPropertyConstants.BANK_ROUTING_NUMBER, bank.getBankRoutingNumber());
        bankMap.put(KFSPropertyConstants.BANK_ACCOUNT_NUMBER, bank.getBankAccountNumber());
        bankMap.put(KFSPropertyConstants.OBJECT_ID, bank.getObjectId());

        Map<String, Object> chartMap = new HashMap<>();
        chartMap.put(KFSPropertyConstants.LINK, "https://kuali.co/fin/coa/api/v1/reference/coat/"+bank.getCashOffsetFinancialChartOfAccount().getObjectId());
        bankMap.put(KFSPropertyConstants.CASH_OFFSET_FINANCIAL_CHART_OF_ACCOUNT, chartMap);
        return bankMap;
    }

    public static Organization getOrganization() {
        Organization org = new Organization();
        org.setObjectId("ORG12345");
        org.setChartOfAccountsCode("BL");
        org.setOrganizationCode("ANTH");
        org.setResponsibilityCenterCode("04");
        org.setOrganizationName("Anthropology");

        OrganizationExtension orgExt = new OrganizationExtension();
        orgExt.setObjectId("ORGEXT12345");
        orgExt.setChartOfAccountsCode("BL");
        orgExt.setOrganizationCode("ANTH");
        orgExt.setHrmsCompany("IU");
        orgExt.setHrmsIuPositionAllowedFlag(false);
        orgExt.setHrmsIuTenureAllowedFlag(false);

        org.setOrganizationExtension(orgExt);

        Account organizationPlantAccount = new Account();
        organizationPlantAccount.setObjectId("ACCT777444");
        org.setOrganizationPlantAccount(organizationPlantAccount);

        Chart organizationPlantChart = new Chart();
        organizationPlantChart.setObjectId("CHART777444");
        org.setOrganizationPlantChart(organizationPlantChart);
        return org;
    }

    public static IndirectCostRecoveryType getIndirectCostRecoveryType() {
        IndirectCostRecoveryType icrt = new IndirectCostRecoveryType();
        icrt.setName("IndirectCostRecoveryType");
        icrt.setCode("ICRT");
        icrt.setActive(true);

        List<IndirectCostRecoveryExclusionType> icretDetails = new ArrayList<>();
        icrt.setIndirectCostRecoveryExclusionTypeDetails(icretDetails);
        IndirectCostRecoveryExclusionType icret = new IndirectCostRecoveryExclusionType();
        icret.setChartOfAccountsCode("BL");
        icret.setFinancialObjectCode("FOC");
        icret.setNewCollectionRecord(false);
        icret.setActive(true);
        icretDetails.add(icret);

        return icrt;
    }

    public static Deposit getDeposit(Date now) {
        Deposit deposit = new Deposit();
        deposit.setDepositBankCode("FellsWargo");
        Bank bank = new Bank();
        bank.setObjectId("B123");
        deposit.setBank(bank);
        deposit.setDepositDate(now);
        deposit.setDepositAmount(new KualiDecimal(100.02));

        DepositCashReceiptControl receiptControl1 = new DepositCashReceiptControl();
        receiptControl1.setObjectId("DC001");
        DepositCashReceiptControl receiptControl2 = new DepositCashReceiptControl();
        receiptControl2.setObjectId("DC002");
        deposit.getDepositCashReceiptControl().add(receiptControl1);
        deposit.getDepositCashReceiptControl().add(receiptControl2);
        return deposit;
    }

    public static Account getAccount() {
        Account account = new Account();
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("12345");
        account.setAccountName("MyAccount");
        account.setClosed(false);
        return account;
    }

    public static ModuleConfiguration getSysModuleConfiguration(DataDictionaryService dataDictionaryService) {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-SYS");
        result.setPackagePrefixes(new ArrayList<>());
        result.getPackagePrefixes().add("org.kuali.kfs.sys");
        result.setDataDictionaryService(dataDictionaryService);
        return result;
    }

    public static ModuleConfiguration getCoaModuleConfiguration(DataDictionaryService dataDictionaryService) {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-COA");
        result.setPackagePrefixes(new ArrayList<>());
        result.getPackagePrefixes().add("org.kuali.kfs.coa");
        result.setDataDictionaryService(dataDictionaryService);
        return result;
    }

    public static Map<String, String> makeMap(String namespaceCode, String className) {
        Map<String, String> result = new HashMap<>();
        result.put(KRADConstants.NAMESPACE_CODE, namespaceCode);
        result.put(KRADConstants.COMPONENT_NAME, className);
        return result;
    }
}
