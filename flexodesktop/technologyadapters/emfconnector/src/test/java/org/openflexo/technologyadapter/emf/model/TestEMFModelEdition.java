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

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
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
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividual;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualAttributeDataPropertyValue;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFObjectIndividualReferenceObjectPropertyValue;
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
			ApplicationContext applicationContext = new TestApplicationContext(new FileResource("D:/gbe/work/EMF"));
			EMFTechnologyAdapter technologicalAdapter = applicationContext.getTechnologyAdapterService().getTechnologyAdapter(
					EMFTechnologyAdapter.class);

			FlexoResourceCenter resourceCenter = applicationContext.getResourceCenterService().getResourceCenters().get(2);

			MetaModelRepository<EMFMetaModelResource, EMFModel, EMFMetaModel, EMFTechnologyAdapter> metaModelRepository = resourceCenter
					.getMetaModelRepository(technologicalAdapter);

			EMFMetaModelRepository emfMetaModelRepository = (EMFMetaModelRepository) metaModelRepository;
			EMFMetaModelResource metaModelResource = emfMetaModelRepository.getResource("http://www.thalesgroup.com/parameters/1.0");
			EMFModelResource emfModelResource = technologicalAdapter.createEmptyModel(null, metaModelResource,
					technologicalAdapter.getTechnologyContextManager());

			AddEMFObjectIndividual addParameterSet = new AddEMFObjectIndividual(null);
			addParameterSet.setEMFModelResource(emfModelResource);
			addParameterSet.setEMFClassURI("http://www.thalesgroup.com/parameters/1.0/ParameterSet");
			EMFObjectIndividual parameterSet = addParameterSet.performAction(null);
			addParameterSet.finalizePerformAction(null, parameterSet);

			AddEMFObjectIndividualAttributeDataPropertyValue<String> addParameterSetName = new AddEMFObjectIndividualAttributeDataPropertyValue<String>(
					null);
			addParameterSetName.setObjectIndividual(parameterSet);
			addParameterSetName.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/ParameterSet/name"));
			addParameterSetName.setValue("ParameterSet Name");
			EMFObjectIndividualAttributeDataPropertyValue parameterSetNameParameter = addParameterSetName.performAction(null);
			addParameterSetName.finalizePerformAction(null, parameterSetNameParameter);

			AddEMFObjectIndividual addIntParameter = new AddEMFObjectIndividual(null);
			addIntParameter.setEMFModelResource(emfModelResource);
			addIntParameter.setEMFClassURI("http://www.thalesgroup.com/parameters/1.0/IntParameterValue");
			EMFObjectIndividual intParameter = addIntParameter.performAction(null);
			addIntParameter.finalizePerformAction(null, intParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<String> addIntParameterName = new AddEMFObjectIndividualAttributeDataPropertyValue<String>(
					null);
			addIntParameterName.setObjectIndividual(intParameter);
			addIntParameterName.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/GenericParameter/name"));
			addIntParameterName.setValue("IntParameter Name");
			EMFObjectIndividualAttributeDataPropertyValue intParameterNameParameter = addIntParameterName.performAction(null);
			addIntParameterName.finalizePerformAction(null, intParameterNameParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<Integer> addIntParameterValue = new AddEMFObjectIndividualAttributeDataPropertyValue<Integer>(
					null);
			addIntParameterValue.setObjectIndividual(intParameter);
			addIntParameterValue.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/IntParameterValue/value"));
			addIntParameterValue.setValue(Integer.valueOf(12));
			EMFObjectIndividualAttributeDataPropertyValue intParameterValueParameter = addIntParameterValue.performAction(null);
			addIntParameterValue.finalizePerformAction(null, intParameterValueParameter);

			AddEMFObjectIndividual addDoubleParameter = new AddEMFObjectIndividual(null);
			addDoubleParameter.setEMFModelResource(emfModelResource);
			addDoubleParameter.setEMFClassURI("http://www.thalesgroup.com/parameters/1.0/DoubleParameterValue");
			EMFObjectIndividual doubleParameter = addDoubleParameter.performAction(null);
			addDoubleParameter.finalizePerformAction(null, doubleParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<String> addDoubleParameterName = new AddEMFObjectIndividualAttributeDataPropertyValue<String>(
					null);
			addDoubleParameterName.setObjectIndividual(doubleParameter);
			addDoubleParameterName.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/GenericParameter/name"));
			addDoubleParameterName.setValue("DoubleParameter Name");
			EMFObjectIndividualAttributeDataPropertyValue doubleParameterNameParameter = addDoubleParameterName.performAction(null);
			addDoubleParameterName.finalizePerformAction(null, doubleParameterNameParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<Double> addDoubleParameterValue = new AddEMFObjectIndividualAttributeDataPropertyValue<Double>(
					null);
			addDoubleParameterValue.setObjectIndividual(doubleParameter);
			addDoubleParameterValue.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/DoubleParameterValue/value"));
			addDoubleParameterValue.setValue(Double.valueOf(42.12));
			EMFObjectIndividualAttributeDataPropertyValue doubleParameterValueParameter = addDoubleParameterValue.performAction(null);
			addDoubleParameterValue.finalizePerformAction(null, doubleParameterValueParameter);

			AddEMFObjectIndividual addBoolParameter = new AddEMFObjectIndividual(null);
			addBoolParameter.setEMFModelResource(emfModelResource);
			addBoolParameter.setEMFClassURI("http://www.thalesgroup.com/parameters/1.0/BoolParameterValue");
			EMFObjectIndividual boolParameter = addBoolParameter.performAction(null);
			addBoolParameter.finalizePerformAction(null, boolParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<String> addBoolParameterName = new AddEMFObjectIndividualAttributeDataPropertyValue<String>(
					null);
			addBoolParameterName.setObjectIndividual(boolParameter);
			addBoolParameterName.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/GenericParameter/name"));
			addBoolParameterName.setValue("BoolParameter Name");
			EMFObjectIndividualAttributeDataPropertyValue boolParameterNameParameter = addBoolParameterName.performAction(null);
			addBoolParameterName.finalizePerformAction(null, boolParameterNameParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<Boolean> addBoolParameterValue = new AddEMFObjectIndividualAttributeDataPropertyValue<Boolean>(
					null);
			addBoolParameterValue.setObjectIndividual(boolParameter);
			addBoolParameterValue.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/BoolParameterValue/value"));
			addBoolParameterValue.setValue(Boolean.TRUE);
			EMFObjectIndividualAttributeDataPropertyValue boolParameterValueParameter = addBoolParameterValue.performAction(null);
			addBoolParameterValue.finalizePerformAction(null, boolParameterValueParameter);

			AddEMFObjectIndividual addStringParameter = new AddEMFObjectIndividual(null);
			addStringParameter.setEMFModelResource(emfModelResource);
			addStringParameter.setEMFClassURI("http://www.thalesgroup.com/parameters/1.0/StringParameterValue");
			EMFObjectIndividual stringParameter = addStringParameter.performAction(null);
			addStringParameter.finalizePerformAction(null, stringParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<String> addStringParameterName = new AddEMFObjectIndividualAttributeDataPropertyValue<String>(
					null);
			addStringParameterName.setObjectIndividual(stringParameter);
			addStringParameterName.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/GenericParameter/name"));
			addStringParameterName.setValue("StringParameter Name");
			EMFObjectIndividualAttributeDataPropertyValue stringParameterNameParameter = addStringParameterName.performAction(null);
			addStringParameterName.finalizePerformAction(null, stringParameterNameParameter);

			AddEMFObjectIndividualAttributeDataPropertyValue<String> addStringParameterValue = new AddEMFObjectIndividualAttributeDataPropertyValue<String>(
					null);
			addStringParameterValue.setObjectIndividual(stringParameter);
			addStringParameterValue.setAttributeDataProperty((EMFAttributeDataProperty) metaModelResource.getResourceData(null)
					.getDataProperty("http://www.thalesgroup.com/parameters/1.0/StringParameterValue/value"));
			addStringParameterValue.setValue("StringParameter Value");
			EMFObjectIndividualAttributeDataPropertyValue stringParameterValueParameter = addStringParameterValue.performAction(null);
			addStringParameterValue.finalizePerformAction(null, stringParameterValueParameter);

			AddEMFObjectIndividualReferenceObjectPropertyValue<EObject> addParameterSetParameters = new AddEMFObjectIndividualReferenceObjectPropertyValue<EObject>(
					null);
			addParameterSetParameters.setObjectIndividual(parameterSet);
			addParameterSetParameters.setReferenceObjectProperty((EMFReferenceObjectProperty) metaModelResource.getResourceData(null)
					.getObjectProperty("http://www.thalesgroup.com/parameters/1.0/ParameterSet/ownedParameters"));
			addParameterSetParameters.setValue(intParameter.getObject());
			EMFObjectIndividualReferenceObjectPropertyValue parameterSetParameters = addParameterSetParameters.performAction(null);
			addParameterSetParameters.finalizePerformAction(null, parameterSetParameters);

			addParameterSetParameters.setValue(doubleParameter.getObject());
			parameterSetParameters = addParameterSetParameters.performAction(null);
			addParameterSetParameters.finalizePerformAction(null, parameterSetParameters);

			addParameterSetParameters.setValue(boolParameter.getObject());
			parameterSetParameters = addParameterSetParameters.performAction(null);
			addParameterSetParameters.finalizePerformAction(null, parameterSetParameters);

			addParameterSetParameters.setValue(stringParameter.getObject());
			parameterSetParameters = addParameterSetParameters.performAction(null);
			addParameterSetParameters.finalizePerformAction(null, parameterSetParameters);

			emfModelResource.saveResourceData();
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
}
