package edu.arizona.kfs.module.ld.batch.service;

import java.io.File;

public interface LaborEncumbranceAdjustmentService {

    static final String LABOR_PERSONNEL_ENCUMBRANCE_DOC_TYPE = "LLPE";
    static final String LABOR_UACCESS_ORIGIN_CODE = "UE";
    
    static final String ENCUMBRANCE_BALANCE_FILE = "ld_encumb_bal_tmp";
    static final String ENCUMBRANCE_INPUT_FILE = "ld_encumb_in";
    static final String SORTED_ENCUMBRANCE_INPUT_FILE = "ld_encumb_sort_out";
    static final String ENCUMBRANCE_OUTPUT_FILE = "ld_encumb_out";
    static final String ENCUMBRANCE_ERROR_FILE = "ld_encumb_err";
    static final String NIGHTLY_OUT_BACKUP = "ld_encumb_labentry_kfs_backup";
    
    /**
     * Builds a sorted text version of the balance table (IE encumbrances only) for use by
     * the encumbrance balance file comparison.
     * 
     * @author jonathan
     */
    boolean buildBalanceFile( Integer fiscalYear, File outputFile );
    
    /**
     * Outputs an origin entry file with the needed transactions to adjust the
     * existing balances in the balance table to the values from the input file.
     * 
     * Note: The input and balance files *MUST* be sorted identically.  They are
     * walked over in order to detect additions/removals.
     * 
     * @author jonathan
     */
    boolean buildEncumbranceDifferenceFile( File inputFile, File balanceFile, File outputFile, File errorFile, File reconFile);
}
