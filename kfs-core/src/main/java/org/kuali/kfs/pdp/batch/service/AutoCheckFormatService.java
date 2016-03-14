package org.kuali.kfs.pdp.batch.service;

public interface AutoCheckFormatService {

	/**
	 * Process checks for ALL customer profiles
	 * 
	 * @return
	 */
	public boolean processChecks();

	/**
	 * Process checks for a specific customer profile
	 * @param profileId
	 * @return
	 */
	public boolean processChecksByCustomerProfile(String profileId);
	
}
