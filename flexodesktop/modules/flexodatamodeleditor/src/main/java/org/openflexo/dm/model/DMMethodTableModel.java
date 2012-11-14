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

import javax.naming.InvalidNameException;
import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.components.tabular.model.TypeSelectorColumn;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ReservedKeyword;
import org.openflexo.view.controller.FlexoController;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMMethodTableModel extends AbstractModel<DMEntity, DMMethod> {

	public static final String METHOD_NAME_COLUMN_TITLE = "name";
	protected static final Logger logger = Logger.getLogger(DMMethodTableModel.class.getPackage().getName());

	public DMMethodTableModel(DMEntity entity, FlexoProject project) {
		super(entity, project);
		addToColumns(new IconColumn<DMMethod>("method_icon", 30) {
			@Override
			public Icon getIcon(DMMethod method) {
				return DMEIconLibrary.DM_METHOD_ICON;
			}
		});
		addToColumns(new IconColumn<DMMethod>("read_only", 25) {
			@Override
			public Icon getIcon(DMMethod method) {
				return method.getIsReadOnly() ? DMEIconLibrary.READONLY_ICON : DMEIconLibrary.MODIFIABLE_ICON;
			}

			@Override
			public String getLocalizedTooltip(DMMethod method) {
				return method.getIsReadOnly() ? FlexoLocalization.localizedForKey("is_read_only") : FlexoLocalization
						.localizedForKey("is_not_read_only");
			}
		});
		addToColumns(new EditableStringColumn<DMMethod>(METHOD_NAME_COLUMN_TITLE, 150) {
			@Override
			public String getValue(DMMethod method) {
				return method.getName();
			}

			@Override
			public void setValue(DMMethod method, String aValue) {
				try {
					if (ReservedKeyword.contains(aValue)) {
						throw new InvalidNameException(aValue + " is a reserved keyword.");
					}
					method.setName(aValue);
				} catch (DuplicateMethodSignatureException e) {
					FlexoController.showError(FlexoLocalization.localizedForKey("sorry_this_signature_matches_an_other_method_signature"));
				} catch (InvalidNameException e) {
					FlexoController.showError(FlexoLocalization.localizedForKey("reserved_word"));
				}
			}
		});
		addToColumns(new TypeSelectorColumn<DMMethod>("returns", 150, project) {
			@Override
			public DMType getValue(DMMethod method) {
				return method.getReturnType();
			}

			@Override
			public void setValue(DMMethod method, DMType aValue) {
				method.setReturnType(aValue, true);
			}
		});

		/*EntitySelectorColumn<DMMethod,DMEntity> esc = new EntitySelectorColumn<DMMethod,DMEntity>("returns", 150, project) {
		    public DMEntity getValue(DMMethod method)
		    {
		    	if (method.getReturnType() != null)
		    		return method.getReturnType().getBaseEntity();
		    	return null;
		    }

		    public void setValue(DMMethod method, DMEntity aValue)
		    {
		    	if (method.getReturnType() == null) method.setReturnType(DMType.makeResolvedDMType(aValue));
		    	else method.getReturnType().setBaseEntity(aValue);
		    }
		};
		esc.setNullStringRepresentation("void");
		 addToColumns(esc);*/
		addToColumns(new StringColumn<DMMethod>("parameters", 200) {
			@Override
			public String getValue(DMMethod method) {
				return method.getParameterListAsString(false);
			}

		});

		addToColumns(new EditableStringColumn<DMMethod>("description", 250) {
			@Override
			public String getValue(DMMethod method) {
				return method.getDescription();
			}

			@Override
			public void setValue(DMMethod method, String aValue) {
				method.setDescription(aValue);
			}
		});

	}

	public DMEntity getDMEntity() {
		return getModel();
	}

	@Override
	public DMMethod elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return getDMEntity().getOrderedMethods().elementAt(row);
		} else {
			return null;
		}
	}

	public DMMethod entityAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMEntity() != null) {
			return getDMEntity().getOrderedMethods().size();
		}
		return 0;
	}

}
