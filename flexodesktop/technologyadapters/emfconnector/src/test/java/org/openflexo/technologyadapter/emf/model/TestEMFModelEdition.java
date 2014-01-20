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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.TestFlexoServiceManager;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.viewpoint.VirtualModelModelFactory;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelRepository;
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
	protected static final Logger logger = Logger.getLogger(TestEMFModelEdition.class.getPackage().getName());

	@Test
	public void test() {
		try {
			TestFlexoServiceManager applicationContext = new TestFlexoServiceManager(new FileResource(
					new File("src/test/resources").getAbsolutePath()));
			EMFTechnologyAdapter technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(
					EMFTechnologyAdapter.class);

			FlexoResourceCenter<?> resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(2);

			EMFMetaModelRepository emfMetaModelRepository = resourceCenter
					.getRepository(EMFMetaModelRepository.class, technologicalAdapter);

			EMFMetaModelResource emfMetaModelResource = emfMetaModelRepository.getResource("http://www.thalesgroup.com/parameters/1.0");
			EMFModelResource emfModelResource = null;
			try {
				emfModelResource = technologicalAdapter.createNewEMFModel(File.createTempFile("coucou", ".emf"), "myURI",
						emfMetaModelResource);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// TODO
			VirtualModelModelFactory factory = null;

			EMFObjectIndividual intParameter = createIntParameter(emfModelResource, emfMetaModelResource, "BoolParameter Name",
					Integer.valueOf(12), factory);

			EMFObjectIndividual doubleParameter = createDoubleParameter(emfModelResource, emfMetaModelResource, "StringParameter Name",
					Double.valueOf(42.12), factory);

			EMFObjectIndividual boolParameter = createBoolParameter(emfModelResource, emfMetaModelResource, "BoolParameter Name", true,
					factory);

			EMFObjectIndividual stringParameter = createStringParameter(emfModelResource, emfMetaModelResource, "StringParameter Name",
					"StringParameter Value", factory);

			EMFObjectIndividual parameterSet = createParameterSet(emfModelResource, emfMetaModelResource, "ParameterSet Name",
					Arrays.asList(intParameter, doubleParameter, boolParameter, stringParameter), factory);

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
		} catch (FlexoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected EMFObjectIndividual addEMFObjectIndividual(EMFModelResource emfModelResource, String classURI,
			VirtualModelModelFactory factory) {
		EMFObjectIndividual result = null;
		AddEMFObjectIndividual addObject = factory.newInstance(AddEMFObjectIndividual.class);
		// addObject.setEMFModelResource(emfModelResource);
		// addObject.setEMFClassURI(classURI);
		result = addObject.performAction(null);
		addObject.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividual removeEMFObjectIndividual(EMFModelResource emfModelResource, EMFObjectIndividual objectIndividual,
			VirtualModelModelFactory factory) {
		EMFObjectIndividual result = null;
		RemoveEMFObjectIndividual removeObject = factory.newInstance(RemoveEMFObjectIndividual.class);
		// removeObject.setEMFModelResource(emfModelResource);
		// removeObject.setObjectIndividual(objectIndividual);
		removeObject.performAction(null);
		result = objectIndividual;
		removeObject.finalizePerformAction(null, null);
		return result;
	}

	protected EMFObjectIndividualAttributeDataPropertyValue addEMFObjectIndividualAttributeDataPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value,
			VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividualAttributeDataPropertyValue result = null;
		AddEMFObjectIndividualAttributeDataPropertyValue addName = factory
				.newInstance(AddEMFObjectIndividualAttributeDataPropertyValue.class);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeDataProperty((EMFAttributeDataProperty)
		// emfMetaModelResource.getResourceData(null).getDataProperty(propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualAttributeDataPropertyValue removeEMFObjectIndividualAttributeDataPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value,
			VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividualAttributeDataPropertyValue result = null;
		RemoveEMFObjectIndividualAttributeDataPropertyValue addName = factory
				.newInstance(RemoveEMFObjectIndividualAttributeDataPropertyValue.class);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeDataProperty((EMFAttributeDataProperty)
		// emfMetaModelResource.getResourceData(null).getDataProperty(propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualAttributeObjectPropertyValue addEMFObjectIndividualAttributeObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value,
			VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividualAttributeObjectPropertyValue result = null;
		AddEMFObjectIndividualAttributeObjectPropertyValue addName = factory
				.newInstance(AddEMFObjectIndividualAttributeObjectPropertyValue.class);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeObjectProperty((EMFAttributeObjectProperty) emfMetaModelResource.getResourceData(null).getDataProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualAttributeObjectPropertyValue removeEMFObjectIndividualAttributeObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value,
			VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividualAttributeObjectPropertyValue result = null;
		RemoveEMFObjectIndividualAttributeObjectPropertyValue addName = factory
				.newInstance(RemoveEMFObjectIndividualAttributeObjectPropertyValue.class);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setAttributeObjectProperty((EMFAttributeObjectProperty) emfMetaModelResource.getResourceData(null).getDataProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualReferenceObjectPropertyValue addEMFObjectIndividualReferenceObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value,
			VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividualReferenceObjectPropertyValue result = null;
		AddEMFObjectIndividualReferenceObjectPropertyValue addName = factory
				.newInstance(AddEMFObjectIndividualReferenceObjectPropertyValue.class);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setReferenceObjectProperty((EMFReferenceObjectProperty) emfMetaModelResource.getResourceData(null).getObjectProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividualReferenceObjectPropertyValue removeEMFObjectIndividualReferenceObjectPropertyValue(
			EMFMetaModelResource emfMetaModelResource, EMFObjectIndividual objectIndividual, String propertyURI, Object value,
			VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividualReferenceObjectPropertyValue result = null;
		RemoveEMFObjectIndividualReferenceObjectPropertyValue addName = factory
				.newInstance(RemoveEMFObjectIndividualReferenceObjectPropertyValue.class);
		// addName.setObjectIndividual(objectIndividual);
		// addName.setReferenceObjectProperty((EMFReferenceObjectProperty) emfMetaModelResource.getResourceData(null).getObjectProperty(
		// propertyURI));
		// addName.setValue(value);
		result = addName.performAction(null);
		addName.finalizePerformAction(null, result);
		return result;
	}

	protected EMFObjectIndividual createParameterSet(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, List<EMFObjectIndividual> ownedParameters, VirtualModelModelFactory factory) throws FileNotFoundException,
			ResourceLoadingCancelledException, FlexoException {
		EMFObjectIndividual result = null;
		// ParameterSet object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/ParameterSet", factory);
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/ParameterSet/name", name, factory);
		// OwnedParameters parameter
		for (EMFObjectIndividual ownedParameter : ownedParameters) {
			addEMFObjectIndividualReferenceObjectPropertyValue(emfMetaModelResource, result,
					"http://www.thalesgroup.com/parameters/1.0/ParameterSet/ownedParameters", ownedParameter.getObject(), factory);
		}
		return result;
	}

	protected EMFObjectIndividual createIntParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, Integer value, VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException,
			FlexoException {
		EMFObjectIndividual result = null;
		// IntParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/IntParameterValue", factory);
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name, factory);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/IntParameterValue/value", value, factory);
		return result;
	}

	protected EMFObjectIndividual createDoubleParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, Double value, VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException,
			FlexoException {
		EMFObjectIndividual result = null;
		// DoubleParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/DoubleParameterValue", factory);
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name, factory);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/DoubleParameterValue/value", value, factory);
		return result;
	}

	protected EMFObjectIndividual createBoolParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, Boolean value, VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException,
			FlexoException {
		EMFObjectIndividual result = null;
		// BoolParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/BoolParameterValue", factory);
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name, factory);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/BoolParameterValue/value", value, factory);
		return result;
	}

	protected EMFObjectIndividual createStringParameter(EMFModelResource emfModelResource, EMFMetaModelResource emfMetaModelResource,
			String name, String value, VirtualModelModelFactory factory) throws FileNotFoundException, ResourceLoadingCancelledException,
			FlexoException {
		EMFObjectIndividual result = null;
		// StringParameter object
		result = addEMFObjectIndividual(emfModelResource, "http://www.thalesgroup.com/parameters/1.0/StringParameterValue", factory);
		// Name parameter
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/GenericParameter/name", name, factory);
		// Value parameter.
		addEMFObjectIndividualAttributeDataPropertyValue(emfMetaModelResource, result,
				"http://www.thalesgroup.com/parameters/1.0/StringParameterValue/value", value, factory);
		return result;
	}
}
