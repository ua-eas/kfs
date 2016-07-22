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
package org.kuali.kfs.krad.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name="KRNS_NTE_TYP_T")
public class NoteType extends PersistableBusinessObjectBase {

	@Id
	@Column(name="NTE_TYP_CD")
	private String noteTypeCode;
	@Column(name="TYP_DESC_TXT")
	private String noteTypeDescription;
	@Column(name="ACTV_IND")
	private boolean noteTypeActiveIndicator;

	/**
	 * Default constructor.
	 */
	public NoteType() {

	}

	/**
	 * Gets the noteTypeCode attribute.
	 *
	 * @return Returns the noteTypeCode
	 *
	 */
	public String getNoteTypeCode() {
		return noteTypeCode;
	}

	/**
	 * Sets the noteTypeCode attribute.
	 *
	 * @param noteTypeCode The noteTypeCode to set.
	 *
	 */
	public void setNoteTypeCode(String noteTypeCode) {
		this.noteTypeCode = noteTypeCode;
	}


	/**
	 * Gets the noteTypeDescription attribute.
	 *
	 * @return Returns the noteTypeDescription
	 *
	 */
	public String getNoteTypeDescription() {
		return noteTypeDescription;
	}

	/**
	 * Sets the noteTypeDescription attribute.
	 *
	 * @param noteTypeDescription The noteTypeDescription to set.
	 *
	 */
	public void setNoteTypeDescription(String noteTypeDescription) {
		this.noteTypeDescription = noteTypeDescription;
	}


	/**
	 * Gets the noteTypeActiveIndicator attribute.
	 *
	 * @return Returns the noteTypeActiveIndicator
	 *
	 */
	public boolean isNoteTypeActiveIndicator() {
		return noteTypeActiveIndicator;
	}


	/**
	 * Sets the noteTypeActiveIndicator attribute.
	 *
	 * @param noteTypeActiveIndicator The noteTypeActiveIndicator to set.
	 *
	 */
	public void setNoteTypeActiveIndicator(boolean noteTypeActiveIndicator) {
		this.noteTypeActiveIndicator = noteTypeActiveIndicator;
	}
}

