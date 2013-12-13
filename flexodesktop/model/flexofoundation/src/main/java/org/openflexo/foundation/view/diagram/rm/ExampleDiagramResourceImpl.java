package org.openflexo.foundation.view.diagram.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.DiagramSpecificationResource;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject.ExampleDiagramBuilder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.AccessibleProxyObject;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.xmlcode.StringEncoder;

public abstract class ExampleDiagramResourceImpl extends FlexoXMLFileResourceImpl<ExampleDiagram> implements ExampleDiagramResource,
		AccessibleProxyObject {

	private RelativePathFileConverter relativePathFileConverter;
	private StringEncoder encoder;

	public static ExampleDiagramResource makeExampleDiagramResource(DiagramSpecificationResource dsResource, String exampleDiagramName,
			ViewPointLibrary viewPointLibrary) {
		try {
			File exampleDiagramFile = new File(dsResource.getDirectory(), exampleDiagramName + ".diagram");
			ModelFactory factory = new ModelFactory(ExampleDiagramResource.class);
			ExampleDiagramResourceImpl returned = (ExampleDiagramResourceImpl) factory.newInstance(ExampleDiagramResource.class);
			returned.setName(exampleDiagramFile.getName());
			returned.setFile(exampleDiagramFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setServiceManager(viewPointLibrary.getServiceManager());
			returned.setFactory(returned.getServiceManager().getXMLSerializationService().getExampleDiagramFactory());
			returned.relativePathFileConverter = new RelativePathFileConverter(exampleDiagramFile.getParentFile());
			dsResource.addToContents(returned);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ExampleDiagramResource retrieveExampleDiagramResource(DiagramSpecificationResource dsResource, File exampleDiagramFile,
			ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ExampleDiagramResource.class);
			ExampleDiagramResourceImpl returned = (ExampleDiagramResourceImpl) factory.newInstance(ExampleDiagramResource.class);
			returned.setName(exampleDiagramFile.getName());
			returned.setFile(exampleDiagramFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setURI(dsResource.getURI() + "/" + exampleDiagramFile.getName());
			returned.setServiceManager(viewPointLibrary.getServiceManager());
			returned.setFactory(returned.getServiceManager().getXMLSerializationService().getExampleDiagramFactory());
			returned.relativePathFileConverter = new RelativePathFileConverter(exampleDiagramFile.getParentFile());
			dsResource.addToContents(returned);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	@Override
	public Class<ExampleDiagram> getResourceDataClass() {
		return ExampleDiagram.class;
	}

	@Override
	public final ExampleDiagramBuilder instanciateNewBuilder() {
		return new ExampleDiagramBuilder(getContainer().getDiagramSpecification(), this, getServiceManager().getXMLSerializationService()
				.getExampleDiagramFactory());
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Return example diagram stored by this resource<br>
	 * Do not force load the resource data
	 * 
	 * @return
	 */
	@Override
	public ExampleDiagram getLoadedExampleDiagram() {
		if (isLoaded()) {
			return getExampleDiagram();
		}
		return null;
	}

	/**
	 * Return example diagram stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	@Override
	public ExampleDiagram getExampleDiagram() {
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

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 */
	@Override
	public ExampleDiagram loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException,
			FileNotFoundException, ResourceDependencyLoopException {

		ExampleDiagram returned = super.loadResourceData(progress);
		returned.init(getContainer().getDiagramSpecification(), getFile().getName().substring(0, getFile().getName().length() - 8));
		getContainer().getDiagramSpecification().addToExampleDiagrams(returned);
		setChanged();
		notifyObservers(new DataModification("exampleDiagram", null, returned));
		setChanged();
		notifyObservers(new DataModification("loadedExampleDiagram", null, returned));
		returned.clearIsModified();
		return returned;
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

	@Override
	public DiagramSpecificationResource getContainer() {
		return (DiagramSpecificationResource) performSuperGetter(CONTAINER);
	}

}
