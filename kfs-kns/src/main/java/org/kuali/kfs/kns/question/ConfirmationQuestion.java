/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
 * This class support the ConfirmationQuestion. For example: a Yes/No dialog window.
 */

public class ConfirmationQuestion extends QuestionBase {

    public static final String YES = "0";
    public static final String NO = "1";

    public ConfirmationQuestion() {
        // this should be set by question form
        super("Are you sure you want to cancel?", new ArrayList(2));
        this.buttons.add("Yes");
        this.buttons.add("No");

    }

}
