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
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.inspector.InspectableObject;

public class WKFDataObject extends WKFArtefact implements InspectableObject, DeletableObject, LevelledObject {

	public static final String IS_COLLECTION = "isCollection";
	public static final String TYPE = "type";
	private boolean isCollection = false;
	private DataObjectType type;

	public enum DataObjectType {
		INPUT, OUTPUT;
	}

	/**
	 * Constructor used during deserialization
	 */
	public WKFDataObject(FlexoProcessBuilder builder) {
		this(builder.process);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public WKFDataObject(FlexoProcess process) {
		super(process);
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setIsCollection(boolean collection) {
		this.isCollection = collection;
		setChanged();
		notifyAttributeModification(IS_COLLECTION, !collection, collection);
	}

	public DataObjectType getType() {
		return type;
	}

	public void setType(DataObjectType type) {
		this.type = type;
		setChanged();
		notifyAttributeModification(TYPE, null, type);
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.DATA_OBJECT_INSPECTOR;
	}

	@Override
	public String getClassNameKey() {
		return "data_object";
	}

	@Override
	public String getFullyQualifiedName() {
		return "DATA_OBJECT." + getText();
	}

}
