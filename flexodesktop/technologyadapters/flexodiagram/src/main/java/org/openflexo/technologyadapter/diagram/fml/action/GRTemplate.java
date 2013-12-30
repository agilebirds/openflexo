package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.List;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;
import org.openflexo.technologyadapter.diagram.metamodel.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.Diagram;

/**
 * This interface is implemented by all objects which may be used to "prototype" an {@link EditionPattern} relatively to a
 * {@link GraphicalRepresentation}
 * 
 * @author Vincent
 * 
 */
public interface GRTemplate {

	/**
	 * Return the model slot which encodes the access to a {@link Diagram} conform to a {@link DiagramSpecification}, in the context of a
	 * {@link VirtualModel} (which is a context where a diagram is federated with other sources of informations)
	 * 
	 * @return
	 */
	public TypedDiagramModelSlot getTypedDiagramModelSlot();

	/**
	 * Return the graphical representation which should be used as prototype
	 * 
	 * @return
	 */
	public GraphicalRepresentation getGraphicalRepresentation();

	/**
	 * Return the descendants of this {@link GRTemplate}, which might be used in {@link EditionPattern} prototyping
	 * 
	 * @return
	 */
	public List<? extends GRTemplate> getDescendants();

	/**
	 * Return parent of this {@link GRTemplate}
	 * 
	 * @return
	 */
	public GRTemplate getParent();

	/**
	 * Return name of this GRTemplate
	 * 
	 * @return
	 */
	public String getName();

}
