/**
 * 
 */
package org.openflexo.technologyadapter.xsd.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xml.rm.XMLReaderSAXHandler;
import org.openflexo.technologyadapter.xml.rm.XMLWriter;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSDTechnologyContextManager;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.toolbox.IProgress;

/**
 * @author xtof
 * 
 */

// TODO : there is code to refactor to be able to merge XMLFileResourceImpl & XMLXSDFileResourceImpl

public abstract class XMLXSDFileResourceImpl extends FlexoFileResourceImpl<XMLXSDModel> implements XMLXSDFileResource {

	// Constants

	static final String CDATA_TYPE_NAME = "CDATA";

	protected static final Logger logger = Logger.getLogger(XMLXSDFileResourceImpl.class.getPackage().getName());

	private XSDMetaModelResource metamodelResource = null;

	// Properties

	private boolean isLoaded = false;

	/**
	 * 
	 * @param modelURI
	 * @param xmlFile
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public static XMLXSDFileResource makeXMLXSDFileResource(String modelUri, File xmlFile, XSDMetaModelResource metaModelResource,
			XSDTechnologyContextManager technologyContextManager) {

		try {
			ModelFactory factory = new ModelFactory(XMLXSDFileResource.class);
			XMLXSDFileResourceImpl returned = (XMLXSDFileResourceImpl) factory.newInstance(XMLXSDFileResource.class);
			returned.setName(xmlFile.getName());
			returned.setFile(xmlFile);
			returned.setURI(xmlFile.toURI().toString());
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setResourceData(new XMLXSDModel(modelUri, xmlFile, technologyContextManager.getTechnologyAdapter()));
			returned.getModel().setResource(returned);
			returned.setMetaModelResource(metaModelResource);

			// FIXME : check that this is ok
			technologyContextManager.registerResource(returned);

			// FIXME : comment ça marche le resource Manager?
			// test pour créer le fichier si jamais il n'existe pas

			if (xmlFile.exists()) {
				logger.warning("will load an existing File: " + xmlFile.getCanonicalPath());
				returned.loadResourceData(null);
			} else {
				returned.save(null);
				returned.isLoaded = true;
			}
			return returned;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.resource.FlexoResource#save(org.openflexo.toolbox.IProgress)
	 */

	@Override
	public void save(IProgress progress) throws SaveResourceException {

		File myFile = this.getFile();

		if (!myFile.exists()) {
			// Creates a new file
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SaveResourceException(this);
			}
		}

		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}

		if (resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			resourceData.clearIsModified(false);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getURI() + " : " + getFile().getName());
			}
		} else
			logger.warning("Model is Empty!");

	}

	private void writeToFile() throws SaveResourceException {

		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(getFile()), "UTF-8");
			XMLWriter<XMLXSDFileResource, XMLXSDModel> writer = new XMLWriter<XMLXSDFileResource, XMLXSDModel>(this, out);

			writer.writeDocument();

		} catch (Exception e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			IOUtils.closeQuietly(out);
		}
		logger.info("Wrote " + getFile());
	}

	@Override
	public XMLXSDModel getModel() {
		return resourceData;
	}

	@Override
	public XMLXSDModel getModelData() {
		if (!isLoaded()) {
			try {
				resourceData = loadResourceData(null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resourceData;
	}

	/**
	 * URI here is the full path to the file
	 */
	@Override
	public String getURI() {
		return this.getFile().toURI().toString();
	}

	@Override
	public XMLXSDModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FileNotFoundException, FlexoException {

		if (!isLoaded()) {

			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				factory.setXIncludeAware(true);
				factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
				SAXParser saxParser = factory.newSAXParser();

				XMLReaderSAXHandler<XMLXSDModel, XSDMetaModel, XSOntIndividual, XSOntProperty> handler = new XMLReaderSAXHandler<XMLXSDModel, XSDMetaModel, XSOntIndividual, XSOntProperty>(
						this, false);
				saxParser.parse(this.getFile(), handler);

				isLoaded = true;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return resourceData;
	}

	@Override
	public FlexoMetaModelResource<XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> getMetaModelResource() {
		return metamodelResource;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.technologyadapter.FlexoModelResource#setMetaModelResource(org.openflexo.foundation.technologyadapter.FlexoMetaModelResource)
	 */
	@Override
	public void setMetaModelResource(FlexoMetaModelResource<XMLXSDModel, XSDMetaModel, XSDTechnologyAdapter> mmRes) {

		metamodelResource = (XSDMetaModelResource) mmRes;

		this.getModel().setMetaModel(mmRes.getMetaModelData());

	}

	@Override
	public Class<XMLXSDModel> getResourceDataClass() {
		return XMLXSDModel.class;
	}

	// Lifecycle Management

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}

}
