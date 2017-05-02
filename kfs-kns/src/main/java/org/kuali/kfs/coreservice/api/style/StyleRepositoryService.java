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
package org.kuali.kfs.coreservice.api.style;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * Service for interacting with {@link Style} data. This service primarily
 * consists of pure data-access operations on a repository of styles which are
 * accessible based on their name or id which are both unique.
 */
@WebService(name = "styleRepositoryService", targetNamespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface StyleRepositoryService {

    /**
     * Returns the style with the given name.  If no style with the given name
     * can be found, this method will return null.
     *
     * @param styleName the name of the style to retrieve, must not be null or
     *                  blank
     * @return the style with the given name, or null if no style with the given
     * name could be found
     * @throws RiceIllegalArgumentException if the given styleName is null or blank
     */
    @WebMethod(operationName = "getStyle")
    @WebResult(name = "style")
    Style getStyle(@WebParam(name = "styleName") String styleName) throws RiceIllegalArgumentException;

    /**
     * Creates or updates the Style represented by the given record.  If the
     * id on the style is not null, then it will update the existing
     * style record which has that id.  Otherwise it will create a new style in
     * the repository.
     * <p>
     * <p>When updating an existing style, the caller needs to ensure that the
     * id, versionNumber, and objectId values are set on the given Style
     * object.
     *
     * @param style the style data to create or update in the repository
     * @return the style with the given name, or null if no style with the given
     * name could be found
     * @throws RiceIllegalArgumentException if the given style is null
     */
    @WebMethod(operationName = "saveStyle")
    void saveStyle(@WebParam(name = "style") Style style) throws RiceIllegalArgumentException;

    /**
     * Returns a list of the names for all active styles in the repository. If
     * there are no active styles, this list will be empty.  It will never
     * return null.
     *
     * @return the list of names for all active styles
     */
    @WebMethod(operationName = "getStyleNames")
    @XmlElementWrapper(name = "names", required = true)
    @XmlElement(name = "name", required = false)
    @WebResult(name = "styleNames")
    List<String> getAllStyleNames();

}
