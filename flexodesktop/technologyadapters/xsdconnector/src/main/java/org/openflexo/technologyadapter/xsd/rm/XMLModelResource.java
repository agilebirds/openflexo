/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.technologyadapter.xsd.rm;

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
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.LoadResourceException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;

@SuppressWarnings("serial")
public class XMLModelResource extends FlexoStorageResource<XMLModel> implements FlexoModelResource<XMLModel, XSDMetaModel> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XMLModelResource.class.getPackage()
			.getName());

	public XMLModelResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public XMLModelResource(FlexoProject aProject) {
		super(aProject);
	}

	public XMLModelResource(FlexoProject project, XMLModel newProjectOntology, FlexoProjectFile ontologyFile)
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
		return ResourceType.OWL_ONTOLOGY;
	}

	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	@Override
	protected XMLModel performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<XMLModel> getResourceDataClass() {
		return XMLModel.class;
	}

	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTechnologyAdapter(TechnologyAdapter<?, ?> technologyAdapter) {
		// TODO Auto-generated method stub
	}

	@Override
	public XSDMetaModel getMetaModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMetaModel(XSDMetaModel aMetaModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public XMLModel getModelData() {
		// TODO Auto-generated method stub
		return null;
	}
}
