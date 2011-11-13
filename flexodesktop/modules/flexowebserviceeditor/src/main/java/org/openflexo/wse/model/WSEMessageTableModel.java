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

import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;

import org.openflexo.components.tabular.model.AbstractModel;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.IconColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WSEMessageTableModel extends AbstractModel<ServiceOperation, ServiceMessageDefinition> {

	protected static final Logger logger = Logger.getLogger(WSEMessageTableModel.class.getPackage().getName());

	public WSEMessageTableModel(ServiceOperation model, FlexoProject project, boolean readOnly) {
		super(model, project);
		addToColumns(new IconColumn<ServiceMessageDefinition>("process_icon", 30) {
			@Override
			public Icon getIcon(ServiceMessageDefinition object) {
				if ((object).isInputMessageDefinition())
					return WSEIconLibrary.WS_IN_MESSAGE_LEFT_ICON;
				else if ((object).isOutputMessageDefinition())
					return WSEIconLibrary.WS_OUT_MESSAGE_LEFT_ICON;
				else if ((object).isFaultMessageDefinition())
					return WSEIconLibrary.WS_FAULT_MESSAGE__LEFT_ICON;
				return null;
			}

			@Override
			public String getLocalizedTooltip(ServiceMessageDefinition object) {
				if ((object).isInputMessageDefinition())
					return FlexoLocalization.localizedForKey("input_message_def");
				else if ((object).isOutputMessageDefinition())
					return FlexoLocalization.localizedForKey("output_message_def");
				else if ((object).isFaultMessageDefinition())
					return FlexoLocalization.localizedForKey("fault_message_def");
				return null;
			}
		});

		if (readOnly)
			addToColumns(new StringColumn<ServiceMessageDefinition>("name", 190) {
				@Override
				public String getValue(ServiceMessageDefinition object) {
					return (object).getName();
				}
			});
		else {
			addToColumns(new EditableStringColumn<ServiceMessageDefinition>("name", 190) {
				@Override
				public String getValue(ServiceMessageDefinition object) {
					return (object).getName();
				}

				@Override
				public void setValue(ServiceMessageDefinition object, String aValue) {
					(object).setName(aValue);
					selectObject(object);
				}
			});
		}
		/*  addToColumns(new EditableStringColumn("description", 365) {
		      public String getValue(FlexoModelObject object)
		      {
		          return ((MessageDefinition) object).getDescription();
		      }

		      public void setValue(FlexoModelObject object, String aValue)
		      {
		          ((MessageDefinition) object).setDescription(aValue);
		      }
		  });*/
		setRowHeight(20);
	}

	public ServiceOperation getServiceOperation() {
		return getModel();
	}

	public Vector getMessages() {
		Vector a = new Vector();
		if (getServiceOperation().isInOutOperation()) {
			a.add(getServiceOperation().getInputMessageDefinition());
			a.add(getServiceOperation().getOutputMessageDefinition());
		} else if (getServiceOperation().isInOperation()) {
			a.add(getServiceOperation().getInputMessageDefinition());
		} else if (getServiceOperation().isOutOperation()) {
			a.add(getServiceOperation().getOutputMessageDefinition());
		}
		return a;
	}

	@Override
	public ServiceMessageDefinition elementAt(int row) {
		if ((row >= 0) && (row < getRowCount())) {
			return (ServiceMessageDefinition) getMessages().get(row);
		} else {
			return null;
		}
	}

	public ServiceMessageDefinition messageAt(int row) {
		return elementAt(row);
	}

	@Override
	public int getRowCount() {
		if (getServiceOperation() != null) {
			return getMessages().size();
		}
		return 0;
	}

}
