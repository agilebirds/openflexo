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
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.metamodel.XSDDataType;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntObjectProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
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
import com.sun.xml.xsom.XSType;

/**
 * 
 * This class defines and implements the XSD MetaModel FileResource
 * 
 * @author sylvain, luka, Christophe
 * 
 */

public abstract class XSDMetaModelResourceImpl extends
		FlexoFileResourceImpl<XSDMetaModel> implements
		XSOntologyURIDefinitions, XSDMetaModelResource {

	private static final Logger logger = Logger
			.getLogger(XSDMetaModelResourceImpl.class.getPackage().getName());

	// Properties

	private XSSchemaSet schemaSet;
	private XSDeclarationsFetcher fetcher;

	private boolean isLoaded = false;
	private boolean isLoading = false;
	private boolean isReadOnly = true;

	public static XSDMetaModelResource makeXSDMetaModelResource(
			File xsdMetaModelFile,
			String uri,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(XSDMetaModelResource.class);
			XSDMetaModelResource returned = factory
					.newInstance(XSDMetaModelResource.class);
			returned.setTechnologyAdapter(technologyContextManager
					.getTechnologyAdapter());
			returned.setURI(uri);
			returned.setName("Unnamed");
			returned.setFile(xsdMetaModelFile);

			technologyContextManager
					.registerMetaModel((FlexoMetaModelResource<XMLXSDModel, XSDMetaModel>) returned);

			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

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
	 *            a progress monitor in case the resource data is not
	 *            immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 */
	@Override
	public XSDMetaModel loadResourceData(IProgress progress)
			throws ResourceLoadingCancelledException {

		if (loadWhenUnloaded())
			return resourceData;
		else {
			logger.warning("Not able to load resource");
			return null;
		}
	}

	private static boolean mapsToClass(XSElementDecl element) {
		if (element.getType().isComplexType()) {
			return true;
		} else {
			return false;
		}
	}

	private void loadClasses() {
		// TODO if a declaration (base) type is derived, get the correct
		// superclass

		XSDMetaModel aModel = (XSDMetaModel) getMetaModelData();

		try {

			for (XSComplexType complexType : fetcher.getComplexTypes()) {
				try {
					XSOntClass xsClass = aModel.createOntologyClass(
							complexType.getName(), fetcher.getUri(complexType));
				} catch (DuplicateURIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			for (XSElementDecl element : fetcher.getElementDecls()) {
				if (mapsToClass(element)) {

					XSOntClass xsClass = aModel.createOntologyClass(
							element.getName(), fetcher.getUri(element));
					XSType type = element.getType();
					if (type != null) {
						XSOntClass superClass = aModel.getClass(fetcher
								.getUri(type));
						if (superClass != null)
							xsClass.addToSuperClasses(superClass);
					}
				}
			}
		} catch (DuplicateURIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void addDomainIfPossible(XSOntProperty property, String conceptUri,
			XSDMetaModel aModel) {
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

		XSDMetaModel aModel = (XSDMetaModel) getMetaModelData();

		/*
		 * for (XSSimpleType simpleType : fetcher.getSimpleTypes()) {
		 * XSOntDataProperty xsDataProperty = loadDataProperty(simpleType);
		 * xsDataProperty.setDataType(computeDataType(simpleType)); }
		 */
		/*
		 * S for (XSComplexType complexType : fetcher.getComplexTypes()) { if
		 * (complexType.isLocal()){ // TODO Manage Local Types XSElementDecl
		 * ownerElem = complexType.getScope(); XSOntClass xsClass =
		 * loadClass(ownerElem); xsClass.addToSuperClasses(getRootConcept()); }
		 * }
		 */

		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element) == false) {
				String uri = fetcher.getUri(element);
				XSOntDataProperty xsDataProperty = aModel.createDataProperty(
						element.getName(), uri, element.getType());
				addDomainIfPossible(xsDataProperty, uri, aModel);
			}
		}

		for (XSAttributeDecl attribute : fetcher.getAttributeDecls()) {
			String uri = fetcher.getUri(attribute);
			XSOntDataProperty xsDataProperty = aModel.createDataProperty(
					attribute.getName(), uri, attribute.getType());
			xsDataProperty.setIsFromAttribute(true);
			addDomainIfPossible(xsDataProperty, uri, aModel);
		}

	}

	private void loadObjectProperties() {

		XSDMetaModel aModel = (XSDMetaModel) getMetaModelData();

		for (XSElementDecl element : fetcher.getElementDecls()) {
			if (mapsToClass(element)) {
				String uri = fetcher.getUri(element);
				XSOntClass c = aModel.getClass(fetcher.getUri(element));
				String name = element.getName();
				XSOntObjectProperty xsObjectProperty = aModel
						.createObjectProperty(name,
								fetcher.getNamespace(element) + "#" + name, c);
				addDomainIfPossible(xsObjectProperty, uri, aModel);
			}
		}

	}

	public boolean load() {

		if (resourceData == null) {
			this.resourceData = new XSDMetaModel(getURI(), getFile(),
					(XSDTechnologyAdapter) getTechnologyAdapter());
			resourceData.setResource(this);
		}

		if (isLoading() == true) {
			return false;
		}
		isLoading = true;
		isLoaded = false;
		schemaSet = XSOMUtils.read(getFile());
		if (schemaSet != null) {
			fetcher = new XSDeclarationsFetcher();
			fetcher.fetch(schemaSet);
			getMetaModelData().clearAllRangeAndDomain();
			loadClasses();
			loadDataProperties();
			loadObjectProperties();
			isLoaded = true;
		} else
			logger.info("I've not been able to parse the file" + getFile());
		isLoading = false;
		return isLoaded;
	}

	public boolean loadWhenUnloaded() {
		if (isLoaded() == false) {
			return load();
		}
		return true;
	}

	@Override
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

	// TODO : pas propre, a traiter rapidement

	public XSDeclarationsFetcher getFetcher() {
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
