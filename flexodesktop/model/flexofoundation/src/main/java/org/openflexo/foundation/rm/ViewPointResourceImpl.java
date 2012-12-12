package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointInfo;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.StringUtils;

public abstract class ViewPointResourceImpl extends FlexoXMLFileResourceImpl<ViewPoint> implements ViewPointResource {

	public static ViewPointResource makeViewPointResource(File viewPointDirectory, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ViewPointResource.class);
			ViewPointResource returned = factory.newInstance(ViewPointResource.class);
			ViewPointInfo vpi = ViewPoint.findViewPointInfo(viewPointDirectory);
			if (vpi == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setFile(viewPointDirectory);
			returned.setURI(vpi.uri);
			returned.setName(vpi.name);
			if (StringUtils.isNotEmpty(vpi.version)) {
				returned.setVersion(new FlexoVersion(vpi.version));
			}
			returned.setOpenflexoVersion(new FlexoVersion(StringUtils.isNotEmpty(vpi.openflexoVersion) ? vpi.openflexoVersion : "1.4.5"));
			returned.setViewPointLibrary(viewPointLibrary);
			viewPointLibrary.registerViewPoint(returned);

			// Now look for example diagrams
			if (viewPointDirectory.exists() && viewPointDirectory.isDirectory()) {
				for (File f : viewPointDirectory.listFiles()) {
					if (f.getName().endsWith(".shema")) {
						ExampleDiagramResource exampleDiagramResource = ExampleDiagramResourceImpl.makeExampleDiagramResource(f,
								viewPointLibrary);
						returned.addToContents(exampleDiagramResource);
					}
				}
			}

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public final ViewPointBuilder instanciateNewBuilder() {
		return new ViewPointBuilder(getViewPointLibrary(), getViewPoint());
	}

	@Override
	public ViewPoint getViewPoint() {
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
	public ViewPoint loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException {
		ViewPoint returned;
		try {
			returned = ViewPoint.openViewPoint(getFile(), getViewPointLibrary(), getOpenflexoVersion());
			for (EditionPattern ep : returned.getEditionPatterns()) {
				ep.finalizeParentEditionPatternDeserialization();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexoException(e);
		}
		returned.loadWhenUnloaded();
		return returned;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 */
	@Override
	public void save(IProgress progress) {
		getViewPoint().save();
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
