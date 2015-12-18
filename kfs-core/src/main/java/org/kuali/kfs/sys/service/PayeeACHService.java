package org.kuali.kfs.sys.service;

public interface PayeeACHService {
	
	public boolean isPayeeSignedUpForACH(String payeeTypeCode, String payeeIdNumber);

}
