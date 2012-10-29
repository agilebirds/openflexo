package org.openflexo.fge;

import org.openflexo.model.factory.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.xml.XMLDeserializer;
import org.openflexo.model.xml.XMLSerializer;

/**
 * This is the default PAMELA model factory for class involved in FlexoGraphicalEngine model management<br>
 * Please feel free to override this class
 * 
 * @author sylvain
 * 
 */
public class FGEModelFactory extends ModelFactory {

	private XMLSerializer serializer;
	private XMLDeserializer deserializer;

	public FGEModelFactory() throws ModelDefinitionException {
		importClass(GraphicalRepresentation.class);
		importClass(DrawingGraphicalRepresentation.class);
		importClass(ShapeGraphicalRepresentation.class);
		importClass(ConnectorGraphicalRepresentation.class);
		importClass(GeometricGraphicalRepresentation.class);

		deserializer = new XMLDeserializer(this);
		serializer = new XMLSerializer(getStringEncoder());
	}
}
