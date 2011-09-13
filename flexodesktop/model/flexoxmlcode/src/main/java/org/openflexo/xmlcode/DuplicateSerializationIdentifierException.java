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
package org.openflexo.xmlcode;

public class DuplicateSerializationIdentifierException extends Exception {

	private Object serializationIdentifier;
	private Object serializedObject;
	private Object duplicate;
	private ModelEntity modelEntity;
	private String xmlTag;
	
	public DuplicateSerializationIdentifierException(Object serializationIdentifier, Object serializedObject, Object duplicate, ModelEntity modelEntity, String xmlTag) {
		this.serializationIdentifier = serializationIdentifier;
		this.serializedObject = serializedObject;
		this.duplicate = duplicate;
		this.modelEntity = modelEntity;
		this.xmlTag = xmlTag;
	}

	public Object getSerializationIdentifier() {
		return serializationIdentifier;
	}

	public Object getSerializedObject() {
		return serializedObject;
	}

	public Object getDuplicate() {
		return duplicate;
	}
	
	@Override
	public String getMessage() {
		return "Found the same identifier '" + serializationIdentifier + "' for different objects: " + serializedObject.getClass() + " has the same serialization identifier than "
				+ duplicate.getClass() + "\n"
				+
				"I was serializing '"+xmlTag+"' on entity "+modelEntity.name;
	}

}
