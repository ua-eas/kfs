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
package org.kuali.kfs.module.cam;



/**
 * Constants for cams business object property names.
 */
public class CamsPropertyConstants {

    public static class Asset {
        public static final String DOCUMENT_TYPE_CODE = "documentTypeCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String PRIMARY_DEPRECIATION_METHOD = "primaryDepreciationMethodCode";
        public static final String SALVAGE_AMOUNT = "salvageAmount";
        public static final String ASSET_DATE_OF_SERVICE = "capitalAssetInServiceDate";
        public static final String ASSET_RETIREMENT_FISCAL_YEAR = "retirementFiscalYear";
        public static final String ASSET_RETIREMENT_FISCAL_MONTH = "retirementPeriodCode";
        public static final String ASSET_INVENTORY_STATUS = "inventoryStatusCode";
        public static final String ASSET_WARRANTY_WARRANTY_NUMBER = "assetWarranty.warrantyNumber";
        public static final String NATIONAL_STOCK_NUMBER = "nationalStockNumber";
        public static final String GOVERNMENT_TAG_NUMBER = "governmentTagNumber";
        public static final String OLD_TAG_NUMBER = "oldTagNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE = "organizationOwnerChartOfAccountsCode";
        public static final String ORGANIZATION_OWNER_ACCOUNT_NUMBER = "organizationOwnerAccountNumber";
        public static final String ORGANIZATION_CODE = "organizationOwnerAccount.organizationCode";
        public static final String ORGANIZATION_OWNER_ACCOUNTS_COA_CODE = "organizationOwnerAccount.chartOfAccountsCode";
        public static final String ORGANIZATION_TEXT = "assetOrganization.organizationText";
        public static final String VENDOR_NAME = "vendorName";
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String CAPITAL_ASSET_DESCRIPTION = "capitalAssetDescription";
        public static final String ASSET_DEPRECIATION_DATE = "depreciationDate";
        public static final String ASSET_PAYMENTS = "assetPayments";
        public static final String ASSET_COMPONENTS = "assetComponents";
        public static final String ASSET_WARRANTY = "assetWarranty";
        public static final String ASSET_REPAIR_HISTORY = "assetRepairHistory";
        public static final String ESTIMATED_SELLING_PRICE = "estimatedSellingPrice";
        public static final String ESTIMATED_FABRICATION_COMPLETION_DATE = "estimatedFabricationCompletionDate";
        public static final String FABRICATION_ESTIMATED_TOTAL_AMOUNT = "fabricationEstimatedTotalAmount";
        public static final String MANUFACTURER_NAME = "manufacturerName";
        public static final String MANUFACTURER_MODEL_NUMBER = "manufacturerModelNumber";
        public static final String CREATE_DATE = "createDate";
        public static final String FINANCIAL_DOCUMENT_POSTING_PERIOD_CODE = "financialDocumentPostingPeriodCode";
        public static final String FINANCIAL_DOCUMENT_POSTING_YEAR = "financialDocumentPostingYear";
        public static final String REPRESENTATIVE_UNIVERSAL_IDENTIFIER = "representativeUniversalIdentifier";
        public static final String ASSET_REPRESENTATIVE = "assetRepresentative";
        public static final String RECEIVE_DATE = "receiveDate";
        public static final String REPLACEMENT_AMOUNT = "replacementAmount";
        public static final String SERIAL_NUMBER = "serialNumber";
        public static final String LAST_INVENTORY_DATE = "lastInventoryDate";
        public static final String LAST_INVENTORY_DATE_UPDATE_BUTTON = "lastInventoryDateUpdateButton";
        public static final String TOTAL_COST_AMOUNT = "totalCostAmount";
        public static final String ACCUMULATED_DEPRECIATION = "accumulatedDepreciation";
        public static final String BOOK_VALUE = "bookValue";
        public static final String FEDERAL_CONTRIBUTION = "federalContribution";
        public static final String ORGANIZATION_OWNER_ACCOUNT = "organizationOwnerAccount";
        public static final String CAPITAL_ASSET_TYPE = "capitalAssetType";
        public static final String RETIREMENT_INFO_MERGED_TARGET = "retirementInfo.assetRetirementGlobal.mergedTargetCapitalAssetNumber";
        public static final String ASSET_LOCATIONS = "assetLocations";
        public static final String BUILDING_SUB_ROOM_NUMBER = "buildingSubRoomNumber";
        public final static String ACQUISITION_TYPE_CODE = "acquisitionTypeCode";
        public static final String FABRICATION_ESTIMATED_RETENTION_YEARS = "fabricationEstimatedRetentionYears";
        public static final String FINANCIAL_OBJECT_SUB_TYP_CODE = "financialObjectSubTypeCode";
        public static final String CONDITION_CODE = "conditionCode";
        public static final String INVENTORY_STATUS_CODE = "inventoryStatusCode";
        public static final String REP_USER_AUTH_ID = "assetRepresentative.principalName";
        public static final String LAND_COUNTRY_NAME = "landCountyName";
        public static final String LAND_ACREAGE_SIZE = "landAcreageSize";
        public static final String LAND_PARCEL_NUMBER = "landParcelNumber";
        public static final String QUANTITY = "quantity";
        // Asset Edit reference objects
        public static final String REF_ACQUISITION_TYPE = "acquisitionType";
        public static final String REF_ASSET_CONDITION = "condition";
        public static final String REF_ASSET_DEPRECATION_METHOD = "assetPrimaryDepreciationMethod";
        public static final String REF_ASSET_STATUS = "inventoryStatus";
        public static final String REF_ASSET_TYPE = "capitalAssetType";
        public static final String REF_OBJECT_SUB_TYPE = "financialObjectSubType";
        public static final String REF_CONTRACTS_AND_GRANTS_AGENCY = "agency";

