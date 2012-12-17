package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.xmlcode.StringEncoder;

public abstract class DiagramPaletteResourceImpl extends FlexoXMLFileResourceImpl<DiagramPalette> implements DiagramPaletteResource {

	private RelativePathFileConverter relativePathFileConverter;

	private StringEncoder encoder;

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	public static DiagramPaletteResource makeDiagramPaletteResource(File diagramPaletteFile, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ViewPointResource.class);
			DiagramPaletteResourceImpl returned = (DiagramPaletteResourceImpl) factory.newInstance(DiagramPaletteResource.class);
			returned.setName(diagramPaletteFile.getName());
			returned.setFile(diagramPaletteFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setServiceManager(viewPointLibrary.getFlexoServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(diagramPaletteFile.getParentFile());
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
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Class<DiagramPalette> getResourceDataClass() {
		return DiagramPalette.class;
	}

	@Override
	public DiagramPalette getDiagramPalette() {
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
	public DiagramPalette loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException,
			FileNotFoundException, ResourceDependencyLoopException {

		DiagramPalette returned = super.loadResourceData(progress);
		returned.init(getContainer().getViewPoint(), getFile());
		getContainer().getViewPoint().addToPalettes(returned);
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
