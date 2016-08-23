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
package org.kuali.kfs.kns.rules;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.kuali.kfs.kns.question.ConfirmationQuestion;
import org.kuali.kfs.kns.rule.PromptBeforeValidation;
import org.kuali.kfs.kns.rule.event.PromptBeforeValidationEvent;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * This class simplifies requesting clarifying user input prior to applying business rules. It mostly shields the classes that
 * extend it from being aware of the web layer, even though the input is collected via a series of one or more request/response
 * cycles.
 *
 * Beware: method calls with side-effects will have unusual results. While it looks like the doRules method is executed
 * sequentially, in fact, it is more of a geometric series: if n questions are asked, then the code up to and including the first
 * question is executed n times, the second n-1 times, ..., the last question only one time.
 *
 *
 */
public abstract class PromptBeforeValidationBase implements PromptBeforeValidation {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PromptBeforeValidationBase.class);

    protected String question;
    protected String buttonClicked;
    protected PromptBeforeValidationEvent event;
    protected KualiForm form;

    private class IsAskingException extends RuntimeException {
    }

    /**
     *
     * This class acts similarly to HTTP session, but working inside a REQUEST parameter
     */
    public class ContextSession {
        private final static String DELIMITER = ".";
        PromptBeforeValidationEvent event;

        public ContextSession(String context, PromptBeforeValidationEvent event) {
            this.event = event;

            this.event.setQuestionContext(context);
            if (this.event.getQuestionContext() == null) {
                this.event.setQuestionContext("");
            }

        }

        /**
         * Whether a question with a given ID has already been asked
         *
         * @param id the ID of the question, an arbitrary value, but must be consistent
         * @return
         */
        public boolean hasAsked(String id) {
            return StringUtils.contains(event.getQuestionContext(), id);
        }

        /**
         * Invoked to indicate that the user should be prompted a question
         *
         * @param id the ID of the question, an arbitrary value, but must be consistent
         * @param text the question text, to be displayed to the user
         */
        public void askQuestion(String id, String text) {
            event.setQuestionId(id);
            event.setQuestionType(KRADConstants.CONFIRMATION_QUESTION);
            event.setQuestionText(text);
            event.setPerformQuestion(true);
        }

        public void setAttribute(String name, String value) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("setAttribute(" + name + "," + value + ")");
            }
            event.setQuestionContext(event.getQuestionContext() + DELIMITER + name + DELIMITER + value);

        }

        public String getAttribute(String name) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getAttribute(" + name + ")");
            }
            String result = null;

            Iterator values = Arrays.asList(event.getQuestionContext().split("\\" + DELIMITER)).iterator();

            while (values.hasNext()) {
                if (values.next().equals(name)) {
                    try {
                        result = (String) values.next();
                    }
                    catch (NoSuchElementException e) {
                        result = null;
                    }
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("returning " + result);
            }
            return result;
        }

    }

    /**
     * Implementations will override this method to do perform the actual prompting and/or logic
     *
     * They are able to utilize the following methods:
     * <li> {@link PromptBeforeValidationBase#abortRulesCheck()}
     * <li> {@link PromptBeforeValidationBase#askOrAnalyzeYesNoQuestion(String, String)}
     * <li> {@link #hasAsked(String)}
     *
     * @param document
     * @return
     */
    public abstract boolean doPrompts(Document document);

    private boolean isAborting;

    ContextSession session;

    public PromptBeforeValidationBase() {
    }


    public boolean processPrompts(ActionForm form, HttpServletRequest request, PromptBeforeValidationEvent event) {
        question = request.getParameter(KRADConstants.QUESTION_INST_ATTRIBUTE_NAME);
        buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        this.event = event;
        this.form = (KualiForm) form;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Question is: " + question);
            LOG.debug("ButtonClicked: " + buttonClicked);
            LOG.debug("QuestionContext() is: " + event.getQuestionContext());
        }

        session = new ContextSession(request.getParameter(KRADConstants.QUESTION_CONTEXT), event);

        boolean result = false;

        try {
            result = doPrompts(event.getDocument());
        }
        catch (IsAskingException e) {
            return false;
        }

        if (isAborting) {
            return false;
        }

        return result;
    }

    /**
     * This bounces the user back to the document as if they had never tried to routed it. (Business rules are not invoked
     * and the action is not executed.)
     *
     */
    public void abortRulesCheck() {
        event.setActionForwardName(RiceConstants.MAPPING_BASIC);
        isAborting = true;
    }

    /**
     * This method poses a Y/N question to the user.  If the user has already answered the question, then it returns whether
     * the answer to the question was yes or no
     *
     * Code that invokes this method will behave a bit strangely, so you should try to keep it as simple as possible.
     *
     * @param id an ID for the question
     * @param text the text of the question, to be displayed on the screen
     * @return true if the user answered Yes, false if the user answers no
     * @throws IsAskingException if the user needs to be prompted the question
     */
    public boolean askOrAnalyzeYesNoQuestion(String id, String text) throws IsAskingException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering askOrAnalyzeYesNoQuestion(" + id + "," + text + ")");
        }

        String cached = (String) session.getAttribute(id);
        if (cached != null) {
            LOG.debug("returning cached value: " + id + "=" + cached);
            return new Boolean(cached).booleanValue();
        }

        if (id.equals(question)) {
            session.setAttribute(id, Boolean.toString(!ConfirmationQuestion.NO.equals(buttonClicked)));
            return !ConfirmationQuestion.NO.equals(buttonClicked);
        }
        else if (!session.hasAsked(id)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forcing question to be asked: " + id);
            }
            session.askQuestion(id, text);
        }

        LOG.debug("Throwing Exception to force return to Action");
        throw new IsAskingException();
    }

}
