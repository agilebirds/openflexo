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

import java.lang.reflect.Type;
import java.util.Vector;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.Variable;
import org.openflexo.antar.expr.parser.ParseException;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.binding.ViewPointDataBinding;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class URIParameter extends EditionSchemeParameter {

	private ViewPointDataBinding baseURI;

	private BindingDefinition BASE_URI = new BindingDefinition("baseURI", String.class, BindingDefinitionType.GET, true);

	public BindingDefinition getBaseURIBindingDefinition() {
		return BASE_URI;
	}

	public ViewPointDataBinding getBaseURI() {
		if (baseURI == null) {
			baseURI = new ViewPointDataBinding(this, ParameterBindingAttribute.baseURI, getBaseURIBindingDefinition());
		}
		return baseURI;
	}

	public void setBaseURI(ViewPointDataBinding baseURI) {
		baseURI.setOwner(this);
		baseURI.setBindingAttribute(ParameterBindingAttribute.baseURI);
		baseURI.setBindingDefinition(getBaseURIBindingDefinition());
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
		if (action.getProject().getProjectOntologyLibrary().isDuplicatedURI(action.getProject().getProjectOntology().getURI(), proposedURI)) {
			// declared_uri_must_be_unique_please_choose_an_other_uri
			return false;
		} else if (!action.getProject().getProjectOntologyLibrary()
				.testValidURI(action.getProject().getProjectOntology().getURI(), proposedURI)) {
			// declared_uri_is_not_well_formed_please_choose_an_other_uri
			return false;
		}

		return true;
	}

	@Override
	public Object getDefaultValue(EditionSchemeAction<?> action) {
		if (getBaseURI().isValid()) {
			String baseProposal = (String) getBaseURI().getBindingValue(action);
			if (baseProposal == null) {
				return null;
			}
			baseProposal = JavaUtils.getClassName(baseProposal);
			String proposal = baseProposal;
			Integer i = null;
			while (action.getProject().getProjectOntologyLibrary()
					.isDuplicatedURI(action.getProject().getProjectOntology().getURI(), proposal)) {
				if (i == null) {
					i = 1;
				} else {
					i++;
				}
				proposal = baseProposal + i;
			}
			return proposal;
		}
		return null;
	}

	public Vector<EditionSchemeParameter> getDependancies() {
		if (getBaseURI().isSet() && getBaseURI().isValid()) {
			Vector<EditionSchemeParameter> returned = new Vector<EditionSchemeParameter>();
			try {
				Vector<Variable> variables = Expression.extractVariables(getBaseURI().toString(), null);
				for (Variable v : variables) {
					String parameterName = v.getName().substring(v.getName().indexOf(".") + 1);
					EditionSchemeParameter p = getScheme().getParameter(parameterName);
					if (p != null) {
						returned.add(p);
					} else {
						p = getScheme().getParameter(parameterName.substring(0, parameterName.indexOf(".")));
						if (p != null) {
							returned.add(p);
						}
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TypeMismatchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return returned;
		} else {
			return null;
		}
	}

	public static class BaseURIBindingIsRequiredAndMustBeValid extends BindingIsRequiredAndMustBeValid<URIParameter> {
		public BaseURIBindingIsRequiredAndMustBeValid() {
			super("'base_uri'_binding_is_required", URIParameter.class);
		}

		@Override
		public ViewPointDataBinding getBinding(URIParameter object) {
			return object.getBaseURI();
		}

		@Override
		public BindingDefinition getBindingDefinition(URIParameter object) {
			return object.getBaseURIBindingDefinition();
		}

	}

}
