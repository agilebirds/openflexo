/**
 * 
 */
package org.openflexo.technologyadapter.xsd.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xml.rm.XMLSAXHandler;
import org.openflexo.technologyadapter.xsd.XSDTechnologyContextManager;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XSOntFeatureAssociation;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.toolbox.IProgress;

/**
 * @author xtof
 *
 */
public abstract class XMLXSDFileResourceImpl extends FlexoFileResourceImpl<XMLModel> implements XMLXSDFileResource  {

	//Constants

	static final String CDATA_TYPE_NAME = "CDATA";

	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());
	

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
	public static XMLXSDFileResource makeXMLModelResource(String modelUri,
			File xmlFile, XSDMetaModelResource metaModelResource,
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
			returned.setResourceData(new XMLModel(modelUri, xmlFile, technologyContextManager.getTechnologyAdapter()));
			returned.getModel().setResource(returned);
			technologyContextManager.registerModel((FlexoModelResource<XMLModel, XSDMetaModel>) returned);
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
	public void save(IProgress progress) throws SaveResourceException{

		File myFile = this.getFile();
		
		if (!myFile.exists()){
			//Creates a new file
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
		}
		else 
			logger.warning("Model is Empty!");


	}

	private void writeToFile() throws SaveResourceException {

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			StreamResult result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance(
					"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(getModel().toXML());
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
	public XMLModel getModel() {
		return resourceData;
	}

	@Override
	public XMLModel getModelData() {
		return resourceData;
	}


	// FIXME: behavior here is contradictory with the Lifecycle of the SuperClass => Fix This!
	
	@Override
	public XMLModel getResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FileNotFoundException, FlexoException {
		if (resourceData != null && isLoadable()) {
			resourceData = loadResourceData(progress);
			notifyResourceLoaded();
		}
		return resourceData;
	}
	
	@Override
	public XMLModel loadResourceData(IProgress progress)
			throws ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FileNotFoundException,
			FlexoException {

		if (!isLoaded()) {

			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				factory.setXIncludeAware(true);
				factory.setFeature("http://xml.org/sax/features/namespace-prefixes",true);
				SAXParser saxParser = factory.newSAXParser();


				XMLSAXHandler<XMLModel, XSDMetaModel,XSOntIndividual, XSOntProperty> handler = new XMLSAXHandler<XMLModel, XSDMetaModel,XSOntIndividual, XSOntProperty>(this,false); 
				saxParser.parse(this.getFile(), handler);

				isLoaded = true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return resourceData;
	}


	@Override
	public FlexoMetaModelResource<XMLModel, XSDMetaModel> getMetaModelResource() {
		return metamodelResource;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.technologyadapter.FlexoModelResource#setMetaModelResource(org.openflexo.foundation.technologyadapter.FlexoMetaModelResource)
	 */
	@Override
	public void setMetaModelResource(
			FlexoMetaModelResource<XMLModel, XSDMetaModel> mmRes) {

		metamodelResource = (XSDMetaModelResource) mmRes;
		
		this.getModelData().setMetaModel(mmRes.getMetaModelData());
		
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.FlexoObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<XMLModel> getResourceDataClass() {
		return XMLModel.class;
	}



	// Lifecycle Management
	

	public boolean isLoaded() {
		return isLoaded;
	}

	
}
