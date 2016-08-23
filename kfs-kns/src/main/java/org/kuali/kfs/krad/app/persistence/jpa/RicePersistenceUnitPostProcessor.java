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
package org.kuali.kfs.krad.app.persistence.jpa;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import javax.sql.DataSource;

public class RicePersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RicePersistenceUnitPostProcessor.class);

    public static final String KRAD_APPLICATION_PERSISTENCE_UNIT_NAME = "krad-application-unit";
    public static final String KRAD_SERVER_PERSISTENCE_UNIT_NAME = "krad-server-unit";

    private DataSource jtaDataSource;

    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo mutablePersistenceUnitInfo) {
        mutablePersistenceUnitInfo.setJtaDataSource(getJtaDataSource());
        addKRADManagedClassNames(mutablePersistenceUnitInfo);
        if (mutablePersistenceUnitInfo.getPersistenceUnitName().equals(KRAD_APPLICATION_PERSISTENCE_UNIT_NAME) || mutablePersistenceUnitInfo.getPersistenceUnitName().equals(
            KRAD_SERVER_PERSISTENCE_UNIT_NAME)) {
            addRiceManagedClassNamesToKRADPersistenceUnit(mutablePersistenceUnitInfo);
        }
    }

    /**
     * Adds all the KNS Managed entities to the persistence unit - which is important, becuase all
     * persistence units get the KNS entities to manage
     *
     * @param mutablePersistenceUnitInfo
     */
    public void addKRADManagedClassNames(MutablePersistenceUnitInfo mutablePersistenceUnitInfo) {
        addManagedClassNames(mutablePersistenceUnitInfo, new KRADPersistableBusinessObjectClassExposer());
    }

    /**
     * Adds the class names listed by exposed by the given exposer into the persistence unit
     *
     * @param mutablePersistenceUnitInfo the persistence unit to add managed JPA entity class names to
     * @param exposer                    the exposer for class names to manage
     */
    public void addManagedClassNames(MutablePersistenceUnitInfo mutablePersistenceUnitInfo, PersistableBusinessObjectClassExposer exposer) {
        for (String exposedClassName : exposer.exposePersistableBusinessObjectClassNames()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("JPA will now be managing class: " + exposedClassName);
            }
            mutablePersistenceUnitInfo.addManagedClassName(exposedClassName);
        }
    }

    public void addRiceManagedClassNamesToKRADPersistenceUnit(MutablePersistenceUnitInfo mutablePersistenceUnitInfo) {
        addManagedClassNames(mutablePersistenceUnitInfo, new RiceToNervousSystemBusinessObjectClassExposer());
    }

    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }

    public void setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }

}
