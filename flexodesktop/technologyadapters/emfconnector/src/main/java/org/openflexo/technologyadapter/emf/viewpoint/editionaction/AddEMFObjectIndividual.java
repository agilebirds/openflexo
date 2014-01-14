/*
 * (c) Copyright 2010-2011 AgileBirds
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

package org.openflexo.technologyadapter.emf.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.AEMFMetaModelObjectImpl;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividualReferenceObjectPropertyValueAsList;

/**
 * Create EMF Object.
 * 
 * @author gbesancon
 * 
 */

@FIBPanel("Fib/AddEMFObjectIndividual.fib")
public class AddEMFObjectIndividual extends AddIndividual<EMFModelSlot, EMFObjectIndividual> {

	private static final Logger logger = Logger.getLogger(AddEMFObjectIndividual.class.getPackage().getName());

	// Binding to host the container specification for the individual to be created
	private DataBinding<List> container;

	public AddEMFObjectIndividual(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public EMFClassClass getOntologyClass() {
		return (EMFClassClass) super.getOntologyClass();
	}

	public void setOntologyClass(EMFClassClass ontologyClass) {
		super.setOntologyClass(ontologyClass);
	}

	@Override
	public Class<EMFObjectIndividual> getOntologyIndividualClass() {
		return EMFObjectIndividual.class;
	}

	@Override
	public EMFObjectIndividual performAction(EditionSchemeAction action) {
		EMFObjectIndividual result = null;
		List container = null;
		TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot> modelSlotInstance = (TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot>) getModelSlotInstance(action);
		if (modelSlotInstance.getResourceData() != null) {
			IFlexoOntologyClass aClass = getOntologyClass();
			if (aClass instanceof EMFClassClass) {
				EMFClassClass emfClassClass = (EMFClassClass) aClass;
				// Create EMF Object
				EObject eObject = EcoreUtil.create(emfClassClass.getObject());
				// put it in its container
				try {
					container = getContainer().getBindingValue(action);
				} catch (TypeMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Instanciate Wrapper.
				result = modelSlotInstance.getResourceData().getConverter()
						.convertObjectIndividual(modelSlotInstance.getResourceData(), eObject);

				// Put it in its container
				if (container == null) {
					modelSlotInstance.getResourceData().getEMFResource().getContents().add(eObject);
				} else {
					// TODO This needs strong testing
					container.add(result);
					result.setContainPropertyValue((EMFObjectIndividualReferenceObjectPropertyValueAsList) container);
				}

				for (DataPropertyAssertion dataPropertyAssertion : getDataAssertions()) {
					if (dataPropertyAssertion.evaluateCondition(action)) {
						logger.info("DataPropertyAssertion=" + dataPropertyAssertion);
						EMFAttributeDataProperty property = (EMFAttributeDataProperty) dataPropertyAssertion.getOntologyProperty();
						logger.info("Property=" + property);
						// Vincent: force to recompute the declared type(since it is automatically compute from the uri).
						// Not sure of this, when loading a view/viewpoint, the declared type is seek from the ontology
						// but while the virtual model is null, the declared type is always Object.(see DataPropertyAssertion).
						// In the case we manipulate IntegerParameters in openflexo connected with int in EMF, then value of Integer is a
						// Long, producing a cast exception.
						dataPropertyAssertion.getValue().setDeclaredType(dataPropertyAssertion.getType());
						Object value = dataPropertyAssertion.getValue(action);
						logger.info("Value=" + value);
						// Set Data Attribute in EMF
						result.getObject().eSet(property.getObject(), value);
					}
				}
				for (ObjectPropertyAssertion objectPropertyAssertion : getObjectAssertions()) {
					if (objectPropertyAssertion.evaluateCondition(action)) {
						logger.info("ObjectPropertyAssertion=" + objectPropertyAssertion);
						if (objectPropertyAssertion.getOntologyProperty() instanceof EMFAttributeObjectProperty) {
							EMFAttributeObjectProperty property = (EMFAttributeObjectProperty) objectPropertyAssertion
									.getOntologyProperty();
							logger.info("Property=" + property);
							Object value = objectPropertyAssertion.getValue(action);
							logger.info("Value=" + value);
							// Set Data Attribute in EMF
							if (value instanceof AEMFMetaModelObjectImpl) {
								result.getObject().eSet(property.getObject(), ((AEMFMetaModelObjectImpl<?>) value).getObject());
							} else {
								result.getObject().eSet(property.getObject(), value);
							}
						} else if (objectPropertyAssertion.getOntologyProperty() instanceof EMFReferenceObjectProperty) {
							EMFReferenceObjectProperty property = (EMFReferenceObjectProperty) objectPropertyAssertion
									.getOntologyProperty();
							logger.info("Property=" + property);
							Object value = objectPropertyAssertion.getValue(action);
							logger.info("Value=" + value);
							// Set Data Attribute in EMF
							if (value instanceof AEMFMetaModelObjectImpl) {
								result.getObject().eSet(property.getObject(), ((AEMFMetaModelObjectImpl<?>) value).getObject());
							} else {
								if (value instanceof EMFObjectIndividual) {
									((EMFObjectIndividual) result).getObject().eSet(property.getObject(),
											((EMFObjectIndividual) value).getObject());
								} else {
									((EMFObjectIndividual) result).getObject().eSet(property.getObject(), value);
								}
							}
						} else {
							logger.warning("Unexpected "
									+ objectPropertyAssertion.getOntologyProperty()
									+ " of "
									+ (objectPropertyAssertion.getOntologyProperty() != null ? objectPropertyAssertion
											.getOntologyProperty().getClass() : null));
						}
					}
				}
				modelSlotInstance.getResourceData().setIsModified();
				logger.info("********* Added individual " + result.getName() + " as " + aClass.getName());
			} else {
				logger.warning("Not allowed to create new Enum values. getOntologyClass()=" + getOntologyClass());
				return null;
			}
		} else {
			logger.warning("Model slot not correctly initialised : model is null");
			return null;
		}

		return result;
	}

	public DataBinding<List> getContainer() {
		if (container == null) {
			container = new DataBinding<List>(this, List.class, DataBinding.BindingDefinitionType.GET);
		}
		return container;
	}

	public void setContainer(DataBinding<List> containerReference) {
		if (containerReference != null) {
			containerReference.setOwner(this);
			containerReference.setBindingName("container");
			containerReference.setDeclaredType(List.class);
			containerReference.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.container = containerReference;
	}

}
