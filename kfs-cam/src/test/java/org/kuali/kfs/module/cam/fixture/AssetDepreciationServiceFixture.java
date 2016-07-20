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
package org.kuali.kfs.module.cam.fixture;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

// @Transactional

public enum AssetDepreciationServiceFixture {

    DATA();

    private static Properties properties;
    static {
        String propertiesFileName = "org/kuali/kfs/module/cam/document/service/depreciation_service.properties";
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream(propertiesFileName));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    static String TEST_RECORD = "testRecord";
    static String ASSET = "asset";
    static String ASSET_PAYMENT = "assetPayment";
    static String DEPRECIATION_DATE = "depreciationDate";
    static String FIELD_NAMES = "fieldNames";
    static String NUM_OF_REC = "numOfRecords";
    static String DELIMINATOR = "deliminator";
    static String RESULT = "result";

    private AssetDepreciationServiceFixture() {
    }
    
    private <T> List<T> getObjects(Class<T> clazz, String prefix) {
        Integer numOfRecords = new Integer(properties.getProperty(prefix + "." + NUM_OF_REC));
        List<T> results = new ArrayList<>();

        String deliminator = properties.getProperty(DELIMINATOR);
        String fieldNames = properties.getProperty(prefix + "." + FIELD_NAMES);

        for (int i = 1; i <= numOfRecords.intValue(); i++) {
            String propertyKey = prefix + "." + TEST_RECORD + i;

            T item = CamsFixture.DATA_POPULATOR.buildTestDataObject(clazz, properties, propertyKey, fieldNames, deliminator);
            results.add(item);
        }
        return results;
    }

    public List<Asset> getAssets() {
        return (getObjects(Asset.class, ASSET));
    }

    public List<AssetPayment> getAssetPaymentsFromPropertiesFile() {
        return getObjects(AssetPayment.class, ASSET_PAYMENT);
    }

    public List<AssetPaymentInfo> getResultsFromPropertiesFile() {
        return getObjects(AssetPaymentInfo.class, RESULT);
    }

    public String getDepreciationDateString() {
        return properties.getProperty(DEPRECIATION_DATE);
    }
    
