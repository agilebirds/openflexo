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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTextArea.FIBTextAreaImpl.class)
@XMLElement(xmlTag = "TextArea")
public interface FIBTextArea extends FIBTextWidget {

	@PropertyIdentifier(type = boolean.class)
	public static final String VALIDATE_ON_RETURN_KEY = "validateOnReturn";
	@PropertyIdentifier(type = Integer.class)
	public static final String COLUMNS_KEY = "columns";
	@PropertyIdentifier(type = Integer.class)
	public static final String ROWS_KEY = "rows";

	@Override
	@Getter(value = VALIDATE_ON_RETURN_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean isValidateOnReturn();

	@Override
	@Setter(VALIDATE_ON_RETURN_KEY)
	public void setValidateOnReturn(boolean validateOnReturn);

	@Override
	@Getter(value = COLUMNS_KEY)
	@XMLAttribute
	public Integer getColumns();

	@Override
	@Setter(COLUMNS_KEY)
	public void setColumns(Integer columns);

	@Getter(value = ROWS_KEY)
	@XMLAttribute
	public Integer getRows();

	@Setter(ROWS_KEY)
	public void setRows(Integer rows);

	public static abstract class FIBTextAreaImpl extends FIBTextWidgetImpl implements FIBTextArea {

		public Integer rows = null;

		public FIBTextAreaImpl() {
		}

		@Override
		public String getBaseName() {
			return "TextArea";
		}

		/**
		 * @return the rows
		 */
		@Override
		public Integer getRows() {
			return rows;
		}

		/**
		 * @param rows
		 *            the rows to set
		 */
		@Override
		public void setRows(Integer rows) {
			this.rows = rows;

		}

	}
}
