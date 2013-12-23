package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.List;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;

/**
 * Access to the graphical repesentation as well as a diagram specification
 * 
 * @author Vincent
 * 
 */
public interface GRTemplate {

	/**
	 * Return the graphical representation
	 * 
	 * @return
	 */
	public GraphicalRepresentation getGraphicalRepresentation();

	public DiagramSpecification getDiagramSpecification();

	public List<? extends GRTemplate> getDescendants();

	public GRTemplate getParent();

	public String getName();

}
