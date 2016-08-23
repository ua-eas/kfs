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
package org.kuali.kfs.krad.util;

import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.uif.util.UifFormManager;
import org.kuali.rice.core.framework.util.ApplicationThreadLocal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Holds all of our thread local variables and accessors for those
 *
 *
 */
public final class GlobalVariables {

    private static ThreadLocal<LinkedList<GlobalVariables>> GLOBAL_VARIABLES_STACK = new ApplicationThreadLocal<LinkedList<GlobalVariables>>() {
        protected LinkedList<GlobalVariables> initialValue() {
            LinkedList<GlobalVariables> globalVariablesStack = new LinkedList<GlobalVariables>();
            globalVariablesStack.add(new GlobalVariables());
            return globalVariablesStack;
        }
    };

    private static GlobalVariables getCurrentGlobalVariables() {
        return GLOBAL_VARIABLES_STACK.get().getLast();
    }

    static GlobalVariables pushGlobalVariables() {
        GlobalVariables vars = new GlobalVariables();
        GLOBAL_VARIABLES_STACK.get().add(vars);
        return vars;
    }

    static GlobalVariables popGlobalVariables() {
        return GLOBAL_VARIABLES_STACK.get().removeLast();
    }

    static void reset() {
        LinkedList<GlobalVariables> stack = GLOBAL_VARIABLES_STACK.get();
        stack.clear();
        stack.add(new GlobalVariables());
    }

    private UserSession userSession = null;
    private String hideSessionFromTestsMessage = null;
    private org.kuali.kfs.krad.util.MessageMap messageMap = new org.kuali.kfs.krad.util.MessageMap();
    private Map<String,Object> requestCache = new HashMap<String, Object>();
    private UifFormManager uifFormManager = null;

    private GlobalVariables() {}

    /**
     * @return the UserSession that has been assigned to this thread of execution it is important that this not be called by
     *         anything that lives outside
     */
    public static UserSession getUserSession() {
        GlobalVariables vars = getCurrentGlobalVariables();
        String message = vars.hideSessionFromTestsMessage;
        if (message != null) {
            throw new RuntimeException(message);
        }
        return vars.userSession;
    }

    /**
     * Sets an error message for tests that try to use the session without declaring it.
     * This method should be use by only KualiTestBase, not by other test code and especially not by production code.
     *
     * @param message the detail to throw, or null to allow access to the session
     */
    public static void setHideSessionFromTestsMessage(String message) {
        GlobalVariables vars = getCurrentGlobalVariables();
        vars.hideSessionFromTestsMessage = message;
    }

    /**
     * sets the userSession object into the global variable for this thread
     *
     * @param userSession
     */
    public static void setUserSession(UserSession userSession) {
        GlobalVariables vars = getCurrentGlobalVariables();
        vars.userSession = userSession;
    }

    public static org.kuali.kfs.krad.util.MessageMap getMessageMap() {
        GlobalVariables vars = getCurrentGlobalVariables();
        return vars.messageMap;
    }

    /**
     * Merges a message map into the global variables error map
     * @param messageMap
     */
    public static void mergeErrorMap(org.kuali.kfs.krad.util.MessageMap messageMap) {
        getMessageMap().getErrorMessages().putAll(messageMap.getErrorMessages());
        getMessageMap().getWarningMessages().putAll(messageMap.getWarningMessages());
        getMessageMap().getInfoMessages().putAll(messageMap.getInfoMessages());
    }

    /**
     * Sets a new (clean) MessageMap
     *
     * @param messageMap
     */
    public static void setMessageMap(org.kuali.kfs.krad.util.MessageMap messageMap) {
        GlobalVariables vars = getCurrentGlobalVariables();
        vars.messageMap = messageMap;
    }

    public static Object getRequestCache(String cacheName) {
        GlobalVariables vars = getCurrentGlobalVariables();
        return vars.requestCache.get(cacheName);
    }

    public static void setRequestCache(String cacheName, Object cacheObject) {
        GlobalVariables vars = getCurrentGlobalVariables();
        vars.requestCache.put(cacheName, cacheObject);
    }

    /**
     * Retrieves the {@link UifFormManager} which can be used to store and remove forms
     * from the session
     *
     * @return UifFormManager
     */
    public static UifFormManager getUifFormManager() {
        GlobalVariables vars = getCurrentGlobalVariables();
        return vars.uifFormManager;
    }

    /**
     * Sets a {@link UifFormManager} for the current thread
     *
     * @param uifFormManager
     */
    public static void setUifFormManager(UifFormManager uifFormManager) {
        GlobalVariables vars = getCurrentGlobalVariables();
        vars.uifFormManager = uifFormManager;
    }

    /**
     * Clears out GlobalVariable objects with the exception of the UserSession
     */
    public static void clear() {
        GlobalVariables vars = getCurrentGlobalVariables();
        vars.messageMap = new org.kuali.kfs.krad.util.MessageMap();
        vars.requestCache = new HashMap<String,Object>();
    }

    /**
     * Pushes a new GlobalVariables object onto the ThreadLocal GlobalVariables stack, invokes the runnable,
     * and pops the GlobalVariables off in a finally clause
     * @param callable the code to run under a new set of GlobalVariables
     */
    public static <T> T doInNewGlobalVariables(Callable<T> callable) throws Exception {
        return doInNewGlobalVariables(null, callable);
    }

    /**
     * Convenience method that creates a new GlobalVariables stack frame, initialized with the provided
     * UserSession (which may be the previous UserSession).
     * @param userSession the UserSession to initialize the new frame with (may be null)
     * @param callable the code to run under a new set of GlobalVariables
     * @throws Exception
     */
    public static <T> T doInNewGlobalVariables(UserSession userSession, Callable<T> callable) throws Exception {
        try {
            GlobalVariables vars = pushGlobalVariables();
            if (userSession != null) {
                vars.userSession = userSession;
            }
            return callable.call();
        } finally {
            popGlobalVariables();
        }
    }
}