        public static class AssetLocation {
            public static final String CONTACT_NAME = "offCampusLocation.assetLocationContactName";
            public static final String STREET_ADDRESS = "offCampusLocation.assetLocationStreetAddress";
            public static final String CITY_NAME = "offCampusLocation.assetLocationCityName";
            public static final String STATE_CODE = "offCampusLocation.assetLocationStateCode";
            public static final String ZIP_CODE = "offCampusLocation.assetLocationZipCode";
            public static final String COUNTRY_CODE = "offCampusLocation.assetLocationCountryCode";
        }
    }

    public static class AssetObject {
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String ACTIVE = "active";
    }

    public static class AssetPayment {
        public static final String TRANSFER_PAYMENT_CODE = "transferPaymentCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String ORIGINATION_CODE = "financialSystemOriginationCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String OBJECT_CODE = "financialObjectCode";
        public static final String FINANCIAL_OBJECT = "financialObject";
        public static final String OBJECT_CODE_CURRENT ="objectCodeCurrent"; 
        public static final String SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String OBJECT_TYPE_CODE = "financialObject.financialObjectTypeCode";
        public static final String PROJECT_CODE = "projectCode";
        public static final String PAYMENT_SEQ_NUMBER = "paymentSequenceNumber";
        public static final String TRANSACTION_DC_CODE = "transactionDebitCreditCode";
        public static final String ORGANIZATION_REFERENCE_ID = "organizationReferenceId";
        public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
        public static final String REQUISITION_NUMBER = "requisitionNumber";
        public static final String FINANCIAL_DOCUMENT_POSTING_DATE = "financialDocumentPostingDate";
        public static final String FINANCIAL_DOCUMENT_POSTING_YEAR = "financialDocumentPostingYear";
        public static final String FINANCIAL_DOCUMENT_POSTING_PERIOD_CODE = "financialDocumentPostingPeriodCode";
        public static final String ACCOUNT_CHARGE_AMOUNT = "accountChargeAmount";
        public static final String PRIMARY_DEPRECIATION_BASE_AMOUNT = "primaryDepreciationBaseAmount";
        public static final String ACCUMULATED_DEPRECIATION_AMOUNT = "accumulatedPrimaryDepreciationAmount";
        public static final String PREVIOUS_YEAR_DEPRECIATION_AMOUNT = "previousYearPrimaryDepreciationAmount";
        public static final String PERIOD_1_DEPRECIATION_AMOUNT = "period1Depreciation1Amount";
        public static final String PERIOD_2_DEPRECIATION_AMOUNT = "period2Depreciation1Amount";
        public static final String PERIOD_3_DEPRECIATION_AMOUNT = "period3Depreciation1Amount";
        public static final String PERIOD_4_DEPRECIATION_AMOUNT = "period4Depreciation1Amount";
        public static final String PERIOD_5_DEPRECIATION_AMOUNT = "period5Depreciation1Amount";
        public static final String PERIOD_6_DEPRECIATION_AMOUNT = "period6Depreciation1Amount";
        public static final String PERIOD_7_DEPRECIATION_AMOUNT = "period7Depreciation1Amount";
        public static final String PERIOD_8_DEPRECIATION_AMOUNT = "period8Depreciation1Amount";
        public static final String PERIOD_9_DEPRECIATION_AMOUNT = "period9Depreciation1Amount";
        public static final String PERIOD_10_DEPRECIATION_AMOUNT = "period10Depreciation1Amount";
        public static final String PERIOD_11_DEPRECIATION_AMOUNT = "period11Depreciation1Amount";
        public static final String PERIOD_12_DEPRECIATION_AMOUNT = "period12Depreciation1Amount";
    }

