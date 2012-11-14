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
package org.openflexo.wse.model;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEDMEntityTableModel extends AbstractModel<DMPackage, DMEntity> {

	protected static final Logger logger = Logger.getLogger(WSEDMEntityTableModel.class.getPackage().getName());

	public WSEDMEntityTableModel(DMPackage aPackage, FlexoProject project) {
		super(aPackage, project);
		addToColumns(new IconColumn<DMEntity>("entity_icon", 30) {
			@Override
			public Icon getIcon(DMEntity object) {
				return DMEIconLibrary.DM_ENTITY_ICON;
			}
		});
		addToColumns(new StringColumn<DMEntity>("name", 150) {
			@Override
			public String getValue(DMEntity object) {
				return object.getName();
			}

			/*  public void setValue(FlexoModelObject object, String aValue)
			  {
			      try {
			          ((DMEntity) object).setName(aValue);
			      } catch (InvalidNameException e) {
			          WSEController.notify(FlexoLocalization.localizedForKey("invalid_entity_name"));
			      }
			  }*/
		});
		addToColumns(new StringColumn<DMEntity>("class", 150) {
			@Override
			public String getValue(DMEntity object) {
				return object.getEntityClassName();
			}
			/*
			            public void setValue(FlexoModelObject object, String aValue)
			            {
			                try {
			                    ((DMEntity) object).setEntityClassName(aValue);
			                } catch (DuplicateClassNameException e) {
			                    FlexoController.notify(e.getLocalizedMessage());
			                } catch (InvalidNameException e) {
			                    WSEController.notify(FlexoLocalization.localizedForKey("invalid_entity_class_name"));
			                }
			                selectObject(object);
			            }*/
		});

		/*        addToColumns(new EntitySelectorColumn("parent", 150, project) {
		            public FlexoModelObject getValue(FlexoModelObject object)
		            {
		                return ((DMEntity) object).getParentEntity();
		            }

		            public void setValue(FlexoModelObject object, FlexoModelObject aValue)
		            {
		                ((DMEntity) object).setParentEntity((DMEntity) aValue);
		            }
		        });*/
		addToColumns(new StringColumn<DMEntity>("description", 250) {
			@Override
			public String getValue(DMEntity object) {
				return object.getDescription();
			}

			/*  public void setValue(FlexoModelObject object, String aValue)
			  {
			      ((DMEntity) object).setDescription(aValue);
			  }*/
		});
	}

	public DMPackage getDMPackage() {
		return getModel();
	}

	@Override
	public DMEntity elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return getDMPackage().getOrderedChildren().elementAt(row);
		} else {
			return null;
		}
	}

	public DMEntity entityAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getDMPackage() != null) {
			return getDMPackage().getOrderedChildren().size();
		}
		return 0;
	}

}
