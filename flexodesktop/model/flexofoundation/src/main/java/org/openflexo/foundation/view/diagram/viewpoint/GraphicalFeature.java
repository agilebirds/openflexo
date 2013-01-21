package org.openflexo.foundation.view.diagram.viewpoint;

import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.GRParameter;

/**
 * This class represent a graphical feature that is to be associated on a ViewElement
 * 
 * @author sylvain
 * 
 */
public abstract class GraphicalFeature<T, GR extends GraphicalRepresentation> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GraphicalFeature.class.getPackage().getName());

	private String name;
	private Class<T> type;
	private GRParameter parameter;

	public GraphicalFeature(String name, GRParameter parameter, Class<T> type) {
		this.name = name;
		this.parameter = parameter;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public GRParameter getParameter() {
		return parameter;
	}

	public Class<T> getType() {
		return type;
	}

	public abstract void applyToGraphicalRepresentation(GR gr, T value);

	public abstract T retrieveFromGraphicalRepresentation(GR gr);

	@Override
	public String toString() {
		return "GraphicalFeature[" + getName() + "]";
	}
}
