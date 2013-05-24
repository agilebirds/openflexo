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

import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.xml.FlexoProcessBuilder;

/**
 * Activity related of a set of sub-processes executed in paralell or sequential
 * 
 * @author sguerin
 * 
 */
public class MultipleInstanceSubProcessNode extends SubProcessNode {

	public static final String IS_SEQUENTIAL = "isSequential";

	private static final Logger logger = Logger.getLogger(MultipleInstanceSubProcessNode.class.getPackage().getName());

	// isSequential == false -> FORK, isSequential == true -> LOOP
	protected boolean isSequential = false;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public MultipleInstanceSubProcessNode(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public MultipleInstanceSubProcessNode(FlexoProcess process) {
		super(process);
	}

	/**
	 * Dynamic constructor with ServiceInterface...
	 */
	public MultipleInstanceSubProcessNode(FlexoProcess process, ServiceInterface _interface) {
		this(process);
		setServiceInterface(_interface);
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

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "parallel_sub_process";
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.MULTIPLE_INSTANCE_SUB_PROCESS_NODE_INSPECTOR;
	}

	public boolean getIsSequential() {
		return isSequential;
	}

	public void setIsSequential(boolean isSequential) {
		this.isSequential = isSequential;
		setChanged();
		notifyAttributeModification(IS_SEQUENTIAL, !isSequential, isSequential);
	}

	@Override
	public boolean mightHaveOperationPetriGraph() {
		return true;
	}

}
