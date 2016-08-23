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
package org.kuali.kfs.kns.rule.event;

import org.apache.log4j.Logger;
import org.kuali.kfs.kns.rule.PromptBeforeValidation;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Event for handling warnings/questions before rules are called.
 *
 *
 */
public class PromptBeforeValidationEvent extends KualiDocumentEventBase {
    private static final Logger LOG = Logger.getLogger(PromptBeforeValidationEvent.class);

    boolean performQuestion;
    String actionForwardName;
    String questionId;
    String questionText;
    String questionType;
    String questionCaller;
    String questionContext;


    /**
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public PromptBeforeValidationEvent(String description, String errorPathPrefix, Document document) {

        super(description, errorPathPrefix);
        this.document = document;

        LOG.debug(description);

        performQuestion = false;
    }


    /**
     * @return Returns the actionForwardName.
     */
    public String getActionForwardName() {
        return actionForwardName;
    }

    /**
     * @param actionForwardName The actionForwardName to set.
     */
    public void setActionForwardName(String actionForwardName) {
        this.actionForwardName = actionForwardName;
    }

    /**
     * @return Returns the performQuestion.
     */
    public boolean isPerformQuestion() {
        return performQuestion;
    }

    /**
     * @param performQuestion The performQuestion to set.
     */
    public void setPerformQuestion(boolean performQuestion) {
        this.performQuestion = performQuestion;
    }

    /**
     * @return Returns the questionCaller.
     */
    public String getQuestionCaller() {
        return questionCaller;
    }

    /**
     * @param questionCaller The questionCaller to set.
     */
    public void setQuestionCaller(String questionCaller) {
        this.questionCaller = questionCaller;
    }

    /**
     * @return Returns the questionContext.
     */
    public String getQuestionContext() {
        return questionContext;
    }

    /**
     * @param questionContext The questionContext to set.
     */
    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    /**
     * @return Returns the questionId.
     */
    public String getQuestionId() {
        return questionId;
    }

    /**
     * @param questionId The questionId to set.
     */
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    /**
     * @return Returns the questionText.
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * @param questionText The questionText to set.
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * @return Returns the questionType.
     */
    public String getQuestionType() {
        return questionType;
    }

    /**
     * @param questionType The questionType to set.
     */
    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    /**
     * @see KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class<? extends BusinessRule> getRuleInterfaceClass() {
        return PromptBeforeValidation.class;
    }


    /**
     * @see KualiDocumentEvent#invokeRuleMethod(BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return true;
    }
}
