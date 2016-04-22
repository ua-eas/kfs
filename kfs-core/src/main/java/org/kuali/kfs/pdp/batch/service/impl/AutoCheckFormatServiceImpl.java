package org.kuali.kfs.pdp.batch.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
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
    private BusinessObjectService  businessObjectService;

    /* (non-Javadoc)
     * @see edu.uci.kfs.pdp.batch.service.AutoCheckFormatService#processChecks()
     */
    @Override
	public boolean processChecks() {
		boolean status = true;

		List<Campus> findAllCampuses = getCampusService().findAllCampuses();

		// Go through all the campuses and process the checks ready for format
		for(Campus campus : findAllCampuses){
			String campusCode = campus.getCode();

			// Create FormatSelection object for current campus
			FormatSelection formatSelection = getFormatService().getDataForFormat(campusCode);

			status = processChecksForCampus(formatSelection);
		}

		return status;
	}

	/* (non-Javadoc)
	 * @see org.kuali.kfs.pdp.batch.service.AutoCheckFormatService#processChecksByCustomerProfile(java.lang.String)
	 */
	@Override
	public boolean processChecksByCustomerProfile(String profileId) {
		LOG.info("Starting formating process for customer profile id: " + profileId);

		//if no profileId is null then we will process checks for all customer profiles
		if(StringUtils.isBlank(profileId)){
			return true;
		}

		// retrieve a valid customer from
		CustomerProfile customerProfile = getCustomerProfileByProfileID(profileId);

		if (ObjectUtils.isNull(customerProfile)) {
			LOG.error("There is no customer profile matching id: " + profileId);
			return false;
		}

		// Create FormatSelection object for current campus
		FormatSelection formatSelection = getFormatService().getDataForFormat(customerProfile.getDefaultPhysicalCampusProcessingCode());

		ArrayList<CustomerProfile> custProfileList = new ArrayList<CustomerProfile>();
		custProfileList.add(customerProfile);

		// we will filter down the customer profile list to have only the specified customer profile
		formatSelection.setCustomerList(custProfileList);

		return processChecksForCampus(formatSelection);
	}

	/**
	 * @param campusCode
	 * @return
	 */
	protected boolean processChecksForCampus(FormatSelection formatSelection) {
		boolean status = true;

        // Using the formatSelection object, create a
		AutoCheckFormat autoFormat = createAutoCheckFormat(formatSelection);

		if(ObjectUtils.isNull(autoFormat))
			return false;

		status = formatChecks(autoFormat);

		return status;
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
     * Check format process - Common process for any type of implementation we want to use
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
            LOG.error("AutoCheckFormatService.formatChecks: " + e.getMessage(), e);
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
                LOG.error("There are no payments that match your selection for format process.(Campus Code="+autoFormat.getCampus()+")");
                return false;
            }

            autoFormat.setFormatProcessSummary(formatProcessSummary);
            return true;

        } catch (Exception e) {
            LOG.error("AutoCheckFormatService.markPaymentsForFormat: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param formatSelection
     * @return AutoCheckFormat
     */
    @SuppressWarnings("unchecked")
    protected AutoCheckFormat createAutoCheckFormat(FormatSelection formatSelection) {
        AutoCheckFormat autoFormat = new AutoCheckFormat();

        if (ObjectUtils.isNotNull(formatSelection.getStartDate())) {
            LOG.error("The format process is already running. It began at: " + getDateTimeService().toDateTimeString(formatSelection.getStartDate()));
            return null;
        }

        autoFormat.setCampus(formatSelection.getCampus());
        autoFormat.setPaymentDate(getDateTimeService().toDateString(getDateTimeService().getCurrentTimestamp()));
        autoFormat.setPaymentTypes(PdpConstants.PaymentTypes.ALL);
        autoFormat.setRanges(formatSelection.getRangeList());

        List<CustomerProfile> customers = generateListOfCustomerProfilesReadyForFormat(formatSelection);
        autoFormat.setCustomers(customers);

        return autoFormat;
    }


    /**
     * Return a list of CustomerProfile that are eligible for format
     * @param formatSelection
     * @return List<CustomerProfile>
     */
    protected List<CustomerProfile> generateListOfCustomerProfilesReadyForFormat(FormatSelection formatSelection) {
        List<CustomerProfile> customers = formatSelection.getCustomerList();

        if(ObjectUtils.isNull(customers)) return new ArrayList<CustomerProfile>();

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


	/**
	 * @return the businessObjectService
	 */
	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}


	/**
	 * @param businessObjectService the businessObjectService to set
	 */
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}


	/**
	 * Retrieve customer profile by profile id
	 * @param profileId
	 * @return CustomerProfile
	 */
	protected CustomerProfile getCustomerProfileByProfileID(String profileId) {
		CustomerProfile customerProfile = getBusinessObjectService().findBySinglePrimaryKey(CustomerProfile.class, profileId);
		return customerProfile;
	}

}