    public static class AssetPaymentDetail {
        public static final String SEQUENCE_NUMBER = "sequenceNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String OBJECT_CODE = "objectCode";
        public static final String SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String PROJECT_CODE = "projectCode";
        public static final String ORGANIZATION_REFERENCE_ID = "organizationReferenceId";
        public static final String AMOUNT = "amount";
        public static final String PURCHASE_ORDER = "purchaseOrderNumber";
        public static final String REQUISITION_NUMBER = "requisitionNumber";
        public static final String DOCUMENT_NUMBER = "expenditureFinancialDocumentNumber";
        public static final String DOCUMENT_TYPE_CODE = "expenditureFinancialDocumentTypeCode";
        public static final String DOCUMENT_TYPE = "expenditureFinancialSystemDocumentTypeCode";
        public static final String ORIGINATION_CODE = "expenditureFinancialSystemOriginationCode";
        public static final String ORIGINATION = "expenditureFinancialSystemOrigination";
        public static final String DOCUMENT_POSTING_DATE = "expenditureFinancialDocumentPostedDate";
        public static final String DOCUMENT_POSTING_FISCAL_YEAR = "postingYear";
        public static final String DOCUMENT_POSTING_FISCAL_MONTH = "postingPeriodCode";
        public static final String ACCOUNT = "account";
        public static final String VERSION_NUMBER = "versionNumber";
    }

    public static class AssetPaymentAssetDetail {
        public static final String ASSET = "asset";

    }

    public static class AssetLocationGlobalDetail {
        public static final String ASSET = "asset";
        public static final String CAPITAL_ASSET_NUMBER = "assetLocationGlobalDetails.capitalAssetNumber";
    }

    public static class AssetType {
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String ASSET_DEPRECIATION_LIFE_LIMIT = "depreciableLifeLimit";
    }

    public static class AssetOrganization {
        public static final String ASSET_ORGANIZATION = "assetOrganization";
        public static final String ORGANIZATION_TAG_NUMBER = "organizationTagNumber";
    }

    public static class AssetTransferDocument {
        public static final String ORGANIZATION = "organization";
        public static final String ORGANIZATION_OWNER_ACCOUNT = "organizationOwnerAccount";
        public static final String OLD_ORGANIZATION_OWNER_ACCOUNT = "oldOrganizationOwnerAccount";
        public static final String ORGANIZATION_OWNER_ACCOUNT_NUMBER = "organizationOwnerAccountNumber";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE = "organizationOwnerChartOfAccountsCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING = "building";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static final String BUILDING_SUB_ROOM_NUMBER = "buildingSubRoomNumber";
        public static final String CAMPUS = "campus";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS = "organizationOwnerChartOfAccounts";
        public static final String OFF_CAMPUS_STATE = "offCampusState";
        public static final String BUILDING_ROOM = "buildingRoom";
        public static final String OFF_CAMPUS_STATE_CODE = "offCampusStateCode";
        public static final String OFF_CAMPUS_ADDRESS = "offCampusAddress";
        public static final String OFF_CAMPUS_CITY = "offCampusCityName";
        public static final String OFF_CAMPUS_ZIP = "offCampusZipCode";
        public static final String OFF_CAMPUS_CONTACT_NAME = "offCampusName";
        public static final String LOCATION_TAB = "locationTabKey";
        public static final String TRANSFER_FUND_FINANCIAL_DOC = "transferOfFundsFinancialDocument";
        public static final String TRANSFER_FUND_FINANCIAL_DOC_NUM = "transferOfFundsFinancialDocumentNumber";
        public static final String REP_USER_AUTH_ID = "assetRepresentative.principalName";
        public static final String ASSET = "asset";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    }

