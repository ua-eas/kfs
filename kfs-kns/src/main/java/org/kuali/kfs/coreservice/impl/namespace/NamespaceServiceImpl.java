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
package org.kuali.kfs.coreservice.impl.namespace;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.api.namespace.Namespace;
import org.kuali.kfs.coreservice.api.namespace.NamespaceService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonMap;

public class NamespaceServiceImpl implements NamespaceService {

    private BusinessObjectService boService;

    @Override
    public Namespace getNamespace(String code) {
        if (StringUtils.isBlank(code)) {
            throw new RiceIllegalArgumentException("the code is blank");
        }

        return NamespaceBo.to(boService.findByPrimaryKey(NamespaceBo.class, singletonMap("code", code)));
    }

    @Override
    public List<Namespace> findAllNamespaces() {
        List<NamespaceBo> namespaceBos = (List<NamespaceBo>) boService.findAll(NamespaceBo.class);
        List<Namespace> namespaces = new ArrayList<Namespace>();

        for (NamespaceBo bo : namespaceBos) {
            namespaces.add(NamespaceBo.to(bo));
        }
        return Collections.unmodifiableList(namespaces);
    }

    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
    }
}
