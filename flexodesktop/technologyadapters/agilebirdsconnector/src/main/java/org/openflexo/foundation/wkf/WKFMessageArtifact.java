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

import org.openflexo.foundation.DeletableObject;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;

public class WKFMessageArtifact extends WKFArtefact implements InspectableObject, DeletableObject, LevelledObject {

	public static final String INITIATING = "initiating";
	private boolean initiating = true;

	/**
	 * Constructor used during deserialization
	 */
	public WKFMessageArtifact(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public WKFMessageArtifact(FlexoProcess process) {
		super(process);
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public String getClassNameKey() {
		return "message_artifact";
	}

	@Override
	public String getFullyQualifiedName() {
		return "MESSAGE." + getText();
	}

	public boolean isInitiating() {
		return initiating;
	}

	public void setInitiating(boolean initiating) {
		this.initiating = initiating;
		setChanged();
		notifyAttributeModification(INITIATING, !initiating, initiating);
	}
}
