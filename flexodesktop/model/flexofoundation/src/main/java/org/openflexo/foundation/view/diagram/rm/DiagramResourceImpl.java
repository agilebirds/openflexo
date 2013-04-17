package org.openflexo.foundation.view.diagram.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.rm.VirtualModelInstanceResourceImpl;
import org.openflexo.foundation.view.View;
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

	public static DiagramResource makeDiagramResource(String name, DiagramSpecification diagramSpecification, View view) {
		try {
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
			return returned;
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

				System.out.println("Found virtualModelURI=" + vmiInfo.virtualModelURI);
				System.out.println("Qui correspond a " + viewResource.getViewPoint().getVirtualModelNamed(vmiInfo.virtualModelURI));
				VirtualModel vm = viewResource.getViewPoint().getVirtualModelNamed(vmiInfo.virtualModelURI);
				if (vm != null) {
					returned.setVirtualModelResource(vm.getResource());
				}
			}
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

}
