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
package org.openflexo.foundation.wkf.node;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.help.ApplicationHelpEntryPoint;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * This represents a FlexoNode at the level 'Activity'
 * 
 * @author sguerin
 * 
 */
public class ActivityNode extends AbstractActivityNode implements ApplicationHelpEntryPoint {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ActivityNode.class.getPackage().getName());

	private TaskType taskType;

	/**
	 * Constructor used during deserialization
	 */
	public ActivityNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ActivityNode(FlexoProcess process) {
		super(process);
	}

	/**
	 * Overrides delete
	 * 
	 * @see org.openflexo.foundation.wkf.node.AbstractActivityNode#delete()
	 */
	@Override
	public final void delete() {
		super.delete();
		deleteObservers();
	}

	@Override
	public String getInspectorName() {
		if (getNodeType() == NodeType.NORMAL) {
			return Inspectors.WKF.ACTIVITY_NODE_INSPECTOR;
		} else if (getNodeType() == NodeType.BEGIN) {
			return Inspectors.WKF.BEGIN_ACTIVITY_NODE_INSPECTOR;
		} else if (getNodeType() == NodeType.END) {
			return Inspectors.WKF.END_ACTIVITY_NODE_INSPECTOR;
		} else {
			return super.getInspectorName();
		}
	}

	// Used for old models, deprecated now !
	@Override
	public void finalizeRoleLinking() {
		super.finalizeRoleLinking();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "activity_node";
	}

	@Override
	public ApplicationHelpEntryPoint getParentHelpEntry() {
		return getProcess();
	}

	@Override
	public List<ApplicationHelpEntryPoint> getChildsHelpObjects() {
		Vector<ApplicationHelpEntryPoint> reply = new Vector<ApplicationHelpEntryPoint>();
		reply.addAll(getAllOperationNodes());
		return reply;
	}

	@Override
	public String getShortHelpLabel() {
		return getName();
	}

	@Override
	public String getTypedHelpLabel() {
		return "Activity : " + getName();
	}

	@Override
	public boolean mightHaveOperationPetriGraph() {
		// Implement here some limitations if you want !
		return true;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
		setChanged();
		notifyAttributeModification("taskType", null, taskType);
	}

}
