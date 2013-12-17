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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.ActivityGroup;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;

public class MakeActivityGroup extends FlexoAction<MakeActivityGroup, WKFObject, WKFObject> {

	private static final Logger logger = Logger.getLogger(MakeActivityGroup.class.getPackage().getName());

	public static FlexoActionType<MakeActivityGroup, WKFObject, WKFObject> actionType = new FlexoActionType<MakeActivityGroup, WKFObject, WKFObject>(
			"group_activities", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public MakeActivityGroup makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new MakeActivityGroup(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return false/* ((object != null)
						&& object instanceof AbstractNode
						&& ((AbstractNode)object).getLevel() == FlexoLevel.ACTIVITY)*/;
		}

		@Override
		public boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return false /*getAllActivityLevelNodes(object, globalSelection).size() > 0*/;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(MakeActivityGroup.actionType, AbstractNode.class);
	}

	private String newGroupName;
	private ActivityGroup newActivityGroup;

	MakeActivityGroup(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		Vector<PetriGraphNode> nodesToGroup = getAllActivityLevelNodes(getFocusedObject(), getGlobalSelection());

		/*System.out.println("Make activity group");
		System.out.println("focused = "+getFocusedObject());
		System.out.println("global selection = "+getGlobalSelection());
		System.out.println("nodesToGroup="+nodesToGroup);*/

		if (nodesToGroup.size() == 0) {
			logger.warning("Sorry, no nodes to group");
		} else {
			if (nodesToGroup.firstElement().getParentPetriGraph() instanceof ActivityPetriGraph) {
				FlexoPetriGraph pg = nodesToGroup.firstElement().getParentPetriGraph();
				newActivityGroup = new ActivityGroup(getFocusedObject().getProcess(), nodesToGroup);
				// TODO: following must be put in finalizer of MakeActivityGroup action
				newActivityGroup.setX(nodesToGroup.firstElement().getX("bpe"), "collabsed_" + "bpe");
				newActivityGroup.setY(nodesToGroup.firstElement().getY("bpe"), "collabsed_" + "bpe");
				newActivityGroup.setGroupName(getNewGroupName());
				pg.addToGroups(newActivityGroup);
			}
		}
	}

	public String getNewGroupName() {
		return newGroupName;
	}

	public void setNewGroupName(String newGroupName) {
		this.newGroupName = newGroupName;
	}

	public ActivityGroup getNewActivityGroup() {
		return newActivityGroup;
	}

	protected static Vector<PetriGraphNode> getAllActivityLevelNodes(WKFObject focusedObject, Vector<WKFObject> globalSelection) {
		Vector<PetriGraphNode> returned = new Vector<PetriGraphNode>();
		Vector<WKFObject> allObjects = new Vector<WKFObject>(globalSelection != null ? globalSelection : new Vector<WKFObject>());
		if (!allObjects.contains(focusedObject)) {
			allObjects.add(focusedObject);
		}
		ActivityPetriGraph pg = null;
		for (WKFObject o : allObjects) {
			// GPO: Throttle here to enable WKFGroup on the root petri graph only
			pg = o.getProcess().getActivityPetriGraph();
			if (o instanceof PetriGraphNode && !((PetriGraphNode) o).isGrouped()
					&& ((PetriGraphNode) o).getParentPetriGraph() instanceof ActivityPetriGraph
					&& (pg == null || ((PetriGraphNode) o).getParentPetriGraph() == pg)) {
				returned.add((PetriGraphNode) o);
				if (pg == null) {
					pg = (ActivityPetriGraph) ((PetriGraphNode) o).getParentPetriGraph();
				}
			}
		}
		return returned;
	}
}
