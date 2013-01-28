package org.openflexo.foundation.view.diagram.rm;

import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.VirtualModelInstanceResourceImpl;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.viewpoint.DiagramSpecification;

/**
 * Default implementation for {@link DiagramResource}
 * 
 * 
 * @author Sylvain
 * 
 */
public abstract class DiagramResourceImpl extends VirtualModelInstanceResourceImpl<Diagram> implements DiagramResource {

	public static DiagramResource makeDiagramResource(String name, DiagramSpecification virtualModel, View view) {
		return (DiagramResource) VirtualModelInstanceResourceImpl.makeVirtualModelInstanceResource(name, virtualModel, view);
	}

	@Override
	public Diagram getDiagram() {
		try {
			return getResourceData(null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			e.printStackTrace();
		} catch (FlexoException e) {
			e.printStackTrace();
		}
		return null;
	}

}
