package org.kuali.kfs.pdp.batch.service.impl;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
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
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

public class AutoCheckFormatServiceImpl implements AutoCheckFormatService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoCheckFormatServiceImpl.class);
	
	private FormatService formatService;
	private DateTimeService dateTimeService;
	private CampusService campusService;
	
	/* (non-Javadoc)
	 * @see edu.uci.kfs.pdp.batch.service.AutoCheckFormatService#processChecks()
	 */
	@Override
	public boolean processChecks() {

		List<Campus> findAllCampuses = getCampusService().findAllCampuses();
		
		// Go through all the campuses and process the checks ready for format
		for(Campus campus : findAllCampuses){
			String campusCode = campus.getCode();
			
			// Create FormatSelection object for current campus
	        FormatSelection formatSelection = generateFormatSelectionForCampus(campusCode);
	        
	        // Using the formatSelection object, create a
			AutoCheckFormat autoFormat = createAutoCheckFormat(formatSelection);
			
			if(ObjectUtils.isNull(autoFormat))
				return false;
	
			formatChecks(autoFormat);
		}
		
		return true; 
	}


	/**
	 * @param campusCode
	 * @return
	 */
	protected FormatSelection generateFormatSelectionForCampus(String campusCode) {
		Date formatStartDate = getFormatService().getFormatProcessStartDate(campusCode);

		// create new FormatSelection object an set the campus code and the start date
		FormatSelection formatSelection = new FormatSelection();
		formatSelection.setCampus(campusCode);
		formatSelection.setStartDate(formatStartDate);

		// if format process not started yet, populate the other data as well
		if (formatStartDate == null) {
		    formatSelection.setCustomerList(getFormatService().getAllCustomerProfiles());
		    formatSelection.setRangeList(getFormatService().getAllDisbursementNumberRanges());
		}
		return formatSelection;
	}

	
	/**
	 *  Check format process - Common process for any type of implementation we want to use
	 * @param autoFormat
	 * @return
	 */
	protected boolean  formatChecks(AutoCheckFormat autoFormat) {
		
	    // Mark payments for format, if there are no payments for format then end the job
		if (!markPaymentsForFormat(autoFormat))
			return true;

		// Format checks
		KualiInteger processId = autoFormat.getFormatProcessSummary().getProcessId();

        try {
            getFormatService().performFormat(processId.intValue());
            // You have reached the end. success!
            return true;
        }
        catch (FormatException e) {
        	LOG.error("ERROR AutoCheckFormatStep: " + e.getMessage(), e);
        	return false;
        }
	}

	/**
	 * @param autoFormat
	 * @return
	 */
	protected boolean markPaymentsForFormat(AutoCheckFormat autoFormat) {
		try {
			Date paymentDate = getDateTimeService().convertToSqlDate(autoFormat.getPaymentDate());
			FormatProcessSummary formatProcessSummary = getFormatService().startFormatProcess(GlobalVariables.getUserSession().getPerson(), autoFormat.getCampus(), autoFormat.getCustomers(), paymentDate, autoFormat.getPaymentTypes());
			
			if (formatProcessSummary.getProcessSummaryList().size() == 0) {
				LOG.error("Warning AutoCheckFormatStep: There are no payments that match your selection for format process.");
				return false;
			}
			
			autoFormat.setFormatProcessSummary(formatProcessSummary);
			return true;
			
		} catch (Exception e) {
			LOG.error("ERROR AutoCheckFormatStep: " + e.getMessage(), e);
			return false;
		}
	}

	/**
	 * @param formatSelection
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected AutoCheckFormat createAutoCheckFormat(FormatSelection formatSelection) {

        AutoCheckFormat autoFormat = new AutoCheckFormat();
        
        if (formatSelection.getStartDate() != null) {
        	LOG.error("ERROR AutoCheckFormatStep: The format process is already running. It began at: " + getDateTimeService().toDateTimeString(formatSelection.getStartDate()));
            return null;
        }
        else {
            List<CustomerProfile> customers = formatSelection.getCustomerList();

            for (CustomerProfile element : customers) {

                if (formatSelection.getCampus().equals(element.getDefaultPhysicalCampusProcessingCode())) {
                    element.setSelectedForFormat(Boolean.TRUE);
                }
                else {
                    element.setSelectedForFormat(Boolean.FALSE);
                }
            }
            
            autoFormat.setCampus(formatSelection.getCampus());
            autoFormat.setPaymentDate(getDateTimeService().toDateString(getDateTimeService().getCurrentTimestamp()));
            autoFormat.setPaymentTypes(PdpConstants.PaymentTypes.ALL);
            autoFormat.setCustomers(customers);
            autoFormat.setRanges(formatSelection.getRangeList());
        }
		return autoFormat;
	}


	/**
	 * @return the formatService
	 */
	public FormatService getFormatService() {
		return formatService;
	}

	/**
	 * @param formatService the formatService to set
	 */
	public void setFormatService(FormatService formatService) {
		this.formatService = formatService;
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


	/**
	 * @return the campusService
	 */
	public CampusService getCampusService() {
		return campusService;
	}


	/**
	 * @param campusService the campusService to set
	 */
	public void setCampusService(CampusService campusService) {
		this.campusService = campusService;
	}

}
