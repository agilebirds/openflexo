package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.xmlcode.StringEncoder;

public abstract class ExampleDiagramResourceImpl extends FlexoXMLFileResourceImpl<ExampleDiagram> implements ExampleDiagramResource {

	private RelativePathFileConverter relativePathFileConverter;

	private StringEncoder encoder;

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	public static ExampleDiagramResource makeExampleDiagramResource(File exampleDiagramFile, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ExampleDiagramResource.class);
			ExampleDiagramResourceImpl returned = (ExampleDiagramResourceImpl) factory.newInstance(ExampleDiagramResource.class);
			returned.setName(exampleDiagramFile.getName());
			returned.setFile(exampleDiagramFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setServiceManager(viewPointLibrary.getFlexoServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(exampleDiagramFile.getParentFile());

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<ExampleDiagram> getResourceDataClass() {
		return ExampleDiagram.class;
	}

	@Override
	public final ViewPointBuilder instanciateNewBuilder() {
		return new ViewPointBuilder(getViewPointLibrary());
	}

	@Override
	public boolean hasBuilder() {
		return true;
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
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 */
	@Override
	public ExampleDiagram loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException,
			FileNotFoundException, ResourceDependencyLoopException {

		ExampleDiagram returned = super.loadResourceData(progress);
		returned.init(((ViewPointResource) getContainer()).getViewPoint(), getFile());
		((ViewPointResource) getContainer()).getViewPoint().addToExampleDiagrams(returned);
		return returned;
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

}