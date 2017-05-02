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
package org.kuali.kfs.krad.app.persistence.jpa;

//import org.reflections.Reflections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract class which exposes as JPA managed classes all of the business objects in a given package
 */
public abstract class PackagePersistableBusinessObjectClassExposer implements
    PersistableBusinessObjectClassExposer {

    /**
     * Exposes all of the JPA annotated entities in the package given by the getPackageNameToExpose method
     * to be managed by the PersistenceUnit calling this
     *
     * @see PersistableBusinessObjectClassExposer#exposePersistableBusinessObjectClassNames()
     */
    @Override
    public Set<String> exposePersistableBusinessObjectClassNames() {
        Set<String> exposedClassNames = new HashSet<String>();

        for (String packageNameToExpose : getPackageNamesToExpose()) {
            /*Reflections reflections = new Reflections(packageNameToExpose);
			final Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
			for (Class<?> entityClass : entities) {
				exposedClassNames.add(entityClass.getName());
			}

			final Set<Class<?>> mappedSuperclasses = reflections.getTypesAnnotatedWith(MappedSuperclass.class);
			for (Class<?> mappedSuperclassClass : mappedSuperclasses) {
				exposedClassNames.add(mappedSuperclassClass.getName());
			}

			final Set<Class<?>> embeddables = reflections.getTypesAnnotatedWith(Embeddable.class);
			for (Class<?> embeddableClass : embeddables) {
				// may this loop never be entered
				exposedClassNames.add(embeddableClass.getName());
			}*/
        }
        return exposedClassNames;
    }

    /**
     * @return the name of the package to expose all JPA annotated entities in
     */
    public abstract List<String> getPackageNamesToExpose();

}
