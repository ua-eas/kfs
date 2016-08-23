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
package org.kuali.kfs.krad.web.bind;

import org.kuali.kfs.krad.uif.view.ViewModel;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;

/**
 * This is a description of what this class does - swgibson don't forget to fill this in.
 *
 *
 */
public class UifBeanPropertyBindingResult extends BeanPropertyBindingResult {
    private static final long serialVersionUID = -3740046436620585003L;

    public UifBeanPropertyBindingResult(Object target, String objectName,  boolean autoGrowNestedPaths, int autoGrowCollectionLimit) {
        super(target, objectName, autoGrowNestedPaths, autoGrowCollectionLimit);
    }

    /**
     * Create a new {@link BeanWrapper} for the underlying target object.
     * @see #getTarget()
     */
    @Override
    protected UifViewBeanWrapper createBeanWrapper() {
        Assert.state(super.getTarget() != null, "Cannot access properties on null bean instance '" + getObjectName() + "'!");
        Assert.state(super.getTarget() instanceof ViewModel, "Object must be instance of ViewModel to use Uif Bean Wrapper");

        return new UifViewBeanWrapper((ViewModel) super.getTarget());
    }
}
