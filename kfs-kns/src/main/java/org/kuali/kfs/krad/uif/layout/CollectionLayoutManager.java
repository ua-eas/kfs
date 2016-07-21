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
package org.kuali.kfs.krad.uif.layout;

import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.CollectionGroupBuilder;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.field.FieldGroup;
import org.kuali.kfs.krad.uif.view.View;

import java.util.List;

/**
 * Layout manager implementations that work with a collection (such as a table
 * layout) should implement this interface for building the collection
 * <code>Component</code> instances
 * 
 * <p>
 * Unlike other <code>Group</code> instances, <code>CollectionGroup</code>
 * instances need to generate new instances of the configured components for
 * each line of the collection. The <code>Field</code> instances for each line
 * are wrapped differently depending on what <code>LayoutManager</code> is being
 * applied. Therefore as the collection lines are being built (during the
 * applyModel phase) this method will be invoked on the manager so that it may
 * setup the line as needed.
 * </p>
 * 
 * 
 * @see CollectionGroupBuilder
 */
public interface CollectionLayoutManager extends LayoutManager {

	/**
	 * Call to the layout manager to build the components necessary for the
	 * given collection line
	 * 
	 * <p>
	 * As the collection is being iterated over by the
	 * <code>CollectionGroupBuilder</code> this method is invoked for each line.
	 * The builder will create copies of the configured fields and actions for
	 * the line and pass into the layout manager so they can be assembled
	 * </p>
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param model
	 *            - object containing the data
	 * @param collectionGroup
	 *            - collection group the layout manager applies to
	 * @param lineFields
	 *            - the field instances for the collection line which were
	 *            copied from the collection groups items, id and binding
	 *            already updated
	 * @param subCollectionFields
	 *            - group field instances for each sub collection of the current
	 *            line
	 * @param bindingPath
	 *            - binding path for the groups items (if DataBinding)
	 * @param actions
	 *            - list of action instances for the collection line, with id
	 *            and binding updated
	 * @param idSuffix
	 *            - suffix to use for any generated items
	 * @param currentLine
	 *            - object instance for the current line, or null if add line
	 * @param lineIndex
	 *            - index of the collection line being iterated over, or -1 if
	 *            the add line
	 */
	public void buildLine(View view, Object model, CollectionGroup collectionGroup, List<Field> lineFields,
			List<FieldGroup> subCollectionFields, String bindingPath, List<ActionField> actions, String idSuffix,
			Object currentLine, int lineIndex);

	/**
	 * Field group instance that is used as a prototype for creating the
	 * sub-collection field groups. For each sub-collection a copy of the
	 * prototype is made and the list will be passed to the layout manager
	 * buildLine method
	 * 
	 * @return GroupField instance to use as prototype
	 */
	public FieldGroup getSubCollectionFieldGroupPrototype();
}
