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
package org.kuali.kfs.krad.workflow.postprocessor;

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.AfterProcessEvent;
import org.kuali.rice.kew.framework.postprocessor.BeforeProcessEvent;
import org.kuali.rice.kew.framework.postprocessor.DeleteEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentLockingEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.framework.postprocessor.PostProcessor;
import org.kuali.rice.kew.framework.postprocessor.ProcessDocReport;

import java.util.List;


/**
 *
 * This class is the public entry point by which workflow communicates status changes,
 * level changes, and other useful changes.
 *
 * Note that this class delegates all of these activities to the PostProcessorService,
 * which does the actual work.  This is done to ensure proper transaction scoping, and
 * to resolve some issues present otherwise.
 *
 * Because of this, its important to understand that a transaction will be started at
 * the PostProcessorService method call, so any work that needs to be done within the
 * same transaction needs to happen inside that service implementation, rather than
 * in here.
 *
 */
public class KualiPostProcessor implements PostProcessor {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);

    /**
     *
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#doRouteStatusChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange)
     */
    @Override
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().doRouteStatusChange(statusChangeEvent);
    }

    /**
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#doActionTaken(org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent)
     */
    @Override
    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().doActionTaken(event);
    }

    /**
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#afterActionTaken(org.kuali.rice.kew.api.action.ActionType, org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent)
     */
    @Override
    public ProcessDocReport afterActionTaken(ActionType performed, ActionTakenEvent event) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().afterActionTaken(performed, event);
    }

    /**
     *
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#doDeleteRouteHeader(org.kuali.rice.kew.framework.postprocessor.DeleteEvent)
     */
    @Override
    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().doDeleteRouteHeader(event);
    }

    /**
     *
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#doRouteLevelChange(org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange)
     */
    @Override
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().doRouteLevelChange(levelChangeEvent);
    }

    /**
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#beforeProcess(org.kuali.rice.kew.framework.postprocessor.BeforeProcessEvent)
     */
    @Override
    public ProcessDocReport beforeProcess(BeforeProcessEvent beforeProcessEvent) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().beforeProcess(beforeProcessEvent);
    }

    /**
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#afterProcess(org.kuali.rice.kew.framework.postprocessor.AfterProcessEvent)
     */
    @Override
    public ProcessDocReport afterProcess(AfterProcessEvent afterProcessEvent) throws Exception {
        return KRADServiceLocatorInternal.getPostProcessorService().afterProcess(afterProcessEvent);
    }

    /**
     * @see org.kuali.rice.kew.framework.postprocessor.PostProcessor#getDocumentIdsToLock(org.kuali.rice.kew.framework.postprocessor.DocumentLockingEvent)
     */
    @Override
	public List<String> getDocumentIdsToLock(DocumentLockingEvent documentLockingEvent) throws Exception {
		return KRADServiceLocatorInternal.getPostProcessorService().getDocumentIdsToLock(documentLockingEvent);
	}

}
