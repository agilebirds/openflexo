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
package org.openflexo.technologyadapter.emf.viewpoint.editionaction;

import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.AEMFMetaModelObjectImpl;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeDataProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFAttributeObjectProperty;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFReferenceObjectProperty;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

/**
 * Create EMF Object.
 * 
 * @author gbesancon
 * 
 */
@ModelEntity
@ImplementationClass(AddEMFObjectIndividual.AddEMFObjectIndividualImpl.class)
@XMLElement
public interface AddEMFObjectIndividual extends AddIndividual<EMFModelSlot, EMFObjectIndividual> {

	public static abstract class AddEMFObjectIndividualImpl extends AddIndividualImpl<EMFModelSlot, EMFObjectIndividual> implements
			AddEMFObjectIndividual {

		private static final Logger logger = Logger.getLogger(AddEMFObjectIndividual.class.getPackage().getName());

		public AddEMFObjectIndividualImpl() {
			super();
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
			TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot> modelSlotInstance = (TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot>) getModelSlotInstance(action);
			if (modelSlotInstance.getResourceData() != null) {
				IFlexoOntologyClass aClass = getOntologyClass();
				if (aClass instanceof EMFClassClass) {
					EMFClassClass emfClassClass = (EMFClassClass) aClass;
					// Create EMF Object
					EObject eObject = EcoreUtil.create(emfClassClass.getObject());
					modelSlotInstance.getAccessedResourceData().getEMFResource().getContents().add(eObject);
					// Instanciate Wrapper.
					result = modelSlotInstance.getAccessedResourceData().getConverter()
							.convertObjectIndividual(modelSlotInstance.getAccessedResourceData(), eObject);
					for (DataPropertyAssertion dataPropertyAssertion : getDataAssertions()) {
						if (dataPropertyAssertion.evaluateCondition(action)) {
							logger.info("DataPropertyAssertion=" + dataPropertyAssertion);
							EMFAttributeDataProperty property = (EMFAttributeDataProperty) dataPropertyAssertion.getOntologyProperty();
							logger.info("Property=" + property);
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
										result.getObject().eSet(property.getObject(), ((EMFObjectIndividual) value).getObject());
									} else {
										result.getObject().eSet(property.getObject(), value);
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

	}
}
