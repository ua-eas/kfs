package edu.arizona.kfs.module.purap.document;

import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.kfs.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

public class ElectronicInvoiceRejectDocument extends org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument {

    private static final long serialVersionUID = -5309907419256633061L;

    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() started");
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            // create a preq from this eirt
            LOG.info("Creating PREQ from EIRT doc " + getDocumentNumber());
            SpringContext.getBean(ElectronicInvoiceHelperService.class).createPaymentRequest(this);
        }
    }

    /**
     * Overridden to prevent throwing of ValidationException on SaveEvent
     * Copied with no changes from from KFS3 implementation
     */
    @Override
    public void validateBusinessRules(KualiDocumentEvent event) {
        if (GlobalVariables.getMessageMap().hasErrors()) {
            logErrors();
            if (!(event instanceof SaveDocumentEvent)) {
                throw new ValidationException("errors occured before business rule");
            }
        }

        // perform validation against rules engine
        LOG.info("invoking rules engine on document " + getDocumentNumber());
        boolean isValid = true;
        isValid = SpringContext.getBean(KualiRuleService.class).applyRules(event);

        // check to see if the br eval passed or failed
        if (!isValid) {
            logErrors();
            // TODO: better error handling at the lower level and a better error message are
            // needed here
            if (!(event instanceof SaveDocumentEvent)) {
                throw new ValidationException("business rule evaluation failed");
            } else {
                LOG.error("validation error on document " + getDocumentNumber() + " bypassing save.");
                LOG.error("validation error in file " + this.invoiceFileName + " document not saved.");
                GlobalVariables.getMessageMap().clearErrorMessages();
                throw new RuntimeException("business rule evaluation failed");
            }
        } else if (GlobalVariables.getMessageMap().hasErrors()) {
            logErrors();
            if (!(event instanceof SaveDocumentEvent)) {
                throw new ValidationException("Unreported errors occured during business rule evaluation (rule developer needs to put meaningful error messages into global ErrorMap)");
            }
        }
        LOG.debug("validation completed");
    }

}
