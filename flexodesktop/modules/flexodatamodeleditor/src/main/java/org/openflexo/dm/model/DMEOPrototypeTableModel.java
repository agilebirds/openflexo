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
package org.openflexo.dm.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOPrototypeTableModel extends AbstractModel<EOPrototypeRepository, DMEOPrototype> {

	protected static final Logger logger = Logger.getLogger(DMEOPrototypeTableModel.class.getPackage().getName());

	public DMEOPrototypeTableModel(EOPrototypeRepository prototypeRepository, FlexoProject project) {
		super(prototypeRepository, project);
		addToColumns(new IconColumn<DMEOPrototype>("prototype_icon", 30) {
			@Override
			public Icon getIcon(DMEOPrototype prototype) {
				return DMEIconLibrary.DM_EOATTRIBUTE_ICON;
			}
		});
		addToColumns(new StringColumn<DMEOPrototype>("name", 150) {
			@Override
			public String getValue(DMEOPrototype prototype) {
				return prototype.getName();
			}

			// public void setValue(DMEOPrototype prototype, String aValue)
			// {
			// prototype.setName(aValue);
			// }
		});
		addToColumns(new StringColumn<DMEOPrototype>("external_type", 150) {
			@Override
			public String getValue(DMEOPrototype prototype) {
				return prototype.getExternalType();
			}

			// public void setValue(DMEOPrototype prototype, String aValue)
			// {
			// prototype.setExternalType(aValue);
			// }
		});
		addToColumns(new StringColumn<DMEOPrototype>("value_type", 150) {
			@Override
			public String getValue(DMEOPrototype prototype) {
				return prototype.getValueType();
			}

			// public void setValue(DMEOPrototype prototype, String aValue)
			// {
			// prototype.setValueType(aValue);
			// }
		});
		addToColumns(new StringColumn<DMEOPrototype>("width", 150) {
			@Override
			public String getValue(DMEOPrototype prototype) {
				return "" + prototype.getWidth();
			}

			// public void setValue(DMEOPrototype prototype, String aValue)
			// {
			// try {
			// int newValue = Integer.parseInt(aValue);
			// prototype.setWidth(newValue);
			// } catch (NumberFormatException e) {
			// }
			// }
		});
		/*addToColumns(new EditableStringColumn<DMEOPrototype>("description", 250) {
		    public String getValue(DMEOPrototype prototype)
		    {
		        return prototype.getDescription();
		    }

		    public void setValue(DMEOPrototype prototype, String aValue)
		    {
		        prototype.setDescription(aValue);
		    }
		});*/

	}

	public EOPrototypeRepository getPrototypeRepository() {
		return getModel();
	}

	@Override
	public DMEOPrototype elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return (DMEOPrototype) getPrototypeRepository().getOrderedChildren().elementAt(row);
		} else {
			return null;
		}
	}

	public DMEOPrototype prototypeAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getPrototypeRepository() != null) {
			return getPrototypeRepository().getOrderedChildren().size();
		}
		return 0;
	}

}
