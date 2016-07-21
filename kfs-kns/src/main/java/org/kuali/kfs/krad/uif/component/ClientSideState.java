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
package org.kuali.kfs.krad.uif.component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used on <code>Component</code> properties to indicate the property
 * value should be exposed in the client and populated back from the client
 *
 * <p>
 * Some components have state that can be altered on the client without making a server call. An
 * example of this is the open state for an <code>Disclosure</code>. When the View is refreshed
 * from the server, the refreshed state needs to reflect the last state before the refresh was made. The
 * framework supports this exposure of state in the client and syncing of the client state to the server
 * component by means of this annotation.
 *
 * During the finalize phase, values for properties that contain this annotation will be pulled and added
 * to the ViewState object that is exposed through JavaScript. The property name/value pair is associated
 * with the component id on the ViewState object so that the state can be updated when the view is refreshed.
 *
 * Properties exposed client side can also be accessed and updated by custom script.
 * e.g.
 * var componentState = ViewState['componentId']; // or ViewState.componentId
 * var propertyValue = componentState['propertyName'];
 * </p>
 *
 * <p>
 * The property will be exposed client side with the identifier given by {@link #variableName()}. If not specified,
 * the name of the property for which the annotation applies will be used
 * </p>
 *
 * 
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClientSideState {

    /**
     * Identifier to expose the client side variable as, can be left blank in which case
     * the name of the property the annotation is associated with will be used
     *
     * @return String client side variable name
     */
    public String variableName() default "";

}
