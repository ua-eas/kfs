/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.sys.mock;

import org.kuali.kfs.sys.mail.MailMessage;
import org.kuali.kfs.sys.service.EmailService;

public class MockEmailService implements EmailService {
    public String defaultToAddress = "defaultto@kuali.co";
    public String fromAddress = "from@kuali.co";
    public int emailCount = 0;
    public MailMessage message;
    public boolean htmlMessage;

    @Override
    public String getFromAddress() {
        return fromAddress;
    }

    @Override
    public String getDefaultToAddress() {
        return defaultToAddress;
    }

    @Override
    public void sendMessage(MailMessage message, boolean htmlMessage) {
        emailCount++;
        this.message = message;
        this.htmlMessage = htmlMessage;
    }
}