    public static class AssetComponent {
        public static final String COMPONENT_NUMBER = "componentNumber";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    }

    public static class AssetRetirementGlobal {
        public static final String SHARED_RETIREMENT_INFO = "sharedRetirementInfo";
        public static final String MERGED_TARGET_CAPITAL_ASSET_NUMBER = "mergedTargetCapitalAssetNumber";
        public static final String MERGED_TARGET_CAPITAL_ASSET = "mergedTargetCapitalAsset";
        public static final String MERGED_TARGET_CAPITAL_ASSET_DESC = "mergedTargetCapitalAssetDescription";
        public static final String RETIREMENT_REASON_CODE = "retirementReasonCode";
        public static final String ASSET_RETIREMENT_GLOBAL_DETAILS = "assetRetirementGlobalDetails";
        public static final String CAPITAL_ASSET_NUMBER = "assetRetirementGlobalDetails.capitalAssetNumber";
    }

    public static class AssetRetirementGlobalDetail {
        public static final String ASSET = "asset";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String RETIREMENT_CHART_OF_ACCOUNTS_CODE = "retirementChartOfAccountsCode";
        public static final String RETIREMENT_ACCOUNT_NUMBER = "retirementAccountNumber";
        public static final String RETIREMENT_CONTACT_NAME = "retirementContactName";
        public static final String RETIREMENT_INSTITUTION_NAME = "retirementInstitutionName";
        public static final String RETIREMENT_STREET_ADDRESS = "retirementStreetAddress";
        public static final String RETIREMENT_CITY_NAME = "retirementCityName";
        public static final String RETIREMENT_STATE_CODE = "retirementStateCode";
        public static final String RETIREMENT_ZIP_CODE = "retirementZipCode";
        public static final String RETIREMENT_COUNTRY_CODE = "retirementCountryCode";
        public static final String RETIREMENT_PHONE_NUMBER = "retirementPhoneNumber";
        public static final String ESTIMATED_SELLING_PRICE = "estimatedSellingPrice";
        public static final String SALE_PRICE = "salePrice";
        public static final String CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER = "cashReceiptFinancialDocumentNumber";
        public static final String HANDLING_FEE_AMOUNT = "handlingFeeAmount";
        public static final String PREVENTIVE_MAINTENANCE_AMOUNT = "preventiveMaintenanceAmount";
        public static final String BUYER_DESCRIPTION = "buyerDescription";
        public static final String PAID_CASE_NUMBER = "paidCaseNumber";
    }

    public static class AssetLocationGlobal {
        public static final String ASSET_LOCATION_GLOBAL_DETAILS = "assetLocationGlobalDetails";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static final String BUILDING_SUB_ROOM_NUMBER = "buildingSubRoomNumber";
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
    }

    public static class EquipmentLoanOrReturnDocument {
        public static final String LOAN_DATE = "loanDate";
        public static final String EXPECTED_RETURN_DATE = "expectedReturnDate";
        public static final String LOAN_RETURN_DATE = "loanReturnDate";
        public static final String BORROWER_UNIVERSAL_USER = "borrowerPerson";
        public static final String BORROWER_UNIVERSAL_INDENTIFIER = "borrowerUniversalIdentifier";
        public static final String BORROWER_STATE = "borrowerState";
        public static final String BORROWER_STATE_CODE = "borrowerStateCode";
        public static final String BORROWER_STAORAGE_STATE = "borrowerStorageState";
        public static final String BORROWER_STAORAGE_STATE_CODE = "borrowerStorageStateCode";
        public static final String BORROWER_POSTAL_ZIP_CODE = "borrowerPostalZipCode";
        public static final String BORROWER_STORAGE_ZIP_CODE = "borrowerStorageZipCode";
        public static final String BORROWER_COUNTRY_CODE = "borrowerCountryCode";
        public static final String BORROWER_STAORAGE_COUNTRY_CODE = "borrowerStorageCountryCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String DOCUMENT_HEADER = "documentHeader";
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String BORROWER_PRINCIPAL_NAME = "borrowerPerson.principalName";
    }