    public Date getDepreciationDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(getDepreciationDateString());
    }

    public SystemOptions getSystemOptions() {
        SystemOptions result = new SystemOptions();
        result.setUniversityFiscalYearStartMo("1");
        return result;
    }

    public UniversityDate getUniversityDate() {
        UniversityDate result = new UniversityDate();
        result.setUniversityFiscalYear(2010);
        result.setUniversityFiscalAccountingPeriod("1");
        return result;
    }

    public List<AssetPaymentInfo> getAssetPaymentInfo() {
        List<Asset> assets = getAssets();
        List<AssetPayment> assetPayments = getAssetPaymentsFromPropertiesFile();
        return getAssetPaymentInfo(assets, assetPayments);
    }

    private List<AssetPaymentInfo> getAssetPaymentInfo(List<Asset> assets, List<AssetPayment> assetPayments) {
        List<AssetPaymentInfo> result = new ArrayList<>();
        for (AssetPayment payment : assetPayments) {
            AssetPaymentInfo info = new AssetPaymentInfo();
            info.setAccumulatedPrimaryDepreciationAmount(payment.getAccumulatedPrimaryDepreciationAmount());
            info.setCapitalAssetNumber(payment.getCapitalAssetNumber());
            info.setChartOfAccountsCode(payment.getChartOfAccountsCode());
            info.setFinancialObjectCode(payment.getFinancialObjectCode());
            info.setFinancialSubObjectCode(payment.getFinancialSubObjectCode());
            info.setPaymentSequenceNumber(payment.getPaymentSequenceNumber());
            info.setPrimaryDepreciationBaseAmount(payment.getPrimaryDepreciationBaseAmount());
            info.setProjectCode(payment.getProjectCode());
            info.setSubAccountNumber(payment.getSubAccountNumber());
            for (Asset asset : assets) {
                if (asset.getCapitalAssetNumber().equals(payment.getCapitalAssetNumber())) {
                    info.setSalvageAmount(asset.getSalvageAmount());
                    info.setPrimaryDepreciationMethodCode(asset.getPrimaryDepreciationMethodCode());
                    info.setDepreciationDate(asset.getDepreciationDate());
                    switch (asset.getCapitalAssetTypeCode()) {
                        case "304": 
                            info.setDepreciableLifeLimit(3);
                            break;
                        case "90001": 
                            info.setDepreciableLifeLimit(1);
                            break;
                    }
                }
            }
            switch (payment.getFinancialObjectCode()) {
                case "7000":
                    info.setFinancialObjectSubTypeCode("CM");
                    break;
                case "7030":
                    info.setFinancialObjectSubTypeCode("CF");
            }
            info.setOrganizationPlantAccountNumber("9520004");
            info.setOrganizationPlantChartCode("BL");
            info.setCampusPlantAccountNumber("9520000");
            info.setCampusPlantChartCode("BL");
            info.setFinancialObjectTypeCode("EE");
            info.setAccumulatedRoundingErrorInMillicents(payment.getAccumulatedRoundingErrorInMillicents());
            
            result.add(info);
        }      
        return result;
    }

    public Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV() {        
        List<Asset> assets = getAssets();
        List<AssetPayment> assetPayments = getAssetPaymentsFromPropertiesFile();
        return getPrimaryDepreciationBaseAmountForSV(assets, assetPayments);
    }

    private Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV(List<Asset> assets,
            List<AssetPayment> assetPayments) {
        Map<Long, KualiDecimal> result = new HashMap<>();
        for (Asset asset : assets) {
            if (asset.getPrimaryDepreciationMethodCode().equals("SV")) {
                result.put(asset.getCapitalAssetNumber(), KualiDecimal.ZERO);
            }            
        }
        for (AssetPayment payment : assetPayments) {
            KualiDecimal total = result.get(payment.getCapitalAssetNumber());
            if (total != null) {
                result.put(payment.getCapitalAssetNumber(), total.add(payment.getPrimaryDepreciationBaseAmount()));
            }
        }
        return result;
    }

    public List<AssetObjectCode> getAssetObjectCodes() {
        List<AssetObjectCode> result = new ArrayList<>();
        AssetObjectCode assetObjectCode = new AssetObjectCode();
        assetObjectCode.setChartOfAccountsCode("BL");
        assetObjectCode.setUniversityFiscalYear(2010);
        assetObjectCode.setFinancialObjectSubTypeCode("CM");
        assetObjectCode.setCapitalizationFinancialObjectCode("8610");
        assetObjectCode.setAccumulatedDepreciationFinancialObjectCode("8910");
        assetObjectCode.setDepreciationExpenseFinancialObjectCode("5115");
        assetObjectCode.setActive(true);
        assetObjectCode.setObjectCode(new ArrayList<>());
        ObjectCode objectCode = new ObjectCode();
        objectCode.setChartOfAccountsCode("BL");
        objectCode.setFinancialObjectCode("7000");
        assetObjectCode.getObjectCode().add(objectCode);
        result.add(assetObjectCode);
        
        assetObjectCode = new AssetObjectCode();
        assetObjectCode.setChartOfAccountsCode("BL");
        assetObjectCode.setUniversityFiscalYear(2010);
        assetObjectCode.setFinancialObjectSubTypeCode("CF");
        assetObjectCode.setCapitalizationFinancialObjectCode("8611");
        assetObjectCode.setAccumulatedDepreciationFinancialObjectCode("8910");
        assetObjectCode.setDepreciationExpenseFinancialObjectCode("5115");
        assetObjectCode.setActive(true);
        assetObjectCode.setObjectCode(new ArrayList<>());
        objectCode = new ObjectCode();
        objectCode.setChartOfAccountsCode("BL");
        objectCode.setFinancialObjectCode("7030");
        assetObjectCode.getObjectCode().add(objectCode);
        result.add(assetObjectCode);
        
        return result;
    }

    public Set<Long> getAssetsWithNoDepreciation() {
        Set<Long> result = new HashSet<>();
        List<Asset> assets = getAssets();
        List<AssetPayment> assetPayments = getAssetPaymentsFromPropertiesFile();
        for (Asset asset : assets) {
            result.add(asset.getCapitalAssetNumber());         
        }
        for (AssetPayment payment : assetPayments) {
            KualiDecimal depreciatedAmount = payment.getAccumulatedPrimaryDepreciationAmount();
            if (depreciatedAmount != null && !depreciatedAmount.equals(KualiDecimal.ZERO)) {
                result.remove(payment.getCapitalAssetNumber());
            }
        }
        return result;
    }

}
