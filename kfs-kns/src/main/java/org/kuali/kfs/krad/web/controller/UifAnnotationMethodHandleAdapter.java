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
package org.kuali.kfs.krad.web.controller;

import org.kuali.kfs.krad.web.bind.UifServletRequestDataBinder;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.HttpServletRequest;

/**
 * Overloaded in order to hook in the UIF Binder classes
 *
 *
 */
public class UifAnnotationMethodHandleAdapter extends AnnotationMethodHandlerAdapter {

    @Override
    protected ServletRequestDataBinder createBinder(HttpServletRequest request, Object target,
            String objectName) throws Exception {
        if (target != null) {
            // only override for UifFormBase models so that non KRAD spring MVC
            // can be used in same dispatcher servlet.
            if (target instanceof UifFormBase) {
                return new UifServletRequestDataBinder(target, objectName);
            }
        }

        return super.createBinder(request, target, objectName);
    }

}
