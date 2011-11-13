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
package org.openflexo.foundation.ie.cl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEPartialComponent;
import org.openflexo.foundation.ie.PartialComponentInstance;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.logging.FlexoLogger;

public abstract class PartialComponentDefinition extends ComponentDefinition {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(PartialComponentDefinition.class.getPackage().getName());

	public PartialComponentDefinition(FlexoComponentLibrary componentLibrary) {
		super(componentLibrary);
	}

	public PartialComponentDefinition(String aComponentName, FlexoComponentFolder aFolder, FlexoProject prj)
			throws DuplicateResourceException {
		super(aComponentName, aFolder.getComponentLibrary(), aFolder, prj);
	}

	public PartialComponentDefinition(FlexoComponentLibraryBuilder builder) {
		super(builder);
	}

	public PartialComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
			FlexoProject project) throws DuplicateResourceException {
		super(aComponentName, componentLibrary, aFolder, project);
	}

	@Override
	public IEPartialComponent getWOComponent() {
		return (IEPartialComponent) super.getWOComponent();
	}

	@Override
	public final boolean isPage() {
		return false;
	}

	@Override
	public List<OperationNode> getAllOperationNodesLinkedToThisComponent() {
		List<OperationNode> results = new ArrayList<OperationNode>();
		for (ComponentInstance instance : getComponentInstances()) {
			PartialComponentInstance inst = (PartialComponentInstance) instance;
			if (inst.getReusableWidget() == null) {
				// ActionNode and OperationNode also uses TabComponentInstance
				continue;
			}

			results.addAll(inst.getReusableWidget().getComponentDefinition().getAllOperationNodesLinkedToThisComponent());
		}

		return results;
	}
}
