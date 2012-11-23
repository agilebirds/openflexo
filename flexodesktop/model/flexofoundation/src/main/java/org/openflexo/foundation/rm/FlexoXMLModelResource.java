package org.openflexo.foundation.rm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.ontology.xsd.ProjectXSOntology;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;

@SuppressWarnings("serial")
public class FlexoXMLModelResource extends FlexoStorageResource<ProjectXSOntology> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(FlexoXMLModelResource.class
			.getPackage().getName());

	public FlexoXMLModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoXMLModelResource(FlexoProject aProject) {
		super(aProject);
	}

	public FlexoXMLModelResource(FlexoProject project, ProjectXSOntology newProjectOntology, FlexoProjectFile ontologyFile)
			throws InvalidFileNameException, DuplicateResourceException {
		super(project);
		_resourceData = newProjectOntology;
		newProjectOntology.setFlexoResource(this);
		this.setResourceFile(ontologyFile);
	}

	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException {
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (_resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
			}
		}
		if (clearIsModified) {
			getResourceData().clearIsModified(false);
		}
	}

	private void writeToFile() throws SaveResourceException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			StreamResult result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance(
					"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(this.getResourceData().getOntology().toXML());
			transformer.transform(source, result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			IOUtils.closeQuietly(out);
		}

		logger.info("Wrote " + getFile());
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PROJECT_ONTOLOGY;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	protected ProjectXSOntology performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler)
			throws LoadResourceException, FileNotFoundException, ProjectLoadingCancelledException {
		// TODO Auto-generated method stub
		return null;
	}
}
