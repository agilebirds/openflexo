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
package org.openflexo.foundation.wkf;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class ActivityGroup extends WKFGroup implements InspectableObject {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ActivityGroup.class.getPackage().getName());

	// ==========================================================
	// ======================= Constructor ======================
	// ==========================================================

	/**
	 * Constructor used during deserialization
	 */
	public ActivityGroup(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ActivityGroup(FlexoProcess process) {
		super(process);
		setIsVisible(true);
	}

	/**
	 * Dynamic constructor
	 */
	public ActivityGroup(FlexoProcess process, Vector<PetriGraphNode> nodes) {
		this(process);
		for (PetriGraphNode n : nodes)
			addToNodes(n);
	}

	/**
	 * Return all activities for this group
	 * 
	 * @return a Vector of ActivityNode
	 */
	public Vector<ActivityNode> getAllActivityNodes() {
		// TODO: optimize me later !!!
		Vector<ActivityNode> returned = new Vector<ActivityNode>();
		Enumeration<PetriGraphNode> en = getNodes().elements();
		while (en.hasMoreElements()) {
			PetriGraphNode current = en.nextElement();
			if (current instanceof ActivityNode) {
				returned.add((ActivityNode) current);
			}
		}
		return returned;
	}

	@Override
	public String getClassNameKey() {
		return "activity_group";
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + ".GROUP." + getGroupName();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.ACTIVITY_GROUP_INSPECTOR;
	}

}
