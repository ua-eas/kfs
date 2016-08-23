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
package org.kuali.kfs.kns.question;

import java.util.ArrayList;

/**
 * This class is a base class to implement questions types.
 * <p>
 * <p>
 * "confirmation questions") rather than specific questions.
 */

public class QuestionBase implements Question {
    String question;
    ArrayList buttons;

    /**
     * default constructor
     *
     * @param question the question to assign to this question prompt
     * @param buttons  the buttons associated with it
     */
    public QuestionBase(String question, ArrayList buttons) {
        this.question = question;
        this.buttons = buttons;
    }

    /**
     * returns the index associated with a specified button
     *
     * @param btnText the text of the button
     * @return the index of this button
     */
    public String getButtonIndex(String btnText) {
        return "" + buttons.indexOf(btnText);
    }

    /**
     * @return Returns the buttons.
     */
    public ArrayList getButtons() {
        return buttons;
    }

    /**
     * @param buttons The buttons to set.
     */
    public void setButtons(ArrayList buttons) {
        this.buttons = buttons;
    }

    /**
     * @return Returns the question.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question The question to set.
     */
    public void setQuestion(String question) {
        this.question = question;
    }
}