    public static class AssetGlobal {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String ORGANIZATION_OWNER_ACCOUNT = "organizationOwnerAccount";
        public static final String ORGANIZATION_OWNER_CHART = "organizationOwnerChartOfAccounts";
        public static final String INVENTORY_STATUS_CODE = "inventoryStatusCode";
        public static final String CAPITAL_ASSET_DESCRIPTION = "capitalAssetDescription";
        public static final String ASSET_SHARED_DETAILS = "assetSharedDetails";
        public static final String ORGANIZATION_OWNER_ACCOUNT_NUMBER = "organizationOwnerAccountNumber";
        public static final String VENDOR_NAME = "vendorName";
        public static final String MFR_NAME = "manufacturerName";
        public static final String ORGANIZATION_TEXT = "organizationText";
        public static final String ACQUISITION_TYPE = "acquisitionType";
        public static final String ACQUISITION_TYPE_CODE = "acquisitionTypeCode";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS = "organizationOwnerChartOfAccountsCode";
        public static final String ORGANIZATION_OWNER_ACCT_NUMBER = "organizationOwnerAccountNumber";
        public static final String CAPITAL_ASSET_TYPE = "capitalAssetType";
        public static final String ASSET_PAYMENT_DETAILS = "assetPaymentDetails";
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER = "separateSourceCapitalAssetNumber";
        public static final String SEPERATE_SOURCE_PAYMENT_SEQUENCE_NUMBER = "separateSourcePaymentSequenceNumber";
        public static final String CAPITAL_ASSET_DEPRECIATION_DATE = "capitalAssetDepreciationDate";
        public static final String SEPARATE_SOURCE_CAPITAL_ASSET = "separateSourceCapitalAsset";
        public static final String SEPARATE_SOURCE_REMAINING_AMOUNT = "separateSourceRemainingAmount";
    }

    public static class AssetGlobalDetail {
        public static String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String VERSION_NUM = "versionNumber";
        public static final String CAMPUS = "campus";
        public static final String BUILDING = "building";
        public static final String BUILDING_ROOM = "buildingRoom";
        public static final String OFF_CAMPUS_STATE = "offCampusState";
        public static String CAMPUS_CODE = "campusCode";
        public static String BUILDING_CODE = "buildingCode";
        public static String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static String BUILDING_SUB_ROOM_NUMBER = "buildingSubRoomNumber";
        public static String OFF_CAMPUS_NAME = "offCampusName";
        public static String OFF_CAMPUS_ADDRESS = "offCampusAddress";
        public static String OFF_CAMPUS_CITY_NAME = "offCampusCityName";
        public static String OFF_CAMPUS_STATE_CODE = "offCampusStateCode";
        public static String OFF_CAMPUS_ZIP_CODE = "offCampusZipCode";
        public static String OFF_CAMPUS_COUNTRY_CODE = "offCampusCountryCode";
        public static final String ASSET_UNIQUE_DETAILS = "assetGlobalUniqueDetails";
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        // **** Asset Separate ****
        // needed to highlight fields
        public static final String ASSET_GLOBAL_UNIQUE_DETAILS = "assetGlobalUniqueDetails";
        public static String REPRESENTATIVE_UNIVERSAL_IDENTIFIER = "representativeUniversalIdentifier";
        public static String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static String CAPITAL_ASSET_DESCRIPTION = "capitalAssetDescription";
        public static String MANUFACTURER_NAME = "manufacturerName";
        public static String ORGANIZATION_TEXT = "organizationText";
        public static String MANUFACTURER_MODEL_NUMBER = "manufacturerModelNumber";
        public static String SEPARATE_SOURCE_AMOUNT = "separateSourceAmount";
        public static String LOCATION_QUANTITY = "locationQuantity";
    }

    public static class AssetDepreciationConvention {
        public static final String FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObjectSubTypeCode";
    }

    public static class AssetPaymentDocument {
        public static final String ASSET = "asset";
        public static final String ASSET_PAYMENTS = "assetPayments";
        public static final String ASSET_PAYMENT_ASSET_DETAIL = "assetPaymentAssetDetail";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    }

