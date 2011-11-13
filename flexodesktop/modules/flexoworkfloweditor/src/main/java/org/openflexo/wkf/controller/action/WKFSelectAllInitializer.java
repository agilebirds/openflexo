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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.WorkflowModelObject;
import org.openflexo.foundation.wkf.action.WKFSelectAll;
import org.openflexo.foundation.wkf.node.FatherNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class WKFSelectAllInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	WKFSelectAllInitializer(WKFControllerActionInitializer actionInitializer) {
		super(WKFSelectAll.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<WKFSelectAll> getDefaultInitializer() {
		return new FlexoActionInitializer<WKFSelectAll>() {
			@Override
			public boolean run(ActionEvent e, WKFSelectAll action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<WKFSelectAll> getDefaultFinalizer() {
		return new FlexoActionFinalizer<WKFSelectAll>() {
			@Override
			public boolean run(ActionEvent e, WKFSelectAll action) {
				if (action.getFocusedObject() instanceof WKFObject) {

					WKFObject focused = (WKFObject) action.getFocusedObject();
					FlexoPetriGraph pg = null;
					if (focused instanceof FlexoProcess) {
						pg = ((FlexoProcess) focused).getActivityPetriGraph();
					} else if (focused instanceof FatherNode) {
						pg = ((FatherNode) focused).getContainedPetriGraph();
					} else if (focused instanceof FlexoPetriGraph) {
						pg = (FlexoPetriGraph) focused;
					}
					if (pg != null) {
						Vector<PetriGraphNode> newSelection = pg.getNodes();
						getControllerActionInitializer().getWKFSelectionManager().setSelectedObjects(newSelection);
						return true;
					}
					return false;
				}

				else if (action.getFocusedObject() instanceof WorkflowModelObject) {

					WorkflowModelObject focused = (WorkflowModelObject) action.getFocusedObject();
					RoleList rl = null;
					if (focused instanceof Role) {
						rl = ((Role) focused).getRoleList();
					} else if (focused instanceof RoleList) {
						rl = (RoleList) focused;
					}
					if (rl != null) {
						Vector<Role> newSelection = rl.getRoles();
						getControllerActionInitializer().getWKFSelectionManager().setSelectedObjects(newSelection);
						return true;
					}
					return false;
				}
				return false;
			}
		};
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_A, FlexoCst.META_MASK);
	}

}
