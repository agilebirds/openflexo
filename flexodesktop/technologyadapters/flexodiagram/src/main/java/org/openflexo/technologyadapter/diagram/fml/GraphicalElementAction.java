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
package org.openflexo.technologyadapter.diagram.fml;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.AbstractActionScheme;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.VirtualModel;

public class GraphicalElementAction extends EditionPatternObject {

	private ActionMask actionMask = ActionMask.DoubleClick;
	private AbstractActionScheme abstractActionScheme;
	private GraphicalElementPatternRole graphicalElementPatternRole;

	public static enum ActionMask {
		SingleClick, DoubleClick, ShiftClick, AltClick, CtrlClick, MetaClick;
	}

	private DataBinding<Boolean> conditional;

	public GraphicalElementAction(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getURI() {
		return null;
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	public GraphicalElementPatternRole getGraphicalElementPatternRole() {
		return graphicalElementPatternRole;
	}

	public void setGraphicalElementPatternRole(GraphicalElementPatternRole graphicalElementPatternRole) {
		this.graphicalElementPatternRole = graphicalElementPatternRole;
	}

	public DataBinding<Boolean> getConditional() {
		if (conditional == null) {
			conditional = new DataBinding<Boolean>(this, Boolean.class, DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		return conditional;
	}

	public void setConditional(DataBinding<Boolean> conditional) {
		if (conditional != null) {
			conditional.setOwner(this);
			conditional.setDeclaredType(Boolean.class);
			conditional.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			conditional.setBindingName("conditional");
		}
		this.conditional = conditional;
	}

	public boolean evaluateCondition(EditionPatternInstance editionPatternInstance) {
		if (getConditional().isValid()) {
			try {
				return getConditional().getBindingValue(editionPatternInstance);
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			} catch (NullReferenceException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return getGraphicalElementPatternRole() != null ? getGraphicalElementPatternRole().getEditionPattern() : null;
	}

	@Override
	public BindingModel getBindingModel() {
		return getEditionPattern().getBindingModel();
	}

	@Override
	public VirtualModel getVirtualModel() {
		return getEditionPattern().getVirtualModel();
	}

	public AbstractActionScheme getAbstractActionScheme() {
		return abstractActionScheme;
	}

	public void setAbstractActionScheme(AbstractActionScheme abstractActionScheme) {
		this.abstractActionScheme = abstractActionScheme;
	}

	public ActionMask getActionMask() {
		return actionMask;
	}

	public void setActionMask(ActionMask actionMask) {
		this.actionMask = actionMask;
	}

}
