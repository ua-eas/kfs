package edu.arizona.kfs.module.prje.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

import edu.arizona.kfs.module.prje.dataaccess.PRJEDao;
import edu.arizona.kfs.module.prje.dataaccess.impl.PRJEDaoOjb;

public class PRJEServiceBaseImpl {
    private static Logger LOG = Logger.getLogger(PRJEServiceBaseImpl.class);
    
    static final String FILENAME = "gl_entry_prje";
    static final String DATA_EXT = GeneralLedgerConstants.BatchFileSystem.EXTENSION;
    static final String DONE_EXT = GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
    static final String PROP_EXT = ".properties";
    public static final String DATA_FILE = FILENAME + DATA_EXT;
    public static final String DONE_FILE = FILENAME + DONE_EXT;
    static final String PROP_FILE = FILENAME + PROP_EXT;
    
    private PRJEDao prjeDataAccess;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private ObjectCodeService objectCodeService;
    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private String batchFileDirectoryName;
    private String propertyFileDirectoryName;
    
    public PRJEDao getPrjeDataAccess() {
        if ( prjeDataAccess != null ) {
            return prjeDataAccess;
        }
        else {
            return new PRJEDaoOjb();
        }
    }
    
    public Properties getProperties() {
        File propDir = new File(getPropertyFileDirectoryName());
        if ( propDir.exists() && propDir.isDirectory() ) {
            File propFile = new File(propDir, PROP_FILE);
            if ( propFile.exists() && propFile.isFile() ) {
                try {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(propFile));
                    return properties;
                }
                catch ( IOException e ) {
                    throw new RuntimeException(e);
                }
            }
            else {
                LOG.error(propFile+" is not a regular file");
            }
        }
        else {
            LOG.error(propDir+" is not a regular directory");
        }

        return new Properties();
    }
    
    public void setPrjeDataAccess(PRJEDao prjeDataAccess) {
        this.prjeDataAccess = prjeDataAccess;
    }

    public BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService != null ) {
            return businessObjectService;
        }
        else {
            return KRADServiceLocator.getBusinessObjectService();
        }
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public ParameterService getParameterService() {
        if ( parameterService != null ) {
            return parameterService;
        }
        else {
            return SpringContext.getBean(ParameterService.class);
        }
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ObjectCodeService getObjectCodeService() {
        if ( objectCodeService != null ) {
            return objectCodeService;
        }
        else {
            return SpringContext.getBean(ObjectCodeService.class);
        }
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public DateTimeService getDateTimeService() {
        if ( dateTimeService != null ) {
            return dateTimeService;
        }
        else {
            return SpringContext.getBean(DateTimeService.class);
        }
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
 
    public UniversityDateService getUniversityDateService() {
        if ( universityDateService != null ) {
            return universityDateService;
        }
        else {
            return SpringContext.getBean(UniversityDateService.class);
        }
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public String getBatchFileDirectoryName() {
        return batchFileDirectoryName;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public String getPropertyFileDirectoryName() {
        return propertyFileDirectoryName;
    }

    public void setPropertyFileDirectoryName(String propertyFileDirectoryName) {
        this.propertyFileDirectoryName = propertyFileDirectoryName;
    }
}
