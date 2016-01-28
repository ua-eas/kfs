package org.kuali.kfs.pdp.batch.service.impl;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.AutoCheckFormatService;
import org.kuali.kfs.pdp.businessobject.AutoCheckFormat;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.FormatProcessSummary;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.service.FormatService;
import org.kuali.kfs.pdp.service.impl.exception.FormatException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiInteger;

public class AutoCheckFormatServiceImpl implements AutoCheckFormatService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoCheckFormatServiceImpl.class);
	private FormatService formatService;
	private DateTimeService dateTimeService;
	
	@Override
	public boolean processChecks() {
		
		AutoCheckFormat autoFormat = new AutoCheckFormat();
		
		//Prepare check data
        FormatSelection formatSelection = formatService.getDataForFormat(GlobalVariables.getUserSession().getPerson());
        
        autoFormat.setCampus("IR");
        
        if (formatSelection.getStartDate() != null) {
        	LOG.error("ERROR AutoCheckFormatStep: The format process is already running. It began at: " + getDateTimeService().toDateTimeString(formatSelection.getStartDate()));
            return false;
        }

        List<CustomerProfile> customers = markPaymentsForFormat(formatSelection);

        autoFormat.setPaymentDate(getDateTimeService().toDateString(getDateTimeService().getCurrentTimestamp()));
        autoFormat.setPaymentTypes(PdpConstants.PaymentTypes.ALL);
        autoFormat.setCustomers(customers);
        autoFormat.setRanges(formatSelection.getRangeList());
        
        // Mark payments for format
		generateFormatProcesSummaryForPayments(autoFormat);

		// Format checks
		KualiInteger processId = autoFormat.getFormatProcessSummary().getProcessId();

        try {
            formatService.performFormat(processId.intValue());
        }
        catch (FormatException e) {
        	LOG.error("ERROR AutoCheckFormatStep: " + e.getMessage(), e);
        	return false;
        }
		
        // You have reached the end. success!
		return true;
	}

	/**
	 * @param formatSelection
	 * @return
	 */
	private List<CustomerProfile> markPaymentsForFormat(
			FormatSelection formatSelection) {
		List<CustomerProfile> customers = formatSelection.getCustomerList();

        for (CustomerProfile element : customers) {

            if (formatSelection.getCampus().equals(element.getDefaultPhysicalCampusProcessingCode())) {
                element.setSelectedForFormat(Boolean.TRUE);
            }
            else {
                element.setSelectedForFormat(Boolean.FALSE);
            }
        }
		return customers;
	}

	/**
	 * @param autoFormat
	 */
	private void generateFormatProcesSummaryForPayments(
			AutoCheckFormat autoFormat) {
		try {
			Date paymentDate = getDateTimeService().convertToSqlDate(autoFormat.getPaymentDate());
			FormatProcessSummary formatProcessSummary = formatService.startFormatProcess(GlobalVariables.getUserSession().getPerson(), autoFormat.getCampus(), autoFormat.getCustomers(), paymentDate, autoFormat.getPaymentTypes());
			
			if (formatProcessSummary.getProcessSummaryList().size() == 0) {
				LOG.error("Warning AutoCheckFormatStep: There are no payments that match your selection for format process.");
				return;
			}
			
			autoFormat.setFormatProcessSummary(formatProcessSummary);
			
		} catch (Exception e) {
			LOG.error("ERROR AutoCheckFormatStep: " + e.getMessage(), e);
			return;
		}
	}

	/**
	 * @return the dateTimeService
	 */
	public DateTimeService getDateTimeService() {
		return dateTimeService;
	}

	/**
	 * @param dateTimeService the dateTimeService to set
	 */
	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

}
