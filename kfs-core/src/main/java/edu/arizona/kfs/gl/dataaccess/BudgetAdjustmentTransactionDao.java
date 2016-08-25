package edu.arizona.kfs.gl.dataaccess;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.Transaction;

import edu.arizona.kfs.gl.businessobject.BudgetAdjustmentTransaction;

public interface BudgetAdjustmentTransactionDao {

	/**
	 * Saves a budget adjustment transaction record
	 * @param ba a budget adjustment transaction to save
	 */
	public void save(BudgetAdjustmentTransaction ba);
		
	/**
	 * Returns all budget adjustment transactions currently in the database
	 * 
	 * @return an Iterator with all budget adjustment transactions from the database
	 */
	public Iterator getAllBudgetAdjustmentTransactions();
	
	/**
	 * Returns all budget adjustment transactions for a particular document number
	 * 
	 * @param docNumber
	 * @return an iterator of budget adjustment transaction records
	 */
	public Iterator getByDocNumber(String docNumber);
	
	/**
	 * Deletes a budget adjustment transaction record
	 * 
	 * @param ba a budget adjustment transaction to delete
	 */
	public void delete(BudgetAdjustmentTransaction ba);
	
	/**
	 * Since budget adjustment transactions are temporary, this method
	 * removes all of the currently existing budget adjustment
	 * transactions from the database
	 */
	public void deleteAllBudgetAdjustmentTransactions();
}
