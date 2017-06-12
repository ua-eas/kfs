package edu.arizona.kfs.module.ld.batch.dataaccess;

import java.io.File;

public interface LaborEncumbranceAdjustmentDao {

    
    int buildFileForEncumbranceBalances( Integer fiscalYear, File fileName );
}
