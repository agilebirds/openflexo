/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.technologyadapter.emf.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.openflexo.ApplicationContext;
import org.openflexo.TestApplicationContext;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividual;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividual;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualAttributeObjectPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.RemoveEMFObjectIndividualReferenceObjectPropertyValue;
import org.openflexo.toolbox.FileResource;

/**
 * Test Class for EMF Model Edition.
 * 
 * @author gbesancon
 * 
 */
public class TestEMFModelEdition {
	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	@Test
	public void test() {
		try {
			ApplicationContext applicationContext = new TestApplicationContext(new FileResource(
					new File("src/test/resources").getAbsolutePath()));
			EMFTechnologyAdapter technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(
					EMFTechnologyAdapter.class);

			FlexoResourceCenter resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(2);

			MetaModelRepository<EMFMetaModelResource, EMFModel, EMFMetaModel, EMFTechnologyAdapter> metaModelRepository = resourceCenter
					.getMetaModelRepository(technologicalAdapter);

			EMFMetaModelRepository emfMetaModelRepository = (EMFMetaModelRepository) metaModelRepository;
			EMFMetaModelResource emfMetaModelResource = emfMetaModelRepository.getResource("http://www.thalesgroup.com/parameters/1.0");
			EMFModelResource emfModelResource = null;
			try {
				emfModelResource = technologicalAdapter.createEmptyModel(File.createTempFile("coucou", ".emf"), "myURI",
						emfMetaModelResource, technologicalAdapter.getTechnologyContextManager());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			EMFObjectIndividual intParameter = createIntParameter(emfModelResource, emfMetaModelResource, "BoolParameter Name",
					Integer.valueOf(12));

			EMFObjectIndividual doubleParameter = createDoubleParameter(emfModelResource, emfMetaModelResource, "StringParameter Name",
					Double.valueOf(42.12));

			EMFObjectIndividual boolParameter = createBoolParameter(emfModelResource, emfMetaModelResource, "BoolParameter Name", true);

			EMFObjectIndividual stringParameter = createStringParameter(emfModelResource, emfMetaModelResource, "StringParameter Name",
					"StringParameter Value");

			EMFObjectIndividual parameterSet = createParameterSet(emfModelResource, emfMetaModelResource, "ParameterSet Name",
					Arrays.asList(intParameter, doubleParameter, boolParameter, stringParameter));

			emfModelResource.save(null);
		} catch (SaveResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceLoadingCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResourceDependencyLoopException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FlexoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected EMFObjectIndividual addEMFObjectIndividual(EMFModelResource emfModelResource, String classURI) {
		EMFObjectIndividual result = null;
		AddEMFObjectIndividual addObject = new AddEMFObjectIndividual(null);
		// addObject.setEMFModelResource(emfModelResource);
		// addObject.setEMFClassURI(classURI);
		result = addObject.performAction(null);
		addObject.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividual removeEMFObjectIndividual(EMFModelResource emfModelResource, EMFObjectIndividual objectIndividual) {
		EMFObjectIndividual result = null;
		RemoveEMFObjectIndividual removeObject = new RemoveEMFObjectIndividual(null);
		// removeObject.setEMFModelResource(emfModelResource);
		// removeObject.setObjectIndividual(objectIndividual);
		removeObject.performAction(null);
		result = objectIndividual;
		removeObject.finalizePerformAction(null, null);
		return result;
	}

	protected EMFObjectIndividualAttributeDataPropertyValue addEMFObjectIndividualAttributeDataPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value)
			throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividualAttributeDataPropertyValue result = null;
		AddEMFObjectIndividualAttributeDataPropertyValue addName = new AddEMFObjectIndividualAttributeDataPropertyValue(null);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeDataProperty((EMFAttributeDataProperty)
		// emfMetaModelResource.getResourceData(null).getDataProperty(propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualAttributeDataPropertyValue removeEMFObjectIndividualAttributeDataPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value)
			throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividualAttributeDataPropertyValue result = null;
		RemoveEMFObjectIndividualAttributeDataPropertyValue addName = new RemoveEMFObjectIndividualAttributeDataPropertyValue(null);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeDataProperty((EMFAttributeDataProperty)
		// emfMetaModelResource.getResourceData(null).getDataProperty(propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualAttributeObjectPropertyValue addEMFObjectIndividualAttributeObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value)
			throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividualAttributeObjectPropertyValue result = null;
		AddEMFObjectIndividualAttributeObjectPropertyValue addName = new AddEMFObjectIndividualAttributeObjectPropertyValue(null);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeObjectProperty((EMFAttributeObjectProperty) emfMetaModelResource.getResourceData(null).getDataProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected <T> EMFObjectIndividualAttributeObjectPropertyValue removeEMFObjectIndividualAttributeObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, T value)
			throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividualAttributeObjectPropertyValue result = null;
		RemoveEMFObjectIndividualAttributeObjectPropertyValue<T> addName = new RemoveEMFObjectIndividualAttributeObjectPropertyValue<T>(
				null);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeObjectProperty((EMFAttributeObjectProperty) emfMetaModelResource.getResourceData(null).getDataProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected <T> EMFObjectIndividualReferenceObjectPropertyValue addEMFObjectIndividualReferenceObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, T value)
			throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividualReferenceObjectPropertyValue result = null;
		AddEMFObjectIndividualReferenceObjectPropertyValue<T> addName = new AddEMFObjectIndividualReferenceObjectPropertyValue<T>(null);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setReferenceObjectProperty((EMFReferenceObjectProperty) emfMetaModelResource.getResourceData(null).getObjectProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected <T> EMFObjectIndividualReferenceObjectPropertyValue removeEMFObjectIndividualReferenceObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, T value)
			throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividualReferenceObjectPropertyValue result = null;
		RemoveEMFObjectIndividualReferenceObjectPropertyValue<T> addName = new RemoveEMFObjectIndividualReferenceObjectPropertyValue<T>(
				null);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setReferenceObjectProperty((EMFReferenceObjectProperty) emfMetaModelResource.getResourceData(null).getObjectProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividual createParameterSet(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, List<EMFObjectIndividual> ownedParameters) throws FileNotFoundException, ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FlexoException {
		EMFObjectIndividual result = null;
		// ParameterSet object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/ParameterSet");
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/ParameterSet/name", name);
		// OwnedParameters parameter
		for (EMFObjectIndividual ownedParameter : ownedParameters) {
			addEMFObjectIndividualReferenceObjectPropertyValue(emfMetaModelResource, result,
					"http://www.thalesgroup.com/parameters/1.0/ParameterSet/ownedParameters", ownedParameter.getObject());
		}
		return result;
	}

	protected EMFObjectIndividual createIntParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, Integer value) throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FlexoException {
		EMFObjectIndividual result = null;
		// IntParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/IntParameterValue");
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/IntParameterValue/value", value);
		return result;
	}

	protected EMFObjectIndividual createDoubleParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, Double value) throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FlexoException {
		EMFObjectIndividual result = null;
		// DoubleParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/DoubleParameterValue");
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/DoubleParameterValue/value", value);
		return result;
	}

	protected EMFObjectIndividual createBoolParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, Boolean value) throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FlexoException {
		EMFObjectIndividual result = null;
		// BoolParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/BoolParameterValue");
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/BoolParameterValue/value", value);
		return result;
	}

	protected EMFObjectIndividual createStringParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, String value) throws FileNotFoundException, ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FlexoException {
		EMFObjectIndividual result = null;
		// StringParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/StringParameterValue");
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/StringParameterValue/value", value);
		return result;
	}
}
