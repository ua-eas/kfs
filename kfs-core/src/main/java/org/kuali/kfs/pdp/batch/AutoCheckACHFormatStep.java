package org.kuali.kfs.pdp.batch;

import java.util.Date;

import org.kuali.kfs.pdp.batch.service.AutoCheckFormatService;
import org.kuali.kfs.sys.batch.AbstractStep;

public class AutoCheckACHFormatStep extends AbstractStep {
	private AutoCheckFormatService autoCheckFormatService;

	@Override
	public boolean execute(String jobName, Date jobRunDate)
			throws InterruptedException {
		return autoCheckFormatService.processChecks();
	}

	public AutoCheckFormatService getAutoCheckFormatService() {
		return autoCheckFormatService;
	}

	public void setAutoCheckFormatService(
			AutoCheckFormatService autoCheckFormatService) {
		this.autoCheckFormatService = autoCheckFormatService;
	}

}
