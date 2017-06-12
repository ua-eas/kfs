package edu.arizona.kfs.module.ld.batch.dataaccess;

public interface LaborEncumbranceDao {
    
    public Integer getEmployeeRecord(String emplid, String positionNumber, String financialBalanceTypeCode, String accountNumber, String financialObjectCode) ; 

}
