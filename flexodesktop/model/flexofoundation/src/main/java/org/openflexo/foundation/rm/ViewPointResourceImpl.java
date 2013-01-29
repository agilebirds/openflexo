package org.openflexo.foundation.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoXMLFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.RelativePathFileConverter;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.StringEncoder;

public abstract class ViewPointResourceImpl extends FlexoXMLFileResourceImpl<ViewPoint> implements ViewPointResource {

	static final Logger logger = Logger.getLogger(FlexoXMLFileResourceImpl.class.getPackage().getName());

	private RelativePathFileConverter relativePathFileConverter;

	private StringEncoder encoder;

	@Override
	public StringEncoder getStringEncoder() {
		if (encoder == null) {
			return encoder = new StringEncoder(super.getStringEncoder(), relativePathFileConverter);
		}
		return encoder;
	}

	public static ViewPointResource makeViewPointResource(String name, String uri, File viewPointDirectory,
			ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ViewPointResource.class);
			ViewPointResourceImpl returned = (ViewPointResourceImpl) factory.newInstance(ViewPointResource.class);
			returned.setServiceManager(viewPointLibrary.getFlexoServiceManager());
			String baseName = viewPointDirectory.getName().substring(0, viewPointDirectory.getName().length() - 10);
			File xmlFile = new File(viewPointDirectory, baseName + ".xml");
			returned.setName(name);
			returned.setURI(uri);
			returned.setVersion(new FlexoVersion("0.1"));
			returned.setFile(xmlFile);
			returned.setDirectory(viewPointDirectory);
			returned.setViewPointLibrary(viewPointLibrary);
			viewPointLibrary.registerViewPoint(returned);
			returned.relativePathFileConverter = new RelativePathFileConverter(viewPointDirectory);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ViewPointResource retrieveViewPointResource(File viewPointDirectory, ViewPointLibrary viewPointLibrary) {
		try {
			ModelFactory factory = new ModelFactory(ViewPointResource.class);
			ViewPointResourceImpl returned = (ViewPointResourceImpl) factory.newInstance(ViewPointResource.class);
			returned.setServiceManager(viewPointLibrary.getFlexoServiceManager());
			String baseName = viewPointDirectory.getName().substring(0, viewPointDirectory.getName().length() - 10);
			File xmlFile = new File(viewPointDirectory, baseName + ".xml");
			ViewPointInfo vpi = findViewPointInfo(viewPointDirectory);
			if (vpi == null) {
				// Unable to retrieve infos, just abort
				return null;
			}
			returned.setFile(xmlFile);
			returned.setDirectory(viewPointDirectory);
			returned.setURI(vpi.uri);
			returned.setName(vpi.name);
			if (StringUtils.isNotEmpty(vpi.version)) {
				returned.setVersion(new FlexoVersion(vpi.version));
			}
			if (StringUtils.isEmpty(vpi.modelVersion)) {
				// This is the old model, convert to new model
				restructureViewPointFrom0_1(viewPointDirectory, xmlFile);
			}

			returned.setModelVersion(new FlexoVersion(StringUtils.isNotEmpty(vpi.modelVersion) ? vpi.modelVersion : "0.1"));
			returned.setViewPointLibrary(viewPointLibrary);
			viewPointLibrary.registerViewPoint(returned);

			System.out.println("ViewPointResource " + xmlFile.getAbsolutePath() + " version " + returned.getModelVersion());

			// Now look for virtual models
			if (viewPointDirectory.exists() && viewPointDirectory.isDirectory()) {
				for (File f : viewPointDirectory.listFiles()) {
					if (f.isDirectory()) {
						File virtualModelFile = new File(f, f.getName() + ".xml");
						if (virtualModelFile.exists()) {
							// TODO: check other VirtualModel types
							DiagramSpecificationResource diagramSpecificationResource = DiagramSpecificationResourceImpl
									.retrieveDiagramSpecificationResource(f, virtualModelFile, viewPointLibrary);
							returned.addToContents(diagramSpecificationResource);
						}
					}
				}
			}

			returned.relativePathFileConverter = new RelativePathFileConverter(viewPointDirectory);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public final ViewPointBuilder instanciateNewBuilder() {
		return new ViewPointBuilder(getViewPointLibrary(), this, getModelVersion());
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
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 */
	@Override
	public ViewPoint loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FlexoException, FileNotFoundException,
			ResourceDependencyLoopException {

		relativePathFileConverter = new RelativePathFileConverter(getDirectory());

		ViewPoint returned = super.loadResourceData(progress);

		String baseName = getDirectory().getName().substring(0, getDirectory().getName().length() - 10);

		returned.init(baseName,/* getDirectory(), getFile(),*/getViewPointLibrary());

		for (VirtualModel<?> vm : returned.getVirtualModels()) {
			for (EditionPattern ep : vm.getEditionPatterns()) {
				ep.finalizeParentEditionPatternDeserialization();
			}
		}

		return returned;
	}

	@Override
	public Class<ViewPoint> getResourceDataClass() {
		return ViewPoint.class;
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

	@Override
	public boolean hasBuilder() {
		return true;
	}

	private static class ViewPointInfo {
		public String uri;
		public String version;
		public String name;
		public String modelVersion;
	}

	private static ViewPointInfo findViewPointInfo(File viewpointDirectory) {
		Document document;
		try {
			logger.fine("Try to find infos for " + viewpointDirectory);

			String baseName = viewpointDirectory.getName().substring(0, viewpointDirectory.getName().length() - 10);
			File xmlFile = new File(viewpointDirectory, baseName + ".xml");

			if (xmlFile.exists()) {

				document = readXMLFile(xmlFile);
				Element root = getElement(document, "ViewPoint");
				if (root != null) {
					ViewPointInfo returned = new ViewPointInfo();
					Iterator<Attribute> it = root.getAttributes().iterator();
					while (it.hasNext()) {
						Attribute at = it.next();
						if (at.getName().equals("uri")) {
							logger.fine("Returned " + at.getValue());
							returned.uri = at.getValue();
						} else if (at.getName().equals("name")) {
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
						if (StringUtils.isNotEmpty(returned.uri)) {
							if (returned.uri.indexOf("/") > -1) {
								returned.name = returned.uri.substring(returned.uri.lastIndexOf("/") + 1);
							} else if (returned.uri.indexOf("\\") > -1) {
								returned.name = returned.uri.substring(returned.uri.lastIndexOf("\\") + 1);
							} else {
								returned.name = returned.uri;
							}
						}
					}
					return returned;
				}
			} else {
				logger.warning("While analysing viewpoint candidate: " + viewpointDirectory.getAbsolutePath() + " cannot find file "
						+ xmlFile.getAbsolutePath());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.fine("Returned null");
		return null;
	}

	private static void restructureViewPointFrom0_1(File viewPointDirectory, File xmlFile) {

		System.out.println("converting " + viewPointDirectory.getAbsolutePath());

		File diagramSpecificationDir = new File(viewPointDirectory, "DiagramSpecification");
		diagramSpecificationDir.mkdir();

		System.out.println("Creating directory " + diagramSpecificationDir.getAbsolutePath());

		try {
			Document viewPointDocument = FlexoXMLFileResourceImpl.readXMLFile(xmlFile);
			Document diagramSpecificationDocument = FlexoXMLFileResourceImpl.readXMLFile(xmlFile);

			for (File f : viewPointDirectory.listFiles()) {
				if (!f.equals(xmlFile) && !f.equals(diagramSpecificationDir) && !f.getName().endsWith("~")) {
					if (f.getName().endsWith(".shema")) {
						try {
							File renamedExampleDiagramFile = new File(f.getParentFile(), f.getName().substring(0, f.getName().length() - 6)
									+ ".diagram");
							FileUtils.rename(f, renamedExampleDiagramFile);
							f = renamedExampleDiagramFile;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					File destFile = new File(diagramSpecificationDir, f.getName());
					FileUtils.rename(f, destFile);
					System.out.println("Moving file " + f.getAbsolutePath() + " to " + destFile.getAbsolutePath());
				}
				if (f.getName().endsWith("~")) {
					f.delete();
				}
			}

			Element diagramSpecification = FlexoXMLFileResourceImpl.getElement(diagramSpecificationDocument, "ViewPoint");
			diagramSpecification.setName("DiagramSpecification");
			FileOutputStream fos = new FileOutputStream(new File(diagramSpecificationDir, "DiagramSpecification.xml"));
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			try {
				outputter.output(diagramSpecificationDocument, fos);
			} catch (IOException e) {
				e.printStackTrace();
			}
			fos.flush();
			fos.close();
		} catch (JDOMException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}