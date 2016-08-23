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
package org.kuali.kfs.kns.util;

import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.rice.core.framework.util.ApplicationThreadLocal;

import java.util.HashMap;
import java.util.Map;


@Deprecated
public final class KNSGlobalVariables {

    private KNSGlobalVariables() {
        throw new UnsupportedOperationException("do not call");
    }

    private static ThreadLocal<KualiForm> kualiForms = new ApplicationThreadLocal<KualiForm>();

    private static ThreadLocal<MessageList> messageLists = new ApplicationThreadLocal<MessageList>() {
        @Override
        protected MessageList initialValue() {
            return new MessageList();
        }
    };

    private static ThreadLocal<HashMap<String, AuditCluster>> auditErrorMaps = new ApplicationThreadLocal<HashMap<String, AuditCluster>>() {
        @Override
        protected HashMap<String, AuditCluster> initialValue() {
            return new HashMap<String, AuditCluster>();
        }
    };

    /**
     * @return ArrayList containing messages.
     */
    @Deprecated
    public static MessageList getMessageList() {
        return messageLists.get();
    }

    /**
     * Sets a new message list
     *
     * @param messageList
     */
    @Deprecated
    public static void setMessageList(MessageList messageList) {
        messageLists.set(messageList);
    }

    /**
     * @return KualiForm that has been assigned to this thread of execution.
     */
    @Deprecated
    public static KualiForm getKualiForm() {
        return kualiForms.get();
    }

    /**
     * @return ArrayList containing audit error messages.
     */
    @Deprecated
    public static Map<String, AuditCluster> getAuditErrorMap() {
        return auditErrorMaps.get();
    }

    /**
     * Sets a new (clean) AuditErrorList
     *
     * @param errorMap
     */
    @Deprecated
    public static void setAuditErrorMap(HashMap<String, AuditCluster> errorMap) {
        auditErrorMaps.set(errorMap);
    }

    /**
     * sets the kualiForm object into the global variable for this thread
     *
     * @param kualiForm
     */
    @Deprecated
    public static void setKualiForm(KualiForm kualiForm) {
        kualiForms.set(kualiForm);
    }

    @Deprecated
    public static void clear() {
        GlobalVariables.clear();
        messageLists.set(new MessageList());
        auditErrorMaps.set(new HashMap<String, AuditCluster>());
        kualiForms.set(null);
    }
}