    public static class AssetObjectCode {
        public static final String CAPITALIZATION_FINANCIAL_OBJECT = "capitalizationFinancialObject";
        public static final String ACCUMULATED_DEPRECIATION_FINANCIAL_OBJECT = "accumulatedDepreciationFinancialObject";
    }

    public static class AssetRetirementReason {
        public static String RETIREMENT_REASON_CODE = "retirementReasonCode";
    }

    public static class BarcodeInventory {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static String ASSET_TAG_NUMBER = "assetTagNumber";
        public static String UPLOAD_SCAN_INDICATOR = "uploadScanIndicator";
        public static String INVENTORY_DATE = "uploadScanTimestamp";
        public static String CAMPUS_CODE = "campusCode";
        public static String BUILDING_CODE = "buildingCode";
        public static String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static String BUILDING_SUBROOM_NUMBER = "buildingSubRoomNumber";
        public static String ASSET_CONDITION_CODE = "assetConditionCode";
        public static String CAMPUS_REFERENCE = "campus";
        public static String BUILDING_REFERENCE = "building";
        public static String BUILDING_ROOM_REFERENCE = "buildingRoom";
        public static String CONDITION_REFERENCE = "condition";
        public static final String BARCODE_INVENTORY_DETAIL = "barcodeInventoryErrorDetail";
    }

    public static class AssetAcquisitionType {
        public final static String ACQUISITION_TYPE_CODE = "acquisitionTypeCode";
    }

    public static class AssetRepairHistory {
        public final static String INCIDENT_DATE = "incidentDate";
    }

    public static final String ASSET_LOCATION_COMMON_ERROR_SECTION_ID = "assetLocationErrorSection";
    public static final String COMMON_ERROR_SECTION_ID = "commonErrorSection";
    public static final String BCIE_GLOBAL_REPLACE_ERROR_SECTION_ID = "globalReplaceErrorSection";
    public static final String DOCUMENT_NUMBER = "documentNumber";


    public static class AssetLock {
        public static final String DOCUMENT_NUMBER = "documentNumber";
    }

    public static class AssetPaymentAllocation {    	
    	public static final String ASSET_DISTRIBUTION_BY_AMOUNT_CODE = "1";
    	public static final String ASSET_DISTRIBUTION_EVENLY_CODE = "2";
    	public static final String ASSET_DISTRIBUTION_BY_TOTAL_COST_CODE = "3";
    	public static final String ASSET_DISTRIBUTION_BY_PERCENTAGE_CODE = "4";
    	public static final String ASSET_DISTRIBUTION_DEFAULT_CODE = ASSET_DISTRIBUTION_EVENLY_CODE;
    }
    
    public static class GeneralLedgerEntry {
        public static final String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
        public static final String FINANCIAL_OBJECT = "financialObject";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String GENERAL_LEDGER_ACCOUNT_IDENTIFIER = "generalLedgerAccountIdentifier";
        public static final String ACTIVITY_STATUS_CODE = "activityStatusCode";
        public static final String TRANSACTION_DATE = "transactionDate";
        public static final String REFERENCE_FINANCIAL_DOCUMENT_NUMBER = "referenceFinancialDocumentNumber";
        public static final String TRANSACTION_LEDGER_ENTRY_AMOUNT = "transactionLedgerEntryAmount";
        public static final String TRANSACTION_LEDGER_SUBMIT_AMOUNT = "transactionLedgerSubmitAmount";
        public static final String TRANSACTION_DEBIT_CREDIT_CODE = "transactionDebitCreditCode";
        public static final String PURAP_LINE_ASSET_ACCOUNTS = "purApLineAssetAccounts";
        public static final String GENERAL_LEDGER_ENTRY_ASSETS = "generalLedgerEntryAssets";
        public static final String ORGNIZATION_REFERENCE_ID = "organizationReferenceId";
        public static final String PROJECT_CD = "projectCode";
    }

    public static class Entry {
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObject.financialObjectSubTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String TRANSACTION_DATE_TIME_STAMP = "transactionDateTimeStamp";
        public static final String DOCUMENT_NUMBER = "documentNumber";
    }

