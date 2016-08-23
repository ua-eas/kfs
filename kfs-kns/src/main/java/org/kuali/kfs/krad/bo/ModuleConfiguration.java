/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.krad.bo;

import org.kuali.kfs.krad.datadictionary.DataDictionaryLocationConfigurer;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.PersistenceService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.EntityManager;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in.
 *
 *
 *
 */
public class ModuleConfiguration implements InitializingBean, ApplicationContextAware {

	//protected static Logger LOG = Logger.getLogger(ModuleConfiguration.class);

	protected String namespaceCode;
	protected ApplicationContext applicationContext;

	protected List<String> packagePrefixes;

	protected List<String> databaseRepositoryFilePaths;

	protected List<String> dataDictionaryPackages;

	protected List<String> scriptConfigurationFilePaths;

	protected List<String> jobNames;

	protected List<String> triggerNames;

	//optional
	protected String dataSourceName;

	//optional
	protected EntityManager entityManager;

	protected Map<Class, Class> externalizableBusinessObjectImplementations;

	protected boolean initializeDataDictionary;

	protected PersistenceService persistenceService;

	protected DataDictionaryService dataDictionaryService;

	public ModuleConfiguration() {
		databaseRepositoryFilePaths = new ArrayList<String>();
		dataDictionaryPackages = new ArrayList<String>();
		scriptConfigurationFilePaths = new ArrayList<String>();
		jobNames = new ArrayList<String>();
		triggerNames = new ArrayList<String>();
	}

	/**
	 * @return the databaseRepositoryFilePaths
	 */
	public List<String> getDatabaseRepositoryFilePaths() {
		return this.databaseRepositoryFilePaths;
	}

	/**
	 * @param databaseRepositoryFilePaths the databaseRepositoryFilePaths to set
	 */
	public void setDatabaseRepositoryFilePaths(
			List<String> databaseRepositoryFilePaths) {
		this.trimList(databaseRepositoryFilePaths);
		this.databaseRepositoryFilePaths = databaseRepositoryFilePaths;
	}

	/**
	 * @return the dataDictionaryPackages
	 */
	public List<String> getDataDictionaryPackages() {
		return this.dataDictionaryPackages;
	}

	/**
	 * @param dataDictionaryPackages the dataDictionaryPackages to set
	 */
	public void setDataDictionaryPackages(List<String> dataDictionaryPackages) {
		this.trimList(dataDictionaryPackages);
		this.dataDictionaryPackages = dataDictionaryPackages;
	}

	/**
	 * @return the externalizableBusinessObjectImplementations
	 */
	public Map<Class, Class> getExternalizableBusinessObjectImplementations() {
		if (this.externalizableBusinessObjectImplementations == null)
			return null;
		return Collections.unmodifiableMap(this.externalizableBusinessObjectImplementations);
	}

	/**
	 * @param externalizableBusinessObjectImplementations the externalizableBusinessObjectImplementations to set
	 */
	public void setExternalizableBusinessObjectImplementations(
			Map<Class, Class> externalizableBusinessObjectImplementations) {
		if (externalizableBusinessObjectImplementations != null) {
			for (Class implClass : externalizableBusinessObjectImplementations.values()) {
				int implModifiers = implClass.getModifiers();
				if (Modifier.isInterface(implModifiers) || Modifier.isAbstract(implModifiers)) {
					throw new RuntimeException("Externalizable business object implementation class " +
							implClass.getName() + " must be a non-interface, non-abstract class");
				}
			}
		}
		this.externalizableBusinessObjectImplementations = externalizableBusinessObjectImplementations;
	}

	public List<String> getPackagePrefixes(){
		return this.packagePrefixes;
	}

	public void setPackagePrefixes(List<String> packagePrefixes){
		this.trimList(packagePrefixes);
		this.packagePrefixes = packagePrefixes;
	}

	public void setInitializeDataDictionary(boolean initializeDataDictionary){
		this.initializeDataDictionary = initializeDataDictionary;
	}

	public List<String> getScriptConfigurationFilePaths(){
		return this.scriptConfigurationFilePaths;
	}

	/**
	 * @return the jobNames
	 */
	public List<String> getJobNames() {
		return this.jobNames;
	}

	/**
	 * @param jobNames the jobNames to set
	 */
	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}


	/**
	 * @return the triggerNames
	 */
	public List<String> getTriggerNames() {
		return this.triggerNames;
	}

	/**
	 * @param triggerNames the triggerNames to set
	 */
	public void setTriggerNames(List<String> triggerNames) {
		this.triggerNames = triggerNames;
	}

	/**
	 * @return the initializeDataDictionary
	 */
	public boolean isInitializeDataDictionary() {
		return this.initializeDataDictionary;
	}

	/**
	 * @param scriptConfigurationFilePaths the scriptConfigurationFilePaths to set
	 */
	public void setScriptConfigurationFilePaths(
			List<String> scriptConfigurationFilePaths) {
		this.scriptConfigurationFilePaths = scriptConfigurationFilePaths;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (isInitializeDataDictionary() && getDataDictionaryPackages() != null && !getDataDictionaryPackages().isEmpty() ) {
			if ( getDataDictionaryService() == null ) {
				setDataDictionaryService(KRADServiceLocatorWeb.getDataDictionaryService());
			}
			if ( getDataDictionaryService() == null ) {
				setDataDictionaryService((DataDictionaryService)applicationContext.getBean( KRADServiceLocatorWeb.DATA_DICTIONARY_SERVICE ));
			}
			DataDictionaryLocationConfigurer ddl = new DataDictionaryLocationConfigurer( getDataDictionaryService() );
			ddl.setDataDictionaryPackages(getDataDictionaryPackages());
			ddl.afterPropertiesSet();
		}
		if (getDatabaseRepositoryFilePaths() != null) {
		    for (String repositoryLocation : getDatabaseRepositoryFilePaths()) {
				// Need the OJB persistence service because it is the only one ever using the database repository files
		    	if (getPersistenceService() == null) {
		    		setPersistenceService(KRADServiceLocatorWeb.getPersistenceServiceOjb());
		    	}
		    	if ( persistenceService == null ) {
		    		setPersistenceService((PersistenceService)applicationContext.getBean( KRADServiceLocatorWeb.PERSISTENCE_SERVICE_OJB  ));
		    	}
		    	getPersistenceService().loadRepositoryDescriptor( repositoryLocation );
			}
		}
	}

	/**
	 * @return the namespaceCode
	 */
	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	/**
	 * @param namespaceCode the namespaceCode to set
	 */
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the dataDictionaryService
	 */
	public DataDictionaryService getDataDictionaryService() {
		return this.dataDictionaryService;
	}

	/**
	 * @param dataDictionaryService the dataDictionaryService to set
	 */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}

	/**
	 * @return the persistenceService
	 */
	public PersistenceService getPersistenceService() {
		return this.persistenceService;
	}

	/**
	 * @param persistenceService the persistenceService to set
	 */
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

    public String getDataSourceName() {
        return this.dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
	 *
	 * This method passes by reference. It will alter the list passed in.
	 *
	 * @param stringList
	 */
	protected void trimList(List<String> stringList){
		if(stringList != null){
			// we need to trim whitespace from the stringList. Because trim() creates a new string
			// we have to explicitly put the new string back into the list
			for(int i=0; i<stringList.size(); i++){
				String elmt = stringList.get(i);
				elmt = elmt.trim();
				stringList.set(i, elmt);
			}
		}
	}

}
