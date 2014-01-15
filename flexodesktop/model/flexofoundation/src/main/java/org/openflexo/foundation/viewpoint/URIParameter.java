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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Vector;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.antar.expr.BindingValue;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlot;
import org.openflexo.foundation.view.TypeAwareModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.toolbox.StringUtils;

@ModelEntity
@ImplementationClass(URIParameter.URIParameterImpl.class)
@XMLElement
public interface URIParameter extends InnerModelSlotParameter<TypeAwareModelSlot<?, ?>> {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String BASE_URI_KEY = "baseURI";

	@Getter(value = BASE_URI_KEY)
	@XMLAttribute(xmlTag = "base")
	public DataBinding<String> getBaseURI();

	@Setter(BASE_URI_KEY)
	public void setBaseURI(DataBinding<String> baseURI);

	public static abstract class URIParameterImpl extends InnerModelSlotParameterImpl<TypeAwareModelSlot<?, ?>> implements URIParameter {

		private DataBinding<String> baseURI;

		public URIParameterImpl() {
			super();
		}

		@Override
		public TypeAwareModelSlot<?, ?> getModelSlot() {
			TypeAwareModelSlot<?, ?> returned = super.getModelSlot();
			if (returned != null) {
				return returned;
			} else {
				if (getEditionScheme() != null && getEditionScheme().getVirtualModel() != null) {
					if (getEditionScheme().getVirtualModel().getModelSlots(TypeAwareModelSlot.class).size() > 0) {
						return getEditionScheme().getVirtualModel().getModelSlots(TypeAwareModelSlot.class).get(0);
					}
				}
			}
			return null;
		}

		@Override
		public DataBinding<String> getBaseURI() {
			if (baseURI == null) {
				baseURI = new DataBinding<String>(this, String.class, BindingDefinitionType.GET);
				baseURI.setBindingName("baseURI");
			}
			return baseURI;
		}

		@Override
		public void setBaseURI(DataBinding<String> baseURI) {
			if (baseURI != null) {
				baseURI.setOwner(this);
				baseURI.setBindingName("baseURI");
				baseURI.setDeclaredType(String.class);
				baseURI.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.baseURI = baseURI;
		}

		@Override
		public Type getType() {
			return String.class;
		}

		@Override
		public WidgetType getWidget() {
			return WidgetType.URI;
		}

		@Override
		public boolean getIsRequired() {
			return true;
		}

		@Override
		public boolean isValid(EditionSchemeAction action, Object value) {
			if (!(value instanceof String)) {
				return false;
			}

			String proposedURI = (String) value;

			if (StringUtils.isEmpty(proposedURI)) {
				return false;
			}
			if (proposalIsNotUnique(action, proposedURI)) {
				// declared_uri_must_be_unique_please_choose_an_other_uri
				return false;
			} else if (proposalIsWellFormed(action, proposedURI) == false) {
				// declared_uri_is_not_well_formed_please_choose_an_other_uri
				return false;
			}

			return true;
		}

		private String getActionOntologyURI(EditionSchemeAction<?, ?, ?> action) {
			return action.getProject().getURI();
		}

		private boolean proposalIsNotUnique(EditionSchemeAction<?, ?, ?> action, String uriProposal) {
			return action.getProject().isDuplicatedURI(getActionOntologyURI(action), uriProposal);
		}

		private boolean proposalIsWellFormed(EditionSchemeAction<?, ?, ?> action, String uriProposal) {
			return action.getProject().testValidURI(getActionOntologyURI(action), uriProposal);
		}

		@Override
		public Object getDefaultValue(EditionSchemeAction<?, ?, ?> action) {
			if (getBaseURI().isValid()) {
				String baseProposal = null;
				try {
					baseProposal = getBaseURI().getBindingValue(action);
				} catch (TypeMismatchException e) {
					e.printStackTrace();
				} catch (NullReferenceException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				if (baseProposal == null) {
					return null;
				}
				TypeAwareModelSlot modelSlot = getModelSlot();

				return modelSlot.generateUniqueURIName(
						(TypeAwareModelSlotInstance) action.getVirtualModelInstance().getModelSlotInstance(modelSlot), baseProposal);

				/*baseProposal = JavaUtils.getClassName(baseProposal);
				String proposal = baseProposal;
				Integer i = null;
				while (proposalIsNotUnique(action, proposal)) {
					if (i == null) {
						i = 1;
					} else {
						i++;
					}
					proposal = baseProposal + i;
				}
				System.out.println("Generate URI " + proposal);
				return proposal;*/
			}
			return null;
		}

		public Vector<EditionSchemeParameter> getDependancies() {
			if (getBaseURI().isSet() && getBaseURI().isValid()) {
				Vector<EditionSchemeParameter> returned = new Vector<EditionSchemeParameter>();
				for (BindingValue bv : getBaseURI().getExpression().getAllBindingValues()) {
					EditionSchemeParameter p = getScheme().getParameter(bv.getVariableName());
					if (p != null) {
						returned.add(p);
					}
				}
				return returned;
			} else {
				return null;
			}
		}

	}

	public static class BaseURIBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<URIParameter> {
		public BaseURIBindingIsRequiredAndMustBeValid() {
			super("'base_uri'_binding_is_required", URIParameter.class);
		}

		@Override
		public DataBinding<String> getBinding(URIParameter object) {
			return object.getBaseURI();
		}

	}

}
