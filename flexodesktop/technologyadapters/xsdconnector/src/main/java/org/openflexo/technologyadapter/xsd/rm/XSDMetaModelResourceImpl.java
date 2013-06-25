/*
 * (c) Copyright 2010-2012 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoResourceTree;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.model.XSDDataType;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSDeclarationsFetcher;
import org.openflexo.technologyadapter.xsd.model.XSOMUtils;
import org.openflexo.technologyadapter.xsd.model.XSOntClass;
import org.openflexo.technologyadapter.xsd.model.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.model.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSOntologyURIDefinitions;
import org.openflexo.toolbox.IProgress;
import org.w3c.dom.Document;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;



/**
 *
 * This class defines and implements the XSD MetaModel FileResource
 * 
 * @author sylvain, luka, Christophe
 * 
 */


public abstract class XSDMetaModelResourceImpl extends FlexoFileResourceImpl<XSDMetaModel> implements XSOntologyURIDefinitions, XSDMetaModelResource {

	private static final Logger logger = Logger.getLogger(XSDMetaModelResourceImpl.class.getPackage().getName());

	// Properties 

	private XSSchemaSet schemaSet;
	private XSDeclarationsFetcher fetcher;

	private boolean isLoaded = false;
	private boolean isLoading = false;
	private boolean isReadOnly = true;


	@Override
	public XSDMetaModel getMetaModelData() {
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
	public XSDMetaModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException {
		XSDMetaModel returned = new XSDMetaModel(getURI(), getFile(), (XSDTechnologyAdapter) getTechnologyAdapter());
		loadWhenUnloaded();
		return returned;
	}



	private static boolean mapsToClass(XSElementDecl element) {
		if (element.getType().isComplexType()) {
			return true;
		} else {
			return false;
		}
	}

	

	private void loadClasses() {
		// TODO if a declaration (base) type is derived, get the correct superclass

		XSDMetaModel aModel = (XSDMetaModel) getModel();

		try {
			XSOntClass thingClass = aModel.createOntologyClass("Thing", XS_THING_URI);


			for (XSComplexType complexType : fetcher.getComplexTypes()) {
				try {
					XSOntClass xsClass = aModel.createOntologyClass(complexType.getName(),fetcher.getUri(complexType));
				} catch (DuplicateURIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (XSElementDecl element : fetcher.getElementDecls()) {
				if (mapsToClass(element)) {

					XSOntClass xsClass = aModel.createOntologyClass(element.getName(),fetcher.getUri(element));
					try {
						XSOntClass superClass = aModel.getClass(fetcher.getUri(element.getType()));
						xsClass.addToSuperClasses(superClass);
					} catch (Exception e) {
						logger.info("XSOntology: unable to set superclass for "+ element.getName());
					}
				}
			}
		}catch (DuplicateURIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	
	private void addDomainIfPossible(XSOntProperty property, String conceptUri, XSDMetaModel aModel) {
		String ownerUri = fetcher.getOwnerURI(conceptUri);
		if (ownerUri != null) {
			XSOntClass owner = aModel.getClass(ownerUri);
			if (owner != null) {
				property.newDomainFound(owner);
				owner.addPropertyTakingMyselfAsDomain(property);
			}
		}
	}

	private void loadDataProperties() {

		XSDMetaModel aModel = (XSDMetaModel) getModel();
		
		/*
		for (XSSimpleType simpleType : fetcher.getSimpleTypes()) {
			XSOntDataProperty xsDataProperty = loadDataProperty(simpleType);
			xsDataProperty.setDataType(computeDataType(simpleType));
		}
		 */
		/*S
		for (XSComplexType complexType : fetcher.getComplexTypes()) {
			if (complexType.isLocal()){
				// TODO Manage Local Types
				XSElementDecl ownerElem = complexType.getScope();
				XSOntClass xsClass = loadClass(ownerElem);
				xsClass.addToSuperClasses(getRootConcept());
			}
		}
		 */

		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element) == false) {
				String uri = fetcher.getUri(element);
				XSOntDataProperty xsDataProperty = aModel.createDataProperty(element.getName(), uri ,element.getType());
				addDomainIfPossible(xsDataProperty, uri,aModel);
			}
		}

		for (XSAttributeDecl attribute : fetcher.getAttributeDecls()) {
			XSOntDataProperty xsDataProperty = aModel.createDataProperty(attribute.getName(), fetcher.getUri(attribute),attribute.getType());
			xsDataProperty.setIsFromAttribute(true);
		}

	}

	private void loadObjectProperties() {

		XSDMetaModel aModel = (XSDMetaModel) getModel();

		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element)) {
				String uri = fetcher.getUri(element);
				XSOntClass c = aModel.getClass(fetcher.getUri(element));
				String name = element.getName();
				XSOntObjectProperty xsObjectProperty = aModel.createObjectProperty(fetcher.getNamespace(element) + "#" + name ,  name, c);
				addDomainIfPossible(xsObjectProperty, uri,aModel);
			}
		}

	}

	
	
	public boolean load() {
		if (isLoading() == true) {
			return false;
		}
		isLoading = true;
		isLoaded = false;
		schemaSet = XSOMUtils.read(getFile());
		if (schemaSet != null) {
			fetcher = new XSDeclarationsFetcher();
			fetcher.fetch(schemaSet);
			getModel().clearAllRangeAndDomain(); 
			loadClasses();
			loadDataProperties();
			loadObjectProperties();
			isLoaded = true;
		}
		isLoading = false;
		return isLoaded;
	}

	public boolean loadWhenUnloaded() {
		if (isLoaded() == false) {
			return load();
		}
		return false;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public boolean isLoading() {
		return isLoading;
	}


	public boolean getIsReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public XSOntology getModel() {
		return resourceData;
	}


	protected XSDeclarationsFetcher getFetcher() {
		return fetcher;
	}



	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 */
	@Override
	public void save(IProgress progress) {
		logger.info("Not implemented yet");
	}

	/**
	 * This method updates the resource.
	 */
	@Override
	public FlexoResourceTree update() {
		return null;
	}

	@Override
	public Class<XSDMetaModel> getResourceDataClass() {
		return XSDMetaModel.class;
	}

}
