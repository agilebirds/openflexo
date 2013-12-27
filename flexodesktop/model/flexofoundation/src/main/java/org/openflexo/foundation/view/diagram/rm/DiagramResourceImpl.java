/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.foundation.view.diagram.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.rm.VirtualModelInstanceResourceImpl;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.diagram.DiagramTechnologyAdapter;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramSpecification;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.StringUtils;

/**
 * Default implementation for {@link DiagramResource}
 * 
 * 
 * @author Sylvain
 * 
 */
public abstract class DiagramResourceImpl extends VirtualModelInstanceResourceImpl<Diagram> implements DiagramResource {

	private static final Logger logger = Logger.getLogger(DiagramResourceImpl.class.getPackage().getName());

	public static DiagramResource makeDiagramResource(String name, DiagramSpecification diagramSpecification, View view, String title) {
		try {
			// NPE protection => cannot create diagram without a DiagramSpecification
			if (diagramSpecification != null && view != null) {
				ModelFactory factory = new ModelFactory(DiagramResource.class);
				DiagramResourceImpl returned = (DiagramResourceImpl) factory.newInstance(DiagramResource.class);
				String baseName = name;
				File xmlFile = new File(view.getResource().getFile().getParentFile(), baseName + DiagramResource.DIAGRAM_SUFFIX);
				returned.setProject(view.getProject());
				returned.setName(name);
				returned.setFile(xmlFile);
				returned.setURI(view.getURI() + "/" + baseName);
				returned.setServiceManager(view.getProject().getServiceManager());
				returned.setVirtualModelResource(diagramSpecification.getResource());
				view.getResource().addToContents(returned);
				returned.setTitle(title);
				return returned;
			} else {
				logger.severe("Diagram Creation Failed due to a lack of information: either view or diagramspecification is null");
				return null;
			}
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DiagramResource retrieveDiagramResource(File diagramFile, ViewResource viewResource) {
		try {
			ModelFactory factory = new ModelFactory(DiagramResource.class);
			DiagramResourceImpl returned = (DiagramResourceImpl) factory.newInstance(DiagramResource.class);
			String baseName = diagramFile.getName().substring(0, diagramFile.getName().length() - DiagramResource.DIAGRAM_SUFFIX.length());
			File xmlFile = new File(viewResource.getFile().getParentFile(), baseName + DiagramResource.DIAGRAM_SUFFIX);
			returned.setProject(viewResource.getProject());
			returned.setName(baseName);
			returned.setFile(xmlFile);
			returned.setURI(viewResource.getURI() + "/" + baseName);
			returned.setServiceManager(viewResource.getProject().getServiceManager());
			VirtualModelInstanceInfo vmiInfo = findVirtualModelInstanceInfo(xmlFile, "Diagram");
			if (vmiInfo == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			if (viewResource.getViewPoint() != null && StringUtils.isNotEmpty(vmiInfo.virtualModelURI)) {

				VirtualModel vm = viewResource.getViewPoint().getVirtualModelNamed(vmiInfo.virtualModelURI);
				if (vm != null) {
					returned.setVirtualModelResource(vm.getResource());
				}
			}
			returned.setTitle(vmiInfo.title);
			viewResource.addToContents(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
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

	@Override
	public Class<Diagram> getResourceDataClass() {
		return Diagram.class;
	}

	@Override
	public Diagram loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FileNotFoundException, FlexoException {
		Diagram returned = super.loadResourceData(progress);
		if (returned.isSynchronizable()) {
			returned.synchronize(null);
		}
		getContainer().getView().addToVirtualModelInstances(returned);
		returned.clearIsModified();
		return returned;
	}

	@Override
	public TechnologyAdapter getTechnologyAdapter() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(DiagramTechnologyAdapter.class);
		}
		return null;
	}

}
