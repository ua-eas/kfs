package edu.arizona.kfs.fp.batch;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.batch.XmlBatchInputFileTypeBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

/**
 * Batch input type for the procurement cardholder job.
 */
public class ProcurementCardHolderInputFileType extends XmlBatchInputFileTypeBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderInputFileType.class);

    private DateTimeService dateTimeService;

    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    /**
     * No additional information is added to procurement cardholder batch files.
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.bo.Person, java.lang.Object, java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String userIdentifier) {
        String fileName = "pcdh_" + principalName;
        if (StringUtils.isNotBlank(userIdentifier)) {
            fileName += "_" + userIdentifier;
        }
        fileName += "_" + getDateTimeService().toDateTimeStringForFilename(getDateTimeService().getCurrentDate());

        // remove spaces in filename
        fileName = StringUtils.remove(fileName, " ");

        return fileName;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return KFSConstants.ProcurementCardholder.PCDH_FILE_TYPE_IDENTIFIER;
    }

    /**
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    public boolean validate(Object parsedFileContents) {
        return true;
    }

    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), "_");
        if (fileNameParts.length >= 2) {
            return fileNameParts[1];
        }
        return null;
    }

    public String getTitleKey() {
        return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_PCDH;
    }

}
