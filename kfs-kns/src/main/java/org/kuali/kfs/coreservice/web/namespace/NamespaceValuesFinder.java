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
package org.kuali.kfs.coreservice.web.namespace;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.api.CoreServiceApiServiceLocator;
import org.kuali.kfs.coreservice.api.CoreServiceConstants;
import org.kuali.kfs.coreservice.api.namespace.Namespace;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NamespaceValuesFinder extends KeyValuesBase {

    @Override
    public List<KeyValue> getKeyValues() {

        // get a list of all Namespaces
        List<Namespace> namespaces = CoreServiceApiServiceLocator.getNamespaceService().findAllNamespaces();
        // copy the list of codes before sorting, since we can't modify the results from this method
        namespaces = namespaces == null ? new ArrayList<>(0) : new ArrayList<>(namespaces);

        // sort using comparator.
        Collections.sort(namespaces, NamespaceComparator.INSTANCE);

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>(namespaces.size());
        labels.add(new ConcreteKeyValue("", ""));
        final List<KeyValue> namespaceLabels = namespaces.stream()
            .filter((Namespace namespace) -> StringUtils.equalsIgnoreCase(namespace.getApplicationId(), CoreServiceConstants.ApplicationIdentifiers.FINANCIALS) || StringUtils.equalsIgnoreCase(namespace.getApplicationId(), CoreServiceConstants.ApplicationIdentifiers.RICE) || StringUtils.equalsIgnoreCase(namespace.getCode(), CoreServiceConstants.ApplicationIdentifiers.GENERAL_KUALI))
            .map((Namespace namespace) -> new ConcreteKeyValue(namespace.getCode(), namespace.getCode() + " - " + namespace.getName()))
            .collect(Collectors.toList());
        labels.addAll(namespaceLabels);
        return labels;
    }

    private static class NamespaceComparator implements Comparator<Namespace> {
        public static final Comparator<Namespace> INSTANCE = new NamespaceComparator();

        @Override
        public int compare(Namespace o1, Namespace o2) {
            return o1.getCode().compareTo(o2.getCode());
        }
    }
}
