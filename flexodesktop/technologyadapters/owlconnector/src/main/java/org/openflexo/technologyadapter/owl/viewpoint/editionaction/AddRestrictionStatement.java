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
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.owl.model.OWLClass;
import org.openflexo.technologyadapter.owl.model.OWLConcept;
import org.openflexo.technologyadapter.owl.model.OWLProperty;
import org.openflexo.technologyadapter.owl.model.OWLRestriction;
import org.openflexo.technologyadapter.owl.model.OWLRestriction.RestrictionType;
import org.openflexo.technologyadapter.owl.model.OWLStatement;
import org.openflexo.technologyadapter.owl.model.SubClassStatement;

// No more applicable
@Deprecated
@FIBPanel("Fib/AddRestrictionStatementPanel.fib")
@ModelEntity
@ImplementationClass(AddRestrictionStatement.AddRestrictionStatementImpl.class)
@XMLElement
public interface AddRestrictionStatement extends AddStatement<OWLStatement> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String OBJECT_KEY = "object";
	@PropertyIdentifier(type = String.class)
	public static final String PROPERTY_URI_KEY = "propertyURI";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String RESTRICTION_TYPE_KEY = "restrictionType";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String CARDINALITY_KEY = "cardinality";

	@Getter(value = OBJECT_KEY)
	@XMLAttribute
	public DataBinding<?> getObject();

	@Setter(OBJECT_KEY)
	public void setObject(DataBinding<?> object);

	@Getter(value = PROPERTY_URI_KEY)
	@XMLAttribute
	public String _getPropertyURI();

	@Setter(PROPERTY_URI_KEY)
	public void _setPropertyURI(String propertyURI);

	@Getter(value = RESTRICTION_TYPE_KEY)
	@XMLAttribute
	public DataBinding<RestrictionType> getRestrictionType();

	@Setter(RESTRICTION_TYPE_KEY)
	public void setRestrictionType(DataBinding<RestrictionType> restrictionType);

	@Getter(value = CARDINALITY_KEY)
	@XMLAttribute
	public DataBinding<Integer> getCardinality();

	@Setter(CARDINALITY_KEY)
	public void setCardinality(DataBinding<Integer> cardinality);

	public static abstract class AddRestrictionStatementImpl extends AddStatementImpl<OWLStatement> implements AddRestrictionStatement {

		private static final Logger logger = Logger.getLogger(AddRestrictionStatement.class.getPackage().getName());

		private String propertyURI;

		public AddRestrictionStatementImpl() {
			super();
		}

		@Override
		public String _getPropertyURI() {
			return propertyURI;
		}

		@Override
		public void _setPropertyURI(String propertyURI) {
			this.propertyURI = propertyURI;
		}

		@Override
		public IFlexoOntologyStructuralProperty getProperty() {
			return getObjectProperty();
		}

		@Override
		public void setProperty(IFlexoOntologyStructuralProperty aProperty) {
			setObjectProperty(aProperty);
		}

		public OWLProperty getObjectProperty() {
			if (getVirtualModel() != null) {
				return (OWLProperty) getVirtualModel().getOntologyProperty(_getPropertyURI());
			}
			return null;
		}

		public void setObjectProperty(IFlexoOntologyStructuralProperty p) {
			_setPropertyURI(p != null ? p.getURI() : null);
		}

		public OWLConcept<?> getPropertyObject(EditionSchemeAction action) {
			try {
				return (OWLConcept<?>) getObject().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}

		private DataBinding<?> object;

		public Type getObjectType() {
			if (getObjectProperty() instanceof IFlexoOntologyObjectProperty
					&& ((IFlexoOntologyObjectProperty) getObjectProperty()).getRange() instanceof IFlexoOntologyClass) {
				return IndividualOfClass.getIndividualOfClass((IFlexoOntologyClass) ((IFlexoOntologyObjectProperty) getObjectProperty())
						.getRange());
			}
			return IFlexoOntologyConcept.class;
		}

		@Override
		public DataBinding<?> getObject() {
			if (object == null) {
				object = new DataBinding<Object>(this, getObjectType(), BindingDefinitionType.GET);
				object.setBindingName("object");
			}
			return object;
		}

		@Override
		public void setObject(DataBinding<?> object) {
			if (object != null) {
				object.setOwner(this);
				object.setBindingName("object");
				object.setDeclaredType(getObjectType());
				object.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.object = object;
		}

		private DataBinding<RestrictionType> restrictionType;

		@Override
		public DataBinding<RestrictionType> getRestrictionType() {
			if (restrictionType == null) {
				restrictionType = new DataBinding<RestrictionType>(this, RestrictionType.class, BindingDefinitionType.GET);
				restrictionType.setBindingName("restrictionType");
			}
			return restrictionType;
		}

		@Override
		public void setRestrictionType(DataBinding<RestrictionType> restrictionType) {
			if (restrictionType != null) {
				restrictionType.setOwner(this);
				restrictionType.setBindingName("restrictionType");
				restrictionType.setDeclaredType(RestrictionType.class);
				restrictionType.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.restrictionType = restrictionType;
		}

		public RestrictionType getRestrictionType(EditionSchemeAction action) {
			RestrictionType restrictionType = null;
			try {
				restrictionType = getRestrictionType().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (restrictionType == null) {
				restrictionType = RestrictionType.Some;
			}
			return restrictionType;
		}

		private DataBinding<Integer> cardinality;

		@Override
		public DataBinding<Integer> getCardinality() {
			if (cardinality == null) {
				cardinality = new DataBinding<Integer>(this, Integer.class, BindingDefinitionType.GET);
				cardinality.setBindingName("cardinality");
			}
			return cardinality;
		}

		@Override
		public void setCardinality(DataBinding<Integer> cardinality) {
			if (cardinality != null) {
				cardinality.setOwner(this);
				cardinality.setBindingName("cardinality");
				cardinality.setDeclaredType(Integer.class);
				cardinality.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.cardinality = cardinality;
		}

		public int getCardinality(EditionSchemeAction action) {
			try {
				return getCardinality().getBindingValue(action);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return 1;
		}

		@Override
		public Type getAssignableType() {
			return SubClassStatement.class;
		}

		@Override
		public OWLStatement performAction(EditionSchemeAction action) {
			OWLProperty property = getObjectProperty();
			OWLConcept<?> subject = getPropertySubject(action);
			OWLConcept<?> object = getPropertyObject(action);

			// System.out.println("property="+property+" "+property.getURI());
			// System.out.println("subject="+subject+" "+subject.getURI());
			// System.out.println("object="+object+" "+object.getURI());
			// System.out.println("restrictionType="+getParameterValues().get(action.getRestrictionType()));
			// System.out.println("cardinality="+getParameterValues().get(action.getCardinality()));

			if (subject instanceof OWLClass && object instanceof OWLClass && property instanceof OWLProperty) {
				RestrictionType restrictionType = getRestrictionType(action);
				int cardinality = getCardinality(action);
				OWLRestriction restriction = getModelSlotInstance(action).getAccessedResourceData().createRestriction((OWLClass) subject,
						property, restrictionType, cardinality, (OWLClass) object);

				if (subject instanceof OWLClass) {
					if (subject instanceof OWLClass) {
						((OWLClass) subject).getOntResource().addSuperClass(restriction.getOntResource());
					}
					((OWLClass) subject).updateOntologyStatements();
					return ((OWLClass) subject).getSubClassStatement(restriction);
				}

			}

			return null;
		}

	}
}
