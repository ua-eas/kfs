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
package org.kuali.kfs.sys.businessobject;


import net.sf.ehcache.statistics.StatisticsGateway;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.impl.services.CoreImplServiceLocator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class HealthReport {

    public String status = "";
    public String message = "";
    public List<Metric> metrics;

    public HealthReport() {
        metrics = new ArrayList();
    }

    public HealthReport checkHealth() {

        long startTime = System.currentTimeMillis();
        checkMemory();
        checkDatabase();
        checkSessions();
        checkCache();
        if (getMessage().isEmpty()) {
            status = "OK";
            message = "System checks out";
        }
        Metric metric = new Metric("Health", "executionTime", (System.currentTimeMillis()-startTime)+"ms");
        metrics.add(metric);
        return this;

    }

    protected void checkCache() {

        try {
            doCheckCache();
        } catch (Exception e) {
            status = "Failed";
            message += "Failed to assess Cache health.\n";
        }
    }

    protected void doCheckCache() {
        String cacheName = "http://rice.kuali.org/kim/v2_0/PermissionType";
        CacheManager cm = CoreImplServiceLocator.getCacheManagerRegistry().getCacheManagerByCacheName(cacheName);
        Cache cache = cm.getCache(cacheName);

        net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
        Metric metric = new Metric("Cache", "size", "" + nativeCache.getSize());
        metrics.add(metric);
        StatisticsGateway sg = nativeCache.getStatistics();
        metric = new Metric("Cache", "hitRatio", "" + sg.cacheHitRatio());
        metrics.add(metric);
        metric = new Metric("Cache", "expiredCount", "" + sg.cacheExpiredCount());
        metrics.add(metric);
        metric = new Metric("Cache", "missCount", "" + sg.cacheMissCount());
        metrics.add(metric);
    }

    protected void checkSessions() {
        try {
            doCheckSessions();

        } catch (Exception e) {
            status = "Failed";
            message += "Failed to assess Session health.\n";
        }
    }

    protected void doCheckSessions() throws MalformedObjectNameException, MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        String context = ConfigContext.getCurrentContextConfig().getProperty("app.context.name");

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("Catalina:type=Manager,context=/"+context+",host=localhost");
        Object activeSessions = mBeanServer.getAttribute(objectName, "activeSessions");
        Metric metric = new Metric("Session", "active", activeSessions.toString());
        metrics.add(metric);
        Object expiredSessions = mBeanServer.getAttribute(objectName, "expiredSessions");
        metric = new Metric("Session", "expired", expiredSessions.toString());
        metrics.add(metric);
        Object maxActive = mBeanServer.getAttribute(objectName, "maxActive");
        metric = new Metric("Session", "maxActive", maxActive.toString());
        metrics.add(metric);
    }

    protected void checkDatabase() {

        try {
            doCheckDatabase();
        } catch (Exception e) {
            status = "Failed";
            message += "Failed to assess Database health.\n";
        }


    }

    protected void doCheckDatabase() throws SQLException {
        DataSource ds = (DataSource) ConfigContext.getCurrentContextConfig().getObject(RiceConstants.DATASOURCE_OBJ);
        DataSource ntds = (DataSource) ConfigContext.getCurrentContextConfig().getObject(RiceConstants.NON_TRANSACTIONAL_DATASOURCE_OBJ);
        DataSource riceDS = (DataSource) ConfigContext.getCurrentContextConfig().getObject(RiceConstants.SERVER_DATASOURCE_OBJ);

        Connection conn = ds.getConnection();
        Boolean closed = conn.isClosed();
        conn.close();
        Metric metric = new Metric("Database", "kfs-xa-closed", closed.toString());
        metrics.add(metric);

        conn = ntds.getConnection();
        closed = conn.isClosed();
        conn.close();
        metric = new Metric("Database", "kfs-closed", closed.toString());
        metrics.add(metric);

        conn = riceDS.getConnection();
        closed = conn.isClosed();
        conn.close();
        metric = new Metric("Database", "rice-xa-closed", closed.toString());
        metrics.add(metric);
    }

    protected void checkMemory() {
        try {
            doCheckMemory();
        } catch (Exception e) {
            status = "Failed";
            message += "Failed to assess Memory health.\n";
        }

    }

    protected void doCheckMemory() {
        Runtime runtime = Runtime.getRuntime();
        Metric metric  = new Metric("Memory", "free", ""+ runtime.freeMemory());
        metrics.add(metric);
        metric  = new Metric("Memory", "max", ""+ runtime.maxMemory());
        metrics.add(metric);
        metric  = new Metric("Memory", "total", ""+ runtime.totalMemory());
        metrics.add(metric);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
