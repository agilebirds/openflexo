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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.SetPropertyValueAction;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.technologyadapter.emf.EMFModelSlot;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;

@ModelEntity(isAbstract = true)
@ImplementationClass(SetEMFPropertyValue.SetEMFPropertyValueImpl.class)
public abstract interface SetEMFPropertyValue<T> extends AssignableAction<EMFModelSlot, T>, SetPropertyValueAction {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SUBJECT_KEY = "subject";

	@Override
	@Getter(value = SUBJECT_KEY)
	@XMLAttribute
	public DataBinding<?> getSubject();

	@Override
	@Setter(SUBJECT_KEY)
	public void setSubject(DataBinding<?> subject);

	public static abstract class SetEMFPropertyValueImpl<T> extends AssignableActionImpl<EMFModelSlot, T> implements SetEMFPropertyValue<T> {

		private static final Logger logger = Logger.getLogger(SetEMFPropertyValue.class.getPackage().getName());

		public SetEMFPropertyValueImpl() {
			super();
		}

		private DataBinding<?> subject;

		@Override
		public Type getSubjectType() {
			if (getProperty() != null && getProperty().getDomain() instanceof IFlexoOntologyClass) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) getProperty().getDomain());
			}
			return IFlexoOntologyConcept.class;
		}

		public EMFObjectIndividual getSubject(EditionSchemeAction action) {
			try {
				return (EMFObjectIndividual) getSubject().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public DataBinding<?> getSubject() {
			if (subject == null) {
				subject = new DataBinding<Object>(this, getSubjectType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getSubjectType();
					}
				};
				subject.setBindingName("subject");
			}
			return subject;
		}

		@Override
		public void setSubject(DataBinding<?> subject) {
			if (subject != null) {
				subject = new DataBinding<Object>(subject.toString(), this, getSubjectType(), BindingDefinitionType.GET) {
					@Override
					public Type getDeclaredType() {
						return getSubjectType();
					}
				};
				subject.setBindingName("subject");
			}
			this.subject = subject;
		}

		@Override
		public TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot> getModelSlotInstance(EditionSchemeAction action) {
			return (TypeAwareModelSlotInstance<EMFModel, EMFMetaModel, EMFModelSlot>) super.getModelSlotInstance(action);

		}

		public static class SubjectIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<SetEMFPropertyValue> {
			public SubjectIsRequiredAndMustBeValid() {
				super("'subject'_binding_is_required_and_must_be_valid", SetEMFPropertyValue.class);
			}

			@Override
			public DataBinding<IFlexoOntologyConcept> getBinding(SetEMFPropertyValue object) {
				return object.getSubject();
			}

		}

	}
}
