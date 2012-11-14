package org.openflexo.model.exceptions;

public class PropertyClashException extends ModelDefinitionException {

	public PropertyClashException(String propertyIdentifier) {
		super("Property '" + propertyIdentifier + "' is clashing");
		// TODO Auto-generated constructor stub
	}

}
