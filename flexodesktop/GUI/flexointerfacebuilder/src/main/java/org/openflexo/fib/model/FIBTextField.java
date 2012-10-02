/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fib.model;

import java.lang.reflect.Type;

public class FIBTextField extends FIBWidget {

	// TODO: handle font
	private boolean validateOnReturn = false;
	private Integer columns;
	private boolean passwd = false;
	private String text = null;

	public FIBTextField() {
	}

	@Override
	protected String getBaseName() {
		return "TextField";
	}

	@Override
	public Type getDefaultDataClass() {
		return String.class;
	}

	/**
	 * @return the columns
	 */
	public Integer getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	/**
	 * @return the passwd
	 */
	public boolean isPasswd() {
		return passwd;
	}

	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(boolean passwd) {
		this.passwd = passwd;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the validateOnReturn
	 */
	public boolean isValidateOnReturn() {
		return validateOnReturn;
	}

	/**
	 * @param validateOnReturn the validateOnReturn to set
	 */
	public void setValidateOnReturn(boolean validateOnReturn) {
		this.validateOnReturn = validateOnReturn;
	}

}
