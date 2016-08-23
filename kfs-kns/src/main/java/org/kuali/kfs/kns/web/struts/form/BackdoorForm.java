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
package org.kuali.kfs.kns.web.struts.form;

import org.kuali.kfs.kns.web.struts.action.BackdoorAction;

/**
 * A Struts ActionForm for the {@link BackdoorAction}.
 *
 * @see BackdoorAction
 *
 *
 */
public class BackdoorForm extends org.kuali.kfs.kns.web.struts.form.KualiForm {

	private static final long serialVersionUID = -2720178686804392055L;

	private String methodToCall = "";
    private String backdoorId;
    private Boolean showBackdoorLogin;
    private Boolean isAdmin;
    private String linkTarget;
    private String targetName;
    //determines whether to show the backdoor login textbox in the backdoor links page
    private String backdoorLinksBackdoorLogin;

    private String graphic="yes";

    public String getBackdoorId() {
        return backdoorId;
    }
    public void setBackdoorId(String backdoorId) {
        this.backdoorId = backdoorId;
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    public Boolean getIsAdmin() {
        return isAdmin;
    }
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    public Boolean getShowBackdoorLogin() {
        return showBackdoorLogin;
    }
    public void setShowBackdoorLogin(Boolean showBackdoorLogin) {
        this.showBackdoorLogin = showBackdoorLogin;
    }
    public String getLinkTarget() {
        return linkTarget;
    }
    public void setLinkTarget(String linkTarget) {
        this.linkTarget = linkTarget;
    }
    /*
    public String getTargetName() {
        return targetName;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    */
    public String getGraphic(){
    	return this.graphic;
    }
    public void setGraphic(String choice){
    	if(!org.apache.commons.lang.StringUtils.isEmpty(choice)&&choice.trim().equals("no")){
    		this.graphic="no";
    	}else{
    		this.graphic="yes";
    	}
    }
	public String getBackdoorLinksBackdoorLogin() {
		return backdoorLinksBackdoorLogin;
	}
	public void setBackdoorLinksBackdoorLogin(String backdoorLinksBackdoorLogin) {
		this.backdoorLinksBackdoorLogin = backdoorLinksBackdoorLogin;
	}
}
