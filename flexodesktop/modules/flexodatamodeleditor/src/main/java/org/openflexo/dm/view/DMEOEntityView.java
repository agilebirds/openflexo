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
package org.openflexo.dm.view;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.components.tabular.TabularViewAction;
import org.openflexo.dm.model.DMEOAttributeTableModel;
import org.openflexo.dm.model.DMEORelationshipTableModel;
import org.openflexo.dm.model.DMMethodTableModel;
import org.openflexo.dm.model.DMPropertyTableModel;
import org.openflexo.dm.model.DMReadOnlyEOAttributeTableModel;
import org.openflexo.dm.model.DMReadOnlyMethodTableModel;
import org.openflexo.dm.model.DMReadOnlyPropertyTableModel;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.action.CreateDMEOAttribute;
import org.openflexo.foundation.dm.action.CreateDMEORelationship;
import org.openflexo.foundation.dm.action.CreateDMMethod;
import org.openflexo.foundation.dm.action.CreateDMProperty;
import org.openflexo.foundation.dm.action.DMDelete;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManager;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class DMEOEntityView extends DMView<DMEOEntity> {

	private DMPropertyTableModel propertyTableModel;
	protected DMTabularView propertyTable;

	private DMEOAttributeTableModel eoAttributeTableModel;
	protected DMTabularView eoAttributeTable;

	private DMEORelationshipTableModel eoRelationshipTableModel;
	protected DMTabularView eoRelationshipTable;

	private DMMethodTableModel methodTableModel;
	protected DMTabularView methodTable;

	JTabbedPane tabbedPane;

	public DMEOEntityView(DMEOEntity entity, DMController controller) {
		super(entity, controller, "entity_($name)");

		addAction(new TabularViewAction(CreateDMProperty.actionType, controller.getEditor()) {
			@Override
			protected Vector<FlexoObject> getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				if (tabbedPane.getSelectedIndex() == 1) {
					return getDMEOEntity();
				}
				return null;
			}
		});

		addAction(new TabularViewAction(CreateDMMethod.actionType, controller.getEditor()) {
			@Override
			protected Vector<FlexoObject> getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				if (tabbedPane.getSelectedIndex() == 1) {
					return getDMEOEntity();
				}
				return null;
			}
		});

		addAction(new TabularViewAction(CreateDMEOAttribute.actionType, controller.getEditor()) {
			@Override
			protected Vector<FlexoObject> getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				if (tabbedPane.getSelectedIndex() == 0) {
					return getDMEOEntity();
				}
				return null;
			}
		});

		addAction(new TabularViewAction(CreateDMEORelationship.actionType, controller.getEditor()) {
			@Override
			protected Vector<FlexoObject> getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				if (tabbedPane.getSelectedIndex() == 0) {
					return getDMEOEntity();
				}
				return null;
			}
		});

		addAction(new TabularViewAction(DMDelete.actionType, controller.getEditor()) {
			@Override
			protected Vector<FlexoObject> getGlobalSelection() {
				return getViewSelection();
			}

			@Override
			protected FlexoModelObject getFocusedObject() {
				return null;
			}
		});

		finalizeBuilding();
	}

	@Override
	protected JComponent buildContentPane() {
		tabbedPane = new JTabbedPane();

		if (getDMEOEntity().getRepository() != null && getDMEOEntity().getRepository().isReadOnly()) {
			eoAttributeTableModel = new DMReadOnlyEOAttributeTableModel(getDMEOEntity(), getDMController());
		} else {
			eoAttributeTableModel = new DMEOAttributeTableModel(getDMEOEntity(), getDMController());
		}
		addToMasterTabularView(eoAttributeTable = new DMTabularView(getDMController(), eoAttributeTableModel, 12));

		eoRelationshipTableModel = new DMEORelationshipTableModel(getDMEOEntity(), getDMController());
		addToMasterTabularView(eoRelationshipTable = new DMTabularView(getDMController(), eoRelationshipTableModel, 12));

		tabbedPane.add(FlexoLocalization.localizedForKey("database_design"), new JSplitPane(JSplitPane.VERTICAL_SPLIT, eoAttributeTable,
				eoRelationshipTable));
		if (getDMEOEntity().getRepository().isReadOnly()) {
			propertyTableModel = new DMReadOnlyPropertyTableModel(getDMEOEntity(), getDMController().getProject());
		} else {
			propertyTableModel = new DMPropertyTableModel(getDMEOEntity(), getDMController().getProject());
		}
		addToMasterTabularView(propertyTable = new DMTabularView(getDMController(), propertyTableModel, 12));

		if (getDMEOEntity().getRepository().isReadOnly()) {
			methodTableModel = new DMReadOnlyMethodTableModel(getDMEOEntity(), getDMController().getProject());
		} else {
			methodTableModel = new DMMethodTableModel(getDMEOEntity(), getDMController().getProject());
		}
		addToMasterTabularView(methodTable = new DMTabularView(getDMController(), methodTableModel, 12));

		tabbedPane.add(FlexoLocalization.localizedForKey("common_design"), new JSplitPane(JSplitPane.VERTICAL_SPLIT, propertyTable,
				methodTable));

		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateControls();
			}
		});

		return tabbedPane;
	}

	public DMEOEntity getDMEOEntity() {
		return getDMObject();
	}

	public DMProperty getSelectedDMProperty() {
		SelectionManager sm = getDMController().getSelectionManager();
		Vector<FlexoObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMProperty) {
			return (DMProperty) selection.firstElement();
		}
		return null;
	}

	public DMMethod getSelectedDMMethod() {
		SelectionManager sm = getDMController().getSelectionManager();
		Vector<FlexoObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMMethod) {
			return (DMMethod) selection.firstElement();
		}
		return null;
	}

	public DMEOAttribute getSelectedDMEOAttribute() {
		SelectionManager sm = getDMController().getSelectionManager();
		Vector<FlexoObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMEOAttribute) {
			return (DMEOAttribute) selection.firstElement();
		}
		return null;
	}

	public DMEORelationship getSelectedDMEORelationship() {
		SelectionManager sm = getDMController().getSelectionManager();
		Vector<FlexoObject> selection = sm.getSelection();
		if (selection.size() == 1 && selection.firstElement() instanceof DMEORelationship) {
			return (DMEORelationship) selection.firstElement();
		}
		return null;
	}

	public DMTabularView getEoAttributeTable() {
		return eoAttributeTable;
	}

	public DMTabularView getEoRelationshipTable() {
		return eoRelationshipTable;
	}

	public DMTabularView getMethodTable() {
		return methodTable;
	}

	public DMTabularView getPropertyTable() {
		return propertyTable;
	}

	/**
	 * Overrides willShow
	 * 
	 * @see org.openflexo.view.ModuleView#willShow()
	 */
	@Override
	public void willShow() {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides willHide
	 * 
	 * @see org.openflexo.view.ModuleView#willHide()
	 */
	@Override
	public void willHide() {
		// TODO Auto-generated method stub

	}

}
