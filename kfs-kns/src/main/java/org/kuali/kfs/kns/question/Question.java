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
 * This interface defines methods that are required to support c Confirmation Question.
 *
 *
 */
public interface Question {
    /**
     * returns the index associated with a specified button
     *
     * @param btnText the text of the button
     * @return the index of this button
     */
    public String getButtonIndex(String btnText);

    /**
     * @return Returns the buttons.
     */
    public abstract ArrayList getButtons();

    /**
     * @param buttons The buttons to set.
     */
    public abstract void setButtons(ArrayList buttons);

    /**
     * @return Returns the question.
     */
    public abstract String getQuestion();

    /**
     * @param question The question to set.
     */
    public abstract void setQuestion(String question);
}
