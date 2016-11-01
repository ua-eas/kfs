package edu.arizona.kfs.fp.batch.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.fp.batch.service.ProcurementCardHolderUpdateService;
import edu.arizona.kfs.fp.businessobject.ProcurementCardHolderLoad;

/**
 * This is the default implementation of the ProcurementCardHolderUpdateService interface.
 * 
 * @see edu.arizona.kfs.fp.batch.service.ProcurementCardHolderUpdateService
 */
@Transactional
public class ProcurementCardHolderUpdateServiceImpl implements ProcurementCardHolderUpdateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderUpdateServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private Person cardholderUser;

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }

    /**
     * This method retrieves a collection of temporary procurement card holder records and traverses through this list, updating
     * or inserting procurement card holder records.
     * 
     * @return True if the procurement card holder records were created successfully. If any problem occur while creating the
     *         documents, a runtime exception will be thrown.
     * @see edu.arizona.kfs.fp.batch.service#ProcurementCardHolderUpdateService#updateProcurementCardHolderRecords()
     */
    public boolean updateProcurementCardHolderRecords() {

        // load procurement cardholder records from temp table
        List<ProcurementCardHolderLoad> loadedPcardHolders = new ArrayList<ProcurementCardHolderLoad>();
        loadedPcardHolders = retrievePcardHolders();

        // list for saving records
        List<ProcurementCardHolderDetail> procurementCardHolderDetails = new ArrayList<ProcurementCardHolderDetail>();
        ProcurementCardHolderDetail procurementCardHolderDetail;

        for (ProcurementCardHolderLoad procurementCardHolderLoad : loadedPcardHolders) {
            // check for exclusion
            if (!excludeRecord(procurementCardHolderLoad.getCardCancelDate())) {
                // check for existing record - expire date is mmdd
                procurementCardHolderDetail = getExistingProcurementCardHolder(procurementCardHolderLoad.getCreditCardNumber());
                if (ObjectUtils.isNull(procurementCardHolderDetail)) {
                    // insert
                    procurementCardHolderDetail = new ProcurementCardHolderDetail();
                    procurementCardHolderDetail.setCreditCardNumber(procurementCardHolderLoad.getCreditCardNumber());
                    procurementCardHolderDetail.setChartOfAccountsCode("UA");
                    procurementCardHolderDetail.setAccountNumber("0");
                    procurementCardHolderDetail.setCardHolderSystemId(determineSystemId(procurementCardHolderLoad.getCardHolderSystemId(), procurementCardHolderLoad.getCardStatusCode()));
                }
                procurementCardHolderDetail.setCardHolderName(procurementCardHolderLoad.getCardHolderName());
                procurementCardHolderDetail.setCardHolderAlternateName(procurementCardHolderLoad.getCardHolderAlternateName());
                procurementCardHolderDetail.setCardHolderLine1Address(procurementCardHolderLoad.getCardHolderLine1Address());
                procurementCardHolderDetail.setCardHolderLine2Address(procurementCardHolderLoad.getCardHolderLine2Address());
                procurementCardHolderDetail.setCardHolderCityName(procurementCardHolderLoad.getCardHolderCityName());
                procurementCardHolderDetail.setCardHolderStateCode(procurementCardHolderLoad.getCardHolderStateCode());
                procurementCardHolderDetail.setCardHolderZipCode(procurementCardHolderLoad.getCardHolderZipCode());
                procurementCardHolderDetail.setCardHolderWorkPhoneNumber(procurementCardHolderLoad.getCardHolderWorkPhoneNumber());
                procurementCardHolderDetail.setCardLimit(procurementCardHolderLoad.getCardLimit());
                procurementCardHolderDetail.setCardCycleAmountLimit(procurementCardHolderLoad.getCardCycleAmountLimit());
                procurementCardHolderDetail.setCardCycleVolumeLimit(procurementCardHolderLoad.getCardCycleVolumeLimit());
                procurementCardHolderDetail.setCardMonthlyNumber(procurementCardHolderLoad.getCardMonthlyNumber());
                procurementCardHolderDetail.setCardStatusCode(determineStatusCode(procurementCardHolderLoad.getCardStatusCode()));
                procurementCardHolderDetail.setCardCancelCode(determineCancelCode(procurementCardHolderLoad.getCardStatusCode()));
                procurementCardHolderDetail.setCardNoteText(procurementCardHolderLoad.getCardNoteText());
                procurementCardHolderDetail.setCardOpenDate(procurementCardHolderLoad.getCardOpenDate());
                procurementCardHolderDetail.setCardCancelDate(procurementCardHolderLoad.getCardCancelDate());
                if (ObjectUtils.isNotNull(procurementCardHolderLoad.getCardExpireDate())) {
                    procurementCardHolderDetail.setCardExpireDate(determineExpireDate(procurementCardHolderLoad.getCardExpireDate()));
                }
                procurementCardHolderDetails.add(procurementCardHolderDetail);
            }
        }

        try {
            getBusinessObjectService().save(procurementCardHolderDetails);
            if (LOG.isInfoEnabled()) {
                LOG.info("");
            }
        } catch (Exception e) {
            LOG.error("Error persisting " + " " + e.getMessage(), e);
            throw new RuntimeException("Error persisting " + " " + e.getMessage(), e);
        }

        return true;
    }

    /**
     * This method retrieves a list of procurement card holder records from a temporary table.
     * 
     * @return List containing procurement card holder records.
     */
    protected List<ProcurementCardHolderLoad> retrievePcardHolders() {

        // retrieve Procurement Card holder records from Procurement Card holder table order by card number
        List<ProcurementCardHolderLoad> loadedPcardHolders = (List<ProcurementCardHolderLoad>) getBusinessObjectService().findMatchingOrderBy(ProcurementCardHolderLoad.class, new HashMap(), "creditCardNumber", true);
        return loadedPcardHolders;
    }

    /**
     * Gets the matching record from the custom Procurement Cardholder table.
     */
    private ProcurementCardHolderDetail getExistingProcurementCardHolder(String creditCardNumber) {

        Map<String, String> pkMap = new HashMap<String, String>();
        pkMap.put("creditCardNumber", creditCardNumber);
        ProcurementCardHolderDetail procurementCardHolderDetail = (ProcurementCardHolderDetail) getBusinessObjectService().findByPrimaryKey(ProcurementCardHolderDetail.class, pkMap);

        return procurementCardHolderDetail;
    }

    /**
     * Check to see if this record is excluded based on the cancel date
     */
    private boolean excludeRecord(Date cancelDate) {
        // exclude records with a cancel date more than 120 days before the go live date
        if (ObjectUtils.isNotNull(cancelDate)) {
            // create go live date
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, 2010);
            calendar.set(Calendar.MONTH, 12);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.clear(Calendar.HOUR_OF_DAY);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            Date goLiveDate = Date.valueOf(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE));
            Timestamp goLiveDateTS = new Timestamp(goLiveDate.getTime());
            Timestamp cancelDateTS = new Timestamp(cancelDate.getTime());
            // check difference in days
            if (goLiveDateTS.after(cancelDateTS) && getDateTimeService().dateDiff(cancelDateTS, goLiveDateTS, false) > 240) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Determines the status code from the status text
     */
    private String determineStatusCode(String statusText) {
        // determine valid status code from status text
        String statusCode;

        if (statusText.equals("Active")) {
            statusCode = "A";
        } else if (statusText.equals("Card activation")) {
            statusCode = "T";
        } else if (statusText.equals("Monitoring")) {
            statusCode = "M";
        } else if (statusText.equals("Chargeoff Account ")) {
            statusCode = "E";
        } else if (statusText.equals("Open")) {
            statusCode = "O";
        } else {
            statusCode = "C";
        }
        return statusCode;

    }

    /**
     * Determines the cancel code from the cancel text
     */
    private String determineCancelCode(String statusText) {
        // determine valid cancel code from status text
        String cancelCode;

        if (statusText.equals("Canceled")) {
            cancelCode = "E";
        } else if (statusText.equals("Canceled - institution specific")) {
            cancelCode = "I";
        } else if (statusText.equals("Canceled - temporary")) {
            cancelCode = "P";
        } else if (statusText.equals("Canceled - bank request")) {
            cancelCode = "B";
        } else if (statusText.equals("Canceled - cardholder terminated")) {
            cancelCode = "T";
        } else if (statusText.equals("Canceled - collections")) {
            cancelCode = "C";
        } else if (statusText.equals("Canceled - no longer needed")) {
            cancelCode = "N";
        } else if (statusText.equals("Canceled - PA Requests to have card closed, cardholder left company")) {
            cancelCode = "A";
        } else if (statusText.equals("Lost/stolen")) {
            cancelCode = "L";
        } else if (statusText.equals("Lost/stolen - fraud")) {
            cancelCode = "D";
        } else {
            cancelCode = "X";
        }
        return cancelCode;

    }

    /**
     * Determines the system ID from the employee ID and status test
     */
    private String determineSystemId(String employeeId, String statusText) {
        // determine valid system id (net id) from employee id and status text
        String systemId;
        if (statusText.equals("Active") || statusText.equals("Card activation") || statusText.equals("Monitoring") || statusText.equals("Open")) {
            systemId = "0";
        } else {
            systemId = null;
        }

        if (ObjectUtils.isNotNull(employeeId)) {
            cardholderUser = getCardholderUser(employeeId.substring(1));
            if (ObjectUtils.isNotNull(cardholderUser)) {
                // employee found
                systemId = cardholderUser.getPrincipalId();
            }
        }
        return systemId;

    }

    /**
     * Determines the expiration date
     */
    private Date determineExpireDate(String expireDate) {
        // determine valid expiration date from string of mmdd
        int expireMonth = Integer.parseInt(expireDate.substring(0, 2));
        int expireDay = 31;
        int expireYear = Integer.parseInt(expireDate.substring(2));
        expireYear = expireYear + 2000;
        if (expireMonth == 2) {
            if (expireYear % 4 == 0)
                expireDay = 29;
            else
                expireDay = 28;
        }
        if (expireMonth == 4 || expireMonth == 6 || expireMonth == 9 || expireMonth == 11) {
            expireDay = 30;
        }

        Date expDate = Date.valueOf(String.valueOf(expireYear) + "-" + expireDate.substring(0, 2) + "-" + String.valueOf(expireDay));
        return expDate;

    }

    /**
     * Gets the cardholderUser attribute.
     * 
     * @return Returns the cardholderUser
     */
    public Person getCardholderUser(String employeeId) {
        cardholderUser = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(employeeId);
        return cardholderUser;
    }

}
