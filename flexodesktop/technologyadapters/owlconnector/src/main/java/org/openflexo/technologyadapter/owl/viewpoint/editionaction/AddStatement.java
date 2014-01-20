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
package org.openflexo.technologyadapter.owl.viewpoint.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
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
import org.openflexo.technologyadapter.owl.OWLModelSlot;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLStatement;

@ModelEntity(isAbstract = true)
@ImplementationClass(AddStatement.AddStatementImpl.class)
public abstract interface AddStatement<S extends OWLStatement> extends AssignableAction<OWLModelSlot, S>, SetPropertyValueAction {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String SUBJECT_KEY = "subject";

	@Override
	@Getter(value = SUBJECT_KEY)
	@XMLAttribute
	public DataBinding<?> getSubject();

	@Override
	@Setter(SUBJECT_KEY)
	public void setSubject(DataBinding<?> subject);

	public static abstract class AddStatementImpl<S extends OWLStatement> extends AssignableActionImpl<OWLModelSlot, S> implements
			AddStatement<S> {

		private static final Logger logger = Logger.getLogger(AddStatement.class.getPackage().getName());

		public AddStatementImpl() {
			super();
		}

		public OWLConcept<?> getPropertySubject(EditionSchemeAction action) {
			try {
				return (OWLConcept<?>) getSubject().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		/*@Override
		public R getPatternRole() {
			try {
				return super.getPatternRole();
			} catch (ClassCastException e) {
				logger.warning("Unexpected pattern role type");
				setPatternRole(null);
				return null;
			}
		}*/

		// FIXME: if we remove this useless code, some FIB won't work (see EditionPatternView.fib, inspect an AddIndividual)
		// Need to be fixed in KeyValueProperty.java
		/*@Override
		public void setPatternRole(R patternRole) {
			super.setPatternRole(patternRole);
		}*/

		private DataBinding<?> subject;

		@Override
		public Type getSubjectType() {
			return IFlexoOntologyConcept.class;
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

		public static class SubjectIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<AddStatement> {
			public SubjectIsRequiredAndMustBeValid() {
				super("'subject'_binding_is_required_and_must_be_valid", AddStatement.class);
			}

			@Override
			public DataBinding<IFlexoOntologyConcept> getBinding(AddStatement object) {
				return object.getSubject();
			}

		}

		@Override
		public TypeAwareModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot> getModelSlotInstance(EditionSchemeAction action) {
			return (TypeAwareModelSlotInstance<OWLOntology, OWLOntology, OWLModelSlot>) super.getModelSlotInstance(action);
		}

	}
}
