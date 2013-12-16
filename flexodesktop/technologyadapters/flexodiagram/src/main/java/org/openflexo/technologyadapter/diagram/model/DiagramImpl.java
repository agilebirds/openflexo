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
package org.openflexo.technologyadapter.diagram.model;

import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.view.View;
import org.openflexo.technologyadapter.diagram.DiagramTechnologyAdapter;
import org.openflexo.technologyadapter.diagram.rm.DiagramResource;
import org.openflexo.technologyadapter.diagram.rm.DiagramResourceImpl;

/**
 * Default implementation for a diagram in Openflexo build-in diagram technology<br>
 * 
 * @author sylvain
 * 
 */
public abstract class DiagramImpl extends DiagramContainerElementImpl<DrawingGraphicalRepresentation> implements Diagram {

	private static final Logger logger = Logger.getLogger(DiagramImpl.class.getPackage().getName());

	public static DiagramResource newDiagramResource(String diagramName, String diagramTitle, DiagramSpecification diagramSpecification,
			View view, DiagramFactory factory) throws InvalidFileNameException, SaveResourceException {

		DiagramResource newDiagramResource = DiagramResourceImpl.makeDiagramResource(diagramName, diagramSpecification, view);

		Diagram newDiagram = newDiagramResource.getFactory().makeNewDiagram(diagramSpecification);
		newDiagramResource.setResourceData(newDiagram);
		newDiagram.setResource(newDiagramResource);

		newDiagram.setTitle(diagramTitle);

		newDiagramResource.save(null);

		return newDiagramResource;
	}

	@Override
	public DiagramSpecification getMetaModel() {
		return getDiagramSpecification();
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		return getResource().getServiceManager().getService(TechnologyAdapterService.class)
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}

}
