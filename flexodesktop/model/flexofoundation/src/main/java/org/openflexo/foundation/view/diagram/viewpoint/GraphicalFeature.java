package org.openflexo.foundation.view.diagram.viewpoint;

import java.util.logging.Logger;

import org.openflexo.fge.GRParameter;
import org.openflexo.fge.GraphicalRepresentation;

/**
 * This class represent a graphical feature that is to be associated on a DiagramElement
 * 
 * @author sylvain
 * 
 */
public abstract class GraphicalFeature<T, GR extends GraphicalRepresentation> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalFeature.class.getPackage().getName());

	private String name;
	private GRParameter<T> parameter;

	public GraphicalFeature(String name, GRParameter<T> parameter) {
		this.name = name;
		this.parameter = parameter;
	}

	public String getName() {
		return name;
	}

	public GRParameter<T> getParameter() {
		return parameter;
	}

	public Class<T> getType() {
		return parameter.getType();
	}

	public abstract void applyToGraphicalRepresentation(GR gr, T value);

	public abstract T retrieveFromGraphicalRepresentation(GR gr);

	@Override
	public String toString() {
		return "GraphicalFeature[" + getName() + "]";
	}
}
