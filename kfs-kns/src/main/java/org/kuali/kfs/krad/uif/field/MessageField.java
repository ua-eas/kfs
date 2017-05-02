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
package org.kuali.kfs.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.ComponentBase;

/**
 * Encapsulates a text message to be displayed
 * <p>
 * <p>
 * The <code>MessageField</code> is used to display static text in the user
 * interface. The message type can be used to group similar messages for styling
 * </p>
 */
public class MessageField extends FieldBase {
    private static final long serialVersionUID = 4090058533452450395L;

    private String messageText;
    private String messageType;

    public MessageField() {
        super();
    }

    /**
     * Override to render only if the message text has been given or there is a conditional expression on the
     * message text
     *
     * @see ComponentBase#isRender()
     */
    @Override
    public boolean isRender() {
        boolean render = super.isRender();

        if (render) {
            render = getPropertyExpressions().containsKey("messageText") || (StringUtils.isNotBlank(messageText)
                && !StringUtils.equals(messageText, "&nbsp;"));
        }

        return render;
    }

    public MessageField(String messageType) {
        this.messageType = messageType;
    }

    public MessageField(String messageText, String messageType) {
        this.messageText = messageText;
        this.messageType = messageType;
    }

    /**
     * Text that makes up the message that will be displayed
     *
     * @return String message text
     */
    public String getMessageText() {
        return this.messageText;
    }

    /**
     * Setter for the message text
     *
     * @param messageText
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * Type of the field's message, used to suffix the message fields id
     * <p>
     * <p>
     * Messages that have similar intent can be grouped by this type string. For
     * messages of the same type, their id will contain the same suffix which
     * can be used for scripting to apply additional styling or behavior to that
     * groups of messages (for example show/hide)
     * </p>
     *
     * @return String message type
     */
    public String getMessageType() {
        return this.messageType;
    }

    /**
     * Setter for the message's type
     *
     * @param messageType
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

}
