package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;

public abstract class ExampleDiagramResourceImpl extends FlexoXMLFileResourceImpl<ExampleDiagram> implements ExampleDiagramResource {

	public static ExampleDiagramResource makeExampleDiagramResource(File exampleDiagramFile, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ExampleDiagramResource.class);
			ExampleDiagramResource returned = factory.newInstance(ExampleDiagramResource.class);
			returned.setName(exampleDiagramFile.getName());
			returned.setViewPointLibrary(viewPointLibrary);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public final ViewPointBuilder instanciateNewBuilder() {
		return new ViewPointBuilder(getViewPointLibrary());
	}

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
	 */
	@Override
	public ExampleDiagram loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException {
		ExampleDiagram returned;
		try {
			returned = ExampleDiagram.instanciateExampleDiagram(getContainer().getViewPoint(), getFile());
			getContainer().getViewPoint().addToExampleDiagrams(returned);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexoException(e);
		}
		return returned;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 */
	@Override
	public void save(IProgress progress) {
		getExampleDiagram().save();
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getURI() + "]";
	}
}
