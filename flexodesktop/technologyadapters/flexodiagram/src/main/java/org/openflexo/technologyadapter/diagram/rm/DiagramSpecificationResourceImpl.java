package org.openflexo.technologyadapter.diagram.rm;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.openflexo.foundation.resource.PamelaResourceImpl;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.diagram.model.DiagramSpecification;
import org.openflexo.technologyadapter.diagram.model.DiagramSpecificationFactory;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;

public abstract class DiagramSpecificationResourceImpl extends PamelaResourceImpl<DiagramSpecification, DiagramSpecificationFactory>
		implements DiagramSpecificationResource {

	static final Logger logger = Logger.getLogger(DiagramSpecificationResourceImpl.class.getPackage().getName());

	public static DiagramSpecificationResource makeDiagramSpecificationResource(File diagramSpecificationDirectory,
			File diagramSpecificationXMLFile, ViewPointResource viewPointResource, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(DiagramSpecificationResource.class);
			DiagramSpecificationResourceImpl returned = (DiagramSpecificationResourceImpl) factory
					.newInstance(DiagramSpecificationResource.class);
			returned.setName(diagramSpecificationDirectory.getName());
			returned.setDirectory(diagramSpecificationDirectory);
			returned.setFile(diagramSpecificationXMLFile);
			returned.setViewPointLibrary(viewPointLibrary);
			returned.setURI(viewPointResource.getURI() + "/" + diagramSpecificationDirectory.getName());
			returned.setServiceManager(viewPointLibrary.getServiceManager());
			returned.relativePathFileConverter = new RelativePathFileConverter(diagramSpecificationDirectory);
			viewPointResource.addToContents(returned);
			viewPointResource.notifyContentsAdded(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DiagramSpecificationResource retrieveDiagramSpecificationResource(File diagramSpecificationDirectory,
			File diagramSpecificationXMLFile, ViewPointResource viewPointResource, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(DiagramSpecificationResource.class);
			DiagramSpecificationResourceImpl returned = (DiagramSpecificationResourceImpl) factory
					.newInstance(DiagramSpecificationResource.class);
			String baseName = diagramSpecificationDirectory.getName();
			File xmlFile = new File(diagramSpecificationDirectory, baseName + ".xml");
			DiagramSpecificationInfo vpi = findDiagramSpecificationInfo(diagramSpecificationDirectory);
			if (vpi == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setURI(viewPointResource.getURI() + "/" + diagramSpecificationDirectory.getName());
			returned.setFile(xmlFile);
			returned.setDirectory(diagramSpecificationDirectory);
			returned.setName(vpi.name);
			if (StringUtils.isNotEmpty(vpi.version)) {
				returned.setVersion(new FlexoVersion(vpi.version));
			}
			returned.setModelVersion(new FlexoVersion(StringUtils.isNotEmpty(vpi.modelVersion) ? vpi.modelVersion : "0.1"));
			returned.setViewPointLibrary(viewPointLibrary);

			returned.setServiceManager(viewPointLibrary.getServiceManager());

			logger.fine("DiagramSpecificationResource " + xmlFile.getAbsolutePath() + " version " + returned.getModelVersion());

			// Now look for example diagrams
			if (diagramSpecificationDirectory.exists() && diagramSpecificationDirectory.isDirectory()) {
				for (File f : diagramSpecificationDirectory.listFiles()) {
					if (f.getName().endsWith(".diagram")) {
						ExampleDiagramResource exampleDiagramResource = ExampleDiagramResourceImpl.retrieveExampleDiagramResource(returned,
								f, viewPointLibrary);
						logger.fine("ExampleDiagramResource " + exampleDiagramResource.getFile().getAbsolutePath() + " version "
								+ exampleDiagramResource.getModelVersion());
					}
				}
			}

			// Now look for palettes
			if (diagramSpecificationDirectory.exists() && diagramSpecificationDirectory.isDirectory()) {
				for (File f : diagramSpecificationDirectory.listFiles()) {
					if (f.getName().endsWith(".palette")) {
						DiagramPaletteResource diagramPaletteResource = DiagramPaletteResourceImpl.retrieveDiagramPaletteResource(returned,
								f, viewPointLibrary);
						logger.fine("DiagramPaletteResource " + diagramPaletteResource.getFile().getAbsolutePath() + " version "
								+ diagramPaletteResource.getModelVersion());
					}
				}
			}

			returned.relativePathFileConverter = new RelativePathFileConverter(diagramSpecificationDirectory);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Class<DiagramSpecification> getResourceDataClass() {
		return DiagramSpecification.class;
	}

	/**
	 * Return virtual model stored by this resource<br>
	 * Load the resource data when unloaded
	 */
	@Override
	public DiagramSpecification getDiagramSpecification() {
		return getVirtualModel();
	}

	/**
	 * Return virtual model stored by this resource when loaded<br>
	 * Do not force the resource data to be loaded
	 */
	@Override
	public DiagramSpecification getLoadedDiagramSpecification() {
		if (isLoaded()) {
			return getDiagramSpecification();
		}
		return null;
	}

	private static class DiagramSpecificationInfo {
		public String version;
		public String name;
		public String modelVersion;
	}

	private static DiagramSpecificationInfo findDiagramSpecificationInfo(File diagramSpecificationDirectory) {
		Document document;
		try {
			logger.fine("Try to find infos for " + diagramSpecificationDirectory);

			String baseName = diagramSpecificationDirectory.getName();
			File xmlFile = new File(diagramSpecificationDirectory, baseName + ".xml");

			if (xmlFile.exists()) {

				document = readXMLFile(xmlFile);
				Element root = getElement(document, "DiagramSpecification");
				if (root != null) {
					DiagramSpecificationInfo returned = new DiagramSpecificationInfo();
					Iterator<Attribute> it = root.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = it.next();
						if (at.getName().equals("name")) {
							logger.fine("Returned " + at.getValue());
							returned.name = at.getValue();
						} else if (at.getName().equals("version")) {
							logger.fine("Returned " + at.getValue());
							returned.version = at.getValue();
						} else if (at.getName().equals("modelVersion")) {
							logger.fine("Returned " + at.getValue());
							returned.modelVersion = at.getValue();
						}
					}
					if (StringUtils.isEmpty(returned.name)) {
						returned.name = diagramSpecificationDirectory.getName();
					}
					return returned;
				}
			} else {
				logger.warning("While analysing diagram-spec candidate: " + diagramSpecificationDirectory.getAbsolutePath()
						+ " cannot find file " + xmlFile.getAbsolutePath());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return null;
	}

	@Override
	public List<ExampleDiagramResource> getExampleDiagramResources() {
		DiagramSpecification ds = getDiagramSpecification();
		return getContents(ExampleDiagramResource.class);
	}

	@Override
	public List<DiagramPaletteResource> getDiagramPaletteResources() {
		DiagramSpecification ds = getDiagramSpecification();
		return getContents(DiagramPaletteResource.class);
	}

}
