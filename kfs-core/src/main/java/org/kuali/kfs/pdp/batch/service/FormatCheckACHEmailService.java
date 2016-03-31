package org.kuali.kfs.pdp.batch.service;

import org.kuali.kfs.sys.service.VelocityEmailService;

public interface FormatCheckACHEmailService extends VelocityEmailService {
	
	public void setEmailSubject(String subject);

}
