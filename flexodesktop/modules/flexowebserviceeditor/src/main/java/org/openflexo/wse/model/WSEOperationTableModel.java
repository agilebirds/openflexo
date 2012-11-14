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

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.DuplicateWKFObjectException;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

/**
 * Please comment this class
 * 
 * @author dvanvyve
 * 
 */
public class WSEOperationTableModel extends AbstractModel<ServiceInterface, ServiceOperation> {

	protected static final Logger logger = Logger.getLogger(WSEOperationTableModel.class.getPackage().getName());

	public WSEOperationTableModel(ServiceInterface model, FlexoProject project, boolean readOnly) {
		super(model, project);
		addToColumns(new IconColumn<ServiceOperation>("process_icon", 30) {
			@Override
			public Icon getIcon(ServiceOperation object) {
				return WKFIconLibrary.getSmallImageIconForServiceOperation(object);
			}

			@Override
			public String getLocalizedTooltip(ServiceOperation object) {
				return object.getLocalizedLabel();
			}
		});
		if (readOnly) {
			addToColumns(new StringColumn<ServiceOperation>("name", 190) {
				@Override
				public String getValue(ServiceOperation object) {
					return object.getName();
				}
			});
			addToColumns(new StringColumn<ServiceOperation>("description", 365) {
				@Override
				public String getValue(ServiceOperation object) {
					return object.getDescription();
				}

				/*          public void setValue(FlexoModelObject object, String aValue)
				          {
				              ((FlexoPort) object).setDescription(aValue);
				          }*/
			});
		} else {
			addToColumns(new EditableStringColumn<ServiceOperation>("name", 190) {
				@Override
				public String getValue(ServiceOperation object) {
					return object.getName();
				}

				@Override
				public void setValue(ServiceOperation object, String aValue) {
					try {
						object.setName(aValue);
					} catch (DuplicateWKFObjectException e) {
						FlexoController.notify(FlexoLocalization.localizedForKey(e.getLocalizationKey()));
					}
					selectObject(object);
				}
			});
			addToColumns(new EditableStringColumn<ServiceOperation>("description", 365) {
				@Override
				public String getValue(ServiceOperation object) {
					return object.getDescription();
				}

				@Override
				public void setValue(ServiceOperation object, String aValue) {
					object.setDescription(aValue);
				}
			});
		}
		setRowHeight(20);
	}

	public ServiceInterface getServiceInterface() {
		return getModel();
	}

	public Vector<ServiceOperation> getOperations() {
		return getServiceInterface().getOperations();
	}

	@Override
	public ServiceOperation elementAt(int row) {
		if (row >= 0 && row < getRowCount()) {
			return getOperations().get(row);
		} else {
			return null;
		}
	}

	public ServiceOperation processAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getServiceInterface() != null) {
			return getOperations().size();
		}
		return 0;
	}

}