    public static class PurchasingAccountsPayableDocument {
        public static final String PURCHASE_ORDER_IDENTIFIER = "purchaseOrderIdentifier";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PURCHASEING_ACCOUNTS_PAYABLE_ITEM_ASSETS = "purchasingAccountsPayableItemAssets";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String PURAP_DOCUMENT_IDENTIFIER = "purapDocumentIdentifier";
    }

    public static class PurchasingAccountsPayableItemAsset {
        public static final String ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER = "accountsPayableLineItemIdentifier";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String CAPITAL_ASSET_BUILDER_LINE_NUMBER = "capitalAssetBuilderLineNumber";
        public static final String ACCOUNTS_PAYABLE_ITEM_QUANTITY = "accountsPayableItemQuantity";
        public static final String SPLIT_QTY = "splitQty";
        public static final String PURCHASING_ACCOUNTS_PAYABLE_DOCUMENT = "purchasingAccountsPayableDocument";
        public static final String CAMS_DOCUMENT_NUMBER = "capitalAssetManagementDocumentNumber";
    }

    public static class PurchasingAccountsPayableLineAssetAccount {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PUR_ITM_ID = "accountsPayableLineItemIdentifier";
        public static final String CAB_LINE_NUMBER = "capitalAssetBuilderLineNumber";
        public static final String GENERAL_LEDGER_ENTRY = "generalLedgerEntry";
        public static final String PURAP_ITEM_ASSET = "purchasingAccountsPayableItemAsset";
    }

    public static class Pretag {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String PRETAG_DETAIL_CAMPUS_TAG_NUMBER = "pretagDetail.campusTagNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
        public static final String PRE_TAG_DETAIS = "pretagDetails";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ORGANIZATION_CODE = "organizationCode";
        public static final String REPRESENTATIVE_ID = "personUniversal.principalName";
    }

    public static class PretagDetail {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
    }

    public static final String DOCUMENT_NUMBER = "documentNumber";

    public static class PurApLineForm {
        public static final String PURAP_DOCS = "purApDocs";
        public static final String MERGE_QTY = "mergeQty";
        public static final String MERGE_DESC = "mergeDesc";
    }

    public static class GeneralLedgerPendingEntry {
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObject.financialObjectSubTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String TRANSACTION_ENTRY_PROCESSED_TS = "transactionEntryProcessedTs";
        public static final String DOCUMENT_NUMBER = "documentNumber";
    }

    public static class CreditMemoAccountRevision {
        public static final String ACCOUNT_REVISION_TIMESTAMP = "accountRevisionTimestamp";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
    }

    public static class PaymentRequestAccountRevision {
        public static final String ACCOUNT_REVISION_TIMESTAMP = "accountRevisionTimestamp";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
    }

    public static class Account {
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    }

    public static class PreTagExtract {
        public static final String FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String PURAP_ITEM_UNIT_PRICE = "purapItem.itemUnitPrice";        
        public static final String PO_INITIAL_OPEN_TIMESTAMP = "purapItem.purapDocument.purchaseOrderInitialOpenTimestamp";
        public static final String PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE = "purapItem.purapDocument.capitalAssetSystemStateCode";
    }

    public static class purApRequisitionItem {
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
    }

    public static class CapitalAssetInformation {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String ACTION_INDICATOR = "capitalAssetActionIndicator";
        public static final String ASSET_LINE_NUMBER = "capitalAssetLineNumber";
        public static final String ASSET_PROCESSED_IND = "capitalAssetProcessedIndicator";
    }

    public static class PurchasingAccountsPayableProcessingReport {
        public static final String PURAP_DOCUMENT_IDENTIFIER = "purapDocumentIdentifier";
    }

    public static final String PURAP_DOCUMENT = "purapDocument";
    public static final String PURAP_ITEM = "purapItem";
    public static final String ACTIVE = "active";

    public static class AssetGlobalDocumentCreate {
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
    }

    public static class Parameter {
        public static final String PARAMETER_NAME = "name";
        public static final String PARAMETER_DETAIL_TYPE_CODE = "componentCode";
        public static final String PARAMETER_NAMESPACE_CODE = "namespaceCode";
        public static final String PARAMETER_VALUE = "value";

    }

    public static class CapitalAssetLock {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String LOCKING_INFORMATION = "lockingInformation";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String DOCUMENT_TYPE_NAME = "documentTypeName";
    }
}
