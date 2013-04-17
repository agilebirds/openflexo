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
package org.openflexo.foundation.view.diagram.model;

import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterService;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.DiagramTechnologyAdapter;
import org.openflexo.foundation.view.diagram.rm.DiagramResource;
import org.openflexo.foundation.view.diagram.rm.DiagramResourceImpl;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramEditionScheme;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;

/**
 * Represents a diagram in Openflexo build-in diagram technology<br>
 * 
 * As such, a {@link Diagram} is a subclass of {@link VirtualModelInstance} (a diagram is a model conform to {@link DiagramMetaModel})
 * 
 * @author sylvain
 * 
 */
public class Diagram extends VirtualModelInstance<Diagram, DiagramSpecification> {

	private static final Logger logger = Logger.getLogger(Diagram.class.getPackage().getName());

	private DiagramRootPane rootPane;

	public static DiagramResource newDiagramResource(String diagramName, String diagramTitle, DiagramSpecification diagramSpecification,
			View view) throws InvalidFileNameException, SaveResourceException {

		DiagramResource newDiagramResource = DiagramResourceImpl.makeDiagramResource(diagramName, diagramSpecification, view);

		Diagram newDiagram = new Diagram(view, diagramSpecification);
		newDiagramResource.setResourceData(newDiagram);
		newDiagram.setResource(newDiagramResource);

		newDiagram.setTitle(diagramTitle);

		newDiagram.save();

		return newDiagramResource;
	}

	/**
	 * Constructor invoked during deserialization
	 * 
	 * @param componentDefinition
	 */
	public Diagram(VirtualModelInstanceBuilder builder) {
		super(builder);
	}

	/**
	 * Default constructor for OEShema
	 * 
	 * @param shemaDefinition
	 */
	public Diagram(View view, DiagramSpecification diagramSpecification) {
		super(view, diagramSpecification);
	}

	@Override
	public DiagramResource getResource() {
		return (DiagramResource) super.getResource();
	}

	public DiagramRootPane getRootPane() {
		if (rootPane == null) {
			rootPane = new DiagramRootPane(this);
		}
		return rootPane;
	}

	public void setRootPane(DiagramRootPane rootPane) {
		this.rootPane = rootPane;
	}

	@Override
	public DiagramSpecification getMetaModel() {
		return getVirtualModel();
	}

	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		return getProject().getServiceManager().getService(TechnologyAdapterService.class)
				.getTechnologyAdapter(DiagramTechnologyAdapter.class);
	}

	@Override
	public String getClassNameKey() {
		return "diagram";
	}

	public DiagramSpecification getDiagramSpecification() {
		return getVirtualModel();
	}

	/**
	 * @return
	 */
	public static final String getTypeName() {
		return "DIAGRAM";
	}

	/**
	 * Return run-time value for {@link BindingVariable} variable
	 * 
	 * @param variable
	 * @return
	 */
	@Override
	public Object getValueForVariable(BindingVariable variable) {
		if (variable.getVariableName().equals(DiagramEditionScheme.TOP_LEVEL)) {
			return getRootPane();
		}
		return super.getValue(variable);
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		// TODO Auto-generated method stub
		super.finalizeDeserialization(builder);
	}
}
