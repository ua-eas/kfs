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
package org.kuali.kfs.krad.datadictionary.validation;

/**
 *
 *
 */
public class Account {

	private String accountId;
	private String bankName;
	private String totalBalance;
	private String creditBalance;
	private String debitBalance;



	/**
	 * @param accountId
	 * @param bankName
	 * @param totalBalance
	 * @param creditBalanace
	 * @param debitBalance
	 */
	public Account(String accountId, String bankName, String totalBalance, String creditBalance, String debitBalance) {
		this.accountId = accountId;
		this.bankName = bankName;
		this.totalBalance = totalBalance;
		this.creditBalance = creditBalance;
		this.debitBalance = debitBalance;
	}
	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return this.accountId;
	}
	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return this.bankName;
	}
	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * @return the totalBalance
	 */
	public String getTotalBalance() {
		return this.totalBalance;
	}
	/**
	 * @param totalBalance the totalBalance to set
	 */
	public void setTotalBalance(String totalBalance) {
		this.totalBalance = totalBalance;
	}
	/**
	 * @return the creditBalance
	 */
	public String getCreditBalance() {
		return this.creditBalance;
	}
	/**
	 * @param creditBalance the creditBalance to set
	 */
	public void setCreditBalance(String creditBalance) {
		this.creditBalance = creditBalance;
	}
	/**
	 * @return the debitBalance
	 */
	public String getDebitBalance() {
		return this.debitBalance;
	}
	/**
	 * @param debitBalance the debitBalance to set
	 */
	public void setDebitBalance(String debitBalance) {
		this.debitBalance = debitBalance;
	}

}
