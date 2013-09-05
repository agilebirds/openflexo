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
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteObject.DiagramPaletteBuilder;
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

	public static DiagramPaletteResource makeDiagramPaletteResource(DiagramSpecificationResource dsResource, String diagramPaletteName,
			ViewPointLibrary viewPointLibrary) {
		try {
			File diagramPaletteFile = new File(dsResource.getDirectory(), diagramPaletteName + ".palette");
			ModelFactory factory = new ModelFactory(DiagramPaletteResource.class);
			DiagramPaletteResourceImpl returned = (DiagramPaletteResourceImpl) factory.newInstance(DiagramPaletteResource.class);
			returned.setName(diagramPaletteFile.getName());
			returned.setFile(diagramPaletteFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setURI(dsResource.getURI() + "/" + diagramPaletteFile.getName());
			returned.setServiceManager(viewPointLibrary.getServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(diagramPaletteFile.getParentFile());
			dsResource.addToContents(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DiagramPaletteResource retrieveDiagramPaletteResource(DiagramSpecificationResource dsResource, File diagramPaletteFile,
			ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(DiagramPaletteResource.class);
			DiagramPaletteResourceImpl returned = (DiagramPaletteResourceImpl) factory.newInstance(DiagramPaletteResource.class);
			returned.setName(diagramPaletteFile.getName());
			returned.setFile(diagramPaletteFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setURI(dsResource.getURI() + "/" + diagramPaletteFile.getName());
			returned.setServiceManager(viewPointLibrary.getServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(diagramPaletteFile.getParentFile());
			dsResource.addToContents(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public final DiagramPaletteBuilder instanciateNewBuilder() {
		// TODO: use a dedicated builder for ExampleDiagram instead of VirtualModelBuilder
		return new DiagramPaletteBuilder(getContainer().getDiagramSpecification(), this);
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	@Override
	public Class<DiagramPalette> getResourceDataClass() {
		return DiagramPalette.class;
	}

	/**
	 * Return diagram palette stored by this resource<br>
	 * Load the resource data when unloaded
	 */
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
	 * Return diagram palette stored by this resource<br>
	 * Do not force load the resource data
	 * 
	 * @return
	 */
	@Override
	public DiagramPalette getLoadedDiagramPalette() {
		if (isLoaded()) {
			return getDiagramPalette();
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
		returned.init(getContainer().getDiagramSpecification(), getFile().getName().substring(0, getFile().getName().length() - 8));
		getContainer().getDiagramSpecification().addToPalettes(returned);
		setChanged();
		notifyObservers(new DataModification("diagramPalette", null, returned));
		setChanged();
		notifyObservers(new DataModification("loadedDiagramPalette", null, returned));
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

}
